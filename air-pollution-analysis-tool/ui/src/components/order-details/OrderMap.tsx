import {OrderWithDetails} from "../../types/orders/types";
import {useFetchStationsWithCoordinates} from "../../hooks/stations";
import React from "react";
import {MapContainer, TileLayer} from "react-leaflet";
import OrderBorder from "./OrderBorder";
import StationsMarkers from "../markers/StationsMarkers";

const OrderMap = (props: { order: OrderWithDetails }) => {
    const { groundSource, groundParameter, bottomLatitude, bottomLongitude, upperLatitude, upperLongitude } = props.order;
    const { stations } = useFetchStationsWithCoordinates(groundSource, groundParameter, bottomLatitude, bottomLongitude, upperLatitude, upperLongitude);

    return (
        <React.Fragment>
            <MapContainer
                center={[
                    (bottomLatitude + upperLatitude) / 2.0,
                    (bottomLongitude + upperLongitude) / 2.0
                ]}
                zoom={8}
                className='order--details--map'
            >
                <TileLayer
                    attribution='Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>'
                    url="https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}"
                    accessToken="pk.eyJ1IjoicHByemVtZWszMTIiLCJhIjoiY2tvaG5lZ3gyMTBrazJvbGF0NW14MDNnayJ9.H6sNVN1wcgbLuWgnwJkZ0A"
                    maxZoom={18}
                    id="mapbox/streets-v11"
                    tileSize={512}
                    zoomOffset={-1}
                />
                <OrderBorder
                    bottomLatitude={bottomLatitude}
                    bottomLongitude={bottomLongitude}
                    upperLatitude={upperLatitude}
                    upperLongitude={upperLongitude}
                />
                <StationsMarkers stations={stations} />
            </MapContainer>
        </React.Fragment>
    )
};

export default OrderMap;