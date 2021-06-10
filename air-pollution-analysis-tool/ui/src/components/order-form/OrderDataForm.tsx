import './styles.scss';

import {cloneElement} from "react";

import {useOrderFormContext} from '../../contexts/order-form/OrderFormContext';
import {OrderFormState} from '../../types/order-form/types';
import DatePicker from 'react-date-picker';
import Slider from '@material-ui/core/Slider';
import {useTranslation} from 'react-i18next';
import {gql} from "@apollo/client/core";
import {useMutation} from "@apollo/client";
import {Order} from "../../types/orders/types";
import {
    GroundParameterSelector,
    GroundSourceSelector, ModelSelector,
    SatelliteParameterSelector,
    SatelliteSourceSelector
} from "./fields";

const LAT_LONG_PATTERN = "^[-+]?([1-8]?\d(\.\d+)?|90(\.0+)?),\s*[-+]?(180(\.0+)?|((1[0-7]\d)|([1-9]?\d))(\.\d+)?)$";

interface FieldProps {
    fieldName: string;
    className?: string;
    children: React.ReactNode;
}

const OrderField = (props: FieldProps) => {
    const {children, fieldName, className} = props;
    const [t,] = useTranslation();

    return (
        <div className='form--field'>
            <label htmlFor={fieldName} className='form--label'>
                {t(`order.${fieldName}`)}
            </label>
            {cloneElement(children as any, {name: fieldName, className: className ? `${className} form--input` : 'form--input'})}
        </div>
    )
}

const CREATE_ORDER_QUERY = gql`
     mutation CreateOrder($orderRequest: OrderRequest!){
       createOrder(orderRequest: $orderRequest) {
           id
           name
           isGroundDataReady
           isSatelliteDataReady
           startDate
           endDate
           groundSource
           groundParameter
           satelliteSource
           satelliteParameter
           lastUpdatedDate
           createDate
       }
     }
`

export const OrderDataForm = () => {
    const [ createOrder, { data }] = useMutation<Order>(CREATE_ORDER_QUERY);
    const {state, dispatch} = useOrderFormContext();
    const [t,] = useTranslation();

    const submitFieldLabel = t('order.submit')

    const {
        name,
        dateFrom,
        dateTo,
        model,
        bottomLatitude,
        bottomLongitude,
        upperLatitude,
        upperLongitude
    } = state;

    const isValid = (order: OrderFormState) => {
        return true;
    }

    const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (isValid(state)) {
            createOrder({
                variables: {
                    orderRequest: {
                        ...state,
                        dateFrom: state.dateFrom.toISOString(),
                        dateTo: state.dateTo.toISOString()
                    }
                }
            }).then(result => {
                console.log('Created order: ', result.data);
            })
        }
    }

    const onChangeCoordinates = (e: React.ChangeEvent<HTMLInputElement>, value?: any) => {
        e.preventDefault();
        dispatch({
            type: 'SET_COORDINATES',
            payload: value ? value : (e.target.value as any)
        })
    }

    return (
        <form onSubmit={onSubmit} className='order--form--inputs'>
            <div className='order--form--fields'>
                <OrderField fieldName="name">
                    <input
                        type="text"
                        required={true}
                        value={name}
                        onChange={(e) => {
                            e.preventDefault();
                            dispatch({
                                type: 'SET_NAME',
                                payload: e.target.value
                            });
                        }}
                    />
                </OrderField>
                <OrderField fieldName="groundSource">
                    <GroundSourceSelector
                        required={true}
                        onChange={(e) => {
                            if (e) {
                                dispatch({type: 'SET_GROUND_SOURCE', payload: e.value})
                            }
                        }}
                    />
                </OrderField>
                <OrderField fieldName="groundParameter">
                    <GroundParameterSelector
                        required={true}
                        onChange={(e) => {
                            if (e) {
                                dispatch({type: 'SET_GROUND_PARAMETER', payload: e.value})
                            }
                        }}
                    />
                </OrderField>
                <OrderField fieldName="satelliteSource">
                    <SatelliteSourceSelector
                        required={true}
                        onChange={(e) => {
                            if (e) {
                                dispatch({type: 'SET_SATELLITE_SOURCE', payload: e.value})
                            }
                        }}
                    />
                </OrderField>
                <OrderField fieldName="satelliteParameter">
                    <SatelliteParameterSelector
                        required={true}
                        onChange={(e) => {
                            if (e) {
                                dispatch({type: 'SET_SATELLITE_PARAMETER', payload: e.value})
                            }
                        }}
                    />
                </OrderField>
                <OrderField fieldName="model">
                    <ModelSelector
                        required={false}
                        isClearable={true}
                        onChange={(e) => {
                            if (e) {
                                dispatch({type: 'SET_MODEL', payload: e.value})
                            } else {
                                dispatch({ type: 'CLEAR_MODEL', payload: undefined })
                            }
                        }}
                    />
                </OrderField>
                <OrderField fieldName="distance" className='distance--slider'>
                    <Slider
                        disabled={!model || model === ""}
                        min={100}
                        max={20000}
                        defaultValue={10000}
                        step={200}
                        valueLabelDisplay='auto'
                        scale={(number) => number / 1000}
                        valueLabelFormat={(value, index) => `${value}km`}
                        onChangeCommitted={(e, value) => {
                            if (typeof value === 'number') {
                                dispatch({
                                    type: 'SET_DISTANCE',
                                    payload: value
                                })
                            }
                            console.log(state);
                        }}
                    />
                </OrderField>
                <OrderField fieldName="dateFrom">
                    <DatePicker
                        value={dateFrom}
                        required={true}
                        clearIcon={null}
                        format="dd/MM/yyyy"
                        onChange={(e) => {
                            dispatch({type: 'SET_DATE_FROM', payload: (e as Date)})
                        }}
                    />
                </OrderField>
                <OrderField fieldName="dateTo">
                    <DatePicker
                        value={dateTo}
                        required={true}
                        clearIcon={null}
                        maxDate={new Date()}
                        format="dd/MM/yyyy"
                        onChange={(e) => {
                            dispatch({type: 'SET_DATE_TO', payload: (e as Date)})
                        }}
                    />
                </OrderField>
                <OrderField fieldName="bottomLatitude">
                    <input
                        // type="hidden" 
                        value={bottomLatitude}
                        required={true}
                        type="number"
                        step="0.000000000000001"
                        pattern={LAT_LONG_PATTERN}
                        onChange={(e) => onChangeCoordinates(e, {
                            upperLongitude,
                            bottomLongitude,
                            upperLatitude,
                            bottomLatitude: e.target.value
                        })}
                    />
                </OrderField>
                <OrderField fieldName="bottomLongitude">
                    <input
                        // type="hidden" 
                        value={bottomLongitude}
                        required={true}
                        type="number"
                        step="0.000000000000001"
                        pattern={LAT_LONG_PATTERN}
                        onChange={(e) => onChangeCoordinates(e, {
                            bottomLatitude,
                            upperLongitude,
                            upperLatitude,
                            bottomLongitude: e.target.value
                        })}
                    />
                </OrderField>
                <OrderField fieldName="upperLatitude">
                    <input
                        // type="hidden" 
                        value={upperLatitude}
                        required={true}
                        type="number"
                        step="0.000000000000001"
                        pattern={LAT_LONG_PATTERN}
                        onChange={(e) => onChangeCoordinates(e, {
                            bottomLatitude,
                            bottomLongitude,
                            upperLongitude,
                            upperLatitude: e.target.value
                        })}
                    />
                </OrderField>
                <OrderField fieldName="upperLongitude">
                    <input
                        // type="hidden" 
                        value={upperLongitude}
                        required={true}
                        type="number"
                        step="0.000000000000001"
                        pattern={LAT_LONG_PATTERN}
                        onChange={(e) => onChangeCoordinates(e, {
                            bottomLatitude,
                            bottomLongitude,
                            upperLatitude,
                            upperLongitude: e.target.value
                        })}
                    />
                </OrderField>
            </div>
            <input type="submit" className='submit--btn' value={submitFieldLabel}/>
        </form>
    )
}