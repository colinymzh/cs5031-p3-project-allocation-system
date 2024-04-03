import React, { useEffect, useState } from 'react';
import { Layout, Table, Button, Modal, Form, Input, message,Alert } from 'antd';
import axios from 'axios';

const { Content } = Layout;

/**
 * ProjectList component displays a list of projects and provides functionalities based on user type.
 */
const ProjectList = () => {
    const [projects, setProjects] = useState([]);
    const [userType, setUserType] = useState(null);
    const [visible, setVisible] = useState(false);
    const [form] = Form.useForm();
    const [userId, setUserId] = useState(null);
    const [isAssigned, setIsAssigned] = useState(false);

    useEffect(() => {
      // Fetch user information from local storage
        const userInfo = localStorage.getItem('userInfo');
        if (userInfo) {
          const { typeId, id } = JSON.parse(userInfo);
          setUserType(typeId);
          setUserId(id);
        }
        fetchProjects(); // Fetch projects
        checkAssignedProject(); // Check if user is assigned a project
      }, []);

  // Function to fetch projects from the server
  const fetchProjects = async () => {
      try {
        const response = await axios.get('http://localhost:8080/project/all');
        setProjects(response.data);
      } catch (error) {
        console.error('Error fetching projects:', error);
      }
    };
  
    const columns_staff = [
      {
        title: 'Title',
        dataIndex: 'title',
        key: 'title',
      },
      {
        title: 'Description',
        dataIndex: 'description',
        key: 'description',
      },
      {
        title: 'Staff Name',
        dataIndex: 'staffName',
        key: 'staffName',
      },
      {
        title: 'Available',
        dataIndex: 'available',
        key: 'available',
        render: (available) => (available === 1 ? 'Available' : 'Unavailable'),
      },
    ];

  const columns_student = [
    {
      title: 'Select',
      dataIndex: 'select',
      key: 'select',
      render: (_, record) => (
        <input
          type="checkbox"
          checked={record.selected}
          onChange={(e) => handleSelect(record.id, e.target.checked)}
          disabled={record.available === 2}
        />
      ),
    },
    {
      title: 'Title',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: 'Description',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: 'Staff Name',
      dataIndex: 'staffName',
      key: 'staffName',
    },
    {
      title: 'Available',
      dataIndex: 'available',
      key: 'available',
      render: (available) => (available === 1 ? 'Available' : 'Unavailable'),
    },
  ];

  // Function to handle project selection by student
  const handleSelect = (id, checked) => {
    const updatedProjects = projects.map((project) =>
      project.id === id ? { ...project, selected: checked } : project
    );
    setProjects(updatedProjects);
  };

  // Function to show create project modal
  const showModal = () => {
    setVisible(true);
  };

  // Function to handle modal cancel
  const handleCancel = () => {
    setVisible(false);
    form.resetFields();
  };

  // Function to handle project creation
  const handleCreate = async () => {
    try {
      const values = await form.validateFields();
      const userInfo = localStorage.getItem('userInfo');
      if (userInfo) {
        const { id } = JSON.parse(userInfo);
        const postData = {
          title: values.title,
          description: values.description,
          staffId: id,
          available: 1,
        };
        const response = await axios.post('http://localhost:8080/project/create', postData);
        console.log(postData);
        console.log(response);
        if (response.status === 200) {
          message.success('Project created successfully');
          setVisible(false);
          form.resetFields();
          fetchProjects();
        } else {
          message.error('Failed to create project');
        }
      }
    } catch (error) {
      console.error('Error creating project:', error);
      message.error('Failed to create project');
    }
  };

// Function to handle project registration by student
  const handleRegister = async () => {
    const selectedProjects = projects.filter((project) => project.selected);
    if (selectedProjects.length === 0) {
      message.warning('Please select at least one project');
      return;
    }
  
    try {
      const requests = selectedProjects.map((project) =>
        axios.post('http://localhost:8080/registration/create', {
          projectId: project.id,
          studentId: userId,
        })
      );
  
      const responses = await Promise.all(requests.map((request) => request.catch((error) => error.response)));
  
      const successfulRegistrations = responses.filter((response) => response && response.status === 200);
      const failedRegistrations = responses.filter((response) => response && response.status !== 200);
  
      if (successfulRegistrations.length > 0) {
        message.success(`Successfully registered for ${successfulRegistrations.length} project(s)`);
      }
  
      failedRegistrations.forEach((response) => {
        const errorMessage = response.data || 'Registration failed';
        message.error(`Project ${response.config.data.projectId}: ${errorMessage}`);
      });
  
      fetchProjects();
    } catch (error) {
      console.error('Error registering projects:', error);
      message.error('Registration failed');
    }
  };

  // Function to check if the student is assigned a project
  const checkAssignedProject = async () => {
    try {
      const userInfo = localStorage.getItem('userInfo');
      if (userInfo) {
        const { id } = JSON.parse(userInfo);
        const response = await axios.get(`http://localhost:8080/registration/student/${id}/assigned`);
        setIsAssigned(response.data);
      }
    } catch (error) {
      console.error('Error checking assigned project:', error);
    }
  };

  const renderContent = () => {
    if (userType === 1) {
        return (
            <div style={{ padding: 24, minHeight: 360 }}>
              <h1>Project List</h1>
              <div style={{ marginBottom: 16 }}>
                <Button type="primary" onClick={handleRegister}>
                  Register
                </Button>
                {isAssigned && (
                  <Alert
                    message="You have been assigned a project. Please check the Registration Management page."
                    type="warning"
                    showIcon
                    style={{ marginLeft: 16 }}
                  />
                )}
              </div>
              <Table dataSource={projects} columns={columns_student} rowKey="id" />
            </div>
          );
    } else if (userType === 2) {
        return (
            <div style={{ padding: 24, minHeight: 360 }}>
              <h1>Project List</h1>
              <Button type="primary" style={{ marginBottom: 16 }} onClick={showModal}>
                Create Project
              </Button>
              <Table dataSource={projects} columns={columns_staff} rowKey="id" />
              <Modal
                title="Create Project"
                visible={visible}
                onOk={handleCreate}
                onCancel={handleCancel}
              >
                <Form form={form} layout="vertical">
                  <Form.Item
                    name="title"
                    label="Title"
                    rules={[{ required: true, message: 'Please enter the project title' }]}
                  >
                    <Input />
                  </Form.Item>
                  <Form.Item
                    name="description"
                    label="Description"
                    rules={[{ required: true, message: 'Please enter the project description' }]}
                  >
                    <Input.TextArea rows={4} />
                  </Form.Item>
                </Form>
              </Modal>
            </div>
          );
    } else {
      return null;
    }
  };

  return <Content style={{ margin: '24px 16px 0' }}>{renderContent()}</Content>;
};

export default ProjectList;