package com.example.cs5031p3.demo.backend.enumsTest;

import com.example.cs5031p3.demo.backend.enums.RegistrationStateEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the RegistrationStateEnum class.
 */
public class ResgistrationStateEnumTest {

    /**
     * Test case to verify the enum values for WAIT state.
     */
    @Test
    public void testEnumValues() {
        Assertions.assertEquals(1, RegistrationStateEnum.WAIT.getCode());
        Assertions.assertEquals("Interested", RegistrationStateEnum.WAIT.getDescription());

        Assertions.assertEquals(2, RegistrationStateEnum.SUCCESS.getCode());
        Assertions.assertEquals("Assigned", RegistrationStateEnum.SUCCESS.getDescription());
    }

}
