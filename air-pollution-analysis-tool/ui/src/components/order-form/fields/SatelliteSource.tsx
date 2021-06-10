// SATELLITE SOURCE SELECTION

import {gql, useQuery} from "@apollo/client";
import {Props as SelectProps} from "react-select/src/Select";
import {useOrderFormContext} from "../../../contexts/order-form/OrderFormContext";
import {useEffect, useState} from "react";
import Select from "react-select";
import {OptionType, useSelection} from "./base";

const FETCH_SATELLITE_SOURCES = gql`
    query {
        satelliteSources {
            name
        }
    }
`

interface SatelliteSourcesQuery {
    satelliteSources: SatelliteSourceDTO[];
}

interface SatelliteSourceDTO {
    name: string;
}

export const SatelliteSourceSelectionField = (props: SelectProps) => {
    const { state, dispatch } = useOrderFormContext();
    const { loading, error, data } = useQuery<SatelliteSourcesQuery>(FETCH_SATELLITE_SOURCES);
    const [ options, setOptions ] = useState<OptionType[]>([]);
    const { transform } = useSelection();
    const { name } = props;

    useEffect(() => {
        if (data === undefined) {
            setOptions([])
        } else {
            const transformedSources = data.satelliteSources.map(source => transform(source.name, name))
            setOptions(transformedSources)
        }
    }, [data, setOptions, name, transform])

    useEffect(() => {
        if (options.length > 0 ? options[0] : null) {
            dispatch({
                type: 'SET_SATELLITE_SOURCE',
                payload: options[0].value
            })
        }
    }, [options, dispatch]);

    return <Select {...props}
                   options={options}
                   value={state.satelliteSource ? transform(state.satelliteSource, name) : undefined}
                   isDisabled={loading || error !== undefined} />
}