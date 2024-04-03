package com.example.cs5031p3.demo.backend.beanTest;

import com.example.cs5031p3.demo.backend.bean.ResponseMessage;
import com.example.cs5031p3.demo.backend.dto.ResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ResponseMessage}.
 */
public class ResponseMessageTest {

    /**
     * Test case for success response without custom message.
     */
    @Test
    public void testSuccessResponse() {
        String data = "TestData";
        ResponseDTO<String> response = ResponseDTO.success(data);

        Assertions.assertEquals(ResponseMessage.SUCCESS.getCode(), response.getCode());
        Assertions.assertEquals(data, response.getData());
    }

    /**
     * Test case for success response with custom message.
     */
    @Test
    public void testSuccessResponseWithMessage() {
        String data = "TestData";
        String message = "Custom success message";
        ResponseDTO<String> response = ResponseDTO.successByMsg(data, message);

        Assertions.assertEquals(ResponseMessage.SUCCESS.getCode(), response.getCode());
        Assertions.assertEquals(message, response.getMessage());
        Assertions.assertEquals(data, response.getData());
    }

    /**
     * Test case for error response.
     */
    @Test
    public void testErrorResponse() {
        ResponseDTO<Object> response = ResponseDTO.errorByMsg(ResponseMessage.LOGIN_FAILED);

        Assertions.assertEquals(ResponseMessage.LOGIN_FAILED.getCode(), response.getCode());
        Assertions.assertEquals(ResponseMessage.LOGIN_FAILED.getMessage(), response.getMessage());
        Assertions.assertNull(response.getData());
    }
}
