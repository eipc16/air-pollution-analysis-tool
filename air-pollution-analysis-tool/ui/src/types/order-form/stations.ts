import {Pageable} from "../shared/pagination";

export interface GroundStation extends Pageable {
    id: string;
    name: string;
    latitude: number;
    longitude: number;
    parameters: string[];
}