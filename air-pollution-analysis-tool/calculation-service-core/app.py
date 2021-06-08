import os
from quart import Quart, request, jsonify
from sklearn.neural_network import MLPRegressor
from sklearn.ensemble import RandomForestRegressor, ExtraTreesRegressor
from sklearn.linear_model import ElasticNet, LinearRegression
from sklearn.model_selection import train_test_split;

import pandas as pd
import asyncio

from tools import mongo_collection_to_dataframe, _connect_to_mongo, calc_stations

models = {
    'LR': {
        'model': LinearRegression,
        'parameters': {}
    },
    'MLPR': {
        'model': MLPRegressor,
        'parameters': {
            'hidden_layer_sizes': 20,
            'activation': 'relu'
        }
    },
    'RandomForestRegressor': {
        'model': RandomForestRegressor,
        'parameters': {
            'n_estimators': 10
        }
    },
    'ExtraTreesRegressor': {
        'model': ExtraTreesRegressor,
        'parameters': {
            'n_estimators': 10
        }
    },
    'ElasticNet': {
        'model': ElasticNet
    }
}


def get_env_variable(name, default_value=None):
    if name in os.environ:
        return os.environ[name]
    return default_value


db = _connect_to_mongo(
    database=get_env_variable('MAIN_DATABASE_NAME', 'main-db'),
    host=get_env_variable('MAIN_DATABASE_HOST', '192.168.49.2'),
    port=get_env_variable('MAIN_DATABASE_PORT', '30318'),
    username=get_env_variable('MAIN_DATABASE_USER', 'main-user'),
    password=get_env_variable('MAIN_DATABASE_PASSWORD', 'main-user-password')
)

loop = asyncio.new_event_loop()
asyncio.set_event_loop(loop)
app = Quart(__name__)


@app.route("/models", methods=['GET'])
async def fetch_models():
    model_list = list(map(lambda x: {'name': x}, models.keys()))
    return jsonify(model_list)


@app.route("/predictions", methods=['POST'])
async def predict():
    args = await request.get_json()

    selected_model = args['model']

    if selected_model not in models.keys():
        return jsonify({'error': f'Model does not exist: {selected_model}'}), 500

    asyncio.get_event_loop().run_in_executor(None, _predict, args)

    return jsonify({})


def _predict(args):
    calculation_request_id = args['calculationRequestId']
    order_id = args['orderId']
    allowed_distance_meters = args['distance']
    model_name = args['model']

    X_train, X_test, y_train, y_test = _prepare_data(order_id, allowed_distance_meters)
    model = _get_model(model_name)

    model.fit(X_train, y_train)

    test_result = model.predict(X_test)

    # test data
    test_data = X_test.rename(columns={"hour_x": "hour", "value_x": "satelliteMeasurement"})
    test_data['predictions'] = test_result
    test_data['trueMeasurement'] = y_test
    test_data['calculationRequestId'] = calculation_request_id
    db['testData'].insert_many(test_data.to_dict(orient='records'))

    # whole sattelite data
    result_data = mongo_collection_to_dataframe(db, 'satelliteMeasurements', query={'orderId': order_id})
    result_data['calculationRequestId'] = calculation_request_id
    data_to_predict = result_data[['year', 'month', 'day', 'hour', 'minute', 'longitude', 'latitude', 'value']]
    final_predictions=model.predict(data_to_predict)
    res = result_data.assign(predictions=final_predictions)
    db['satellitePredictions'].insert_many(res.to_dict(orient='records'))

    return final_predictions


def _prepare_data(order_id, allowed_distance_meters):
    satellite_measurements = mongo_collection_to_dataframe(db, 'satelliteMeasurements', query={'orderId': order_id})
    ground_measurements = mongo_collection_to_dataframe(db, 'groundMeasurements', query={'orderId': order_id})

    sat_attrs = satellite_measurements[['year', 'month', 'day', 'hour', 'minute', 'latitude', 'longitude', 'value']]
    ground_attrs = ground_measurements[['year', 'month', 'day', 'hour', 'location', 'value']]
    stations = ground_measurements[['location', 'longitude', 'latitude']].drop_duplicates(subset=['location'])

    sattelite_with_locations = sat_attrs.assign(location=calc_stations(sat_attrs, stations, allowed_distance_meters))
    sattelite_with_locations_correct = sattelite_with_locations.loc[sattelite_with_locations['location'] != 'NO_LOCATION']

    merged = pd.merge(
        sattelite_with_locations_correct,
        ground_attrs,
        how='inner',
        on=['year', 'month', 'day', 'location']).drop_duplicates()

    y = merged['value_y']
    X = merged[['year', 'month', 'day', 'hour_x', 'minute', 'longitude', 'latitude', 'value_x']]

    return train_test_split(X, y, test_size=.3, random_state=1234)


def _get_model(model_name):
    model_config = models[model_name]

    if 'parameters' in model_config:
        parameters = model_config['parameters']
        return model_config['model'](**parameters)
    else:
        return model_config['model']()


if __name__ == '__main__':
    app.run(debug=get_env_variable('ENABLE_DEBUG', False),
            port=get_env_variable('SERVER_PORT', 5001))
