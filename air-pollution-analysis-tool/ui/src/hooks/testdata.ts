import {gql} from "@apollo/client/core";
import {Pageable, Pagination} from "../types/shared/pagination";
import {useApolloClient} from "@apollo/client";
import {useCallback, useEffect, useState} from "react";

interface BaseTestData extends Pageable {
    date: string;
    satelliteMeasurement: number;
    trueMeasurement: number;
    predictions: number;
}

export interface SimpleTestData {
    date: Date;
    satelliteMeasurement: number;
    trueMeasurement: number;
    predictions: number;
}

const TEST_DATA_QUERY = gql`
    query FetchTestData($calculationRequestId: String!, $page: Int!, $size: Int!){
        testData(calculationRequestId: $calculationRequestId, page: $page, size: $size) {
            count
            page
            totalPages
            totalCount
            content {
                date
                trueMeasurement
                predictions
                satelliteMeasurement
            }
        }
    }
`;

type SimpleTestDataPageQuery = {
    testData: Pagination<BaseTestData>
}

const DEFAULT_BATCH_SIZE = 1000;

export type TestDataMap = {
    [calculationRequestId: string]: SimpleTestData[];
}

export const useFetchSimpleTestData = (onTestDataChange?: (testData: TestDataMap) => void) => {
    const client = useApolloClient();
    const [testDataMap, setTestDataMap] = useState<TestDataMap>({});
    const [calculationRequestsToFetch, setCalculationRequestsToFetch] = useState<string[]>([]);
    const [isFinished, setIsFinished] = useState(false);

    const requestData = useCallback(async (query, calculationRequestId, variables) => {
        if (!Object.keys(testDataMap).includes(calculationRequestId)) {
            setTestDataMap(existingData => ({
                ...existingData,
                [calculationRequestId]: []
            }))
        }
        const data = await client.query<SimpleTestDataPageQuery>({query, variables})
            .then(result => result.data.testData)
            .catch(error => {
                throw error;
            });

        const { page, totalPages, content } = data;

        if (content) {

            const mappedContent = content.map(data => ({
                trueMeasurement: data.trueMeasurement,
                predictions: data.predictions,
                satelliteMeasurement: data.satelliteMeasurement,
                date: new Date(data.date)
            } as SimpleTestData))

            setTestDataMap(existingData => ({
                ...existingData,
                [calculationRequestId]: {
                    ...existingData[calculationRequestId],
                    ...mappedContent
                }
            }))
        }

        if (page + 1 < totalPages) {
            await requestData(query, calculationRequestId, { ...variables, page: page + 1 })
        } else {
            setIsFinished(true);
        }

    }, [setTestDataMap])

    useEffect(() => {
        if (onTestDataChange) {
            onTestDataChange(testDataMap)
        }
    }, [testDataMap])

    useEffect(() => {
        setIsFinished(false);
        const fetch = async () => {
            for ( let requestId of calculationRequestsToFetch) {
                await requestData(TEST_DATA_QUERY, requestId, {
                    calculationRequestId: requestId,
                    size: DEFAULT_BATCH_SIZE,
                    page: 0
                })
            }

            console.log(testDataMap);
        }
        fetch();
    }, [calculationRequestsToFetch])

    return {
        fetchData: async (calculationRequestId: string[]) => {
            setCalculationRequestsToFetch(calculationRequestId);
        },
        getData: (calculationRequestIds?: string[]) => {
            if(!calculationRequestIds) {
                return testDataMap;
            }
            return Object.keys(testDataMap)
                .filter(key => calculationRequestIds.includes(key))
                .reduce((obj: TestDataMap, key) => {
                    obj[key] = testDataMap[key];
                    return obj;
                }, {});
        },
        isFinished: () => isFinished
    }
}