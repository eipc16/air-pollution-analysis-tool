import './styles.scss';

import { useParams } from "react-router-dom";
import OrderDetails from "../components/order-details/OrderDetails";

interface PageParams {
    orderId: string;
}

export const OrderDetailsPage = () => {
    const { orderId } = useParams<PageParams>();

    return (
        <div style={{ height: '100%' }}>
            <OrderDetails orderId={orderId} />
        </div>
    )
};