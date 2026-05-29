package com.demo;

/**
 * Simple utility class — intentionally contains a few code smells
 * so SonarQube has something interesting to flag.
 */
public class MathUtils {

    // ── Clean methods ──────────────────────────────────────────────

    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    /**
     * Divides a by b.
     * @throws IllegalArgumentException if b is zero
     */
    public double divide(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        }
        return a / b;
    }

    public long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Negative input: " + n);
        if (n == 0) return 1;
        return n * factorial(n - 1);
    }

    public boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    // ── Code smells for SonarQube to detect ───────────────────────

    // Smell 1: unused local variable
    public int unusedVariableExample(int x) {
        int unused = 42; // Sonar: unused variable
        return x * 2;
    }

    // Smell 2: empty catch block
    public void emptyCatchExample() {
        try {
            int result = Integer.parseInt("not-a-number");
        } catch (NumberFormatException e) {
            // Sonar: empty catch block — exception swallowed silently
        }
    }

    // Smell 3: magic numbers
    public double circleArea(double radius) {
        return 3.14159 * radius * radius; // Sonar: magic number, use Math.PI
    }

    // Smell 4: overly complex / duplicate logic (minor duplication)
    public String classify(int n) {
        if (n > 0) {
            return "positive";
        } else if (n < 0) {
            return "negative";
        } else {
            return "zero";
        }
    }
}
