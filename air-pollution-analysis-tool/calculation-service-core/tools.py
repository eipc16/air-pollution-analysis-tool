import math

import numpy as np
from pandas import DataFrame as DataFrame
from pymongo import MongoClient


def _connect_to_mongo(database, host, port=None, username=None, password=None):
    addr = host if port is None else f'{host}:{port}'

    if username and password:
        mongo_uri = f'mongodb://{username}:{password}@{addr}/{database}'
    else:
        mongo_uri = f'mongodb://{addr}/{database}'

    conn = MongoClient(mongo_uri)

    return conn[database]


def mongo_collection_to_dataframe(database, collection_name, query={}, remove_id=True, drop_class=True):
    cursor = database[collection_name].find(query)
    dataframe = DataFrame(list(cursor))

    if drop_class and '_class' in dataframe.columns:
        del dataframe['_class']

    if remove_id and '_id' in dataframe.columns:
        del dataframe['_id']

    return dataframe


def calc_distance(ground_station, satellite_measurements):
    [satellite_long, sattelite_lat] = satellite_measurements
    R = 6371e3

    results = []

    for ground_measurement in ground_station:
        [ground_long, ground_lat] = ground_measurement
        fi1, fi2 = ground_lat * math.pi / 180, sattelite_lat * math.pi / 180

        dfi, dlambda = (ground_lat - sattelite_lat) * math.pi / 180, (ground_long - satellite_long) * math.pi / 180
        a_np = np.sin(dfi / 2) * np.sin(dfi / 2) + np.cos(fi1) * np.cos(fi2) * np.sin(dlambda / 2) * np.sin(dlambda / 2)
        c_np = 2 * np.arctan2(np.sqrt(a_np), np.sqrt(1 - a_np))
        results.append(R * c_np)

    return np.array(results)


def calc_stations(dataset, stations, max_distance):
    satt_long_lat = dataset[['longitude', 'latitude']]
    stations_long_lat = stations[['longitude', 'latitude']]
    stations_names = stations[['location']].to_numpy()

    # ground stations x distances to satellite measurements
    distances = calc_distance(stations_long_lat.to_numpy(), satt_long_lat.to_numpy().T)
    flipped_distances = distances.T

    results = []

    for dist in flipped_distances:
        if np.where(dist < max_distance)[0].size == 0:
            results.append('NO_LOCATION')
            continue
        min = np.where(np.min(dist) == dist)
        results.append(stations_names[min][0][0])

    return results