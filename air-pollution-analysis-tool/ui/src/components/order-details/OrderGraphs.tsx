import './styles.scss';

import React, {useEffect, useState} from "react";
import OrderCalculationPredictionsSelector, {SimpleCalculationRequest} from "./OrderCalculationPredictionsSelector";
import {TestDataMap, useFetchSimpleTestData} from "../../hooks/testdata";
import {TrueValueAndPredictionsChart} from "./charts";
import PredictionsLineChart from "./charts/PredictionsLineChart";
import {OrderWithDetails} from "../../types/orders/types";
import GroundMeasurementsLineChart from "./charts/GroundMeasurementsLineChart";
import SatelliteMeasurementsLineChart from "./charts/SatelliteMeasurementsLineChart";
import MeanSquareErrorBarChart from "./charts/MeanSquareErrorBarChart";
import SquareErrorPerPredictionLineChart from "./charts/SquareErrorPerPredictionBarChart";
import {LoadingIndicator} from "../loading";

interface OrderGraphsProps {
    order: OrderWithDetails;
}

const OrderGraphs = React.memo((props: OrderGraphsProps) => {
    const { order } = props;
    const [ calculationRequests, setCalculationRequests ] = useState<SimpleCalculationRequest[]>([]);
    const [ testData, setTestData ] = useState<TestDataMap>({});
    const { fetchData, getData, isFinished } = useFetchSimpleTestData((dataMap) => onDateDataChange(dataMap));

    const onDateDataChange = (testDataMap: TestDataMap) => {
        const filteredData = Object.keys(testDataMap)
            .filter(key => calculationRequests.map(x => x.id).includes(key));
        let result = {} as TestDataMap;
        for (let key of filteredData) {
            result = {
                ...result,
                [key]: testDataMap[key]
            }
        }
        setTestData(result);
    }

    const handleSelectedCalculationRequestsChange = async (calculationRequestIds: SimpleCalculationRequest[]) => {
        const uniqueRequests = calculationRequestIds
            .filter((value, index) => calculationRequestIds.indexOf(value) === index);
        console.log(uniqueRequests);
        setCalculationRequests(uniqueRequests);
        const notFetchedRequests = uniqueRequests
            .filter(request => !Object.keys(testData).includes(request.id));
        await fetchData(notFetchedRequests.map(x => x.id));
    }

    useEffect(() => {
        setTestData(getData(calculationRequests.map(x => x.id)));
    }, [calculationRequests])

    return (
        <div className='order--charts--wrapper'>
            <div className='order--grid--header'>
                <OrderCalculationPredictionsSelector
                    className='order--grid--header--selector'
                    orderId={order.id}
                    onSelected={handleSelectedCalculationRequestsChange}
                    selectionMode='MULTI'
                    maxSelections={4}
                />
            </div>
            <div className='charts'>
                {
                    !isFinished() ? (
                        <LoadingIndicator />
                    ) : (
                        <React.Fragment>
                            <TrueValueAndPredictionsChart calculationRequests={calculationRequests} testData={testData} order={order}/>
                            <MeanSquareErrorBarChart calculationRequests={calculationRequests} testData={testData} />
                            <PredictionsLineChart calculationRequests={calculationRequests} testData={testData} order={order} />
                            <GroundMeasurementsLineChart calculationRequests={calculationRequests} testData={testData} order={order} />
                            <SatelliteMeasurementsLineChart calculationRequests={calculationRequests} testData={testData} order={order} />
                            <SquareErrorPerPredictionLineChart testData={testData} calculationRequests={calculationRequests} order={order} />
                        </React.Fragment>
                    )
                }
                {/*{*/}
                {/*    calculationRequests*/}
                {/*        .filter(request => Object.keys(testData).includes(request.id))*/}
                {/*        .map(request => (*/}
                {/*            <SquareErrorPerPredictionLineChart testData={testData[request.id]} calculationRequest={request} order={order} />*/}
                {/*        ))*/}
                {/*}*/}
            </div>
        </div>
    )
});

export default OrderGraphs;