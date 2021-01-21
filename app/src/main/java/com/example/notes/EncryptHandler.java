package com.example.notes;

public class EncryptHandler {
    private byte[] iv;
    private byte[] encrypted;

    public EncryptHandler(byte[] iv_, byte[] encrypted_){
        this.iv = iv_;
        this.encrypted = encrypted_;
    }
    public byte[] getIv()
    {
        return iv;
    }
    public byte [] getEncrypted()
    {
        return encrypted;
    }
}
