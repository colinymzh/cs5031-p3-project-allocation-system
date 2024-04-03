import React, { useState } from 'react';
import { Layout, Form, Input, Button, message } from 'antd';
import axios from 'axios';

const { Content } = Layout;

/**
 * Account component responsible for changing the user's password.
 * Displays a form to verify the current password and another form to change the password.
 */
const Account = () => {
  const [form] = Form.useForm();
  const [showChangePassword, setShowChangePassword] = useState(false);

  // Retrieves user information from local storage
  const userInfo = JSON.parse(localStorage.getItem('userInfo'));

  /**
   * Handles form submission to verify the current password.
   * If the password is correct, displays the form to change the password.
   * Otherwise, displays an error message.
   * @param {Object} values - Form values containing the current password.
   */
  const onFinish = async (values) => {
    try {
      const { currentPassword, newPassword } = values;

      // Verify if the current password is correct
      const verifyResponse = await axios.post('http://localhost:8080/user/verify-password', {
        id: userInfo.id,
        password: currentPassword,
      });

      if (verifyResponse.data.code === 0) {
        // If the current password is correct, show the form to change the password
        setShowChangePassword(true);
      } else {
        // If the current password is incorrect, display an error message
        message.error('The current password is wrong, please re-enter it');
      }
    } catch (error) {
      console.error('Error:', error);
      message.error('Failed to verify current password, please try again');
    }
  };

  /**
   * Handles form submission to change the password.
   * Resets the form and hides the change password form upon successful password change.
   * @param {Object} values - Form values containing the new password.
   */
  const onChangePassword = async (values) => {
    try {
      const { newPassword } = values;
      const body = {
        id: userInfo.id,
        password: newPassword,
      };

      const response = await axios.put('http://localhost:8080/user/password', body);
      console.log(response.data);
      message.success('Password reset completed');
      form.resetFields();
      setShowChangePassword(false);
    } catch (error) {
      console.error('Error:', error);
      message.error('Failed, please try again');
    }
  };

  return (
    <Content style={{ margin: '24px 16px 0' }}>
      <div style={{ padding: 24, minHeight: 360 }}>
        <h1>Change Password</h1>
        {!showChangePassword && (
          <Form form={form} onFinish={onFinish}>
            <Form.Item
              name="currentPassword"
              label="Current Password"
              rules={[{ required: true, message: 'Please enter current password' }]}
            >
              <Input.Password />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                Verify Current Password
              </Button>
            </Form.Item>
          </Form>
        )}
        {showChangePassword && (
          <Form form={form} onFinish={onChangePassword}>
            <Form.Item
              name="newPassword"
              label="New Password"
              rules={[{ required: true, message: 'Please enter a new password' }]}
            >
              <Input.Password />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                Change Password
              </Button>
            </Form.Item>
          </Form>
        )}
      </div>
    </Content>
  );
};

export default Account;