package com.example.cs5031p3.demo.backend.enums;

/**
 * Enum representing different user types.
 */
public enum TypeEnum {

    STUDENT(1,"Student"),

    STAFF(2,"Staff"),

    ;

    Integer code;
    String description;

    /**
     * Constructor for TypeEnum.
     * @param code The code representing the user type.
     * @param description The description of the user type.
     */
    TypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Get the code associated with the user type.
     * @return The code representing the user type.
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Get the description of the user type.
     * @return The description of the user type.
     */
    public String getDescription() {
        return description;
    }

}
