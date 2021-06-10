import React, { createContext, useReducer } from 'react';
import { useContext } from 'react';
import moment from 'moment';

import {
    OrderFormActions,
    OrderFormState,
    SetCoordinatesAction,
    SetDateFromAction,
    SetDateToAction,
    SetDistanceAction,
    SetGroundParameterAction,
    SetGroundSourceAction,
    SetModelAction,
    SetNameAction,
    SetSatelliteParameterAction,
    SetSatelliteSourceAction,
} from '../../types/order-form/types';
import { mockComponent } from 'react-dom/test-utils';

const reducer = (state: OrderFormState, action: OrderFormActions): OrderFormState => {
    switch (action.type) {
        case 'SET_NAME':
            return {
                ...state,
                name: (action as SetNameAction).payload
            }
        case 'SET_GROUND_SOURCE':
            return {
                ...state,
                groundSource: (action as SetGroundSourceAction).payload
            }
        case 'SET_GROUND_PARAMETER':
            return {
                ...state,
                groundParameter: (action as SetGroundParameterAction).payload
            }
        case 'SET_SATELLITE_SOURCE':
            return {
                ...state,
                satelliteSource: (action as SetSatelliteSourceAction).payload
            }
        case 'SET_SATELLITE_PARAMETER':
            return {
                ...state,
                satelliteParameter: (action as SetSatelliteParameterAction).payload
            }
        case 'SET_MODEL':
            return {
                ...state,
                model: (action as SetModelAction).payload
            }
        case 'CLEAR_MODEL':
            return {
                ...state,
                model: undefined,
                distance: undefined
            }
        case 'SET_DISTANCE':
            return {
                ...state,
                distance: (action as SetDistanceAction).payload
            }
        case 'SET_DATE_FROM':
            return {
                ...state,
                dateFrom: (action as SetDateFromAction).payload
            }
        case 'SET_DATE_TO':
            return {
                ...state,
                dateTo: (action as SetDateToAction).payload
            }
        case 'SET_COORDINATES':
            const coordinatesBox = (action as SetCoordinatesAction).payload;
            return {
                ...state,
                bottomLatitude: Math.min(coordinatesBox.bottomLatitude, coordinatesBox.upperLatitude),
                bottomLongitude: Math.min(coordinatesBox.bottomLongitude, coordinatesBox.upperLongitude),
                upperLatitude: Math.max(coordinatesBox.bottomLatitude, coordinatesBox.upperLatitude),
                upperLongitude: Math.max(coordinatesBox.bottomLongitude, coordinatesBox.upperLongitude)
            }
        default:
            return state;
    }
}

const initialState: OrderFormState = { 
    distance: 10000,
    dateFrom: moment().subtract("months", 1).toDate(),
    dateTo: moment().toDate()
}

const OrderFormContextState = createContext<OrderFormState>(initialState);
const OrderFormContextDispatch = createContext<React.Dispatch<OrderFormActions>>((action: OrderFormActions) => {
    throw new Error('OrderFormContextDispatch not initialized!')
});

export const OrderFormContext: React.FC = ({ children }) => {
    const [state, dispatch] = useReducer(reducer, initialState);

    return (
        <OrderFormContextDispatch.Provider value={dispatch}>
            <OrderFormContextState.Provider value={state}>
                {children}
            </OrderFormContextState.Provider>
        </OrderFormContextDispatch.Provider>
    )
}

export const useOrderFormContext = () => {
    const state = useContext(OrderFormContextState);
    const dispatch = useContext(OrderFormContextDispatch);

    return { state, dispatch }
}