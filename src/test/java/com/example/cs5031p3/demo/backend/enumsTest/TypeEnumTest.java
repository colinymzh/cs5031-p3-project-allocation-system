package com.example.cs5031p3.demo.backend.enumsTest;

import com.example.cs5031p3.demo.backend.enums.TypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the TypeEnum class.
 */
public class TypeEnumTest {

    /**
     * Test case to verify the enum values for the STUDENT type.
     */
    @Test
    void testStudentEnumValues() {
        Assertions.assertEquals(1, TypeEnum.STUDENT.getCode());
        Assertions.assertEquals("Student", TypeEnum.STUDENT.getDescription());
    }

    /**
     * Test case to verify the enum values for the STAFF type.
     */
    @Test
    void testStaffEnumValues() {
        Assertions.assertEquals(2, TypeEnum.STAFF.getCode());
        Assertions.assertEquals("Staff", TypeEnum.STAFF.getDescription());
    }
}
