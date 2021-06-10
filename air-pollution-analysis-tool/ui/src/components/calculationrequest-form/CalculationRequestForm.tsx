import {gql} from "@apollo/client/core";
import {useMutation} from "@apollo/client";
import {useTranslation} from "react-i18next";
import {cloneElement, useState} from "react";
import Slider from '@material-ui/core/Slider';

import {ModelSelector} from "../order-form/fields";
import {Button, Modal} from "react-bootstrap";
import {OrderWithDetails} from "../../types/orders/types";
import {TextField} from "@material-ui/core";

interface FieldProps {
    fieldName: string;
    className?: string;
    children: React.ReactNode;
}

const CalcRequestField = (props: FieldProps) => {
    const {children, fieldName, className} = props;
    const [t,] = useTranslation();

    return (
        <div className='form--field'>
            <label htmlFor={fieldName} className='form--label'>
                {t(`calculationRequest.modal.body.fields.${fieldName}`)}
            </label>
            {cloneElement(children as any, {name: fieldName, className: className ? `${className} form--input` : 'form--input'})}
        </div>
    )
}

const CREATE_CALCULATION_REQUEST_QUERY = gql`
    mutation CreateCalculationRequest($calculationRequest: CalculationRequestInput!) {
        createCalculationRequest(calculationRequest: $calculationRequest) {
            id
        }
    }
`

type OrderId = {
    id: string;
}

interface CalculationRequestFormDialogProps {
    order: OrderWithDetails;
}

const CalculationRequestFormDialog = (props: CalculationRequestFormDialogProps) => {
    const { order } = props;
    const [ open, setOpen ] = useState(false);
    const { t } = useTranslation();
    const [ createCalculationRequest ] = useMutation<OrderId>(CREATE_CALCULATION_REQUEST_QUERY);
    const [ model, setModel ] = useState<string | undefined>();
    const [ distance, setDistance ] = useState<number>(10000);
    const [ name, setName ] = useState<string>("");

    const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        createCalculationRequest({
            variables: {
                calculationRequest: {
                    orderId: order.id,
                    model: model,
                    distance: distance,
                    name: name
                }
            }
        }).then((calculationRequest) => {
            setOpen(false);
        });
    }

    const handleClickOpen = () => {
        setOpen(true);
    }

    const handleClose = () => {
        setOpen(false);
    }

    return (
        <div style={{height: '100%'}}>
            <Button onClick={handleClickOpen}
                    disabled={!order.isSatelliteDataReady || !order.isGroundDataReady}
                    className='context--action--button'
                    variant='outline-primary'
            >
                { t('calculationRequest.modal.open') }
            </Button>
            <Modal
                show={open}
                onHide={handleClose}
            >
                <Modal.Header>
                    <Modal.Title>{t('calculationRequest.modal.header.title', { order: order.name })}</Modal.Title>
                </Modal.Header>
                <form onSubmit={onSubmit} className='calc-req--form--inputs'>
                    <Modal.Body>
                        <CalcRequestField fieldName="name">
                            <input
                                type="text"
                                required={true}
                                value={name}
                                onChange={(e) => {
                                    e.preventDefault();
                                    setName(e.target.value)
                                }}
                            />
                        </CalcRequestField>
                        <CalcRequestField fieldName="model">
                            <ModelSelector
                                required={true}
                                isClearable={true}
                                onChange={(e) => {
                                    if (e) {
                                        setModel(e.value);
                                    } else {
                                        setModel(undefined);
                                    }
                                }}
                            />
                        </CalcRequestField>
                        <CalcRequestField fieldName="distance" className='distance--slider'>
                            <Slider
                                disabled={!model || model === ""}
                                min={100}
                                max={20000}
                                defaultValue={10000}
                                step={200}
                                valueLabelDisplay='auto'
                                value={distance}
                                scale={(number) => number / 1000}
                                valueLabelFormat={(value, index) => `${value}km`}
                                onChangeCommitted={(e, value) => {
                                    if (typeof value === 'number') {
                                        setDistance(value);
                                    }
                                }}
                            />
                        </CalcRequestField>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button type='submit'>
                            {t('calculationRequest.modal.footer.submit')}
                        </Button>
                    </Modal.Footer>
                </form>
            </Modal>
        </div>
    )
}

export default CalculationRequestFormDialog;