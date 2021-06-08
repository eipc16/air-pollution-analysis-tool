import pandas as pd
import datetime as dt
import numpy as np
import os
from tools import calc_stations

spring = range(80, 172)
summer = range(172, 264)
fall = range(264, 355)


def season_of_date(date):
    doy = date.timetuple().tm_yday
    if doy in spring:
        season = 4
    elif doy in summer:
        season = 3
    elif doy in fall:
        season = 2
    else:
        season = 1
    return season


def extract_date(date):
    date_obj = dt.datetime.strptime(date, '%Y-%m-%dT%H:%M:%S.%f')
    return [ date_obj.year, date_obj.month, date_obj.day, date_obj.hour, date_obj.minute, date_obj.second, date_obj.weekday(), season_of_date(date_obj) ]


def parse_data(satellite_data_filename):
    res_pkl = pd.DataFrame(pd.read_csv(satellite_data_filename, sep=";"))
    res_pkl[['year', 'month', 'day', 'hour', 'minute', 'second', 'day_of_week', 'season']] = np.array(list(map(lambda x: extract_date(x), res_pkl['Time'])))

    res_pkl['longitude'] = res_pkl['Longitude']
    res_pkl['latitude'] = res_pkl['Latitude']
    res_pkl['value'] = res_pkl['Value']

    res_pkl = res_pkl.drop(columns=['Time', 'Value', 'Longitude', 'Latitude'])
    res_pkl = res_pkl[res_pkl['value'] >= 0]

    return res_pkl[res_pkl['value'] >= 0]


def _merge(sat_measurements, ground_measurements, distance, merge_on):
    satellite_measurements = sat_measurements.drop_duplicates()

    sat_columns = ['year', 'month', 'day', 'hour', 'minute', 'latitude', 'longitude', 'day_of_week', 'season', 'value']
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
    columns.extend(['day_of_week', 'season', 'longitude', 'latitude'])

    if not merged.empty:
        new_data = merged[columns]
        new_data['ground_measurement'] = merged['value_y']
        new_data['satellite_measurement'] = merged['value_x']
        group_by_columns = []
        group_by_columns.extend(columns)
        group_by_columns.append('ground_measurement')

        data = new_data.groupby(group_by_columns, as_index=False).mean()
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

    BASE_FOLDER = "./monthly/"

    for filename in os.listdir(BASE_FOLDER):
        root, ext = os.path.splitext(filename)
        if root.startswith('result_') and ext == '.csv':
            total_count = total_count + 1

    for filename in os.listdir(BASE_FOLDER):
        root, ext = os.path.splitext(filename)
        if root.startswith('result_') and ext == '.csv':
            path = os.path.join(BASE_FOLDER, filename)
            satellite_pickle = parse_data(path)
            main_frame = main_frame.append(_merge(satellite_pickle, ground_data, distance=distance, merge_on=merge_on))
            count = count + 1
            print(f'Loaded: {count}/{total_count}')

    group_by_cols = []
    group_by_cols.extend(merge_on)
    group_by_cols.extend(['longitude', 'latitude', 'satellite_measurement'])
    finished_frame = main_frame.groupby(group_by_cols, as_index=False).mean()

    if hourly:
        finished_frame.to_pickle(f'./final_result/sat_grouped_d={distance}_hourly.pkl')
    else:
        finished_frame.to_pickle(f'./final_result/sat_grouped_d={distance}_daily.pkl')


# _merge_satellite_and_ground(
#     distance=3000,
#     hourly=False
# )
#
# _merge_satellite_and_ground(
#     distance=3000,
#     hourly=True
# )
#
# _merge_satellite_and_ground(
#     distance=10000,
#     hourly=False
# )
#
#
# _merge_satellite_and_ground(
#     distance=10000,
#     hourly=True
# )

from sklearn.linear_model import ElasticNet, LinearRegression, BayesianRidge
from sklearn.ensemble import RandomForestRegressor, ExtraTreesRegressor
from sklearn.neural_network import MLPRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import r2_score, mean_squared_error
import math
import matplotlib.pyplot as plt
import numpy as np

#
# import os
DATASETS = {
    'DAILY_3K': './final_result/sat_d=3000_daily.pkl',
    'HOURLY_3K': './final_result/sat_d=3000_hourly.pkl',
    'DAILY_10K': './final_result/sat_d=10000_daily.pkl',
    'HOURLY_10K': './final_result/sat_d=10000_hourly.pkl'
}

def prepare_set(set_path, hourly=False):
    dataset = pd.read_pickle(set_path)

    winter_datset = dataset[dataset['season'] == 1]
    spring_dataset = dataset[dataset['season'] == 2]
    summer_dataset = dataset[dataset['season'] == 3]
    fall_dataset = dataset[dataset['season'] == 4]

    def _split(values):
        y = values['ground_measurement']
        if hourly:
            X = values[['year', 'month', 'day', 'hour', 'day_of_week', 'season', 'longitude', 'latitude', 'satellite_measurement']]
        else:
            X = values[['year', 'month', 'day', 'day_of_week', 'season', 'longitude', 'latitude', 'satellite_measurement']]

        return train_test_split(X, y, test_size=.2, random_state=1234)

    return {
        'total': _split(dataset),
        'seasons': {
            'winter': _split(winter_datset),
            'spring': _split(spring_dataset),
            'summer': _split(summer_dataset),
            'fall': _split(fall_dataset),
            'total': _split(dataset)
        }
    }

data = prepare_set(DATASETS['DAILY_10K'], hourly=False)

# results = {
#     'total': {
#         'title': 'Totals',
#         'color': 'tab:purple',
#         'data': None
#     },
#     'summer': {
#         'title': 'Summer',
#         'color': 'r',
#         'data': None
#     },
#     'winter': {
#         'title': 'Winter',
#         'color': 'b',
#         'data': None
#     },
#     'fall': {
#         'title': 'Fall',
#         'color': 'tab:orange',
#         'data': None
#     },
#     'spring': {
#         'title': 'Spring',
#         'color': 'g',
#         'data': None
#     }
# }
#
# for season, dataset in data['seasons'].items():
#     X_train, X_test, y_train, y_test = dataset
#
#     model = RandomForestRegressor()
#     model.fit(X_train, y_train)
#     y_predicted = model.predict(X_test)
#     results[season]['data'] = (y_predicted, y_test)
#
# # # X_train, X_test, y_train, y_test = prepare_set(DATASETS['HOURLY_3K'])['total']
# # #
# # # model = LinearRegression()
# # # model.fit(X_train, y_train)
# # # y_predicted = model.predict(X_test)
# # #
# # # r2 = r2_score(y_test, y_predicted)
# # # rmse = math.sqrt(mean_squared_error(y_test, y_predicted))
# # # score = model.score(X_test, y_test)
# #
# # print(f'R2: {r2}, RMSE: {rmse}, Score: {score}')
#
# plt.ylim(0, 100)
# plt.xlim(0, 100)
#
# for _, dataset in results.items():
#     title, color, data = dataset['title'], dataset['color'], dataset['data']
#     X, y = data[0], data[1]
#
#     plt.scatter(X, y, c=color, label=title)
#     coef = np.polyfit(X, y, 1)
#     poly = np.poly1d(coef)
#     plt.plot(X, y, 'o', np.arange(0, 100), poly(np.arange(0, 100)), c=color)
#
# # plt.scatter(results['winter'][0], results['winter'][1], c='b', label='Winter')
# # w_coef = np.polyfit(results['winter'][0],  results['winter'][1], 1)
# # w_poly = np.poly1d(w_coef)
# # plt.plot(results['winter'][0], results['winter'][1], 'yo', results['winter'][0], w_poly(results['winter'][0]), c='b')
# #
# # plt.scatter(results['summer'][0], results['summer'][1], c='r', label='Summer')
# # plt.scatter(results['spring'][0], results['spring'][1], c='g', label='Spring')
# # plt.scatter(results['fall'][0], results['fall'][1], c='tab:orange', label='Fall')
#
# plt.legend(loc='upper left')
# plt.show()