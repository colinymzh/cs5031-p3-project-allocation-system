import React, { useEffect, useState } from 'react';
import { Layout, Table, Button, Modal, message, Select } from 'antd';
import axios from 'axios';

const { Option } = Select;

const { Content } = Layout;

/**
 * Component for displaying and managing registrations.
 */
const RegistrationList = () => {
  const [registrations, setRegistrations] = useState([]);
  const [userType, setUserType] = useState(null);
  const [selectedRegistrationIds, setSelectedRegistrationIds] = useState([]);

  // States and functions for managing project modal
  const [isProjectModalVisible, setIsProjectModalVisible] = useState(false);
  const [projects, setProjects] = useState([]);
  const [selectedProjectId, setSelectedProjectId] = useState(null);

  // Effect hook to fetch user type and registrations
  useEffect(() => {
  
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      const { typeId } = JSON.parse(userInfo);
      setUserType(typeId);
    }
  }, []);

  // Effect hook to fetch registrations
  useEffect(() => {
    fetchRegistrations();
  }, []);

  // Function to fetch registrations based on user type
  const fetchRegistrations = async () => {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      const { id, typeId } = JSON.parse(userInfo);
      if (typeId === 1) {
        try {
          const response = await axios.get(`http://localhost:8080/registration/student/${id}`);
          setRegistrations(response.data);
        } catch (error) {
          console.error('Error fetching registrations:', error);
        }
      } else if (typeId === 2) {
        try {
          const response = await axios.get(`http://localhost:8080/registration/students-registration/${id}`);
          setRegistrations(response.data);
        } catch (error) {
          console.error('Error fetching interested students:', error);
        }
      } else {
        return null;
      }
    }
  };

  // Columns configuration for the registration table
  const columns = [
 
    {
      title: 'Student Name',
      dataIndex: 'studentName',
      key: 'studentName',
    },
    {
      title: 'Project Title',
      dataIndex: 'projectTitle',
      key: 'projectTitle',
    },
    {
        title: 'Staff Name',
        dataIndex: 'staffName',
        key: 'staffName',
      },
    {
      title: 'Registration State',
      dataIndex: 'registrationState',
      key: 'registrationState',
      render: (state) => (state === 1 ? 'Interested' : 'Assigned'),
    },
  ];

  // Function to handle assignment of registrations
  const handleAssign = () => {
    if (selectedRegistrationIds.length > 0) {
      Modal.confirm({
        title: 'Assign Confirmation',
        content: 'Are you sure you want to assign the selected student to this project?',
        onOk: async () => {
          try {
            const response = await axios.put(`http://localhost:8080/registration/assign/${selectedRegistrationIds[0]}`);
            message.success(response.data);
            fetchRegistrations();
          } catch (error) {
            message.error('Failed to assign registration');
            console.error('Error assigning registration:', error);
          }
        },
      });
    } else {
      message.warning('Please select a registration to assign');
    }
  };

  // Function to show project modal
  const showProjectModal = () => {
    setIsProjectModalVisible(true);
    fetchProjects();
  };

  // Function to handle project modal cancel
  const handleProjectModalCancel = () => {
    setIsProjectModalVisible(false);
    setSelectedProjectId(null);
  };

  // Function to handle project change
  const handleProjectChange = (projectId) => {
    setSelectedProjectId(projectId);
  };

  // Function to mark project as unavailable
  const handleMakeUnavailable = async () => {
    if (selectedProjectId) {
        console.log(selectedProjectId)
      try {
        await axios.put(`http://localhost:8080/project/make-unavailable/${selectedProjectId}`);
        message.success('Project marked as unavailable');
        fetchProjects();
        fetchRegistrations();
        setIsProjectModalVisible(false);
        setSelectedProjectId(null);
      } catch (error) {
        message.error('Failed to make project unavailable');
        console.error('Error making project unavailable:', error);
      }
    } else {
      message.warning('Please select a project');
    }
  };

  // Function to fetch projects
  const fetchProjects = async () => {
    try {
      const userInfo = localStorage.getItem('userInfo');
      if (userInfo) {
        const { id } = JSON.parse(userInfo);
        const response = await axios.get(`http://localhost:8080/project/staff/${id}`);
        setProjects(response.data);
      }
    } catch (error) {
      console.error('Error fetching projects:', error);
    }
  };

const renderContent = () => {
    if (userType === 1) {
        return (
            <Content style={{ margin: '24px 16px 0' }}>
              <div style={{ padding: 24, minHeight: 360 }}>
                <h1>Registration List</h1>
                <Table dataSource={registrations} columns={columns} rowKey="registrationId" />
              </div>
            </Content>
          );
    } else if (userType === 2) {
        return (
          <Content style={{ margin: '24px 16px 0' }}>
            <div style={{ padding: 24, minHeight: 360 }}>
              <h1>Registration List</h1>
              {userType === 2 && (
                <div style={{ marginBottom: 16 }}>
                <Button type="primary" onClick={handleAssign}>
                  Assign
                </Button>
              
                <Button 
                  type="primary" 
                  onClick={showProjectModal}
                  style={{ marginLeft: 16 }}
                >
                  Manage Your Projects
                </Button>
              </div>
              )}
              <Table
                dataSource={registrations}
                columns={columns}
                rowKey="registrationId"
                rowSelection={userType === 2 ? {
                  type: 'radio',
                  selectedRowKeys: selectedRegistrationIds,
                  onChange: (selectedRowKeys) => setSelectedRegistrationIds(selectedRowKeys),
                } : null}
              />
            </div>
            <Modal
            title="Make Project Unavailable"
            visible={isProjectModalVisible}
            onCancel={handleProjectModalCancel}
            footer={[
              <Button key="cancel" onClick={handleProjectModalCancel}>
                Cancel
              </Button>,
              <Button key="confirm" type="primary" onClick={handleMakeUnavailable}>
                Confirm
              </Button>,
            ]}
          >
            <Select
              style={{ width: '100%' }}
              placeholder="Select a project"
              onChange={handleProjectChange}
              value={selectedProjectId}
            >
              {projects.map((project) => (
                <Option key={project.id} value={project.id}>
                  {project.title}
                </Option>
              ))}
            </Select>
          </Modal>
        </Content>
      );
    }  else {
      return null;
    }
  };

  return <Content style={{ margin: '24px 16px 0' }}>{renderContent()}</Content>;

};

export default RegistrationList;