package com.example.cs5031p3.demo.backend.dtoTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.cs5031p3.demo.backend.bean.ResponseMessage;
import com.example.cs5031p3.demo.backend.dto.ResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the ResponseDTO class.
 */
public class ResponseDTOTest {

    /**
     * Test case for creating a success response with data.
     */
    @Test
    public void testSuccessWithData() {
        String testData = "Test Data";
        ResponseDTO<String> response = ResponseDTO.success(testData);

        assertEquals(ResponseMessage.SUCCESS.getCode(), response.getCode());
        assertEquals(testData, response.getData());
        assertEquals(null, response.getMessage());
    }

    /**
     * Test case for creating a success response with data and message.
     */
    @Test
    public void testSuccessByMsgWithDataAndMessage() {
        String testData = "Test Data";
        String testMessage = "Test Message";
        ResponseDTO<String> response = ResponseDTO.successByMsg(testData, testMessage);

        assertEquals(ResponseMessage.SUCCESS.getCode(), response.getCode());
        assertEquals(testData, response.getData());
        assertEquals(testMessage, response.getMessage());
    }

    /**
     * Test case for creating an error response with a message.
     */
    @Test
    public void testErrorByMsgWithMessage() {
        ResponseMessage testResponseMessage = ResponseMessage.ERROR;
        ResponseDTO<Object> response = ResponseDTO.errorByMsg(testResponseMessage);

        assertEquals(testResponseMessage.getCode(), response.getCode());
        assertEquals(testResponseMessage.getMessage(), response.getMessage());
        assertEquals(null, response.getData());
    }

    /**
     * Test case for setting response properties using set methods.
     */
    @Test
    void testSetMethods() {
        ResponseDTO<String> response = new ResponseDTO<>(0, "Initial Message", "Initial Data");
        response.setCode(-1);
        response.setMessage("Updated Message");
        response.setData("Updated Data");

        Assertions.assertEquals(-1, response.getCode());
        Assertions.assertEquals("Updated Message", response.getMessage());
        Assertions.assertEquals("Updated Data", response.getData());
    }
}
