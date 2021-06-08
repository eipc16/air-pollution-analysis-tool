from extractors import BaseCopernicusExtractor

import xarray as xr
import numpy as np
import netCDF4
import os, sys

class CopernicusO3Extractor(BaseCopernicusExtractor):
    def get_matches(self, dataset, latitude_values, longitude_values, latitude_range, longitude_range):
        min_latitude, max_latitude = latitude_range
        min_longitude, max_longitude = longitude_range
        matches_latitude = (latitude_values > min_latitude) & (latitude_values < max_latitude)
        matches_longitude = (longitude_values > min_longitude) & (longitude_values < max_longitude)
        matches_all = matches_latitude & matches_longitude
        return np.argwhere(np.array(matches_all))


    def _process_file(self, dataset, latitude_range, longitude_range):
        longitude_values = dataset['longitude']
        latitude_values = dataset['latitude']
        matches = self.get_matches(dataset, latitude_values, longitude_values, latitude_range, longitude_range)
        values = self.load_values(dataset)
        result = [self.get_data(index[0], index[1], index[2], values) for index in matches]
        if len(result) < 1:
            return [], []
        labels = list(result[0].keys())
        return result, labels


    def load_values(self, dataset):
        return {
            'longitude': dataset['longitude'],
            'latitude': dataset['latitude'],
            'base_times': dataset['time'],
            'time_deltas': dataset['delta_time'],
            'qa_values': dataset['qa_value'],
            'ozone_values': dataset['ozone_total_vertical_column'],
            'ozone_precision_values': dataset['ozone_total_vertical_column_precision']
        }

    def get_data(self, time_index, scanline, pixel, dataset_values):
        res = {}
        base_times = dataset_values['base_times']
        time_deltas = dataset_values['time_deltas']
        res['Time'] = np.datetime_as_string((base_times[time_index] + time_deltas[time_index, scanline, pixel].values).values)
        for key in dataset_values.keys():
            ds = dataset_values[key]
            if key != 'base_times' and key != "time_deltas":
                label = f'{ds.long_name} ({ds.units})'
                res[label] = self.get_value(ds, time_index, scanline, pixel)
        return res

    def get_value(self, variable, time, scanline, pixel):
        return np.array([variable[time, scanline, pixel]])[0]
