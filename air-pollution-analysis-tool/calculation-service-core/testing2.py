import pandas as pd
import datetime as dt
import numpy as np
from pandas.core.reshape.merge import merge

# from testing import _merge

# # pkl = pd.read_pickle('./sat_d=3000_daily.pkl')

# pkl = pd.read_pickle('./satelliteMeasurements_89.pkl')
# pkl_2 = pd.read_pickle('./satelliteMeasurements_52.pkl')
# data = pd.read_pickle('./ground.pkl')

# # print(pkl)

# merge_1 = _merge(pkl, data, distance=3000, merge_on=['year', 'month', 'day', 'location'])
# merge_2 = _merge(pkl_2, data, distance=3000, merge_on=['year', 'month', 'day', 'location'])

# result = merge_1.append(merge_2)#.drop_duplicates()

# # print(pkl_1.append(pkl_2).drop_duplicates())

# # # print(pkl_1.drop_duplicates(['year', 'month', 'day']))
# # # print(pkl_2.drop_duplicates(['year', 'month', 'day']))
# # # print(result.drop_duplicates(['year', 'month', 'day']))

# merged = _merge(pkl, data, distance=3000, merge_on=['year', 'month', 'day', 'location'])

# print(merged.loc[(merged['year'] == 2020) & (merged['month'] == 3) & (merged['day'] == 19) & (merged['location'] == 'CZ0TOVK')])

# # print(merged.loc[merged['year'] == 2020 && merged.loc[merged['month'] == 3 && merged.loc[merged['day'] == 19 && merged.loc[merged['location'] == 'CZ0TOVK'])

# # print(_merge(pkl, data, distance=3000, merge_on=['year', 'month', 'day', 'location']))

# # print(pd.DataFrame(columns=['year', 'month', 'day', 'location', 'satellite_measurement', 'ground_measurement']))


def _load_days_of_week(data):
    results = []
    df = data[['year', 'month', 'day']]

    for _, year, month, day  in df.itertuples():
        entry_date = dt.date(year=year, month=month, day=day)
        results.append(entry_date.weekday())

    return np.array(results)


files = [
    "./sat_d=3000_daily.pkl",
    "./sat_d=3000_hourly.pkl",
    "./sat_d=10000_daily.pkl",
    "./sat_d=10000_hourly.pkl"
]

# sat_attrs.assign(location=calc_stations(sat_attrs, stations, allowed_distance_meters))

# for file in files:
#     data = pd.read_pickle(file)
#     data = data.assign(day_of_week=_load_days_of_week(data))
#     print(data.drop_duplicates(subset=['year', 'month']))
    # print(data)

# print(pd.read_pickle('./sat_d=3000_daily.pkl'))
# print(pd.read_pickle('./sat_d=3000_hourly.pkl'))
# print(pd.read_pickle('./sat_d=10000_daily.pkl'))
# print(pd.read_pickle('./sat_d=10000_hourly.pkl'))

import os

for filename in os.listdir('.'):
    root, ext = os.path.splitext(filename)
    if root.startswith('satelliteMeasurements_') and ext == '.pkl':
        pick = pd.read_pickle(filename)
        print(pick.drop_duplicates(subset=['year', 'month']))


