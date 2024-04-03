import React from 'react';
import { Layout, Menu, Button } from 'antd';

const { Header } = Layout;

/**
 * Header component of the application.
 *
 * @param {Function} onLogout - Callback function to handle logout.
 * @param {Object} userInfo - Information about the logged-in user.
 * @returns {JSX.Element} Header component JSX
 */
const AppHeader = ({ onLogout, userInfo }) => {
    /**
     * Handle logout button click event.
     */
  const handleLogout = () => {
    if (onLogout) {
      onLogout();
    }
  };

  return (
    <Header style={{ background: '#f0f2f5' }}>
      <div className="logo" />
      <Menu theme="#f0f2f5" mode="horizontal" defaultSelectedKeys={['home']} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>

        <div style={{ display: 'flex', alignItems: 'center' }}>
          <span style={{ marginRight: '16px' }}>Welcome, {userInfo.name}!</span>
        </div>
        <Menu.Item key="logout" style={{ marginLeft: 'auto' }}>
            <Button type="primary" onClick={handleLogout}>Logout</Button>
        </Menu.Item>
          
      </Menu>
    </Header>
  );
};

export default AppHeader;