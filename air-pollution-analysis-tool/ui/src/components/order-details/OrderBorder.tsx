import {useEffect} from 'react';
import {useMap} from "react-leaflet";
import L, {LatLngBounds} from "leaflet";

interface OrderBorderProps {
    bottomLatitude?: number;
    bottomLongitude?: number;
    upperLatitude?: number;
    upperLongitude?: number;
}

const OrderBorder = (props: OrderBorderProps) => {
    const { bottomLatitude, bottomLongitude, upperLatitude, upperLongitude } = props;
    const map = useMap();

    map.setMaxBounds([
        [-90, -180],
        [90, 180]
    ]);

    useEffect(() => {
        if (!bottomLatitude || !bottomLongitude || !upperLatitude || !upperLongitude) return;
        const layerBounds = new LatLngBounds({ lat: bottomLatitude, lng: bottomLongitude}, { lat: upperLatitude, lng: upperLongitude})

        const layer = L.rectangle(layerBounds,{
            color: "gray",
            weight: 0.45
        });

        layer.addTo(map);
        // We do not want currentLayer here
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [bottomLatitude, bottomLongitude, upperLatitude, upperLongitude, map]);

    return null;
}

export default OrderBorder;