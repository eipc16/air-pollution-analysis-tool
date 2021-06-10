import React, {useEffect, useState} from "react";
import {ChartData, ChartDataset, ScatterDataPoint} from "chart.js";
import {ReactChart} from "chartjs-react";
import {SimpleTestData, TestDataMap} from "../../../hooks/testdata";
import stringToRGB from "../../../utils/color";
import { enUS } from 'date-fns/locale';

import 'chartjs-adapter-date-fns';
import {OrderWithDetails} from "../../../types/orders/types";
import {SimpleCalculationRequest} from "../OrderCalculationPredictionsSelector";
import {useTranslation} from "react-i18next";

type ValuesByTime = {
    [time: number]: number[];
}

const getDataSet = (calculationRequestId: SimpleCalculationRequest, testData: SimpleTestData[]): ChartDataset<'line', ScatterDataPoint[]> => {
    if ('object' === typeof testData) {
        testData = Object.values(testData);
    }

    let values: ValuesByTime = {};

    testData.forEach((value) => {
        const time = value.date.getTime();
        if(!Object.keys(values).includes(`${time}`)) {
            values = {
                ...values,
                [`${time}`]: []
            }
        }
        values[time].push(value.predictions);
    })

    return {
        data: Object.keys(values)
            .map(value => Number.parseInt(value))
            .sort((left, right) => left - right)
            .map(value => {
                const allValues = values[value];
                return {
                    x: value,
                    y: ((allValues.reduce((a, b) => a + b, 0)) / allValues.length) || 0
                }
            }),
        // data: testData.map(value => ({
        //     x: value.date.getTime(),
        //     y: value.predictions
        // })),
        label: calculationRequestId.name,
        backgroundColor: stringToRGB(calculationRequestId.id)
    }
}

const getChartData = (calculationRequests: SimpleCalculationRequest[], testData: TestDataMap): ChartData<'line', ScatterDataPoint[], string> => ({
    labels: calculationRequests.filter(x => Object.keys(testData).includes(x.id)).map(x => x.name),
    datasets: calculationRequests.filter(x => Object.keys(testData).includes(x.id)).map(request => getDataSet(request, testData[request.id]))
})

interface PredictionsLineChartProps {
    calculationRequests: SimpleCalculationRequest[];
    testData: TestDataMap;
    order: OrderWithDetails;
}

const PredictionsLineChart = (props: PredictionsLineChartProps) => {
    const { testData, order, calculationRequests } = props;
    const [data, setChartData] = useState<ChartData<'line', ScatterDataPoint[], string>>({ labels: [], datasets: [] });
    const { t } = useTranslation();

    useEffect(() => {
        const chartData = getChartData(calculationRequests, testData);
        setChartData(chartData);

        let min = Number.MAX_VALUE;
        let max = Number.MIN_VALUE;

    }, [testData, calculationRequests, getChartData]);

    return (
        <div className='chart'>
            <ReactChart
                type='line'
                // ref={chartRef}
                data={data}
                options={{
                    plugins: {
                        legend: {
                            display: true
                        },
                        title: {
                            display: true,
                            text: t('charts.predictions.title')
                        }
                    },
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: t('charts.predictions.x')
                            },
                            type: 'time',
                            min: new Date(order.startDate).getTime(),
                            max: new Date(order.endDate).getTime(),
                            // ticks: {
                            //     source: 'auto'
                            // },
                            adapters: {
                                date: {
                                    locale: enUS
                                }
                            }
                        },
                        y: {
                            title: {
                                display: true,
                                text: t('charts.predictions.y', { parameter: t(`values.groundParameter.${order.groundParameter}`) })
                            }
                        }
                    }
                }}
            />
        </div>
    )
};

export default PredictionsLineChart;