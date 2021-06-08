from api import API
from api.config import load_config, get_api_key

from pathlib import Path
import numpy as np
import requests
import urllib.parse as urlparse
import modapsclient as m

class Modis(API):
    CONFIG_PATH = "./config/config.json"
    TILES_FILE = "./config/modis_tiles.csv"

    def __init__(self, config_name="ModisAPI"):
        self._config = load_config(Modis.CONFIG_PATH, config_name)
        self._defaults = self._config['defaults']
        self._modis_tiles = self._load_modis_tiles()
        self._api_key = get_api_key(self._config)
        self._m = m.ModapsClient()
    
    def _load_modis_tiles(self):
        with open(Modis.TILES_FILE) as tiles_file:
            return np.genfromtxt(tiles_file, delimiter=";")
        return np.array([])

    def download_and_process(self, 
                            download_path,
                            process_func,
                            parameter,
                            target_topic,
                            order_id,
                            box=None, 
                            date_from=None, 
                            date_to=None, 
                            product_type=None,
                            collection=None):
        box = API.get_default_if_empty(box, self._get_default_box())
        date_from = API.get_default_if_empty(date_from, self._defaults['time']['start'])
        date_to = API.get_default_if_empty(date_to, self._defaults['time']['end'])
        product_name = API.get_default_if_empty(product_type, self._defaults['product_type'])
        collection = API.get_default_if_empty(collection, self._defaults['collection'])
        dates_by_month = API.split_by_month(date_from, date_to)
        print(date_from, date_to)
        for i, date in enumerate(dates_by_month, 1):
            print(f'Start processing from {date[0]} to {date[1]}. Chunk: {i}/{len(dates_by_month)}')
            # start, end = API.get_begin_and_end_of_day(date[0], date[1])
            start, end = date[0], date[1]
            north, south, east, west = self._download_internal(download_path, 
                    box=box, date_from=start, date_to=end, product_type=product_name, collection=collection)
            process_func(download_path, order_id, target_topic, parameter, (south, north), (west, east), current_batch=i, total_batches=len(dates_by_month))

    def download(self, 
                    download_path,
                    box=None, 
                    date_from=None, 
                    date_to=None, 
                    product_type=None,
                    collection=None):
        box = API.get_default_if_empty(box, self._get_default_box())
        date_from = API.get_default_if_empty(date_from, self._defaults['time']['start'])
        date_to = API.get_default_if_empty(date_to, self._defaults['time']['end'])
        product_name = API.get_default_if_empty(product_type, self._defaults['product_type'])
        collection = API.get_default_if_empty(collection, self._defaults['collection'])
        return self._download_internal(download_path, box, date_from, date_to, product_name, collection)


    def _download_internal(self, 
                            download_path,
                            box, 
                            date_from, 
                            date_to, 
                            product_type,
                            collection):
        north = box[0] if box[0] > box[2] else box[2]
        south = box[2] if box[2] < box[0] else box[0]
        west = box[1] if box[1] < box[3] else box[3]
        east = box[3] if box[3] > box[1] else box[1]
        print(f"N: {north}, S: {south}, W: {west}, E: {east}")
        file_ids = self._m.searchForFiles(
                products=product_type,
                startTime=date_from,
                endTime=date_to,
                north = box[0] if box[0] > box[2] else box[2],
                south = box[2] if box[2] < box[0] else box[0],
                west = box[1] if box[1] < box[3] else box[3],
                east = box[3] if box[3] > box[1] else box[1],
                coordsOrTiles='coords',
                collection=collection
        )
        print(f'Found: {len(file_ids)} files')
        print(f'URLS: {file_ids}')
        if len(file_ids) < 1 or (len(file_ids) == 1 and file_ids[0] == "No results"):
            raise Exception('No results found')
        Path(download_path).mkdir(parents=True, exist_ok=True)
        file_urls = self._m.getFileUrls(",".join(file_ids))
        filename_by_url = dict([(url, self._get_full_path(download_path, url)) for url in file_urls])
        for url, dest in filename_by_url.items():
            response = requests.get(url, headers={
                'Authorization': f'Bearer {self._api_key}'
            })
            with open(dest, 'wb') as f:
                f.write(response.content)
        return north, south, east, west

    def _get_full_path(self, download_path, url):
        split = urlparse.urlsplit(url)
        file_name = split.path.split("/")[-1]
        return f'{download_path}/{file_name}'.replace("//", "/")

    def get_info(self):
        return {
            "config": self._config,
            'api_key': self._api_key
        }

    def _get_default_box(self):
        box = self._defaults['box']
        return (
            box['bottom_left']['latitude'],
            box['bottom_left']['longitude'],
            box['top_right']['latitude'],
            box['top_right']['longitude']
        )
