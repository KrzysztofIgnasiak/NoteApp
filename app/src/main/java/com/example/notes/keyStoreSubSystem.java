package com.example.notes;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import android.content.Context;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class keyStoreSubSystem {
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String PASSSWORDALIAS = "PasswordKey";
    private static final String DATAALIAS = "DataKey";
    private static SecretKey getOrCreateKey(String alias, Context context)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException,
            NoSuchProviderException, InvalidAlgorithmParameterException {
        //final String ANDROID_KEY_STORE = "AndroidKeyStore";
        SecretKey key;
        KeyStore keyStore  = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        KeyGenerator  keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        if(!keyStore.containsAlias(alias)) {
            keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .build());
           key =  keyGenerator.generateKey();
        }
        else {
            key =  ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
        }
        return key;
    }


    private static EncryptHandler encrypt(byte[] textToEncrypt, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] iv = cipher.getIV(); // initialization vector

        byte[] encryption = cipher.doFinal(textToEncrypt); //encrypt
        EncryptHandler handler = new EncryptHandler(iv,encryption,key);

        return handler;
    }

    private static EncryptHandler encryptWithIv(byte[] textToEncrypt, SecretKey key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec spec = new GCMParameterSpec(128, iv); //generate formal iv from bytes
        cipher.init(Cipher.ENCRYPT_MODE,key,spec);
        byte[] encryption = cipher.doFinal(textToEncrypt); //encrypt

        EncryptHandler handler = new EncryptHandler(iv,encryption,key);

        return handler;

    }

    private static byte[] decrypt(byte[] textToDecrypt,SecretKey key,byte[] encryptionIv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv); //generate formal iv from bytes

        cipher.init(Cipher.DECRYPT_MODE,key,spec);

        byte[] decrypted = cipher.doFinal(textToDecrypt); //decrypt

        return decrypted;
    }

    private static SecretKey getOrCreatePasswordKey(Context context) throws CertificateException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, UnrecoverableEntryException, IOException {
        SecretKey passwordKey = getOrCreateKey(PASSSWORDALIAS,context);
        return passwordKey;
    }
    private static SecretKey getOrCreateDataKey(Context context) throws CertificateException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, UnrecoverableEntryException, IOException {
        SecretKey dataKey = getOrCreateKey(DATAALIAS,context);
        return dataKey;
    }

    public static EncryptHandler encryptPassword(Context context, byte[] text)
    {
        SecretKey key = null;
        try {
            key = getOrCreatePasswordKey(context); //get key
        } catch (CertificateException | IOException | UnrecoverableEntryException | NoSuchProviderException |
                KeyStoreException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        EncryptHandler handler = null;
        try {
            handler = encrypt(text,key); //encrypt
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return handler;
    }

    public static EncryptHandler encryptData(Context context,byte[] text,byte[] iv )
    {
        SecretKey key = null;
        try {
            key = getOrCreateDataKey(context); //get key
        } catch (CertificateException | InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchProviderException | UnrecoverableEntryException | IOException e) {
            e.printStackTrace();
        }
        EncryptHandler handler = null;
        try {
            handler = encryptWithIv(text,key,iv); //encrypt
        } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return handler;
    }


    public static byte[] decryptPassword(byte [] text, Context context, byte[] iv)
    {
        SecretKey key = null;
        try {
            key = getOrCreatePasswordKey(context); //get key
        } catch (CertificateException | IOException | UnrecoverableEntryException | NoSuchProviderException |
                KeyStoreException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte [] decryptedText = null;
        try {
            decryptedText = decrypt(text,key,iv); //decrypt
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

    public static byte[] decryptData(byte[] text, Context context, byte[] iv){
        SecretKey key = null;
        try {
            key = getOrCreateDataKey(context); //get key
        } catch (CertificateException | InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchProviderException | UnrecoverableEntryException | IOException e) {
            e.printStackTrace();
        }
        byte [] decryptedText = null;
        try {
            decryptedText = decrypt(text,key,iv); //decrypt
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decryptedText;
    }
    public static byte [] generateIv()
    {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

}
