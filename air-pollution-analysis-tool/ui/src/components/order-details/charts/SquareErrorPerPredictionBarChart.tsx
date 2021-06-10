import './styles.scss';

import {SimpleTestData, TestDataMap} from "../../../hooks/testdata";
import {useEffect, useState} from "react";
import {ChartData, ChartDataset, ScatterDataPoint} from "chart.js";
import {ReactChart} from "chartjs-react";
import stringToRGB from "../../../utils/color";
import {SimpleCalculationRequest} from "../OrderCalculationPredictionsSelector";
import {useTranslation} from "react-i18next";
import {OrderWithDetails} from "../../../types/orders/types";

type SimpleTestDataMap = {
    [val: number]: number[]
}

const getDataSet = (calculationRequest: SimpleCalculationRequest, testData: SimpleTestData[]):
{
    x_values: number[],
    dataset: ChartDataset<'bar', ScatterDataPoint[]>
} => {
    let groupedData = {} as SimpleTestDataMap;
    let groupDataLabels: number[] = [];

    for (let data of testData) {
        const rounded = Math.round(data.predictions);
        groupDataLabels.push(rounded);

        if(!Object.keys(groupedData).includes(rounded.toString())) {
            groupedData = {
                ...groupedData,
                [rounded]: []
            }
        }

        groupedData[rounded].push(Math.pow((data.trueMeasurement - data.predictions), 2))
    }

    groupDataLabels.sort((a, b) => a - b)

    const x_vals = Array.from({ length: groupDataLabels.length > 0 ? groupDataLabels[groupDataLabels.length - 1] + 5 : 0 }, (v, k) => k)
    const foundLabels = Object.keys(groupedData);

    return {
        x_values: Object.keys(groupedData).map(x => Number.parseInt(x)),
        dataset: {
            label: calculationRequest.name,
            data: x_vals
                .map(val => {
                    if(foundLabels.includes(val.toString())) {
                        const data = groupedData[val]
                        return {
                            x: val,
                            y: data.length > 0 ? (data.reduce((a, b) => a + b, 0)) / data.length : 0
                        }
                    } else {
                        return {
                            x: val,
                            y: 0
                        }
                    }
                }),
            backgroundColor: stringToRGB(calculationRequest.id)
        }
    }
}

const getChartData = (calculationRequests: SimpleCalculationRequest[], testData: TestDataMap):
{
    minVal: number,
    maxVal: number,
    data: ChartData<'bar', ScatterDataPoint[], number>
} => {

    const dataSets = calculationRequests
        .filter(req => Object.keys(testData).includes(req.id))
        .map(req => getDataSet(req, testData[req.id]));

    let values: number[] = [];

    dataSets
        .flatMap(x => x.x_values)
        .forEach((val: number) => {
            if(!values.includes(val)) {
                values.push(val);
            }
        })

    values = values.sort((a, b) => a - b)

    return {
        minVal: values.length > 0 ? values[0] : 0,
        maxVal: values.length > 0 ? values[values.length - 1] : 0,
        data: {
            labels: Array.from({ length: values.length > 0 ? values[values.length - 1] : 0 }, (v, k) => k),
            datasets: dataSets.map(x => x.dataset)
        }
    }
}

interface SquareErrorPerPredictionLineChartProps {
    calculationRequests: SimpleCalculationRequest[];
    testData: TestDataMap;
    order: OrderWithDetails;
}

const sortSimpleTestData = (simpleTestData: SimpleTestData[]) => {
    let data = simpleTestData;
    if ('object' === typeof data) {
        data = Object.values(data);
    }
    return data.sort((a, b) => a.predictions - b.predictions);
}

const sortTestData = (testDataMap: TestDataMap) => {
    return Object.keys(testDataMap)
        .reduce((acc, calcReqId) => ({
            ...acc,
            [calcReqId]: sortSimpleTestData(testDataMap[calcReqId])
        }), {} as TestDataMap);
}

const SquareErrorPerPredictionLineChart = (props: SquareErrorPerPredictionLineChartProps) => {
    const { calculationRequests, testData, order } = props;
    const [data, setChartData] = useState<ChartData<'bar', ScatterDataPoint[], number>>({ labels: [], datasets: [] });
    const { t } = useTranslation();

    useEffect(() => {
        const sortedData = sortTestData(testData);
        const { data } = getChartData(calculationRequests, sortedData);
        setChartData(data);
    }, [testData, calculationRequests, getChartData]);

    return (
        <div className='chart'>
            <ReactChart
                type='bar'
                data={data}
                options={{
                    plugins: {
                        legend: {
                            display: true
                        },
                        title: {
                            display: true,
                            text: t('charts.squareErrorPerPrediction.title')
                        }
                    },
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: t('charts.squareErrorPerPrediction.x', { parameter: t(`values.groundParameter.${order.groundParameter}`) })
                            },
                        },
                        y: {
                            title: {
                                display: true,
                                text: t('charts.squareErrorPerPrediction.y')
                            }
                        }
                    }
                }}
            />
        </div>
    )
}

export default SquareErrorPerPredictionLineChart;