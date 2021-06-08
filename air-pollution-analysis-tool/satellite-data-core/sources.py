import os
import tempfile
from datetime import datetime
from kafka import KafkaProducer
import json

import numpy as np
from pyhdf.SD import *

from api.modis import Modis


def get_env_variable(name, default_value=None):
    if name in os.environ:
        return os.environ[name]
    return default_value


class DataSource:

    def fetch_data(self, order_id, target_topic, parameter, date_from, date_to, latitude_range, longitude_range):
        with tempfile.TemporaryDirectory() as directory:
            self._fetch_data(directory, order_id, target_topic, parameter, date_from, date_to, latitude_range,
                             longitude_range)

    def _fetch_data(self, tempdir, order_id, target_topic, parameter, date_from, date_to, latitude_range,
                    longitude_range):
        raise NotImplementedError('Extending classes should implement method')

    def _process_files(self, dirname, order_id, target_topic, parameter, latitude_range, longitude_range, current_batch,
                       total_batches):
        raise NotImplementedError('Extending classes should implement this method')


class ModisDataSource(DataSource):
    def __init__(self, api):
        self._api = api
        self._BATCH_FILE_SIZE = 100
        self._kafka_host = get_env_variable('KAFKA_HOST', '192.168.49.2')
        self._kafka_port = get_env_variable('KAFKA_PORT', '31885')
        self._kafka_producer = KafkaProducer(
            bootstrap_servers=f'{self._kafka_host}:{self._kafka_port}',
            value_serializer=lambda v: json.dumps(v).encode('utf-8'),
            key_serializer=str.encode,
            max_request_size=104857600
        )

    def _process_file(self, dataset, parameter, latitude_range, longitude_range):
        raise NotImplementedError('This method should be implemented by concrete extractor')

    def _process_files(self, dirname, order_id, target_topic, parameter, latitude_range, longitude_range, current_batch,
                       total_batches):
        file_index = 1
        batch_files = []

        for file in os.listdir(dirname):
            full_path = os.path.join(dirname, file)
            if file.endswith('.hdf'):
                print(f'Processing file... {file_index} (Path: {full_path})')
                try:
                    dataset = SD(full_path, SDC.READ)
                    batch_files.extend(self._process_file(dataset, parameter, latitude_range, longitude_range))
                except HDF4Error:
                    print(f'Couldn\'t process file: {full_path}')
                else:
                    print(f'Processing finished for file... {file_index} (Path: {full_path}). Currently matching files: {len(batch_files)}')
                os.remove(full_path)
                file_index = file_index + 1

        if len(batch_files) == 0:
            self._kafka_producer.send(
                topic=target_topic,
                key=f'SatelliteMeasurements_{order_id}_{current_batch}',
                value={
                    'orderId': order_id,
                    'measurements': [],
                    'currentBatch': current_batch,
                    'totalBatches': total_batches,
                    'currentBatchChunk': 1,
                    'currentBatchTotalChunks': 1
                }
            )
        else:
            batch_file_chunks = self._chunks(batch_files, self._BATCH_FILE_SIZE)
            number_of_batches = len(batch_file_chunks)

            for i, chunk in enumerate(batch_file_chunks, 1):
                self._kafka_producer.send(
                    topic=target_topic,
                    key=f'SatelliteMeasurements_{order_id}_{current_batch}',
                    value={
                        'orderId': order_id,
                        'measurements': batch_files,
                        'currentBatch': current_batch,
                        'totalBatches': total_batches,
                        'currentBatchChunk': i,
                        'currentBatchTotalChunks': number_of_batches
                    }
                )
        print(f'Flushing data to: {target_topic}')
        self._kafka_producer.flush()

    def _chunks(self, l, n):
        n = max(1, n)
        return [l[i:i+n] for i in range(0, len(l), n)]

    def _fetch_data(self, tempdir, order_id, target_topic, parameter, date_from, date_to, latitude_range,
                    longitude_range):
        self._api.download_and_process(
            download_path=os.path.abspath(tempdir),
            order_id=order_id,
            target_topic=target_topic,
            parameter=parameter,
            process_func=self._process_files,
            box=[
                latitude_range[0],
                longitude_range[0],
                latitude_range[1],
                longitude_range[1]
            ],
            date_from=date_from[:10],
            date_to=date_to[:10]
        )

    def _get_matches(self, dataset, latitude_range, longitude_range, qa_flag_name, min_qa_flag):
        min_latitude, max_latitude = latitude_range
        min_longitude, max_longitude = longitude_range
        longitude_values = dataset.select('Longitude').get()
        latitude_values = dataset.select('Latitude').get()
        qa_flags = dataset.select(qa_flag_name).get()
        matches_latitude = (latitude_values > min_latitude) & (latitude_values < max_latitude)
        matches_longitude = (longitude_values > min_longitude) & (longitude_values < max_longitude)
        matches_qa = qa_flags >= min_qa_flag
        matches_all = matches_latitude & matches_longitude & matches_qa
        return np.argwhere(matches_all)


class MOD043KDataSource(ModisDataSource):
    MIN_QUALITY_FLAG = 2
    QUALITY_FLAG_NAME = 'Land_Ocean_Quality_Flag'
    START_TIME = np.datetime64(datetime.strptime("1993-01-01 00:00:00.0", "%Y-%m-%d %H:%M:%S.%f"))

    def __init__(self):
        super().__init__(Modis(config_name='ModisAPI-MOD04_3K'))

    def _process_file(self, dataset, parameter, latitude_range, longitude_range):
        matches_all_indexes = super()._get_matches(dataset, latitude_range, longitude_range,
                                                   MOD043KDataSource.QUALITY_FLAG_NAME,
                                                   MOD043KDataSource.MIN_QUALITY_FLAG)

        all_times = dataset.select('Scan_Start_Time').get()
        matched_times = list(map(lambda x: MOD043KDataSource.START_TIME + np.timedelta64(int(x), 's'),
                                 [all_times[index[0], index[1]] for index in matches_all_indexes]))

        all_longitudes = dataset.select('Longitude').get()
        matched_longitudes = list(map(lambda x: x,
                                      [all_longitudes[index[0], index[1]] for index in matches_all_indexes]))

        all_latitudes = dataset.select('Latitude').get()
        matched_latitudes = list(map(lambda x: x,
                                     [all_latitudes[index[0], index[1]] for index in matches_all_indexes]))

        parameter_config = self._get_parameter_config(parameter)
        all_parameters_data = dataset.select(parameter_config['name'])
        all_parameters = all_parameters_data.get()
        value_extractor = parameter_config['extractor_func']
        value_mapper_func = parameter_config['value_transform_func']

        matched_parameters = list(map(
            lambda x: value_mapper_func(x, all_parameters_data.attributes()),
            [value_extractor(all_parameters, index[0], index[1]) for index in matches_all_indexes]
        ))

        json_objects = []
        for index in range(len(matches_all_indexes)):
            value = str(float(matched_parameters[index])) if isinstance(matched_parameters[index], np.ndarray) else matched_parameters[index]
            if str(value) != str("-9999.0"):
                json_objects.append({
                    'parameter': parameter,
                    'value': value,
                    'latitude': str(matched_latitudes[index]),
                    'longitude': str(matched_longitudes[index]),
                    'date': str(matched_times[index])
                })

        return json_objects

    def _get_parameter_config(self, parameter):
        config = self.get_parameters_config()
        if parameter not in config.keys():
            raise Exception(f'Unknown parameter: {parameter}')
        return config[parameter]

    def get_parameters_config(self):
        return {
            'Optical_Depth_Land_And_Ocean': {
                'name': 'Optical_Depth_Land_And_Ocean',
                'extractor_func': lambda dataset, x, y: dataset[x, y],
                'value_transform_func': lambda x, attr: MOD043KDataSource.scale(x, attr)
            },
            'Corrected_Optical_Depth_Land_0.47': {
                'name': 'Corrected_Optical_Depth_Land',
                'extractor_func': lambda dataset, x, y: dataset[0, x, y],
                'value_transform_func': lambda x, attr: MOD043KDataSource.scale(x, attr)
            },
            'Corrected_Optical_Depth_Land_0.55': {
                'name': 'Corrected_Optical_Depth_Land',
                'extractor_func': lambda dataset, x, y: dataset[1, x, y],
                'value_transform_func': lambda x, attr: MOD043KDataSource.scale(x, attr)
            },
            'Corrected_Optical_Depth_Land_0.66': {
                'name': 'Corrected_Optical_Depth_Land',
                'extractor_func': lambda dataset, x, y: dataset[2, x, y],
                'value_transform_func': lambda x, attr: MOD043KDataSource.scale(x, attr)
            },
            'Corrected_Optical_Depth_Land_wav2p1': {
                'name': 'Corrected_Optical_Depth_Land_wav2p1',
                'extractor_func': lambda dataset, x, y: dataset[x, y],
                'value_transform_func': lambda x, attr: MOD043KDataSource.scale(x, attr)
            }
        }

    @staticmethod
    def scale(values, attributes):
        arr = np.array(values)
        return np.where(arr == -9999, arr, arr * np.float(attributes['scale_factor']))


class MOD04L2DataSource(ModisDataSource):
    MIN_QUALITY_FLAG = 2

    def __init__(self):
        super().__init__(Modis(config_name='ModisAPI-MOD04_L2'))

    def _process_file(self, dataset, parameter, latitude_range, longitude_range):
        matches_all_indexes = self.get_matches(dataset, latitude_range, longitude_range, 'many',
                                               MOD04L2DataSource.MIN_QUALITY_FLAG)
        pass

    def get_parameters_config(self):
        return {
            'Deep_Blue_Aerosol_Optical_Depth_550_Land_Best_Estimate': {

            }
        }
