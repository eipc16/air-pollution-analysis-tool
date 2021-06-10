import {gql, useQuery} from "@apollo/client";
import {Props as SelectProps} from "react-select/src/Select";
import {useOrderFormContext} from "../../../contexts/order-form/OrderFormContext";
import {useEffect, useState} from "react";
import Select from "react-select";
import {OptionType, useSelection} from "./base";

const FETCH_GROUND_SOURCES = gql`
    query {
        groundSources {
            name
        }
    }
`

interface GroundSourceQuery {
    groundSources: GroundSourceDTO[];
}

interface GroundSourceDTO {
    name: string;
}

export const GroundSourceSelectionField = (props: SelectProps) => {
    const { state, dispatch } = useOrderFormContext();
    const { loading, error, data } = useQuery<GroundSourceQuery>(FETCH_GROUND_SOURCES);
    const [ options, setOptions ] = useState<OptionType[]>([]);
    const { transform } = useSelection();
    const { name } = props;

    useEffect(() => {
        if (data === undefined) {
            setOptions([])
        } else {
            const transformedSources = data.groundSources.map(source => transform(source.name, name))
            setOptions(transformedSources)
        }
    }, [data, setOptions, name, transform])

    useEffect(() => {
        if (options.length > 0 ? options[0] : null) {
            dispatch({
                type: 'SET_GROUND_SOURCE',
                payload: options[0].value
            })
        }
    }, [options, dispatch]);

    return <Select {...props}
                   options={options}
                   value={state.groundSource ? transform(state.groundSource, name) : undefined}
                   isDisabled={loading || error !== undefined} />
}