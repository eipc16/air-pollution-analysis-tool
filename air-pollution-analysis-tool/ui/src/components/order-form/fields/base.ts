import {useTranslation} from "react-i18next";
import {useCallback} from "react";

export type OptionType = {
    label: string,
    value: string
}

export const useSelection = () => {
    const [t, ] = useTranslation();

    const transformInner = useCallback((value: string, fieldName?: string): OptionType => ({
        label: t(`values.${fieldName || 'field'}.${value}`),
        value: value
    }), [t])

    return {
        transform: useCallback((value: string, fieldName?: string) => {
            return transformInner(value, fieldName);
        }, [transformInner])
    }
}