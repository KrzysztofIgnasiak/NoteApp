package com.example.notes;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;



public class Security {

    private static final int INTERATIONS = 65536; // how many times we should perform the hashing algorithm
    private static final int KEY_LENGHT = 512; // the desired length of the key in bites
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    private static final SecureRandom Rand = new SecureRandom();

    public static Optional<String> hashPassword(String password, String salt)
    {
        char[] chars = password.toCharArray();
        byte[] bytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(chars,bytes, INTERATIONS,KEY_LENGHT); // how we'are going to generated hashed password

        Arrays.fill(chars, Character.MIN_VALUE); // all information is in spec so it is good idea to delete the oryginal password

        try
        {
            SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM); //algorithm
            byte[] securePassword = fac.generateSecret(spec).getEncoded(); // get hashed key
            return Optional.of(Base64.getEncoder().encodeToString(securePassword)); // password as bytes, it only contains printable characters(then can be saved)
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException exception)
        {
            System.err.println("Exception encountered in hashPassword()");
            return Optional.empty();
        }
        finally{
            spec.clearPassword(); // delete password information from spec
        }
    }


    public static Optional<String> generateSalt(final int length)
    {
        if(length<1)
        {
            System.err.println("error in generateSalt : lenth must be >0");
        }

        byte[] salt = new byte[length];
        Rand.nextBytes(salt);

        return Optional.of(Base64.getEncoder().encodeToString(salt));
    }
}
