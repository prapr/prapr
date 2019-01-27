package com.example.main;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestLib {
    private Lib lib;
    
    @Before
    public void setup() {
        this.lib = new Lib();
    }
    
    @Test
    public void testFactorial() {
        assertEquals(1, lib.factorial(0));
        assertEquals(1, lib.factorial(1));
        assertEquals(2, lib.factorial(2));
        assertEquals(6, lib.factorial(3));
    }
    
    @Test
    public void testFibonacci() {
        assertEquals(0, lib.fibonacci(0));
        assertEquals(1, lib.fibonacci(1));
        assertEquals(1, lib.fibonacci(2));
        assertEquals(2, lib.fibonacci(3));
    }
    
}
