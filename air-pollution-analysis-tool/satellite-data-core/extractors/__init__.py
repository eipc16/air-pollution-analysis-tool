from datetime import datetime, timedelta
import os
import numpy as np
import xarray as xr
from pyhdf.SD import *

class BaseModisExtractor:

    def get_matches(self, dataset, latitude_range, longitude_range, qa_flag_name, min_qa_flag):
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

    def _get_json_data(self, dataset, data_instructions, latitude_range, longitude_range, qa_flag_name, min_qa_flag):
        matches_all_indexes = self.get_matches(dataset, latitude_range, longitude_range, qa_flag_name, min_qa_flag)
        res = {}
        for name, instructions in data_instructions.items():
            print(f'Start processing... {name}')
            data = dataset.select(name)
            data_values = data.get()
            col_name = instructions['column_name_func'](data)
            col_unit = instructions['units_func'](data)
            transform_func = instructions['value_transform_func']
            if "solutions" in instructions:
                for sol_key, sol_name in instructions['solutions'].items():
                    final_name = f'{col_name} ({sol_name}, {col_unit})'
                    res[final_name] = transform_func([data_values[sol_key, index[0], index[1]] for index in matches_all_indexes], data.attributes())
                    
            else:
                res[f'{col_name} ({col_unit})'] = transform_func([data_values[index[0], index[1]] for index in matches_all_indexes], data.attributes())
            
        print(f'Finished processing... {name}')
        return len(matches_all_indexes), res

    @staticmethod
    def write_to_csv(filename, separator, data):
        data_length, json_data = data
        if not os.path.isfile(filename):
            with open(filename, mode='w') as f:
                f.write(separator.join(json_data.keys()) + "\n")
        with open(filename, mode='a') as f:
            for index in range(data_length):
                f.write(separator.join([str(product[index]) for product in json_data.values()]) + "\n")

    def _process_file(self, dataset, latitude_range, longitude_range):
        raise NotImplementedError('This method should be implemented by concrete extractor')

    def process_files(self, dirname, out_file, latitude_range, longitude_range, csv_separator=";", delete_after=False):
        file_index = 1
        for file in os.listdir(dirname):
            full_path = os.path.join(dirname, file)
            if file.endswith('.hdf'):
                print(f'Processing file... {file_index} (Path: {full_path})')
                data = self._process_file(SD(full_path, SDC.READ), latitude_range, longitude_range)
                BaseModisExtractor.write_to_csv(out_file, csv_separator, data)
                print(f'Processing finished for file... {file_index} (Path: {full_path})')
        if delete_after:
            for file in os.listdir(dirname):
                full_path = os.path.join(dirname, file)
                if file.endswith('.hdf'):
                    os.remove(full_path)
        print(f'All results saved to {out_file}')

class BaseCopernicusExtractor:

    def _process_file(self, dataset, latitude_range, longitude_range):
        raise NotImplementedError('This method should be implemented by concrete extractor')

    def process_files(self, dirname, out_file, latitude_range, longitude_range, csv_separator=";", delete_after=False):
        file_index = 1

        for file in os.listdir(dirname):
            full_path = os.path.join(dirname, file)
            if os.path.isfile(full_path) and full_path.endswith(".zip"):
                new_name = full_path.replace('.zip', '.nc')
                os.rename(full_path, new_name)

        for file in os.listdir(dirname):
            full_path = os.path.join(dirname, file)
            if file.endswith('.nc'):
                print(f'Processing file... {file_index} (Path: {full_path})')
                dataset = xr.open_dataset(full_path, group='PRODUCT')
                data, labels = self._process_file(dataset, latitude_range, longitude_range)
                if len(data) == 0:
                    print(f'No matching data found in... {file_index} (Path: {full_path})')
                else:
                    BaseCopernicusExtractor.write_to_csv(out_file, csv_separator, labels, data)
                    print(f'Processing finished for file... {file_index} (Path: {full_path})')
        if delete_after:
            for file in os.listdir(dirname):
                full_path = os.path.join(dirname, file)
                if file.endswith('.nc'):
                    os.remove(full_path)
        print(f'All results saved to {out_file}')
    

    @staticmethod
    def write_to_csv(outfile, csv_separator, labels, data):
        if not os.path.isfile(outfile):
            with open(outfile, mode='w') as f:
                f.write(csv_separator.join(labels) + "\n")
        with open(outfile, mode='a') as f:
            f.write(csv_separator.join(labels) + "\n")
            data = [BaseCopernicusExtractor.map_json_to_csv(obj, labels, csv_separator) for obj in data]
            f.write("\n".join(data))
            f.write("\n")

    @staticmethod
    def map_json_to_csv(json_object, labels, csv_separator):
        return csv_separator.join([str(json_object[label]) for label in labels])