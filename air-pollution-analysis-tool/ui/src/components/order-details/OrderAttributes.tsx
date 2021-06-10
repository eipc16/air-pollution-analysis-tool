import './styles.scss';

import React from 'react';
import {OrderWithDetails} from "../../types/orders/types";
import {useTranslation} from "react-i18next";

import DoneIcon from '@material-ui/icons/Done';
import AutorenewIcon from '@material-ui/icons/Autorenew';

interface OrderAttributesProps {
    order: OrderWithDetails;
}

type ValueType = boolean | string | number | undefined;

interface OrderAttributeEntryProps<T> {
    label: string;
    value: T;
    valueTransform?: (value: T) => React.ReactNode | string;
}

const OrderAttributeEntry = <T extends ValueType>(props: OrderAttributeEntryProps<T>) => {
    const { label, value, valueTransform } = props;

    return (
        <div className='order--attribute'>
            <div className='order--attribute--label'>
                { label }
            </div>
            <div className='order--attribute--value'>
                { valueTransform ? valueTransform(value) : value }
            </div>
        </div>
    )
}

const ValueStatus = (props: { isTrue: boolean }) => {
    if (props.isTrue) {
        return <DoneIcon style={{ color: 'green', alignContent: 'center', width: 'auto' }}/>
    }
    return <AutorenewIcon style={{ color: 'orange' }} className='icon-spinner' />
}

const StringDate = (props: { dateString?: string, withHours: boolean }) => {
    const { dateString, withHours } = props;
    if(!dateString) {
        return <div />
    }
    const date = new Date(dateString);
    return (
        <div>
            {withHours ? ( date.toUTCString() ) : ( date.toDateString() )}
        </div>
    )
}

const OrderAttributes = (props: OrderAttributesProps) => {
    const {
        id,
        name,
        isGroundDataReady,
        isSatelliteDataReady,
        startDate,
        endDate,
        groundSource,
        groundParameter,
        satelliteSource,
        satelliteParameter,
        lastUpdatedDate,
        createDate
    } = props.order;
    const { t } = useTranslation();

    return (
        <React.Fragment>
            <OrderAttributeEntry
                label={t('order.id')}
                value={id}
            />
            <OrderAttributeEntry
                label={t('order.name')}
                value={name}
            />
            <OrderAttributeEntry
                label={t('order.isGroundDataReady')}
                value={isGroundDataReady}
                valueTransform={(value: boolean) => <ValueStatus isTrue={value} />}
            />
            <OrderAttributeEntry
                label={t('order.isSatelliteDataReady')}
                value={isSatelliteDataReady}
                valueTransform={(value: boolean) => <ValueStatus isTrue={value} />}
            />
            <OrderAttributeEntry
                label={t('order.startDate')}
                value={startDate}
                valueTransform={(value: string) => <StringDate dateString={value} withHours={false} />}
            />
            <OrderAttributeEntry
                label={t('order.endDate')}
                value={endDate}
                valueTransform={(value: string) => <StringDate dateString={value} withHours={false} />}
            />
            <OrderAttributeEntry
                label={t('order.groundSource')}
                value={groundSource}
                valueTransform={(value: string) => t(`values.groundSource.${value}`)}
            />
            <OrderAttributeEntry
                label={t('order.groundParameter')}
                value={groundParameter}
                valueTransform={(value: string) => t(`values.groundParameter.${value}`)}
            />
            <OrderAttributeEntry
                label={t('order.satelliteSource')}
                value={satelliteSource}
                valueTransform={(value: string) => t(`values.satelliteSource.${value}`)}
            />
            <OrderAttributeEntry
                label={t('order.satelliteParameter')}
                value={satelliteParameter}
                valueTransform={(value: string) => t(`values.satelliteParameter.${value}`)}
            />
            <OrderAttributeEntry
                label={t('order.createDate')}
                value={createDate}
                valueTransform={(value: string) => <StringDate dateString={value} withHours={true} />}
            />
            <OrderAttributeEntry
                label={t('order.lastUpdatedDate')}
                value={lastUpdatedDate}
                valueTransform={(value?: string) => <StringDate dateString={value} withHours={true} />}
            />
        </React.Fragment>
    )
};

export default OrderAttributes;