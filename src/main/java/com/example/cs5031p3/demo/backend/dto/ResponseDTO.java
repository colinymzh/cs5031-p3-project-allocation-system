package com.example.cs5031p3.demo.backend.dto;

import com.example.cs5031p3.demo.backend.bean.ResponseMessage;

/**
 * Data Transfer Object (DTO) representing a response message with optional data.
 * @param <T> The type of data to be included in the response.
 */
public class ResponseDTO<T> {
    private Integer code;
    private String message;
    private T data;

    /**
     * Get the response code.
     * @return The response code.
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Set the response code.
     * @param code The response code to set.
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * Get the response message.
     * @return The response message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the response message.
     * @param message The response message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get the data associated with the response.
     * @return The data associated with the response.
     */
    public T getData() {
        return data;
    }

    /**
     * Set the data associated with the response.
     * @param data The data to set.
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Private constructor for ResponseDTO.
     * @param code The response code.
     * @param message The response message.
     */
    private ResponseDTO(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Private constructor for ResponseDTO.
     * @param code The response code.
     * @param data The data associated with the response.
     */
    private ResponseDTO(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    /**
     * Public constructor for ResponseDTO.
     * @param code The response code.
     * @param message The response message.
     * @param data The data associated with the response.
     */
    public ResponseDTO(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Create a success response with data.
     * @param data The data to include in the response.
     * @param <T> The type of data.
     * @return A ResponseDTO representing a success response with data.
     */
    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(ResponseMessage.SUCCESS.getCode(), data);
    }

    /**
     * Create a success response with data and a custom message.
     * @param data The data to include in the response.
     * @param message The custom message to include in the response.
     * @param <T> The type of data.
     * @return A ResponseDTO representing a success response with data and a custom message.
     */
    public static <T> ResponseDTO<T> successByMsg(T data, String message) {
        return new ResponseDTO<>(ResponseMessage.SUCCESS.getCode(), message, data);
    }

    /**
     * Create an error response with a predefined message.
     * @param responseMessage The predefined response message.
     * @param <T> The type of data.
     * @return A ResponseDTO representing an error response with a predefined message.
     */
    public static <T> ResponseDTO<T> errorByMsg(ResponseMessage responseMessage) {
        return new ResponseDTO<>(responseMessage.getCode(),responseMessage.getMessage());
    }

}
