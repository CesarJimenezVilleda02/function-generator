package client_code.addition_test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

// Test suite for Edge Cases of Addition
public class AdditionEdgeCaseTest {

    private int largeNumber;
    private int smallNumber;

    @Before
    public void setUp() {
        largeNumber = Integer.MAX_VALUE;
        smallNumber = Integer.MIN_VALUE;
        System.out.println("Setting up test data for AdditionEdgeCaseTest.");
    }

    @Test
    public void testAdditionOverflow() {
        long result = (long) largeNumber + 1;
        assertEquals("Addition should handle overflow correctly", (long) Integer.MIN_VALUE, result);
    }

    @Test
    public void testAdditionUnderflow() {
        long result = (long) smallNumber - 1;
        assertEquals("Addition should handle underflow correctly", (long) Integer.MAX_VALUE, result);
    }

    @After
    public void tearDown() {
        System.out.println("Cleaning up test data for AdditionEdgeCaseTest.");
    }
}
