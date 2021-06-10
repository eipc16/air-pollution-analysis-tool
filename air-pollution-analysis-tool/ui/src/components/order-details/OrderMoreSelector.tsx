import React, {useState} from 'react';
import {OrderWithDetails} from "../../types/orders/types";
import {useTranslation} from "react-i18next";
import {LoadingIndicator} from "../loading";
import OrderGroundData from "./OrderGroundData";
import OrderSatelliteData from "./OrderSatelliteData";
import OrderTestData from "./OrderTestData";
import OrderPredictions from "./OrderPredictions";
import OrderGraphs from "./OrderGraphs";

interface OrderMoreSelectorProps {
    order?: OrderWithDetails;
    isLoading: boolean;
    calculationRequestId?: string;
}

type Pages = 'groundData' | 'satelliteData' | 'testData' | 'graphs' | 'prediction';

const OrderSelector = (props: { name: Pages, currentPage: string, onClick: (name: Pages) => void }) => {
    const { name, currentPage, onClick } = props;
    const { t } = useTranslation();

    return (
        <div
            className={name === currentPage ? 'selector active' : 'selector' }
            onClick={(e) => {
                e.preventDefault();
                onClick(name);
            }}
        >
            { t(`order.subDetails.${name}`) }
        </div>
    )
}

const OrderTabs = (props: { currentPage: Pages, order: OrderWithDetails }) => {
    const { currentPage, order } = props;

    switch (currentPage) {
        case 'groundData':
            return <OrderGroundData orderId={order.id} />
        case 'satelliteData':
            return <OrderSatelliteData orderId={order.id} />
        case 'testData':
            return <OrderTestData orderId={order.id} />
        case "prediction":
            return <OrderPredictions orderId={order.id} />
        case 'graphs':
            return <OrderGraphs order={order} />
        default:
            return <div>Error</div>
    }
}

const OrderMoreSelector = (props: OrderMoreSelectorProps) => {
    const { isLoading, order } = props;
    const [page, setPage] = useState<Pages>('groundData');

    return (
        <div style={{height: '100%', display: 'flex', flexDirection: 'column'}}>
            <div className='selector--header'>
                <OrderSelector name='groundData' currentPage={page} onClick={setPage} />
                <OrderSelector name='satelliteData' currentPage={page} onClick={setPage} />
                {
                    order && order.isGroundDataReady && order.isSatelliteDataReady && (
                        <React.Fragment>
                            <OrderSelector name='testData' currentPage={page} onClick={setPage} />
                            <OrderSelector name='graphs' currentPage={page} onClick={setPage} />
                            <OrderSelector name='prediction' currentPage={page} onClick={setPage} />
                        </React.Fragment>
                    )
                }
            </div>
            <div className='selector--content'>
                {
                    (isLoading || !order) ? (
                        <LoadingIndicator />
                    ) : (
                        <OrderTabs currentPage={page} order={order} />
                    )
                }
            </div>
        </div>
    )
};

export default OrderMoreSelector;