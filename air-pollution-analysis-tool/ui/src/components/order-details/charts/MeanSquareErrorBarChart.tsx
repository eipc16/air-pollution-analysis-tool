import './styles.scss';

import {SimpleTestData, TestDataMap} from "../../../hooks/testdata";
import {useEffect, useState} from "react";
import {ChartData} from "chart.js";
import {ReactChart} from "chartjs-react";
import {SimpleCalculationRequest} from "../OrderCalculationPredictionsSelector";
import {useTranslation} from "react-i18next";

// ChartDataset<'bar', number[]>
const getData = (testData: SimpleTestData[]): number => {
    if ('object' === typeof testData) {
        testData = Object.values(testData);
    }

    if(testData.length === 0) {
        return 0;
    }

    let error = 0;
    for (let i = 0; i < testData.length; i++) {
        error += Math.pow((testData[i].trueMeasurement - testData[i].predictions), 2);
    }
    return error / testData.length
}

const getChartData = (calculationRequests: SimpleCalculationRequest[], testData: TestDataMap): ChartData<'bar', number[], string> => ({
    labels: calculationRequests.map(x => x.name),
    datasets: [{
        data: calculationRequests
            .filter(x => Object.keys(testData).includes(x.id))
            .map((request, i) => getData(testData[request.id])),
        backgroundColor: '#007bff80'
    }]
})

interface MeanSquareErrorBarChartProps {
    testData: TestDataMap;
    calculationRequests: SimpleCalculationRequest[];
}

const MeanSquareErrorBarChart = (props: MeanSquareErrorBarChartProps) => {
    const { testData, calculationRequests } = props;
    const [data, setChartData] = useState<ChartData<'bar', number[], string>>({ labels: [], datasets: [] });
    const { t } = useTranslation();

    useEffect(() => {
        const chartData = getChartData(calculationRequests, testData);
        setChartData(chartData);
    }, [testData, calculationRequests, getChartData]);

    return (
        <div className='chart'>
            <ReactChart
                type='bar'
                data={data}
                options={{
                    plugins: {
                        legend: {
                            display: false
                        },
                        title: {
                            display: true,
                            text: t('charts.MSE.title')
                        }
                    },
                    scales: {
                        x: {

                            title: {
                                display: true,
                                text: t('charts.MSE.x')
                            },
                            stacked: false,
                            display: true
                        },
                        y: {
                            title: {
                                display: true,
                                text: t('charts.MSE.y')
                            }
                        }
                    }
                }}
            />
        </div>
    )
}

export default MeanSquareErrorBarChart;