package com.demo;

public class App {

    public static void main(String[] args) {
        MathUtils utils = new MathUtils();
        System.out.println("Sum: " + utils.add(3, 5));
        System.out.println("Factorial of 5: " + utils.factorial(5));
    }
}
