package com.jobtrack.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utility class for password security using BCrypt.
 */
public class SecurityUtils {

    // Higher cost factor increases security but slows down hashing (standard is 10-12)
    private static final int COST_FACTOR = 12;

    /**
     * Hashes a plain text password using BCrypt.
     * @param plainPassword The user's raw password
     * @return The secure hash string
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(COST_FACTOR, plainPassword.toCharArray());
    }

    /**
     * Verifies if a plain text password matches a stored hash.
     * @param plainPassword The raw password to check
     * @param storedHash The hashed password from the database
     * @return true if it matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), storedHash);
        return result.verified;
    }
}