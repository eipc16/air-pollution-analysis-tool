import { Action } from "../contexts/types";

export interface OrderRequest {
    name: string;
    groundSource: string;
    groundParameter: string;
    satelliteParameter: string;
    satelliteSource: string;
    model?: string;
    distance?: number;
    dateFrom: string;
    dateTo: string;
    bottomLatitude: number;
    bottomLongitude: number;
    upperLatitude: number;
    upperLongitude: number;
}

export interface OrderFormState {
    name?: string;
    groundSource?: string;
    groundParameter?: string;
    satelliteSource?: string;
    satelliteParameter?: string;
    model?: string;
    distance?: number;
    dateFrom: Date;
    dateTo: Date;
    bottomLatitude?: number;
    bottomLongitude?: number;
    upperLatitude?: number;
    upperLongitude?: number;
}

export interface CoordinatesBox {
    bottomLatitude: number;
    bottomLongitude: number;
    upperLatitude: number;
    upperLongitude: number;
}

export interface SetNameAction extends Action<string> {
    type: 'SET_NAME';
}

export interface SetGroundSourceAction extends Action<string> {
    type: 'SET_GROUND_SOURCE';
}

export interface SetGroundParameterAction extends Action<string> {
    type: 'SET_GROUND_PARAMETER';
}

export interface SetSatelliteSourceAction extends Action<string> {
    type: 'SET_SATELLITE_SOURCE';
}

export interface SetSatelliteParameterAction extends Action<string> {
    type: 'SET_SATELLITE_PARAMETER';
}

export interface SetModelAction extends Action<string> {
    type: 'SET_MODEL';
}

export interface SetDistanceAction extends Action<number> {
    type: 'SET_DISTANCE'
}

export interface SetDateFromAction extends Action<Date> {
    type: 'SET_DATE_FROM';
}

export interface SetDateToAction extends Action<Date> {
    type: 'SET_DATE_TO';
}

export interface SetCoordinatesAction extends Action<CoordinatesBox> {
    type: 'SET_COORDINATES';
}

export interface ClearModel extends Action<undefined> {
    type: 'CLEAR_MODEL';
    payload: undefined;
}

export type OrderFormActions =
    SetNameAction
    | SetGroundSourceAction | SetGroundParameterAction
    | SetSatelliteSourceAction | SetSatelliteParameterAction
    | SetModelAction | SetDistanceAction
    | SetDateFromAction | SetDateToAction
    | SetCoordinatesAction | ClearModel;