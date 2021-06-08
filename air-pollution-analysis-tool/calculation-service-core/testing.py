from pandas.core.frame import DataFrame
from tools import _connect_to_mongo
import pandas as pd

# db = _connect_to_mongo(
#     database='dp-db',
#     host='192.168.49.2',
#     port='30317',
#     username='dp-user',
#     password='dp-user-password'
# )
#
# order_id = '60ad4189f3e2d23c3019c758'

# def mongo_collection_to_dataframe(database, collection_name, query={}, remove_id=True, drop_class=True, pickle=False):
#     main_frame = pd.DataFrame()
#     BATCH_SIZE = 1_000_000
#     is_finished = False
#     count = 0
#
#     while not is_finished:
#         cursor = database[collection_name].find(query).limit(BATCH_SIZE).skip(count * BATCH_SIZE)
#
#         try:
#             batch_frame = pd.DataFrame(list(cursor))
#             if drop_class and '_class' in batch_frame.columns:
#                 del batch_frame['_class']
#
#             if remove_id and '_id' in batch_frame.columns:
#                 del batch_frame['_id']
#             # main_frame.append(batch_frame)
#
#             if batch_frame.empty:
#                 is_finished = True
#
#             count = count + 1
#             if pickle:
#                 batch_frame.to_pickle(f'./{collection_name}_{count}.pkl')
#             print(f'Loaded batch: {count}. Finished?: {is_finished}')
#         except Exception:
#             is_finished = True
#
#     return main_frame

# order = mongo_collection_to_dataframe(db, 'orders', query = { 'id': order_id })
# print(order)

# ground_measurements = mongo_collection_to_dataframe(db, 'groundMeasurements', query={'orderId': order_id})
# ground_measurements.to_pickle('./ground.pkl')
#
# satellite_measurements = mongo_collection_to_dataframe(db, 'satelliteMeasurements', query={'orderId': order_id}, pickle=True)
# satellite_measurements.to_pickle('./satellite.pkl')
#
#
# print(pd.read_pickle('./satelliteMeasurements_1.pkl'))
# print(pd.read_pickle('./satelliteMeasurements_2.pkl'))

# ground_pickle = pd.read_pickle('./ground.pkl')
# print(ground_pickle)

import os
from tools import calc_stations

def _merge(sat_measurements, ground_measurements, distance, merge_on):
    satellite_measurements = sat_measurements.drop_duplicates()

    sat_columns = ['year', 'month', 'day', 'hour', 'minute', 'latitude', 'longitude', 'value']
    if pd.Series(sat_columns).isin(satellite_measurements.columns).all():
        sat_attrs = satellite_measurements[sat_columns]
    else:
        sat_attrs = pd.DataFrame(columns=sat_columns)

    ground_columns = ['year', 'month', 'day', 'hour', 'location', 'value']
    if pd.Series(ground_columns).isin(ground_measurements.columns).all():
        ground_attrs = ground_measurements[ground_columns]
    else:
        ground_attrs = pd.DataFrame(columns=ground_columns)

    stations = ground_measurements[['location', 'longitude', 'latitude']].drop_duplicates(subset=['location'])

    sattelite_with_locations = sat_attrs.assign(location=calc_stations(sat_attrs, stations, distance))
    sattelite_with_locations_correct = sattelite_with_locations.loc[sattelite_with_locations['location'] != 'NO_LOCATION']

    merged = pd.merge(
        sattelite_with_locations_correct,
        ground_attrs,
        how='inner',
        on=merge_on).drop_duplicates()

    columns = []
    columns.extend(merge_on)
    columns.extend(['longitude', 'latitude']) 

    if not merged.empty:
        new_data = merged[columns]
        new_data['ground_measurement'] = merged['value_y']
        new_data['satellite_measurement'] = merged['value_x']
        data = new_data
        # group_by_columns = []
        # group_by_columns.extend(columns)
        # group_by_columns.append('ground_measurement')

        # data = new_data.groupby(group_by_columns, as_index=False).mean()
    else:
        total_columns = []
        total_columns.extend(columns)
        total_columns.extend(['ground_measurement', 'satellite_measurement'])
        data = pd.DataFrame(columns=total_columns)

    return data


def _merge_satellite_and_ground(distance, hourly=False):
    ground_data = pd.read_pickle('./ground.pkl')
    main_frame = pd.DataFrame()
    count = 0
    total_count = 0

    merge_on = ['year', 'month', 'day']

    if hourly:
        merge_on.append('hour')

    merge_on.append('location')

    for filename in os.listdir('.'):
        root, ext = os.path.splitext(filename)
        if root.startswith('satelliteMeasurements_') and ext == '.pkl':
            total_count = total_count + 1

    for filename in os.listdir('.'):
        root, ext = os.path.splitext(filename)
        if root.startswith('satelliteMeasurements_') and ext == '.pkl':
            satellite_pickle = pd.read_pickle(filename)
            main_frame = main_frame.append(_merge(satellite_pickle, ground_data, distance=distance, merge_on=merge_on))

            count = count + 1
            print(f'Loaded: {count}/{total_count}')


    group_by_cols = []
    group_by_cols.extend(merge_on)
    group_by_cols.extend(['longitude', 'latitude', 'satellite_measurement'])
    finished_frame = main_frame.groupby(group_by_cols, as_index=False).mean()

    if hourly:
        finished_frame.to_pickle(f'./sat_grouped_d={distance}_hourly.pkl')
    else:
        finished_frame.to_pickle(f'./sat_grouped_d={distance}_daily.pkl')


_merge_satellite_and_ground(distance=3000, hourly=True)
_merge_satellite_and_ground(distance=3000, hourly=False)
_merge_satellite_and_ground(distance=10000, hourly=True)
_merge_satellite_and_ground(distance=10000, hourly=False)
# mainFrame = pd.DataFrame()
#
# count = 0
#
# for filename in os.listdir('.'):
#     root, ext = os.path.splitext(filename)
#     if root.startswith('satelliteMeasurements_') and ext == '.pkl':
#         pick = pd.read_pickle(filename)
#         mainFrame = mainFrame.append(pick)
#         count = count + 1
#
# print(mainFrame)