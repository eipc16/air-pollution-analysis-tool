import {Pageable} from "../shared/pagination";

export interface GroundData extends Pageable {
    id: string;
    year: number;
    month: number;
    day: number;
    hour: number;
    minute: number;
    location: string;
    longitude: number;
    latitude: number;
    value: number;
}

export interface SatelliteData extends Pageable {
    id: string;
    year: number;
    month: number;
    day: number;
    hour: number;
    minute: number;
    longitude: number;
    latitude: number;
    value: number;
}

export interface TestData extends Pageable {
    id: string;
    year: number;
    month: number;
    day: number;
    hour: number;
    minute: number;
    longitude: number;
    latitude: number;
    satelliteMeasurement: number;
    trueMeasurement: number;
    predictions: number;
}

export interface CalculationPrediction extends Pageable {
    id: string;
    year: number;
    month: number;
    day: number;
    hour: number;
    minute: number;
    longitude: number;
    latitude: number;
    value: number;
    predictions: number;
}