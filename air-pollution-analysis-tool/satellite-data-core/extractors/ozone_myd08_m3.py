from extractors import BaseModisExtractor

import numpy as np
import re
from datetime import datetime
from pyproj import Proj, Transformer

class OzoneMYD09M3(BaseModisExtractor):
    DATE_REGEX = re.compile(r'VALUE(\s){16}=\s\"(?P<date>((\d+){4}-(\d+){2}-(\d+){2}))\"', re.VERBOSE)
    UPPER_LEFT_REGEX = re.compile(r'''UpperLeftPointMtrs=\(
                          (?P<upper_left_x>[+-]?\d+\.\d+)
                          ,
                          (?P<upper_left_y>[+-]?\d+\.\d+)
                          \)''', re.VERBOSE)
    LOWER_RIGHT_REGEX = re.compile(r'''LowerRightMtrs=\(
                          (?P<lower_right_x>[+-]?\d+\.\d+)
                          ,
                          (?P<lower_right_y>[+-]?\d+\.\d+)
                          \)''', re.VERBOSE)
    X_DIM_SIZE_REGEX = re.compile(r'XDim=(?P<x_size>\d+)')
    Y_DIM_SIZE_REGEX = re.compile(r'YDim=(?P<y_size>\d+)')
    
    def __init__(self):
        self._extractor_config = OzoneMYD09M3.get_extractor_config()

    def get_matches(self, dataset, latitude_values, longitude_values, latitude_range, longitude_range):
        matching_coords = OzoneMYD09M3.get_matching_longitudes_and_latitudes(dataset, latitude_values, longitude_values, latitude_range, longitude_range)
        # QA_FLAG skipped
        return np.argwhere(matching_coords)

    def _get_json_data(self, dataset, data_instructions, latitude_range, longitude_range):
        latitude_values, longitude_values = OzoneMYD09M3.get_latitudes_and_longitudes(dataset)
        matches_all_indexes = self.get_matches(dataset, latitude_values, longitude_values, latitude_range, longitude_range)
        date_range = OzoneMYD09M3.get_time_range(dataset)
        res = {}
        res['Start date'] = np.repeat(date_range['START'], len(matches_all_indexes))
        res['End date'] = np.repeat(date_range['END'], len(matches_all_indexes))
        res['Latitude'] = [latitude_values[index[0], index[1]] for index in matches_all_indexes]
        res['Longitude'] = [longitude_values[index[0], index[1]] for index in matches_all_indexes]
        for name, instructions in data_instructions.items():
                data = dataset.select(name)
                data_values = data.get()
                col_name = instructions['column_name_func'](data)
                col_unit = instructions['units_func'](data)
                transform_func = instructions['value_transform_func']
                if "micron_levels" in instructions:
                    for orbit_key, orbit_name in instructions['micron_levels'].items():
                        final_name = f'{col_name} ({orbit_name}, {col_unit})'
                        res[final_name] = transform_func([data_values[orbit_key, index[0], index[1]] for index in matches_all_indexes], data.attributes())
                else:
                    res[f'{col_name} ({col_unit})'] = transform_func([data_values[index[0], index[1]] for index in matches_all_indexes], data.attributes())
        return len(matches_all_indexes), res

    def _process_file(self, dataset, latitude_range, longitude_range):
        return self._get_json_data(
            dataset=dataset,
            data_instructions=self._extractor_config,
            latitude_range=latitude_range,
            longitude_range=longitude_range
        )

    @staticmethod
    def get_time_range(dataset):
        core_data = dataset.attributes()['CoreMetadata.0']
        dates = OzoneMYD09M3.DATE_REGEX.findall(core_data)
        return {
            'START': dates[0][1],
            'END': dates[1][1]
        }

    @staticmethod
    def get_matching_longitudes_and_latitudes(dataset, latitude_values, longitude_values, latitude_range, longitude_range):
        min_latitude, max_latitude = latitude_range
        min_longitude, max_longitude = longitude_range
        matches_latitude = (latitude_values > min_latitude) & (latitude_values < max_latitude)
        matches_longitude = (longitude_values > min_longitude) & (longitude_values < max_longitude)
        return matches_latitude & matches_longitude

    @staticmethod
    def get_latitudes_and_longitudes(dataset):
        attrs = dataset.attributes(full=1)
        meta = attrs["StructMetadata.0"][0]
        match_upper_left = OzoneMYD09M3.UPPER_LEFT_REGEX.search(meta)
        x0, y0 = np.float(match_upper_left.group('upper_left_x')), np.float(match_upper_left.group('upper_left_y'))
        x0, y0 = int(x0 / 1000000), int(y0 / 1000000)
        match_lower_right = OzoneMYD09M3.LOWER_RIGHT_REGEX.search(meta)
        x1, y1 = np.float(match_lower_right.group('lower_right_x')), np.float(match_lower_right.group('lower_right_y'))
        x1, y1 = int(x1 / 1000000), int(y1 / 1000000)
        x_size, y_size = int(OzoneMYD09M3.X_DIM_SIZE_REGEX.search(meta).group('x_size')), int(OzoneMYD09M3.Y_DIM_SIZE_REGEX.search(meta).group('y_size'))
        x, y = np.linspace(x0, x1, x_size), np.linspace(y0, y1, y_size)
        longitude_values, latitude_values = np.meshgrid(x, y)
        return latitude_values, longitude_values

    @staticmethod
    def get_extractor_config():
        return {
            'Total_Ozone_Mean_Mean': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: OzoneMYD09M3.scale(x, attr)
            },
            'Total_Ozone_Mean_Std': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: OzoneMYD09M3.scale(x, attr)
            },
            'Total_Ozone_Mean_Min': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: OzoneMYD09M3.scale(x, attr)
            },
            'Total_Ozone_Mean_Max': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: OzoneMYD09M3.scale(x, attr)
            },      
            'Total_Ozone_Std_Deviation_Mean': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: OzoneMYD09M3.scale(x, attr)
            },  
            'Total_Ozone_QA_Mean_Mean': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: OzoneMYD09M3.scale(x, attr)
            },   
            'Total_Ozone_QA_Mean_Std': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: OzoneMYD09M3.scale(x, attr)
            },
            'Total_Ozone_QA_Mean_Min': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: OzoneMYD09M3.scale(x, attr)
            },   
            'Total_Ozone_QA_Mean_Max': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: OzoneMYD09M3.scale(x, attr)
            },
            'Total_Ozone_QA_Std_Deviation_Mean': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: OzoneMYD09M3.scale(x, attr)
            }
        }

    @staticmethod
    def scale(values, attributes):
        arr = np.array(values)
        return np.where(arr == -9999, arr, arr * np.float(attributes['scale_factor']))