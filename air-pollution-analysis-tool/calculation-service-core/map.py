from sklearn.linear_model import ElasticNet, LinearRegression, BayesianRidge
from sklearn.ensemble import RandomForestRegressor, ExtraTreesRegressor
from sklearn.neural_network import MLPRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import r2_score, mean_squared_error, mean_absolute_error
from xgboost import XGBRegressor
import pandas as pd
import os
import math
import json
import matplotlib.pyplot as plt
import numpy as np
import datetime as dt
import geopandas as gpd
from shapely.geometry import Point
from joblib import dump, load

DATASETS = {
    'G_DAILY_10K': {
        'path': './final_result/sat_grouped_d=10000_daily.pkl',
        'hourly': False,
    },
    'G_HOURLY_10K': {
        'path': './final_result/sat_grouped_d=10000_hourly.pkl',
        'hourly': True
    }
}

def prepare_set(set_path, hourly=False):
    dataset = pd.read_pickle(set_path)

    winter_datset = dataset[dataset['season'] == 1]
    spring_dataset = dataset[dataset['season'] == 4]
    summer_dataset = dataset[dataset['season'] == 3]
    fall_dataset = dataset[dataset['season'] == 2]

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

def _random_forest(dataset):
    X_train, X_test, y_train, y_test = dataset
    model = RandomForestRegressor()
    model.fit(X_train, y_train)
    return model

def train_model(dataset_path, hourly):
    trainset = prepare_set(dataset_path, hourly=hourly)

    total_set = _random_forest(trainset['total'])
    winter_model = _random_forest(trainset['seasons']['winter'])
    spring_model = _random_forest(trainset['seasons']['spring'])
    summer_model = _random_forest(trainset['seasons']['summer'])
    fall_model = _random_forest(trainset['seasons']['fall'])

    return total_set, winter_model, spring_model, summer_model, fall_model

def _load_base_set():
    return pd.read_pickle('./final_result/poland_measurements.pkl')

def _assign_points(geojson, longitudes_and_latitudes):
    return longitudes_and_latitudes.apply(lambda x: geojson.contains(Point(x[0], x[1])), axis=1)

def _plot_map():
    geojson = gpd.read_file('./maps/pol.geo.json')
    baseset = _load_base_set()

    winter_m = load('./models/G_HOURLY_10K/winter.joblib')
    summer_m = load('./models/G_HOURLY_10K/summer.joblib')
    fall_m = load('./models/G_HOURLY_10K/fall.joblib')
    spring_m = load('./models/G_HOURLY_10K/spring.joblib')

    frame = pd.DataFrame()

    winter_set = baseset[baseset['season'] == 1]
    winter_set = winter_set.assign(predictions=winter_m.predict(winter_set[['year', 'month', 'day', 'hour', 'day_of_week', 'season', 'longitude', 'latitude', 'value']]))
    frame = frame.append(winter_set)

    geojson.plot(facecolor="none",
                 edgecolor='black')
    plt.scatter(winter_set['longitude'], winter_set['latitude'], c=winter_set['predictions'], cmap="RdYlGn_r", s=1)
    plt.colorbar(label='Estymowane stężenie PM2,5 [μg/m3]')
    plt.title('Średnie estymowane wartości PM2,5\n(Zima, Pomiary dzienne)')
    plt.xlabel("Długość geograficzna")
    plt.ylabel("Szerokość geograficzna")
    plt.savefig('./maps/total_winter.png')
    plt.clf()
    plt.cla()

    fall_set = baseset[baseset['season'] == 2]
    fall_set = fall_set.assign(predictions=fall_m.predict(fall_set[['year', 'month', 'day', 'hour', 'day_of_week', 'season', 'longitude', 'latitude', 'value']]))
    frame = frame.append(fall_set)

    geojson.plot(facecolor="none",
                 edgecolor='black')
    plt.scatter(fall_set['longitude'], fall_set['latitude'], c=fall_set['predictions'], cmap="RdYlGn_r", s=1)
    plt.colorbar(label='Estymowane stężenie PM2,5 [μg/m3]')
    plt.title('Średnie estymowane wartości PM2,5\n(Jesień, Pomiary godzinne)')
    plt.xlabel("Długość geograficzna")
    plt.ylabel("Szerokość geograficzna")
    plt.savefig('./maps/total_fall.png')
    plt.clf()
    plt.cla()

    summer_dataset = baseset[baseset['season'] == 3]
    summer_dataset = summer_dataset.assign(predictions=summer_m.predict(summer_dataset[['year', 'month', 'day', 'hour', 'day_of_week', 'season', 'longitude', 'latitude', 'value']]))
    frame = frame.append(summer_dataset)

    geojson.plot(facecolor="none",
                 edgecolor='black')
    plt.scatter(summer_dataset['longitude'], summer_dataset['latitude'], c=summer_dataset['predictions'], cmap="RdYlGn_r", s=1)
    plt.colorbar(label='Estymowane stężenie PM2,5 [μg/m3]')
    plt.title('Średnie estymowane wartości PM2,5\n(Lato, Pomiary godzinne)')
    plt.xlabel("Długość geograficzna")
    plt.ylabel("Szerokość geograficzna")
    plt.savefig('./maps/total_summer.png')
    plt.clf()
    plt.cla()

    spring_dataset = baseset[baseset['season'] == 4]
    spring_dataset = spring_dataset.assign(predictions=spring_m.predict(spring_dataset[['year', 'month', 'day', 'hour', 'day_of_week', 'season', 'longitude', 'latitude', 'value']]))
    frame = frame.append(spring_dataset)

    geojson.plot(facecolor="none",
                 edgecolor='black')
    plt.scatter(spring_dataset['longitude'], spring_dataset['latitude'], c=spring_dataset['predictions'], cmap="RdYlGn_r", s=1)
    plt.colorbar(label='Estymowane stężenie PM2,5 [μg/m3]')
    plt.title('Średnie estymowane wartości PM2,5\n(Wiosna, Pomiary godzinne)')
    plt.xlabel("Długość geograficzna")
    plt.ylabel("Szerokość geograficzna")
    plt.savefig('./maps/total_spring.png')
    plt.clf()
    plt.cla()

    plot_data = frame[['longitude', 'latitude', 'predictions']].groupby(['longitude', 'latitude'], as_index=False).max()

    geojson.plot(facecolor="none",
                 edgecolor='black')
    plt.scatter(plot_data['longitude'], plot_data['latitude'], c=plot_data['predictions'], cmap="RdYlGn_r", s=1)
    plt.colorbar(label='Estymowane stężenie PM2,5 [μg/m3]')
    plt.title('Średnie estymowane wartości PM2,5\n(Cały rok, Pomiary godzinne)')
    plt.xlabel("Długość geograficzna")
    plt.ylabel("Szerokość geograficzna")
    plt.savefig('./maps/total_daily.png')
    # f, ax = plt.subplots(1, figsize=(16, 16))
    # ax =

def _train_models(path, train_set_path, hourly):
    total_set, winter_model, spring_model, summer_model, fall_model = train_model(train_set_path, hourly)

    dump(total_set, os.path.join(path, 'total.joblib'))
    dump(winter_model, os.path.join(path, 'winter.joblib'))
    dump(spring_model, os.path.join(path, 'spring.joblib'))
    dump(summer_model, os.path.join(path, 'summer.joblib'))
    dump(fall_model, os.path.join(path, 'fall.joblib'))
#
# def _prepare_models(path='./models/'):
#     for dataset, dataset_obj in DATASETS.items():
#         set_path = os.path.join(path, dataset)
#         if not os.path.exists(set_path):
#             os.mkdir(set_path)
#         print(f'Training for dataset: {dataset}')
#         _train_models(set_path, dataset_obj['path'], dataset_obj['hourly'])

def _get_geo_stations_map():
    geojson = gpd.read_file('./maps/pol.geo.json')
    geojson.plot(facecolor="none",
                 edgecolor='black')
    geo_stations_set = pd.read_pickle('./ground.pkl')[['location', 'longitude', 'latitude']].drop_duplicates()
    geo_stations_set = geo_stations_set.assign(contains=_assign_points(geojson, geo_stations_set[['longitude', 'latitude']]))
    geo_stations_set = geo_stations_set[geo_stations_set['contains'] == True]

    print(geo_stations_set)

    plt.scatter(geo_stations_set['longitude'], geo_stations_set['latitude'])
    plt.title('Lokalizacje naziemnych stacji pomiarowych\n(Polska, PM2,5)')
    plt.xlabel("Długość geograficzna")
    plt.ylabel("Szerokość geograficzna")
    plt.savefig('./maps/stations.png')

# _plot_geo_map():


if __name__ == "__main__":
    # _prepare_models()
    # _plot_map()

    for dataset, dataset_obj in DATASETS.items():
        path = dataset_obj['path']
        data = pd.read_pickle(path)
        set = data.groupby(['season'], as_index=False).count()
        print(dataset)
        print(set)
        print('TOTAL: ', np.sum(set['year']))
        print('1 - WINTER, 2 - FALL, 3 - SUMMER, 4 - SPRING')
        print('------------')
        # print('Total: ' + set.apply(lambda x: np.sum(x), axis=0))


    # print(pd.read_pickle('./final_result/poland_measurements.pkl'))

    # _get_geo_stations_map()
    # print(_load_base_set())