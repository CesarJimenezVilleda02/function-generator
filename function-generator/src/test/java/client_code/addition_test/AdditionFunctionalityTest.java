package client_code.addition_test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

// Test suite for Addition functionality
public class AdditionFunctionalityTest {

    private int a;
    private int b;

    @Before
    public void setUp() {
        a = 5;
        b = 3;
        System.out.println("Setting up test data for AdditionFunctionalityTest.");
    }

    @Test
    public void testAdditionResult() {
        int result = a + b;
        assertEquals("Addition result should be correct", 8, result);
    }

    @Test
    public void testAdditionWithZero() {
        int result = a + 0;
        assertEquals("Adding zero should return the same number", a, result);
    }

    @After
    public void tearDown() {
        System.out.println("Cleaning up test data for AdditionFunctionalityTest.");
    }
}