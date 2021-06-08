import numpy as np
import json
from datetime import datetime, timedelta

class NumpyEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return json.JSONEncoder.default(self, obj)

class API:
    IMPL_MESSAGE = "API is an abstract class. This method should be implemented in class extending this class"
    def get_info(self):
        raise NotImplementedError(API.IMPL_MESSAGE)

    @staticmethod
    def get_default_if_empty(value, default_value):
        return default_value if value is None else value

    def print_info(self):
        print(API.beautify(self.get_info()))

    @staticmethod
    def beautify(data):
        return json.dumps(data, indent=4, cls=NumpyEncoder)
    
    # borrowed from https://stackoverflow.com/a/13565185
    @staticmethod
    def _last_day_of_month(any_day):
        next_month = any_day.replace(day=28) + timedelta(days=4)  # this will never fail
        return next_month - timedelta(days=next_month.day)

    @staticmethod
    def split_by_month(begin, end):
        begin = datetime.strptime(begin, "%Y-%m-%d")
        end = datetime.strptime(end, "%Y-%m-%d")

        result = []
        while True:
            if begin.month == 12:
                next_month = begin.replace(year=begin.year+1,month=1, day=1)
            else:
                next_month = begin.replace(month=begin.month+1, day=1)
            if next_month > end:
                break
            result.append ([begin.strftime("%Y-%m-%d"),API._last_day_of_month(begin).strftime("%Y-%m-%d")])
            begin = next_month
        result.append ([begin.strftime("%Y-%m-%d"),end.strftime("%Y-%m-%d")])
        return result

    @staticmethod
    def _parse_date(date):
        return datetime.strptime(date, '%Y-%m-%d')

    @staticmethod
    def get_begin_and_end_of_day(date_from, date_to):
        start = API._parse_date(date_from)
        end = API._parse_date(date_to)
        start_of_first_day = datetime(
            year=start.year,
            month=start.month,
            day=start.day,
            hour=0,
            minute=0,
            second=0
        )
        end_of_last_day = datetime(
            year=end.year,
            month=end.month,
            day=end.day,
            hour=23,
            minute=59,
            second=59
        )
        return start_of_first_day, end_of_last_day