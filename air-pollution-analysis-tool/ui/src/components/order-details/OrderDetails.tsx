import './styles.scss';

import React from 'react';
import {gql} from "@apollo/client/core";
import {useQuery} from "@apollo/client";
import {OrderWithDetails} from "../../types/orders/types";
import {LoadingIndicator} from "../loading";
import OrderAttributes from "./OrderAttributes";
import OrderMap from "./OrderMap";
import OrderMoreSelector from "./OrderMoreSelector";
import CalculationRequestFormDialog from "../calculationrequest-form/CalculationRequestForm";
import {Button} from "react-bootstrap";
import {useTranslation} from "react-i18next";

const ORDER_QUERY = gql`
    query FetchOrderDetails($orderId: ID!) {
        order(id: $orderId) {
            id
            name
            isGroundDataReady
            isSatelliteDataReady
            createDate
            lastUpdatedDate
            startDate
            endDate
            bottomLatitude
            bottomLongitude
            upperLatitude
            upperLongitude
            groundSource
            groundParameter
            satelliteSource
            satelliteParameter
        }
    }
`

type OrderQuery = {
    order: OrderWithDetails;
}

interface OrderDetailsProps {
    orderId: string;
}

const OrderDetails = (props: OrderDetailsProps) => {
    const {orderId} = props;
    const {data, loading} = useQuery<OrderQuery>(ORDER_QUERY, {variables: {orderId: orderId}})
    const { t } = useTranslation();

    return (
        <div className='order--details'>
            <div className='order--info'>
                <div className='order--actions'>
                    {
                        (loading || !data) ? (
                            <LoadingIndicator/>
                        ) : (
                            <React.Fragment>
                                <CalculationRequestFormDialog order={data.order}/>
                                <a
                                    href={`http://localhost:8080/groundMeasurements/${orderId}`}
                                    className={data.order.isGroundDataReady ? 'context--action--link' : 'context--action--link inactiveLink'}
                                    target='_blank'
                                >
                                    <Button className='context--action--button'
                                            variant='outline-primary'
                                            disabled={!data.order.isGroundDataReady}
                                    >
                                        {t('groundData.actions.download')}
                                    </Button>
                                </a>
                                <a
                                    href={`http://localhost:8080/satelliteMeasurements/${orderId}`}
                                    className={data.order.isSatelliteDataReady ? 'context--action--link' : 'context--action--link inactiveLink'}
                                    target='_blank'
                                >
                                    <Button className='context--action--button'
                                            variant='outline-primary'
                                            disabled={!data.order.isSatelliteDataReady}
                                    >
                                        {t('satelliteData.actions.download')}
                                    </Button>
                                </a>
                            </React.Fragment>
                        )
                    }
                </div>
                <div className='order--attributes'>
                    {
                        (loading || !data) ? (
                            <LoadingIndicator/>
                        ) : (
                            <OrderAttributes order={data.order}/>
                        )
                    }
                </div>
                <div className='order--map'>
                    {
                        (loading || !data) ? (
                            <LoadingIndicator/>
                        ) : (
                            <OrderMap order={data.order}/>
                        )
                    }
                </div>
            </div>
            <div className='order--more'>
                <OrderMoreSelector order={data?.order} isLoading={loading}/>
            </div>
        </div>
    )
}

export default OrderDetails;