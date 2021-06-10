interface Pageable {
    id: string;
}

interface Pagination<T extends Pageable> {
    count: number;
    totalCount: number;
    page: number;
    totalPages: number;
    content: T[];
}

export type {
    Pageable,
    Pagination
};