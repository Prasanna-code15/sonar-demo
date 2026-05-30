package com.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.logging.Logger;

/**
 * SecurityUtils — intentionally contains security vulnerabilities
 * for SonarQube (SAST) to detect and report.
 *
 * DO NOT use any of these patterns in production code.
 */
public class SecurityUtils {

    private static final Logger logger = Logger.getLogger(SecurityUtils.class.getName());

    // ── Security Issue 1: Hard-coded credentials ───────────────────
    // Sonar: S2068 — Hard-coded password
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/appdb";
    private static final String DB_USER     = "admin";
    private static final String DB_PASSWORD = "SuperSecret123!"; // hard-coded credential

    // ── Security Issue 2: SQL Injection ───────────────────────────
    // Sonar: S2077 — SQL queries should not be vulnerable to injection attacks
    public String getUserByName(String username) throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement stmt  = conn.createStatement();

        // User input concatenated directly into query — classic SQL injection
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        ResultSet rs  = stmt.executeQuery(query);

        if (rs.next()) {
            return rs.getString("email");
        }
        return null;
    }

    // ── Security Issue 3: Weak hashing algorithm (MD5) ────────────
    // Sonar: S2070 — SHA-1 and MD5 hash algorithms should not be used
    public String hashPasswordMD5(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5"); // weak algorithm
        byte[] hash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // ── Security Issue 4: Weak random number generator ────────────
    // Sonar: S2245 — Using pseudorandom number generators (PRNGs) is security-sensitive
    public String generateToken(int length) {
        Random random = new Random(); // not cryptographically secure; use SecureRandom
        String chars  = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < length; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }

    // ── Security Issue 5: Sensitive data exposed in logs ──────────
    // Sonar: S2068 / S3330 — Credentials should not be logged
    public void authenticateUser(String username, String password) {
        // Logging a password in plain text
        logger.info("Authenticating user: " + username + " with password: " + password);

        if (username.equals("admin") && password.equals(DB_PASSWORD)) {
            logger.info("Authentication successful.");
        } else {
            logger.warning("Authentication failed for user: " + username);
        }
    }

    // ── Security Issue 6: XML External Entity (XXE) ───────────────
    // Sonar: S2755 — XML parsers should not be vulnerable to XXE attacks
    public void parseXml(String xmlInput) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory factory =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        // Missing: factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        // XXE is possible because external entities are not disabled
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(xmlInput)));
    }

    // ── Security Issue 7: Catching overly broad Exception ─────────
    // Sonar: S2221 — "Exception" should not be caught when not required
    public void broadExceptionCatch() {
        try {
            String value = System.getProperty("some.property");
            int number = Integer.parseInt(value);
            logger.info("Parsed: " + number);
        } catch (Exception e) { // too broad — hides real error types
            logger.warning("Something went wrong: " + e.getMessage());
        }
    }
}
