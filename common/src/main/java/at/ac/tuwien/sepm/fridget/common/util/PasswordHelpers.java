package at.ac.tuwien.sepm.fridget.common.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordHelpers {

    private static final int BCRYPT_WORK_FACTOR = 11;
    public static final int MIN_LENGTH = 8;

    private PasswordHelpers() {
    }

    /**
     * @param plainPassword The password in plain text.
     * @return The SHA256-hashed password, called "clientPassword" in other parameters.
     */
    public static String hashOnClient(String plainPassword) {
        return DigestUtils.sha256Hex(plainPassword);
    }

    /**
     * @param clientPassword The password hashed with SHA256 from `hashOnClient`.
     * @return The bcrypt salted+hashed password, called "storedPassword" in other parameters.
     */
    public static String hashForStorage(String clientPassword) {
        return BCrypt.hashpw(clientPassword, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
    }

    /**
     * @param storedPassword The password salted+hashed with bcrypt from `hashForStorage`.
     * @param clientPassword The password hashed with SHA256 from `hashOnClient`.
     * @return True if the passwords are equal, otherwise false.
     */
    public static boolean compareWithDatabase(String storedPassword, String clientPassword) {
        return BCrypt.checkpw(clientPassword, storedPassword);
    }

    /**
     * Determine if the given plaintext password is valid. The following criteria must be met:
     * - The password must not be null
     * - The password must have at least 8 characters
     *
     * @param plainPassword The password in plain text.
     * @return True, if the given password is valid, otherwise false.
     */
    public static boolean isValidPassword(String plainPassword) {
        return plainPassword != null && plainPassword.length() >= MIN_LENGTH;
    }

}
