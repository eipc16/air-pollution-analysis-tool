import React, {useEffect, useState} from 'react';
import {useApolloClient} from "@apollo/client";
import {useTranslation} from "react-i18next";
import {Pagination} from "../../types/shared/pagination";
import {SatelliteData} from "../../types/data/types";
import {GridColumns, XGrid} from "@material-ui/x-grid";
import {gql} from "@apollo/client/core";

const SATELLITE_DATA_QUERY = gql`
    query FetchSatelliteData($orderId: String!, $page: Int!, $size: Int!) {
        satelliteData(orderId: $orderId, page: $page, size: $size) {
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
                value
            }
        }
    }
`;

type FetchSatelliteDataQuery = {
    satelliteData: Pagination<SatelliteData>
}

const getColumnConfigurations = (transformLabelFunc: (label: string) => string): GridColumns => {
    const columns = [
        { field: 'id', hide: true },
        { field: 'year', width: 50 },
        { field: 'month', width: 25 },
        { field: 'day', width: 50 },
        { field: 'hour', width: 50 },
        { field: 'minute', width: 50 },
        { field: 'longitude', width: 100 },
        { field: 'latitude', width: 100 },
        { field: 'value', width: 100 }
    ];

    return columns.map(column => ({
        ...column,
        sortable: false,
        filterable: false,
        flex: 1,
        headerName: transformLabelFunc(`satelliteData.${column.field}`)
    })) as GridColumns;
}

interface OrderSatelliteDataProps {
    orderId: string;
}

const OrderSatelliteData = (props: OrderSatelliteDataProps) => {
    const { orderId } = props;
    const client = useApolloClient();
    const { t } = useTranslation();
    const [ page, setPage ] = useState(0);
    const [ pageSize, setPageSize ] = useState(20);
    const [ isLoading, setIsLoading ] = useState(true);
    const [ error, setError ] = useState<string | undefined>();
    const [ data, setData ] = useState<Pagination<SatelliteData> | undefined>();
    const [ columns, setColumns ] = useState<GridColumns>([]);

    const fetchPage = () => {
        client.query<FetchSatelliteDataQuery>({
            query: SATELLITE_DATA_QUERY,
            variables: {
                orderId: orderId,
                page: page,
                size: pageSize
            }})
            .then(result => {
                setIsLoading(result.loading)
                setError(result.error?.message)
                setData(result.data.satelliteData)
            })
    }

    useEffect(() => {
        setColumns(getColumnConfigurations(label => t(label)));
        fetchPage();
    }, [getColumnConfigurations])

    useEffect(() => {
        fetchPage();
    }, [page, pageSize, orderId])

    return (
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
                    setPage(0);
                }}
                onPageChange={(pageParams) => {
                    setPage(pageParams.page);
                }}
            />
        </div>
    )
};

export default OrderSatelliteData;