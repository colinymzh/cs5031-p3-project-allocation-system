package com.example.cs5031p3.demo.backend.bean;


/**
 * ResponseMessage class represents a response message object with a code and message.
 */
public class ResponseMessage {
    private Integer code;

    private String message;

    /**
     * Constructor to create a ResponseMessage object with a given code and message.
     *
     * @param code    The code associated with the response message
     * @param message The message of the response
     */
    private ResponseMessage(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    /**
     * Get the code associated with the response message.
     *
     * @return The code of the response message
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Get the message of the response.
     *
     * @return The message of the response
     */
    public String getMessage() {
        return message;
    }

    // Static ResponseMessage objects representing common response messages

    /**
     * A static ResponseMessage object representing a successful response.
     */
    public static ResponseMessage SUCCESS = new ResponseMessage(0, "success");

    /**
     * A static ResponseMessage object representing a login failed response.
     */
    public static ResponseMessage LOGIN_FAILED = new ResponseMessage(-1, "Login failed");

    /**
     * A static ResponseMessage object representing an internal server error response.
     */
    public static ResponseMessage INTERNAL_SERVER_ERROR = new ResponseMessage(-2, "Internal server error");

    /**
     * A static ResponseMessage object representing a password error response.
     */
    public static ResponseMessage PASSWORD_ERROR = new ResponseMessage(-3, "Password error");

    /**
     * A static ResponseMessage object representing a generic error response.
     */
    public static ResponseMessage ERROR = new ResponseMessage(-4, "Other error");

}
