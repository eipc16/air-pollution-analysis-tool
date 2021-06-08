from api.sentinel import Sentinel5P
from api.modis import Modis
import os

from extractors.aerosol_mod04_l2_extrator import AerosolMOD04L2Extractor



if __name__ == "__main__":
    api = Sentinel5P()
    api.download('./data/O3/Lisbon/')

    # api2 = Modis(config_name="ModisAPI-MOD04_L2")
    # api2.download('./data/PM2.5/Lisbon/MOD04_L2')

    # # api3 = Modis(config_name="ModisAPI-MOD04_3K")
    # # api3.download('./data/PM2.5/Lisbon/MOD04_3K')

    # # ex = AerosolMOD04L2Extractor()
    # # ex.process_files(
    # #     dirname=os.path.abspath('./data/PM2.5/Lisbon/MOD04_L2'),
    # #     out_file=os.path.abspath('./data/PM2.5/Lisbon/MOD04_L2/combined_test.csv'),
    # #     latitude_range=(37.792422407988575, 39.55911824217184),
    # #     longitude_range=(-9.5361328125, -7.789306640625),
    # #     delete_after=False
    # # )