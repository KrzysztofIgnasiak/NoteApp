package com.example.notes;

public class EncryptHandler {
    private byte[] iv;
    private String encrypted;

    public EncryptHandler(byte[] iv_, String encrypted_){
        this.iv = iv_;
        this.encrypted = encrypted_;
    }
    public byte[] getIv()
    {
        return iv;
    }
    public String getEncrypted()
    {
        return encrypted;
    }
}
