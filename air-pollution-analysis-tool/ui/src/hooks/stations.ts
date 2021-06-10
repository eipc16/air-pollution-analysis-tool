import {ApolloError, gql} from "@apollo/client/core";
import {useApolloClient} from "@apollo/client";
import {Pagination} from "../types/shared/pagination";
import {GroundStation} from "../types/order-form/stations";
import {useCallback, useEffect, useState} from "react";
import {useOrderFormContext} from "../contexts/order-form/OrderFormContext";

const STATION_PAGE_QUERY = gql`
    query FetchStations($source: String!, $parameter: String!, $bottomLatitude: Float!, $upperLatitude: Float!, $bottomLongitude: Float!, $upperLongitude: Float!, $page: Int!, $size: Int!){
        stations(source: $source, parameter: $parameter, bottomLatitude: $bottomLatitude, upperLatitude: $upperLatitude, bottomLongitude: $bottomLongitude, upperLongitude: $upperLongitude, page: $page, size: $size) {
            count
            page
            totalPages
            totalCount
            content {
                id
                name
                latitude
                longitude
                parameters
            }
        }
    }
`;

type StationsPageQuery = {
    stations: Pagination<GroundStation>
}

const DEFAULT_BATCH_SIZE = 1000;

export const useFetchStationsWithCoordinates = (groundSource?: string, groundParameter?: string,
                                                bottomLatitude?: number, bottomLongitude?: number,
                                                upperLatitude?: number, upperLongitude?: number) => {
    const client = useApolloClient();
    const [ stations, setStations ] = useState<GroundStation[]>([]);
    const [ isFinished, setIsFinished ] = useState(false);
    const [ fetchError, setFetchError ] = useState<ApolloError | undefined>();

    const requestData = useCallback((query, variables) => {
        if(!variables.parameter || !variables.source || !variables.bottomLatitude || !variables.bottomLongitude || !variables.upperLatitude || !variables.upperLongitude) {
            console.log('Insufficient state to fetch data: ', variables);
            setStations([]);
            return;
        }
        client.query<StationsPageQuery>({ query, variables })
            .then(async res => {
                const { data, error } = res;
                const { content, totalPages, page } = data.stations;

                console.log('Fetched content: ', data);

                if (content) {
                    setStations(stations => [...stations, ...content]);
                }

                if (error) {
                    setFetchError(error);
                }

                if (page + 1 < totalPages) {
                    requestData(query, { ...variables, page: page + 1 });
                } else {
                    setIsFinished(true);
                }
            }).catch(error => {throw error; });
    }, [setStations]);

    useEffect(() => {
        console.log('Started fetching data...');

        setStations([]);

        requestData(STATION_PAGE_QUERY, {
            source: groundSource,
            parameter: groundParameter,
            size: DEFAULT_BATCH_SIZE,
            page: 0,
            bottomLatitude: bottomLatitude,
            upperLatitude: upperLatitude,
            bottomLongitude: bottomLongitude,
            upperLongitude: upperLongitude
        });
    }, [requestData, groundSource, groundParameter, bottomLongitude, bottomLatitude, upperLongitude, upperLatitude, setStations])

    return {
        stations: stations,
        isFinished: isFinished,
        error: fetchError
    }
}

export const useFetchAllGroundStations = () => {
    const { state } = useOrderFormContext();

    return useFetchStationsWithCoordinates(
        state.groundSource,
        state.groundParameter,
        state.bottomLatitude,
        state.bottomLongitude,
        state.upperLatitude,
        state.upperLongitude
    )
}