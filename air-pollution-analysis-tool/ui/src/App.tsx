import './App.scss';
import './bootstrap.min.css';

import React from 'react';

import {Nav, Navbar} from 'react-bootstrap';
import {useTranslation} from 'react-i18next';
import {BrowserRouter as Router, NavLink, Route, Switch} from 'react-router-dom';
import {Helmet} from "react-helmet";

import {HomePage, OrderDetailsPage, OrdersListPage} from './pages';
import {OrderFormContext} from './contexts/order-form/OrderFormContext';
import {GraphQLContext} from './contexts/graphql/GraphQLContext';

const InnerApp = () => {
    const [t, i18n] = useTranslation();

    console.log('Trans: ', i18n)

    return (
        <React.Fragment>
            <Helmet>
                <meta charSet="utf-8"/>
                <title>{t('nav.title')}</title>
            </Helmet>
            <Router>
                <header className="app--header">
                    <Navbar bg="light" expand="lg">
                        <Navbar.Brand href="#home">{t('nav.title')}</Navbar.Brand>
                        <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                        <Navbar.Collapse id="basic-navbar-nav">
                            <Nav className="mr-auto">
                                <NavLink exact className="nav--link" activeClassName="active" to="/">
                                    <Nav.Link href="/">{t('nav.pages.home')}</Nav.Link>
                                </NavLink>
                                <NavLink exact className="nav--link" activeClassName="active" to="/orders">
                                    <Nav.Link href="/orders">{t('nav.pages.orders')}</Nav.Link>
                                </NavLink>
                            </Nav>
                        </Navbar.Collapse>
                    </Navbar>
                </header>
                <main className="app--main">
                    <Switch>
                        <Route path="/" exact children={<HomePage/>}/>
                        <Route path="/orders" exact children={<OrdersListPage/>}/>
                        <Route path="/orders/:orderId" children={<OrderDetailsPage/>}/>
                    </Switch>
                </main>
            </Router>
        </React.Fragment>
    )
}

const App = () => {
    return (
        <div className="app">
            <GraphQLContext>
                <OrderFormContext>
                    <InnerApp/>
                </OrderFormContext>
            </GraphQLContext>
        </div>
    );
}

export default App;
