import {gql} from "@apollo/client/core";
import {useOrderFormContext} from "../../../contexts/order-form/OrderFormContext";
import {useQuery} from "@apollo/client";
import {useEffect, useState} from "react";
import {OptionType, useSelection} from "./base";
import Select, { Props as SelectProps } from 'react-select';

const FETCH_GROUND_PARAMETERS = gql`
    query FetchGroundParameters($source: String!) {
        groundParameters(source: $source) {
            name
        }
    }
`

interface GroundParametersQuery {
    groundParameters: GroundParameterDTO[];
}

interface GroundParameterDTO {
    name: string;
}

export const GroundSourceParameterField = (props: SelectProps) => {
    const { state } = useOrderFormContext();
    const { groundSource } = state;

    const { loading, error, data } = useQuery<GroundParametersQuery>(FETCH_GROUND_PARAMETERS, {
        variables: {
            source: groundSource
        }
    });

    const [ options, setOptions ] = useState<OptionType[]>([]);
    const { transform } = useSelection();
    const { name } = props;

    useEffect(() => {
        if (data === undefined) {
            setOptions([])
        } else {
            setOptions(data.groundParameters.map(param => transform(param.name, name)))
        }
    }, [data, setOptions, name, transform])

    return <Select {...props} options={options} isDisabled={!groundSource || loading || error !== undefined} />
}