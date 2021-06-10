import {gql} from "@apollo/client/core";
import {useOrderFormContext} from "../../../contexts/order-form/OrderFormContext";
import {useQuery} from "@apollo/client";
import {useEffect, useState} from "react";
import {OptionType, useSelection} from "./base";
import Select, { Props as SelectProps } from 'react-select';

const FETCH_SATELLITE_PARAMETERS = gql`
    query FetchSatelliteParameters($source: String!) {
        satelliteParameters(source: $source) {
            name
        }
    }
`

interface GroundParametersQuery {
    satelliteParameters: SatelliteParameterDTO[];
}

interface SatelliteParameterDTO {
    name: string;
}

export const SatelliteParameterField = (props: SelectProps) => {
    const { state } = useOrderFormContext();
    const { satelliteSource } = state;

    const { loading, error, data } = useQuery<GroundParametersQuery>(FETCH_SATELLITE_PARAMETERS, {
        variables: {
            source: satelliteSource
        }
    });

    const [ options, setOptions ] = useState<OptionType[]>([]);
    const { transform } = useSelection();
    const { name } = props;

    useEffect(() => {
        if (data === undefined) {
            setOptions([])
        } else {
            setOptions(data.satelliteParameters.map(param => transform(param.name, name)))
        }
    }, [data, setOptions, name, transform])

    return <Select {...props} options={options} isDisabled={!satelliteSource || loading || error !== undefined} />
}