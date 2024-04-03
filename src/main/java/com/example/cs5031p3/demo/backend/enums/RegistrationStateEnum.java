package com.example.cs5031p3.demo.backend.enums;

/**
 * Enum representing different states of project registration.
 */
public enum RegistrationStateEnum {

    WAIT(1,"Interested"),

    SUCCESS(2,"Assigned"),

    ;

    Integer code;

    String description;

    /**
     * Constructor for RegistrationStateEnum.
     * @param code The code representing the registration state.
     * @param description The description of the registration state.
     */
    RegistrationStateEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Get the code associated with the registration state.
     * @return The code representing the registration state.
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Get the description of the registration state.
     * @return The description of the registration state.
     */
    public String getDescription() {
        return description;
    }
}
