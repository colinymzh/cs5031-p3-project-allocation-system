import React, { useEffect } from 'react';
import axios from 'axios';
import { Form, Input, Button, Select, Row, Col, Typography, message } from 'antd';
import { useHistory } from 'react-router-dom';
import backgroundImage from '../../assets/bg.jpg';

const { Option } = Select;
const { Title } = Typography;

/**
 * Login component for user authentication.
 * @param {Function} onLogin - Function to handle successful login.
 */
function Login({ onLogin }) {
    const [form] = Form.useForm();
    const history = useHistory();
  
    useEffect(() => {
      // Check if user information is stored in local storage
      const userInfo = localStorage.getItem('userInfo');
      if (userInfo) {
        onLogin(); // If user information exists, user is logged in, call onLogin function
        history.push('/home'); // Redirect to the home page
      }
    }, []);
  
    const handleSubmit = async (values) => {
      console.log('Request:', values);
      try {
        const response = await axios.post('http://localhost:8080/user/login', {
          username: values.username,
          password: values.password,
          typeId: values.typeId,
        });
        console.log(response);
        if (response.data.code === 0) {
          message.success('Login Succeed');
          onLogin();
          localStorage.setItem('userInfo', JSON.stringify(response.data.data)); // 将用户信息存储到本地存储中
          history.push('/home');
        } else {
          message.error('Login Failed');
        }
      } catch (error) {
        message.error('Request Error');
        console.error('Request Error', error);
      }
    };
  
    return (
        <Row justify="center" align="middle" style={{ minHeight: '100vh', backgroundImage: `url(${backgroundImage})`, backgroundSize: 'cover' }}>
          <Col>
            <div style={{ backgroundColor: 'white', padding: '20px', borderRadius: '8px' }}>
              <Form form={form} onFinish={handleSubmit}>
                <Title level={2} style={{ textAlign: 'center' }}>
                  Login
                </Title>
                <Form.Item
                  name="username"
                  rules={[{ required: true, message: 'Please Enter Your Username' }]}
                >
                  <Input placeholder="Username" />
                </Form.Item>
                <Form.Item
                  name="password"
                  rules={[{ required: true, message: 'Please Enter Your Password' }]}
                >
                  <Input.Password placeholder="Password" />
                </Form.Item>
                <Form.Item
                  name="typeId"
                  initialValue="1"
                  rules={[{ required: true, message: 'Please Choose Your Identity' }]}
                >
                  <Select placeholder="Identity">
                    <Option value="1">Student</Option>
                    <Option value="2">Staff</Option>
                  </Select>
                </Form.Item>
                <Form.Item>
                  <Button type="primary" htmlType="submit" block>
                    Login
                  </Button>
                </Form.Item>
              </Form>
            </div>
          </Col>
        </Row>
      );
  }

export default Login;