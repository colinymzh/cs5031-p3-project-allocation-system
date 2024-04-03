import React, { useState } from 'react';
import { Layout, Menu } from 'antd';
import { HomeOutlined, UserOutlined, ProjectOutlined, FormOutlined, CommentOutlined, NotificationOutlined } from '@ant-design/icons';
import { useHistory } from 'react-router-dom';

const { Sider } = Layout;
const { SubMenu } = Menu;

/**
 * Component representing the aside menu in the application.
 */
const Aside = () => {
  const [collapsed, setCollapsed] = useState(false);
  const history = useHistory();

  /**
   * Callback function to handle collapse event of the aside menu.
   * @param {boolean} collapsed - Indicates whether the menu is collapsed or not.
   */
  const onCollapse = (collapsed) => {
    setCollapsed(collapsed);
  };

  /**
   * Callback function to handle menu item clicks.
   * @param {string} key - Key of the clicked menu item.
   */
  const handleMenuClick = (key) => {
    switch (key) {
      case '1':
        history.push('/home');
        break;
      case '2':
        history.push('/home/account');
        break;
      case '3':
        history.push('/home/project');
        break;
      case '4':
        history.push('/home/registration');
        break;
      default:
        break;
    }
  };

  return (
    <Sider collapsible collapsed={collapsed} onCollapse={onCollapse}>
      <div className="logo" />
      <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline" onClick={({ key }) => handleMenuClick(key)}>
        <Menu.Item key="1" icon={<HomeOutlined />}>
          Home
        </Menu.Item>
        <Menu.Item key="2" icon={<UserOutlined />}>
          Account Management
        </Menu.Item>
        <Menu.Item key="3" icon={<ProjectOutlined />}>
          Project Management
        </Menu.Item>
        <Menu.Item key="4" icon={<FormOutlined />}>
          Registration Management
        </Menu.Item>
      </Menu>
    </Sider>
  );
};

export default Aside;
