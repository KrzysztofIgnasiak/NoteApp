package com.example.notes;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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

public class KeyStore_subSystem {
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String PASSSWORDALIAS = "PasswordKey";
    private static final String DATAALIAS = "DataKey";
    private SecretKey GetOrCreateKey(String alias,Context context)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, NoSuchProviderException, InvalidAlgorithmParameterException {
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
                    .build());
           key =  keyGenerator.generateKey();
        }
        else {
            key =  ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
        }
        return key;
    }


    private EncryptHandler Encrypt(String textToEncrypt, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] iv = cipher.getIV(); // initialization vector

        //TODO what is iv and how to store it
        //TODO iv for every note 
        byte[] encryption = cipher.doFinal(textToEncrypt.getBytes("UTF-8"));
        String EncryptedText = new String(encryption, StandardCharsets.UTF_8);
       // return EncryptedText;
        EncryptHandler Handler = new EncryptHandler(iv,EncryptedText);

        return Handler;
    }

    private String decrypt(String textToDecrypt,SecretKey key,byte[] encryptionIv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
        //TODO check this specification
        cipher.init(Cipher.DECRYPT_MODE,key,spec);
        byte[] Todecrpyt = textToDecrypt.getBytes();
        byte[] decrypted = cipher.doFinal(Todecrpyt);

        String decryptedText = new String(decrypted,"UTF-8");

        return decryptedText;


    }
    private SecretKey GetOrCreatePasswordKey(Context context) throws CertificateException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableEntryException, IOException {
        SecretKey passwordKey = GetOrCreateKey(PASSSWORDALIAS,context);
        return passwordKey;
    }
    private SecretKey GetOrCreateDataKey(Context context) throws CertificateException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableEntryException, IOException {
        SecretKey dataKey = GetOrCreateKey(DATAALIAS,context);
        return dataKey;
    }

    public EncryptHandler EncryptPassword(Context context,String Text)
    {
        SecretKey key = null;
        try {
            key = GetOrCreatePasswordKey(context);
        } catch (CertificateException | IOException | UnrecoverableEntryException | NoSuchProviderException |
                KeyStoreException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        EncryptHandler Handler = null;
        try {
            Handler = Encrypt(Text,key);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return Handler;
    }

    public EncryptHandler EncryptData(Context context,String Text)
    {
        SecretKey key = null;
        try {
            key = GetOrCreateDataKey(context);
        } catch (CertificateException | InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchProviderException | UnrecoverableEntryException | IOException e) {
            e.printStackTrace();
        }
        EncryptHandler Handler = null;
        try {
            Handler = Encrypt(Text,key);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                | UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Handler;
    }

    public String DecryptPassword(String text, Context context, byte[] iv)
    {
        SecretKey key = null;
        try {
            key = GetOrCreatePasswordKey(context);
        } catch (CertificateException | IOException | UnrecoverableEntryException | NoSuchProviderException |
                KeyStoreException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        String decryptedText = null;
        try {
            decryptedText = decrypt(text,key,iv);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decryptedText;
    }
    public String DecryptData(String text,Context context, byte[] iv){
        SecretKey key = null;
        try {
            key = GetOrCreateDataKey(context);
        } catch (CertificateException | InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchProviderException | UnrecoverableEntryException | IOException e) {
            e.printStackTrace();
        }
        String decryptedText = null;
        try {
            decryptedText = decrypt(text,key,iv);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

}
