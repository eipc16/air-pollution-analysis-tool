import { ApolloClient, ApolloProvider, InMemoryCache, HttpLink } from '@apollo/client';

const createApolloClient = () => {
    return new ApolloClient({
        link: new HttpLink({
            uri: '/api/graphql'
        }),
        cache: new InMemoryCache()
    });
}

export const GraphQLContext: React.FC = ({ children }) => {
    const client = createApolloClient();

    return (
        <ApolloProvider client={client}>
            { children }
        </ApolloProvider>
    )
}
