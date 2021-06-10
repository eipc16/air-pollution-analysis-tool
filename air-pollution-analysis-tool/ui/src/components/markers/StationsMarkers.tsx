import './styles.scss';

import {GroundStation} from "../../types/order-form/stations";
import {Marker, Tooltip} from "react-leaflet";
import React from "react";
import {useTranslation} from "react-i18next";

// @ts-ignore
import MarkerClusterGroup from 'react-leaflet-markercluster';
import L from "leaflet";

const MarkerIcon = new L.Icon({
    iconUrl: '/images/marker-icon.svg',
    iconRetinaUrl: '/images/marker-icon.svg',
    iconSize: new L.Point(35, 35),
    className: 'leaflet-marker-icon'
});

const StationsMarkers = (props: { stations: GroundStation[] }) => {
    const { stations } = props;
    const { t } = useTranslation();

    return (
        <MarkerClusterGroup>
            {
                stations.map((station, index) => (
                    <Marker key={index}
                            position={[station.latitude, station.longitude]}
                            icon={MarkerIcon}
                    >
                        <Tooltip className='tooltip'>
                            <p className='tooltip--title'>{station.name}</p>
                            <div className='tooltip--details'>

                                <div className='tooltip--details--coords'>
                                    <div className='tooltip--details--coords--lat'>
                                        <div className='label'>
                                            {t('station.latitude')}:
                                        </div>
                                        <div className='value'>
                                            {station.latitude}
                                        </div>
                                    </div>
                                    <div className='tooltip--details--coords--lng'>
                                        <div className='label'>
                                            {t('station.longitude')}:
                                        </div>
                                        <div className='value'>
                                            {station.longitude}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </Tooltip>
                    </Marker>
                ))
            }
        </MarkerClusterGroup>
    )
}

export default StationsMarkers;