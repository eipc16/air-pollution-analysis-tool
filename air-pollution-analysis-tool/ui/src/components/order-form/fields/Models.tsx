import {gql, useQuery} from "@apollo/client";
import {Props as SelectProps} from "react-select/src/Select";
import {useEffect, useState} from "react";
import Select from "react-select";
import {OptionType, useSelection} from "./base";

const FETCH_MODELS = gql`
    query FetchModels {
        models {
            name
        }
    }
`

interface ModelQuery {
    models: ModelDTO[];
}

interface ModelDTO {
    name: string;
}

export const ModelSelectionField = (props: SelectProps) => {
    const { loading, error, data } = useQuery<ModelQuery>(FETCH_MODELS);
    const [models, setModels] = useState<OptionType[]>([]);
    const { name } = props;
    const { transform } = useSelection();

    useEffect(() => {
        if (data === undefined) {
            setModels([])
        } else {
            const transformedModels = data.models.map(model => transform(model.name, name))
            setModels(transformedModels)
        }
    }, [data, setModels, name])

    return <Select {...props} options={models} isDisabled={loading || error !== undefined} />
}