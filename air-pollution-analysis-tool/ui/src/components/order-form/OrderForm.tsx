import "./styles.scss";

import React from "react"
import { AreaSelector } from "./AreaSelector"
import { OrderDataForm } from "./OrderDataForm"



export const OrderForm = () => {
    return (
        <div className='order--form'>
            <OrderDataForm />
            <div className='order--form--map'>
                <AreaSelector />
            </div>
        </div>
    )
}