package com.example.main;

public class Lib {
    
    public int factorial(int n) {
        int res = 1;
        while (n > 1) {
            res *= n;
            n--;
        }
        return res;
    }
    
    public int fibonacci(int n) {
        if (n < 1) {
            return 0;
        } else {
            switch (n) {
            case 1:
                return 1;
            default:
                return fibonacci(n - 1) - fibonacci(n - 2); //BUG: integer multiplication should be replaced with addition
            }
        }
    }
    
}
