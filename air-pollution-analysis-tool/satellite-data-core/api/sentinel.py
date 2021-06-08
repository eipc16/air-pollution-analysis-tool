from api import API
from api.config import load_config, get_credentials

from pathlib import Path
from sentinelsat import SentinelAPI, read_geojson, geojson_to_wkt

class Sentinel5P(API):
    CONFIG_PATH = "./config/config.json"

    def __init__(self, config_name="SentinelAPI"):
        self._config = load_config(Sentinel5P.CONFIG_PATH, config_name)
        self._defaults = self._config['default']
        self._api_link = self._config['api_link']
        user, password = get_credentials(self._config)
        self._api = SentinelAPI(user, password, self._api_link)

    def download_and_process(self, 
                download_path,
                process_func,
                area_gjson=None, 
                date_from=None, 
                date_to=None, 
                platform_name=None, 
                product_type=None):
        area_gjson = API.get_default_if_empty(area_gjson, self._defaults['area'])
        date_from = API.get_default_if_empty(date_from, self._defaults['time']['start'])
        date_to = API.get_default_if_empty(date_to, self._defaults['time']['end'])
        platform_name = API.get_default_if_empty(platform_name, self._defaults['platform_name'])
        product_type=API.get_default_if_empty(product_type, self._defaults['product_type'])
        dates_by_month = API.split_by_month(date_from, date_to)
        latitude_range, longitude_range = self.get_coordinate_ranges(area_gjson)
        for i, date in enumerate(dates_by_month, 1):
            print(f'Start processing from {date[0]} to {date[1]}. Chunk: {i}/{len(dates_by_month)}')
            self._download_internal(download_path, area_gjson, date[0], date[1], platform_name, product_type)
            process_func(download_path, latitude_range, longitude_range)

    def download(self, 
                download_path,
                area_gjson=None, 
                date_from=None, 
                date_to=None, 
                platform_name=None, 
                product_type=None):
        area_gjson = API.get_default_if_empty(area_gjson, self._defaults['area'])
        date_from = API.get_default_if_empty(date_from, self._defaults['time']['start']), 
        date_to = API.get_default_if_empty(date_to, self._defaults['time']['end'])
        platform_name = API.get_default_if_empty(platform_name, self._defaults['platform_name'])
        product_type=API.get_default_if_empty(product_type, self._defaults['product_type'])
        self._download_internal(download_path, area_gjson, date_from, date_to, platform_name, product_type)


    def _download_internal(self, 
                download_path,
                area_gjson=None, 
                date_from=None, 
                date_to=None, 
                platform_name=None, 
                product_type=None):
        products = self._api.query(
            geojson_to_wkt(area_gjson) if area_gjson is not None else None,
            date=self._parse_date(date_from, date_to),
            platformname=platform_name,
            producttype=product_type
        )
        print(date_from, date_to)
        Path(download_path).mkdir(parents=True, exist_ok=True)
        for product in products:
            try:
                self._api.download(product, directory_path=download_path)
            except:
                print(f'Could not download product with id: {product}')

    def _parse_date(self, date_from, date_to):
        print(date_from, date_to)
        return (date_from.replace('-', ''), date_to.replace('-', ''))

    def get_info(self):
        return {
            "config": self._config,
            "api_link": self._api_link
        }

    def get_coordinate_ranges(self, area):
        coords = area['features'][0]['geometry']['coordinates'][0]
        min_latitude, max_latitude = 999999999, -99999999
        min_longitude, max_longitude = 999999999, -99999999
        # [0] - longitude, [1] - latitude
        for coord_tuple in coords:
            if coord_tuple[0] >= max_longitude:
                max_longitude = coord_tuple[0]
            if coord_tuple[0] <= min_longitude:
                min_longitude = coord_tuple[0]
            
            if coord_tuple[1] >= max_latitude:
                max_latitude = coord_tuple[1]
            if coord_tuple[1] <= min_latitude:
                min_latitude = coord_tuple[1]
        return (min_latitude, max_latitude), (min_longitude, max_longitude)