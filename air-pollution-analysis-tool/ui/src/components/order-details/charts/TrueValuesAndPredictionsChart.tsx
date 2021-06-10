import './styles.scss';

import React from 'react';

import {SimpleTestData, TestDataMap} from "../../../hooks/testdata";

import { ReactChart } from "chartjs-react";

import {useEffect, useState} from "react";
import {ChartData, ChartDataset, ScatterDataPoint} from "chart.js";
import stringToRGB from "../../../utils/color";
import {SimpleCalculationRequest} from "../OrderCalculationPredictionsSelector";
import {OrderWithDetails} from "../../../types/orders/types";
import {useTranslation} from "react-i18next";

interface TrueValueAndPredictionsChartProps {
    calculationRequests: SimpleCalculationRequest[];
    testData: TestDataMap;
    order: OrderWithDetails;
}

const getDataSet = (calculationRequest: SimpleCalculationRequest, testData: SimpleTestData[]): ChartDataset<'scatter', ScatterDataPoint[]> => {
    if ('object' === typeof testData) {
        testData = Object.values(testData);
    }
    return {
        data: testData.map(value => ({
            x: value.predictions,
            y: value.trueMeasurement
        })),
        label: calculationRequest.name,
        backgroundColor: stringToRGB(calculationRequest.id)
    }
}

const getChartData = (calculationRequests: SimpleCalculationRequest[], testData: TestDataMap): ChartData<'scatter', ScatterDataPoint[], string> => ({
    labels: calculationRequests.map(x => x.name),
    datasets: calculationRequests
        .filter(request => Object.keys(testData).includes(request.id))
        .map(request => getDataSet(request, testData[request.id]))
})

const TrueValueAndPredictionsChart = (props: TrueValueAndPredictionsChartProps) => {
    const { testData, calculationRequests, order } = props;
    const [data, setChartData] = useState<ChartData<'scatter', ScatterDataPoint[], string>>({ labels: [], datasets: [] });
    const { t } = useTranslation();

    useEffect(() => {
        const chartData = getChartData(calculationRequests, testData);
        setChartData(chartData);
    }, [testData, calculationRequests, getChartData]);

    return (
        <div className='chart'>
            <ReactChart
                type='scatter'
                // ref={chartRef}
                data={data}
                options={{
                    plugins: {
                        legend: {
                            display: true
                        },
                        title: {
                            display: true,
                            text: t('charts.trueVSPrediction.title')
                        }
                    },
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: t('charts.trueVSPrediction.x', { parameter: t(`values.groundParameter.${order.groundParameter}`) })
                            }
                        },
                        y: {
                            title: {
                                display: true,
                                text: t('charts.trueVSPrediction.y', { parameter: t(`values.groundParameter.${order.groundParameter}`) })
                            }
                        }
                    }
                }}
            />
        </div>
    )
};

export default TrueValueAndPredictionsChart;