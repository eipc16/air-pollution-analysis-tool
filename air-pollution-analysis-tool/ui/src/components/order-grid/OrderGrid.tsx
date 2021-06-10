import React, {useEffect, useState} from "react";

import "./styles.scss";
import { gql } from "@apollo/client/core";
import { useApolloClient } from "@apollo/client";

// import {DataGrid, GridCellParams, GridColumns} from '@material-ui/data-grid';
import { XGrid, GridColumns, GridCellParams } from '@material-ui/x-grid';
import {Pagination} from "../../types/shared/pagination";
import {Order} from "../../types/orders/types";
import {useTranslation} from "react-i18next";
import DoneIcon from '@material-ui/icons/Done';
import AutorenewIcon from '@material-ui/icons/Autorenew';
import {Button} from "react-bootstrap";
import { Link } from "react-router-dom";

const ORDER_PAGE_QUERY = gql`
    query FetchOrderPage($page: Int!, $size: Int!){
        orders(page: $page, size: $size) {
            page
            totalPages
            count
            totalCount
            content {
                id
                name
                isGroundDataReady
                isSatelliteDataReady
                startDate
                endDate
                groundSource
                groundParameter
                satelliteSource
                satelliteParameter
                lastUpdatedDate
                createDate
            }
        }
    }
`

const ValueStatus = (props: { isTrue: boolean }) => {
    if (props.isTrue) {
        return <DoneIcon style={{ color: 'green', alignContent: 'center', width: '100%' }}/>
    }
    return <AutorenewIcon style={{ color: 'orange' }} className='icon-spinner' />
}

const getColumnConfigurations = (transformLabelFunc: (label: string) => string): GridColumns => {
    const columns = [
        {
            field: 'name'
        },
        {
            field: 'createDate',
            width: 350,
            valueGetter: (params: any) => {
                const { value } = params;
                const date = new Date(value);
                return `${date.toUTCString()}`
            }
        },
        {
            field: 'lastUpdatedDate',
            width: 350,
            valueGetter: (params: any) => {
                const { value } = params;
                if (!value) {
                    return;
                }
                const date = new Date(value);
                return `${date.toUTCString()}`
            }
        },
        {
            field: 'groundSource',
            valueGetter: (params: any) => {
                const { value } = params;
                return transformLabelFunc(`values.groundSource.${value}`)
            }
        },
        {
            field: 'groundParameter',
            valueGetter: (params: any) => {
                const { value } = params;
                return transformLabelFunc(`values.groundParameter.${value}`)
            }
        },
        {
            field: 'satelliteSource',
            valueGetter: (params: any) => {
                const { value } = params;
                return transformLabelFunc(`values.satelliteSource.${value}`)
            }
        },
        {
            field: 'satelliteParameter',
            valueGetter: (params: any) => {
                const { value } = params;
                return transformLabelFunc(`values.satelliteParameter.${value}`)
            }
        },
        {
            field: 'startDate',
            width: 350,
            valueGetter: (params: any) => {
                const { value } = params;
                if (!value) {
                    return;
                }
                const date = new Date(value);
                return `${date.toDateString()}`
            }
        },
        {
            field: 'endDate',
            width: 350,
            valueGetter: (params: any) => {
                const { value } = params;
                if (!value) {
                    return;
                }
                const date = new Date(value);
                return `${date.toDateString()}`
            }
        },
        {
            field: 'isGroundDataReady',
            renderCell: (params: GridCellParams) => {
                const value = params.value as boolean;
                return <ValueStatus isTrue={value} />
            },
            align: 'center'
        },
        {
            field: 'isSatelliteDataReady',
            renderCell: (params: GridCellParams) => {
                const value = params.value as boolean;
                return <ValueStatus isTrue={value} />
            },
            align: 'center'
        }
    ]
    return columns.map(column => ({
        ...column,
        sortable: false,
        filterable: false,
        flex: 1,
        headerName: transformLabelFunc(`order.${column.field}`)
    })) as GridColumns;
}

type FetchOrderPage = {
    orders: Pagination<Order>
}

const OrderGrid = () => {
    const client = useApolloClient();
    const { t } = useTranslation();
    const [ page, setPage ] = useState(0);
    const [ pageSize, setPageSize ] = useState(20);
    const [ isLoading, setIsLoading ] = useState(true);
    const [ error, setError ] = useState<string | undefined>();
    const [ data, setData ] = useState<Pagination<Order> | undefined>();
    const [ columns, setColumns ] = useState<GridColumns>([]);
    const [ selectedOrder, setSelectedOrder ] = useState<string | undefined>();

    const fetchPage = () => {
        client.query<FetchOrderPage>({
            query: ORDER_PAGE_QUERY,
            variables: {
                page: page,
                size: pageSize
            }})
            .then(result => {
                setIsLoading(result.loading)
                setError(result.error?.message)
                setData(result.data.orders)
            })
    }

    useEffect(() => {
        setColumns(getColumnConfigurations(label => t(label).toString()));
        fetchPage();
    }, [])

    useEffect(() => {
        console.log('fetching...', page, pageSize)
        fetchPage();
    }, [page, pageSize])

    console.log(data?.content)

    return (
        <div style={{ height: '100%', width: '100%', display: 'flex', flexDirection: 'column' }}>
            <div className='grid--header'>
                <div className='grid--title'>
                    { t('tables.orders') }
                </div>
                <div className='context--actions'>
                    <div className='order--context--action--wrapper'>
                        <Button className='order--context--action--button'
                                variant='outline-primary'
                                disabled={isLoading}
                                onClick={() => fetchPage()}
                        >
                            { t('nav.actions.refresh')}
                        </Button>
                    </div>
                    {
                        selectedOrder && (
                            <React.Fragment>
                                <Link to={`/orders/${selectedOrder}`} className='order--context--action--wrapper'>
                                    <Button className='order--context--action--button' variant='outline-primary'>
                                        { t('nav.actions.orderDetails')}
                                    </Button>
                                </Link>
                                <div className='order--context--action--wrapper'>
                                    <Button className='order--context--action--button' variant='outline-danger'>
                                        { t('nav.actions.delete')}
                                    </Button>
                                </div>
                            </React.Fragment>
                        )
                    }
                </div>
            </div>
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
                // onPageChange={(pageParams) => setPage(pageParams.page) }
                onRowSelected={(selection) => {
                    const { id } = selection.data;
                    setSelectedOrder(id);
                }}
            />
        </div>
    )
};

export default OrderGrid;