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

DATASETS = {
    # 'DAILY_3K': {
    #     'path': './final_result/sat_d=3000_daily.pkl',
    #     'hourly': False,
    # },
    # 'HOURLY_3K': {
    #     'path': './final_result/sat_d=3000_hourly.pkl',
    #     'hourly': True
    # },
    # 'DAILY_10K': {
    #     'path': './final_result/sat_d=10000_daily.pkl',
    #     'hourly': False,
    # },
    # 'HOURLY_10K': {
    #     'path': './final_result/sat_d=10000_hourly.pkl',
    #     'hourly': True
    # },
    # 'G_DAILY_3K': {
    #     'path': './final_result/sat_grouped_d=3000_daily.pkl',
    #     'hourly': False,
    # },
    # 'G_HOURLY_3K': {
    #     'path': './final_result/sat_grouped_d=3000_hourly.pkl',
    #     'hourly': True
    # },
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

def _linear_regression(dataset):
    X_train, X_test, y_train, y_test = dataset
    model = LinearRegression()
    model.fit(X_train, y_train)
    return model.predict(X_test), y_test

def _bayesian_ridge(dataset):
    X_train, X_test, y_train, y_test = dataset
    model = BayesianRidge()
    model.fit(X_train, y_train)
    return model.predict(X_test), y_test

def _mlp_regressor(dataset, **kwargs):
    X_train, X_test, y_train, y_test = dataset
    model = MLPRegressor(**kwargs)
    model.fit(X_train, y_train)
    return model.predict(X_test), y_test

def _random_forest(dataset):
    X_train, X_test, y_train, y_test = dataset
    model = RandomForestRegressor()
    model.fit(X_train, y_train)
    return model.predict(X_test), y_test

def _extra_trees(dataset):
    X_train, X_test, y_train, y_test = dataset
    model = ExtraTreesRegressor()
    model.fit(X_train, y_train)
    return model.predict(X_test), y_test

def _xgboost(dataset):
    X_train, X_test, y_train, y_test = dataset
    model = XGBRegressor()
    model.fit(X_train, y_train)
    return model.predict(X_test), y_test

MODELS = {
    'LinearRegression': {
        'name': 'Regresja liniowa',
        'model_func': lambda dataset: _linear_regression(dataset)
    },
    'BayesianRidge': {
        'name': 'Bayesowska regresja liniowa',
        'model_func': lambda dataset: _bayesian_ridge(dataset)
    },
    'MLPRegressor': {
        'name': 'Sieć neuronowa (MLP)',
        'model_func': lambda dataset: _mlp_regressor(dataset, hidden_layer_sizes=(300,), solver='adam', verbose=10, tol=1e-4, max_iter=500)
    },
    # 'MLPRegressorADAM': {
    #     'name': 'Sieć neuronowa (MLP)',
    #     'model_func': lambda dataset: _mlp_regressor(dataset, solver='adam', verbose=10, tol=1e-4, max_iter=500)
    # },
    # 'MLPRegressorSGD': {
    #     'name': 'Sieć neuronowa (MLP)',
    #     'model_func': lambda dataset: _mlp_regressor(dataset, solver='sgd', verbose=10, tol=1e-4, max_iter=500)
    # },
    'RandomForestRegressor': {
        'name': 'Losowy las',
        'model_func': lambda dataset: _random_forest(dataset)
    },
    'ExtraTreesRegressor': {
        'name': 'Losowy las (ExtraTrees)',
        'model_func': lambda dataset: _extra_trees(dataset)
    },
    'XGBRegressor': {
        'name': "XGBoost",
        "model_func": lambda dataset: _xgboost(dataset)
    }
}

BASE_PATH = './calculations'

def _cals_metrics(y_test, y_pred):
    return {
        'R_2': r2_score(y_test, y_pred),
        'RMSE': math.sqrt(mean_squared_error(y_test, y_pred)),
        'MAE': mean_absolute_error(y_test, y_pred)
    }

# y_pred, y_test

def _save_plot(path,
               vals,
               title, x_axis_title, y_axis_title):
    fig, ax = plt.subplots(nrows=1, ncols=1)

    if len(vals) > 1:
        for val in vals:
            test, pred, name, color = val
            ax.scatter(test, pred, c=color, label=name)
        plt.legend(loc='upper left')
    else:
        y_test, y_pred = vals[0]
        ax.scatter(y_test, y_pred)

    # ax.scatter(y_test, y_pred)
    ax.set_ylim(0, 100)
    ax.set_xlim(0, 100)
    ax.set_title(title)
    ax.set_xlabel(x_axis_title)
    ax.set_ylabel(y_axis_title)
    fig.savefig(path)
    plt.close(fig)


def prepare_calculations(full_path, dataset_path, hourly, model_obj):
    for file in os.listdir(full_path):
        os.remove(os.path.join(full_path, file))

    model_name, model_func = model_obj['name'], model_obj['model_func']

    dataset = prepare_set(dataset_path, hourly)

    metrics = {}

    # TOTAL
    total_set = dataset['total']
    y_pred, y_test = model_func(total_set)

    metrics['total'] = _cals_metrics(y_test, y_pred)

    _save_plot(
        path=os.path.join(full_path, 'total.png'),
        vals=[(y_test, y_pred)],
        title=f'Porównanie wartości prawdziwych z estymowanymi\n({model_name})',
        x_axis_title='Zmierzone PM2.5 [μg/m3]',
        y_axis_title='Estymowane PM2.5 [μg/m3]'
    )

# WINTER
    winter_set = dataset['seasons']['winter']
    wy_pred, wy_test = model_func(winter_set)

    metrics['winter'] = _cals_metrics(wy_test, wy_pred)

    _save_plot(
        path=os.path.join(full_path, 'winter.png'),
        vals=[(wy_test, wy_pred)],
        title=f'Porównanie wartości prawdziwych z estymowanymi\n({model_name}, Zima)',
        x_axis_title='Zmierzone PM2.5 [μg/m3]',
        y_axis_title='Estymowane PM2.5 [μg/m3]'
    )

    # SPRING
    spring_set = dataset['seasons']['spring']
    spy_pred, spy_test = model_func(spring_set)

    metrics['spring'] = _cals_metrics(spy_test, spy_pred)

    _save_plot(
        path=os.path.join(full_path, 'spring.png'),
        vals=[(spy_test, spy_pred)],
        title=f'Porównanie wartości prawdziwych z estymowanymi\n({model_name}, Wiosna)',
        x_axis_title='Zmierzone PM2.5 [μg/m3]',
        y_axis_title='Estymowane PM2.5 [μg/m3]'
    )

    # SUMMER
    summer_set = dataset['seasons']['summer']
    s_y_pred, s_y_test = model_func(summer_set)

    metrics['summer'] = _cals_metrics(s_y_test, s_y_pred)

    _save_plot(
        path=os.path.join(full_path, 'summer.png'),
        vals=[(s_y_test, s_y_pred)],
        title=f'Porównanie wartości prawdziwych z estymowanymi\n({model_name}, Lato)',
        x_axis_title='Zmierzone PM2.5 [μg/m3]',
        y_axis_title='Estymowane PM2.5 [μg/m3]'
    )

    # FALL
    fall_set = dataset['seasons']['fall']
    f_y_pred, f_y_test = model_func(fall_set)

    metrics['fall'] = _cals_metrics(f_y_test, f_y_pred)

    _save_plot(
        path=os.path.join(full_path, 'fall.png'),
        vals=[(f_y_test, f_y_pred)],
        title=f'Porównanie wartości prawdziwych z estymowanymi\n({model_name}, Jesień)',
        x_axis_title='Zmierzone PM2.5 [μg/m3]',
        y_axis_title='Estymowane PM2.5 [μg/m3]'
    )

    # COMBINED

    _save_plot(
        path=os.path.join(full_path, 'total_combined.png'),
        vals=[
            (wy_test, wy_pred, 'Zima', 'b'),
            (spy_test, spy_pred, 'Wiosna', 'g'),
            (s_y_test, s_y_pred, 'Lato', 'r'),
            (f_y_test, f_y_pred, 'Jesień', 'tab:orange')
        ],
        title=f'Porównanie wyników dla poszczególnych sezonów\n({model_name})',
        x_axis_title='Zmierzone PM2.5 [μg/m3]',
        y_axis_title='Estymowane PM2.5 [μg/m3]'
    )

    combined_pred = []
    combined_pred.extend(wy_pred)
    combined_pred.extend(spy_pred)
    combined_pred.extend(s_y_pred)
    combined_pred.extend(f_y_pred)
    combined_test = []
    combined_test.extend(wy_test)
    combined_test.extend(spy_test)
    combined_test.extend(s_y_test)
    combined_test.extend(f_y_test)

    metrics['total_combined'] = _cals_metrics(combined_test, combined_pred)

    # Dump metrics to json
    metrics_path = os.path.join(full_path, 'metrics.json')
    with open(metrics_path, mode='w') as metrics_file:
        metrics_file.write(json.dumps(metrics, indent=4))

    return {
        'total': (y_test, y_pred),
        'winter': (wy_test, wy_pred),
        'summer': (s_y_test, s_y_pred),
        'spring': (spy_test, spy_pred),
        'fall': (f_y_test, f_y_pred)
    }

def calcs():
    if not os.path.exists(BASE_PATH):
        os.mkdir(BASE_PATH)

    sets = {}

    for set_name, set_path in DATASETS.items():
        dataset_path, hourly = set_path['path'], set_path['hourly']
        print(f'Starting calculation for dataset: {set_name}')
        dataset_res_path = os.path.join(BASE_PATH, set_name)
        if not os.path.exists(dataset_res_path):
            os.mkdir(dataset_res_path)

        models = {}

        test = pd.read_pickle(dataset_path)
        winter_datset = test[test['season'] == 1]
        spring_dataset = test[test['season'] == 2]
        summer_dataset = test[test['season'] == 3]
        fall_dataset = test[test['season'] == 4]

        print('winter', len(winter_datset))
        print('spring', len(spring_dataset))
        print('summer', len(summer_dataset))
        print('fall', len(fall_dataset))


        for model_name, model_obj in MODELS.items():
            print(f'Starting calculation for model: {model_name}')
            model_res_path = os.path.join(dataset_res_path, model_name)
            if not os.path.exists(model_res_path):
                os.mkdir(model_res_path)

            model_results = prepare_calculations(model_res_path, dataset_path, hourly, model_obj)

            models[model_name] = model_results
            print(f'Finished calculation for model: {model_name}\n##########################')

        # cross model graphs
        #
        # sets[set_name] = models
        # print(f'Finished calculation for dataset: {set_name}\n---------------------------')


if __name__ == "__main__":
    calcs()

#
# Starting calculation for dataset: G_DAILY_10K
# winter 14824
# spring 17832
# summer 45789
# fall 43986
# Starting calculation for dataset: G_HOURLY_10K
# winter 13649
# spring 13330
# summer 38012
# fall 36976
