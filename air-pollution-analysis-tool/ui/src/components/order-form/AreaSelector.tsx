import "leaflet/dist/leaflet.css";
import "./styles.scss";

import "leaflet-area-select";
import React, { useEffect, useState } from "react";
import { MapContainer, Marker, TileLayer, useMap, Tooltip } from "react-leaflet";
import { useGeoLocation } from 'use-geo-location';
import L, { LocationEvent, LeafletEvent, Rectangle, LatLngBounds } from "leaflet";
import { LoadingIndicator } from '../loading/LoadingIndicator';
import { useOrderFormContext } from "../../contexts/order-form/OrderFormContext";
import { CoordinatesBox } from "../../types/order-form/types";
import {useFetchAllGroundStations} from "../../hooks/stations";
// @ts-ignore
import MarkerClusterGroup from 'react-leaflet-markercluster';
import StationsMarkers from "../markers/StationsMarkers";


const AreaSelectUtillity = () => {
    const map = useMap();
    const { state, dispatch } = useOrderFormContext();
    const { bottomLatitude, bottomLongitude, upperLatitude, upperLongitude } = state;
    const [currentLayer, setCurrentLayer] = useState<Rectangle | undefined>();
    // const [selectedBounds, setSelectedBounds] = useState<L.LatLngBounds | undefined>();

    useEffect(() => {

        map.setMinZoom(3);

        map.setMaxBounds([
            [-90, -180],
            [90, 180]
        ]);

        const restrictedAreaRectangleProperties = {
            weight: 0,
            className: "restricted-area"
        }

        const rightRectangle = L.rectangle(
            new LatLngBounds({ lat: -90, lng: 180 }, { lat: 90, lng: 520 }),
            restrictedAreaRectangleProperties
        );

        const leftRectangle = L.rectangle(
            new LatLngBounds({ lat: -90, lng: -520 }, { lat: 90, lng: -180 }),
            restrictedAreaRectangleProperties
        );

        leftRectangle.addTo(map);
        rightRectangle.addTo(map);

        if (!(map as any).selectArea) return;
        (map as any).selectArea.enable();

        map.on("areaselected", (event: LeafletEvent) => {
            console.log('area selected')
            if ((event as LocationEvent).bounds) {
                const locationEvent = (event as LocationEvent);
                const selectedBounds = locationEvent.bounds;

                const bottomLatitude = selectedBounds.getSouthWest().lat;
                const upperLatitude = selectedBounds.getNorthEast().lat;
                const bottomLongitude = selectedBounds.getSouthWest().lng;
                const upperLongitude = selectedBounds.getNorthEast().lng;

                if(bottomLatitude >= -90 && upperLatitude <= 90
                    && bottomLongitude >= -180 && upperLongitude <= 180) {
                    dispatch({
                        type: 'SET_COORDINATES',
                        payload: {
                            bottomLatitude: bottomLatitude,
                            bottomLongitude: bottomLongitude,
                            upperLatitude: upperLatitude,
                            upperLongitude: upperLongitude
                        } as CoordinatesBox
                    });
                }
            }
        })
    }, [map]);

    useEffect(() => {
        if (!bottomLatitude || !bottomLongitude || !upperLatitude || !upperLongitude) return;
        const layerBounds = new LatLngBounds({ lat: bottomLatitude, lng: bottomLongitude}, { lat: upperLatitude, lng: upperLongitude})

        const layer = L.rectangle(layerBounds,{
            color: "gray",
            weight: 0.5
        });

        if (currentLayer) {
            currentLayer.removeFrom(map);
        }
        layer.addTo(map);
        setCurrentLayer(layer);
    // We do not want currentLayer here
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [bottomLatitude, bottomLongitude, upperLatitude, upperLongitude, map]);

    return null;
};

const AreaSelectorInner = (props: { latitude: number, longitude: number }) => {
    const { latitude, longitude } = props;
    const { stations } = useFetchAllGroundStations();

    return (
        <MapContainer center={[latitude, longitude]} zoom={4} className='area--selector--map'>
            <TileLayer
                attribution='Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>'
                url="https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}"
                accessToken="pk.eyJ1IjoicHByemVtZWszMTIiLCJhIjoiY2tvaG5lZ3gyMTBrazJvbGF0NW14MDNnayJ9.H6sNVN1wcgbLuWgnwJkZ0A"
                maxZoom={18}
                id="mapbox/streets-v11"
                tileSize={512}
                zoomOffset={-1}
            />
            <AreaSelectUtillity />
            <StationsMarkers stations={stations} />
        </MapContainer>
    );
}

export const AreaSelector = () => {
    const { loading, error, latitude, longitude } = useGeoLocation();


    if (loading) {
        return (
            <div className='area--selector'>
                <LoadingIndicator />
            </div>
        )
    }

    return (
        <div className='area--selector'>
            {
                !loading && error ? (
                    <AreaSelectorInner latitude={0} longitude={0} />
                ) : (
                    <AreaSelectorInner latitude={latitude || 0} longitude={longitude || 0} />
                )
            }
        </div>
    )
}

