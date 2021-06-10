import './styles.scss';

import React, {useEffect, useState} from 'react';
import OrderCalculationPredictionsSelector, {SimpleCalculationRequest} from "./OrderCalculationPredictionsSelector";
import {useApolloClient} from "@apollo/client";
import {useTranslation} from "react-i18next";
import {Pagination} from "../../types/shared/pagination";
import {TestData} from "../../types/data/types";
import {GridColumns, XGrid} from "@material-ui/x-grid";
import {gql} from "@apollo/client/core";
import {Button} from "react-bootstrap";

import { Link } from "react-router-dom";

const TEST_DATA_QUERY = gql`
    query FetchTestData($calculationRequestId: String!, $page: Int!, $size: Int!) {
        testData(calculationRequestId: $calculationRequestId, page: $page, size: $size) {
            page
            totalPages
            totalCount
            count
            content {
                id
                year
                month
                day
                hour
                minute
                longitude
                latitude
                satelliteMeasurement
                trueMeasurement
                predictions
            }
        }
    }
`;

type FetchTestDataQuery = {
    testData: Pagination<TestData>
}

const getColumnConfigurations = (transformLabelFunc: (label: string) => string): GridColumns => {
    const columns = [
        {field: 'id', hide: true},
        {field: 'year', width: 50},
        {field: 'month', width: 25},
        {field: 'day', width: 50},
        {field: 'hour', width: 50},
        {field: 'minute', width: 50},
        {field: 'longitude', width: 100},
        {field: 'latitude', width: 100},
        {field: 'satelliteMeasurement', width: 100},
        {field: 'trueMeasurement', width: 100},
        {field: 'predictions', width: 100}
    ];

    return columns.map(column => ({
        ...column,
        sortable: false,
        filterable: false,
        flex: 1,
        headerName: transformLabelFunc(`testData.${column.field}`)
    })) as GridColumns;
}

interface OrderTestDataProps {
    orderId: string;
}

const OrderTestData = (props: OrderTestDataProps) => {
    const {orderId} = props;
    const [calculationRequestId, setCalculationRequestId] = useState<SimpleCalculationRequest | null>(null);
    const client = useApolloClient();
    const {t} = useTranslation();
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(20);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | undefined>();
    const [data, setData] = useState<Pagination<TestData> | undefined>();
    const [columns, setColumns] = useState<GridColumns>([]);

    const fetchPage = () => {
        if (calculationRequestId === null) {
            setData(undefined);
            return;
        }
        client.query<FetchTestDataQuery>({
            query: TEST_DATA_QUERY,
            variables: {
                calculationRequestId: calculationRequestId.id,
                page: page,
                size: pageSize
            }
        })
            .then(result => {
                setIsLoading(result.loading)
                setError(result.error?.message)
                setData(result.data.testData)
            })
    }

    useEffect(() => {
        setColumns(getColumnConfigurations(label => t(label)));
        fetchPage();
    }, [getColumnConfigurations])

    useEffect(() => {
        fetchPage();
    }, [page, pageSize, calculationRequestId])

    // useEffect(() => {
    //     setPage(0);
    // }, [calculationRequestId])

    return (
        <div className='order--grid--wrapper'>
            <div className='order--grid--header'>
                <OrderCalculationPredictionsSelector
                    className='order--grid--header--selector'
                    orderId={orderId}
                    onSelected={setCalculationRequestId}
                    selectionMode='SINGLE'
                />
                {
                    calculationRequestId && (
                        <React.Fragment>
                            <a href={`http://localhost:8080/testData/${calculationRequestId.id}`}
                                  className='context--action--link'
                                  target="_blank"
                            >
                                <Button className='context--action--button' variant='outline-primary'>
                                    {t('testData.actions.download')}
                                </Button>
                            </a>
                        </React.Fragment>
                    )
                }
            </div>
            <div className='grid'>
                <XGrid
                    loading={isLoading}
                    error={error}
                    columns={columns}
                    rows={data?.content || []}
                    rowCount={data?.totalCount || 0}
                    pagination={true}
                    paginationMode='server'
                    hideFooterPagination={false}
                    pageSize={pageSize}
                    rowsPerPageOptions={[10, 20, 50]}
                    onPageSizeChange={(pageParams) => {
                        setPageSize(pageParams.pageSize);
                    }}
                    onPageChange={(pageParams) => {
                        setPage(pageParams.page);
                    }}
                />
            </div>
        </div>
    )
};

export default OrderTestData;