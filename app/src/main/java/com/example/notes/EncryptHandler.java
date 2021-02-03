package com.example.notes;

import javax.crypto.SecretKey;

public class EncryptHandler {
    private byte[] iv;
    private byte[] encrypted;
    private SecretKey key;

    public EncryptHandler(byte[] iv_, byte[] encrypted_, SecretKey key_) {
        this.iv = iv_;
        this.encrypted = encrypted_;
        this.key = key_;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getEncrypted() {
        return encrypted;
    }

    public String getKey()
    {
        return key.toString();
    }
}
