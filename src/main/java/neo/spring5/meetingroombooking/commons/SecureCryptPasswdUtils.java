package neo.spring5.meetingroombooking.commons;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SecureCryptPasswdUtils {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 10000;
    private static SecureRandom secureRandom = new SecureRandom();
    private static final int PASSWORD_LENGTH = 13;

    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()<>?";


    public static String generateRandomChars() throws Exception {

        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(CHARSET.length());
            sb.append(CHARSET.charAt(randomIndex));
        }

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("SHA-256 algorithm not available");
        }
        byte[] hashBytes = messageDigest.digest(sb.toString().getBytes());

        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.substring(0, PASSWORD_LENGTH);
    }

    public static String generatePassword(String inputPassword) {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        byte[] hashedPassword = hashPassword(inputPassword.toCharArray(), salt);

        byte[] saltedHash = new byte[hashedPassword.length + SALT_LENGTH];
        System.arraycopy(salt, 0, saltedHash, 0, SALT_LENGTH);
        System.arraycopy(hashedPassword, 0, saltedHash, SALT_LENGTH, hashedPassword.length);

        return Base64.getEncoder().encodeToString(saltedHash);
    }

    public static boolean validatePassword(String inputPassword, String storedPassword) {
        byte[] decodedPassword = Base64.getDecoder().decode(storedPassword);

        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(decodedPassword, 0, salt, 0, SALT_LENGTH);

        byte[] hashedInputPassword = hashPassword(inputPassword.toCharArray(), salt);

        byte[] saltedHash = new byte[hashedInputPassword.length + SALT_LENGTH];
        System.arraycopy(salt, 0, saltedHash, 0, SALT_LENGTH);
        System.arraycopy(hashedInputPassword, 0, saltedHash, SALT_LENGTH, hashedInputPassword.length);

        String encodedInputPassword = Base64.getEncoder().encodeToString(saltedHash);

        return storedPassword.equals(encodedInputPassword);
    }

    private static byte[] hashPassword(char[] inputPassword, byte[] salt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("PBKDF2WithHmacSHA256");
            messageDigest.reset();
            messageDigest.update(salt);
            byte[] hashedPassword = messageDigest.digest(new String(inputPassword).getBytes());

            for (int i = 0; i < ITERATIONS; i++) {
                messageDigest.reset();
                hashedPassword = messageDigest.digest(hashedPassword);
            }

            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not hash password: " + e.getMessage());
        }
    }
}

