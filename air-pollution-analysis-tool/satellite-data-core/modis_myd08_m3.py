from api.modis import Modis
import os

from extractors.ozone_myd08_m3 import OzoneMYD09M3

ex = OzoneMYD09M3()

def process_result(dirname, latitude_range, longitude_range):
    ex.process_files(
        dirname=dirname,
        out_file=dirname + "/result.csv",
        latitude_range=latitude_range,
        longitude_range=longitude_range,
        delete_after=True
    )


if __name__ == "__main__":
    api2 = Modis(config_name="ModisAPI-MYD08_M3")
    api2.download_and_process(
        download_path=os.path.abspath('./data/O3/Lisbon/MYD09M3'),
        process_func=process_result
    )