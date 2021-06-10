import React, {useEffect, useState} from 'react';
import {gql} from "@apollo/client/core";
import {Pagination} from "../../types/shared/pagination";
import {GroundData} from "../../types/data/types";
import {GridColumns, XGrid} from "@material-ui/x-grid";
import {useApolloClient} from "@apollo/client";
import {useTranslation} from "react-i18next";

const GROUND_DATA_QUERY = gql`
    query FetchGroundData($orderId: String!, $page: Int!, $size: Int!) {
        groundData(orderId: $orderId, page: $page, size: $size) {
            page
            totalPages
            totalCount
            count
            content {
                id
                year
                month
                day
                location
                hour
                minute
                longitude
                latitude
                value
            }
        }
    }
`;

type FetchGroundDataQuery = {
    groundData: Pagination<GroundData>
}

const getColumnConfigurations = (transformLabelFunc: (label: string) => string): GridColumns => {
    const columns = [
        { field: 'id', hide: true },
        { field: 'year', width: 50 },
        { field: 'month', width: 50 },
        { field: 'day', width: 50 },
        { field: 'hour', width: 50 },
        { field: 'minute', width: 50 },
        { field: 'location', width: 100 },
        { field: 'longitude', width: 100 },
        { field: 'latitude', width: 100 },
        { field: 'value', width: 100 }
    ];

    return columns.map(column => ({
        ...column,
        sortable: false,
        filterable: false,
        flex: 1,
        headerName: transformLabelFunc(`groundData.${column.field}`)
    })) as GridColumns;
}

interface OrderGroundDataProps {
    orderId: string;
}

const OrderGroundData = (props: OrderGroundDataProps) => {
    const { orderId } = props;
    const client = useApolloClient();
    const { t } = useTranslation();
    const [ page, setPage ] = useState(0);
    const [ pageSize, setPageSize ] = useState(20);
    const [ isLoading, setIsLoading ] = useState(true);
    const [ error, setError ] = useState<string | undefined>();
    const [ data, setData ] = useState<Pagination<GroundData> | undefined>();
    const [ columns, setColumns ] = useState<GridColumns>([]);

    const fetchPage = () => {
        client.query<FetchGroundDataQuery>({
            query: GROUND_DATA_QUERY,
            variables: {
                orderId: orderId,
                page: page,
                size: pageSize
            }})
            .then(result => {
                setIsLoading(result.loading)
                setError(result.error?.message)
                setData(result.data.groundData)
            })
    }

    useEffect(() => {
        setColumns(getColumnConfigurations(label => t(label)));
        fetchPage();
    }, [])

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

export default OrderGroundData;