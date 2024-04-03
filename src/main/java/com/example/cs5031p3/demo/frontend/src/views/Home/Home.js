import React from 'react';
import { Layout, Typography, Row, Col, Card } from 'antd';
import AppHeader from '../../components/Header';
import AppFooter from '../../components/Footer';
import Aside from '../../components/Aside';
import Account from '../Account/Account';
import { Route, Switch } from 'react-router-dom';
import ProjectList from '../ProjectManage/ProjectList';
import RegistrationList from '../RegistrationManage/RegistrationList';

const { Content } = Layout;
const { Title, Paragraph } = Typography;

/**
 * Home component representing the main dashboard of the application.
 * Displays a welcome message and cards for accessing different features.
 * @param {Function} onLogout - Function to handle logout action.
 */
const Home = ({ onLogout }) => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo'));

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Aside />
      <Layout>
        <AppHeader onLogout={onLogout} userInfo={userInfo} />
        <Switch>
          <Route exact path="/home">
            <Content style={{ margin: '24px 16px 0' }}>
              <div style={{ padding: 24, minHeight: 360 }}>
                <Typography>
                  <Title>Welcome to the Project Management System</Title>
                  <Paragraph>
                    This is a comprehensive platform for managing projects, registrations, and user accounts.
                  </Paragraph>
                </Typography>
                <Row gutter={16} style={{ marginTop: 24 }}>
                  <Col span={8}>
                    <Card title="Projects" bordered={false}>
                      Manage and track your projects efficiently.
                    </Card>
                  </Col>
                  <Col span={8}>
                    <Card title="Registrations" bordered={false}>
                      Handle registrations seamlessly.
                    </Card>
                  </Col>
                  <Col span={8}>
                    <Card title="Account" bordered={false}>
                      Manage your personal account details.
                    </Card>
                  </Col>
                </Row>
              </div>
            </Content>
          </Route>
          <Route path="/home/account" component={Account} />
          <Route path="/home/project" component={ProjectList} />
          <Route path="/home/registration" component={RegistrationList} />
        </Switch>
        <AppFooter />
      </Layout>
    </Layout>
  );
};

export default Home;