package com.example.notes;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
public class KeyStore_subSystem {


    private SecretKey GetOrCreateKey(String alias,Context context)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, NoSuchProviderException, InvalidAlgorithmParameterException {
        final String ANDROID_KEY_STORE = "AndroidKeyStore";
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

}
