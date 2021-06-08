from extractors import BaseModisExtractor

import numpy as np
from datetime import datetime

class AerosolMOD04L2Extractor(BaseModisExtractor):
    START_TIME = np.datetime64(datetime.strptime("1993-01-01 00:00:00.0", "%Y-%m-%d %H:%M:%S.%f"))
    MIN_QA_FLAG = 0
    QUALITY_FLAGS = {
        0: 'Bad',
        1: 'Marginal',
        2: 'Good',
        3: 'Very Good'
    }
    
    def __init__(self):
        self._extractor_config = AerosolMOD04L2Extractor.get_extractor_config()

    def _process_file(self, dataset, latitude_range, longitude_range):
        return super()._get_json_data(
            dataset=dataset,
            data_instructions=self._extractor_config,
            latitude_range=latitude_range,
            longitude_range=longitude_range,
            qa_flag_name='Deep_Blue_Aerosol_Optical_Depth_550_Land_QA_Flag',
            min_qa_flag=AerosolMOD04L2Extractor.MIN_QA_FLAG
        )

    @staticmethod
    def get_extractor_config():
        return {
            'Scan_Start_Time': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: 'Time UTC+0',
                'value_transform_func': lambda xs, attr: list(map(lambda x: AerosolMOD04L2Extractor.START_TIME + np.timedelta64(int(x), 's'), xs))
            },
            'Latitude': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: x
            },
            'Longitude': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: x
            },
            'Deep_Blue_Aerosol_Optical_Depth_550_Land_Best_Estimate': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: AerosolMOD04L2Extractor.scale(x, attr)
            },
            'Deep_Blue_Aerosol_Optical_Depth_550_Land_STD': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: AerosolMOD04L2Extractor.scale(x, attr)
            },
            'Deep_Blue_Aerosol_Optical_Depth_550_Land_QA_Flag': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: AerosolMOD04L2Extractor.scale(x, attr)
            },
            'Topographic_Altitude_Land': {
                'column_name_func': lambda x: x.long_name,
                'units_func': lambda x: x.units,
                'value_transform_func': lambda x, attr: AerosolMOD04L2Extractor.scale(x, attr)
            }
        }

    @staticmethod
    def scale(values, attributes):
        arr = np.array(values)
        return np.where(arr == -9999, arr, arr * np.float(attributes['scale_factor']))