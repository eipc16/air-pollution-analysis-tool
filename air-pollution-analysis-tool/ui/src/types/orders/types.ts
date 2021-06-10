import {Pageable} from "../shared/pagination";

export interface Order extends Pageable {
    id: string;
    name: string;
    isGroundDataReady: boolean;
    isSatelliteDataReady: boolean;
    startDate: string;
    endDate: string;
    groundSource: string;
    groundParameter: string;
    satelliteSource: string;
    satelliteParameter: string;
    lastUpdatedDate?: string;
    createDate: string;
}

export interface OrderWithDetails extends Order {
    bottomLatitude: number;
    bottomLongitude: number;
    upperLatitude: number;
    upperLongitude: number;
}

export interface CalculationRequest extends Pageable {
    id: string;
    distance: number;
    name: string;
    model: string;
    orderId: string;
    createDate: string;
}