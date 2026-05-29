package com.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathUtilsTest {

    private MathUtils utils;

    @BeforeEach
    void setUp() {
        utils = new MathUtils();
    }

    @Test
    void testAdd() {
        assertEquals(8, utils.add(3, 5));
        assertEquals(0, utils.add(-3, 3));
        assertEquals(-5, utils.add(-2, -3));
    }

    @Test
    void testSubtract() {
        assertEquals(2, utils.subtract(5, 3));
        assertEquals(-6, utils.subtract(-3, 3));
    }

    @Test
    void testMultiply() {
        assertEquals(15, utils.multiply(3, 5));
        assertEquals(-6, utils.multiply(-2, 3));
        assertEquals(0, utils.multiply(0, 100));
    }

    @Test
    void testDivide() {
        assertEquals(2.5, utils.divide(5, 2), 0.001);
    }

    @Test
    void testDivideByZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> utils.divide(10, 0));
    }

    @Test
    void testFactorial() {
        assertEquals(1, utils.factorial(0));
        assertEquals(1, utils.factorial(1));
        assertEquals(120, utils.factorial(5));
    }

    @Test
    void testFactorialNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> utils.factorial(-1));
    }

    @Test
    void testIsPrime() {
        assertTrue(utils.isPrime(7));
        assertTrue(utils.isPrime(13));
        assertFalse(utils.isPrime(1));
        assertFalse(utils.isPrime(9));
    }

    @Test
    void testClassify() {
        assertEquals("positive", utils.classify(5));
        assertEquals("negative", utils.classify(-3));
        assertEquals("zero", utils.classify(0));
    }
}
