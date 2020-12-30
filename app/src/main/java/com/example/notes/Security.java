package com.example.notes;

import java.security.NoSuchAlgorithmException;
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

    public static Optional<String> hashPassword(String password, String salt)
    {
        char[] chars = password.toCharArray();
        byte[] bytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(chars,bytes, INTERATIONS,KEY_LENGHT); // how we'are going to generated hashed password

        Arrays.fill(chars, Character.MIN_VALUE); // all infromation is in spec so it is good idea to delete the orginal password

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

    public static boolean verifyPassword(String password,String key,String salt)
    {
        Optional<String> optEncrypted = hashPassword(password, salt);
        if(!optEncrypted.isPresent()) return false;
        return optEncrypted.get().equals(key);
    }
}
