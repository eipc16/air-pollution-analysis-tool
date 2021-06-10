import React, {useEffect, useState} from "react";
import {gql} from "@apollo/client/core";
import {useQuery} from "@apollo/client";
import Select, {OptionsType} from "react-select";
import {useTranslation} from "react-i18next";

export interface SimpleCalculationRequest {
    id: string;
    name: string;
    createDate: string;
    model: string;
    distance: number;
}

const CALCULATION_REQUEST_QUERY = gql`
    query FetchCalculationRequest($orderId: String!) {
        calculationRequests(orderId: $orderId) {
            id
            name
            createDate
            model
            distance
        }
    }
`;

type CalculationRequestQuery = {
    calculationRequests: SimpleCalculationRequest[];
}

interface OrderCalculationPredictionsSingleSelectorProps {
    orderId: string;
    className: string;
    selectionMode: 'SINGLE'
    onSelected: (calculationRequestId: SimpleCalculationRequest | null) => void;
}

interface OrderCalculationPredictionsMultiSelectorProps {
    orderId: string;
    className: string;
    selectionMode: 'MULTI'
    onSelected: (calculationRequestId: SimpleCalculationRequest[]) => void;
    maxSelections: number;
}

type OrderCalculationPredictionsSelectorType = OrderCalculationPredictionsSingleSelectorProps | OrderCalculationPredictionsMultiSelectorProps;

export type OptionType = {
    label: string,
    value: SimpleCalculationRequest
}

const OrderCalculationPredictionsSelector = (props: OrderCalculationPredictionsSelectorType) => {
    const { orderId, onSelected, selectionMode, className } = props;
    const { t } = useTranslation();
    const [ selectedValue, setSelectedValue ] = useState<OptionsType<OptionType>>([]);
    const [ options, setOptions ] = useState<OptionType[]>([]);
    const { loading, data } = useQuery<CalculationRequestQuery>(CALCULATION_REQUEST_QUERY, {
        variables: {
            orderId: orderId
        }
    });

    useEffect(() => {
        if (!data) {
            setOptions([]);
        } else {
            setOptions(data.calculationRequests.map(request => {
                const date = new Date(request.createDate).toUTCString();
                const model = t(`values.model.${request.model}`);
                const distance = `${request.distance / 1000} km`
                return {
                    value: request,
                    label: `${request.name} (${model}, ${distance}, ${date})`
                }
            }));
        }
    }, [data, setOptions, t])

    const handleOnSingleSelect = (selectedOption: OptionType | null) => {
        if (selectionMode === 'SINGLE') {
            // @ts-ignore
            onSelected(selectedOption === null ? null : selectedOption.value);
        }
        setSelectedValue(selectedOption === null ? [] : [selectedOption]);
    }

    const handleOnMultiSelect = (selectedOptions: OptionsType<OptionType>) => {
        if(selectedOptions.length === 0) {
            return;
        }
        // @ts-ignore
        if (selectionMode === 'MULTI' && selectedOptions.length <= props.maxSelections) {
            // @ts-ignore
            onSelected(selectedOptions.map(option => option.value))
            setSelectedValue(selectedOptions);
        }
    }

    useEffect(() => {
        console.log(selectedValue);
        if(options.length === 1 || (selectedValue.length === 0 && options.length > 1)) {
            if (selectionMode === 'MULTI') {
                handleOnMultiSelect([options[0]]);
            } else {
                handleOnSingleSelect(options[0]);
            }
        }
    }, [options]);

    return (
        selectionMode === 'MULTI' ? (
            <Select
                className={className}
                options={options}
                value={selectedValue}
                isMulti={true}
                onChange={(value) => handleOnMultiSelect(value)}
                isDisabled={loading || options.length === 1}
            />
        ) : (
            <Select
                className={className}
                options={options}
                value={selectedValue}
                onChange={(value) => handleOnSingleSelect(value)}
                isDisabled={loading || options.length === 1}
            />
        )
    );
};

export default OrderCalculationPredictionsSelector;