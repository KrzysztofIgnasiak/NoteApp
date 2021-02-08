package com.example.notes;

import android.content.Context;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class NotesSecurity {

    public static String decryptNote(String encryptedNote, Context context, byte [] iv)
    {
        byte [] currentEncryptedNoteBytes = new byte[0]; // get bytes
        currentEncryptedNoteBytes = Base64.decode(encryptedNote,Base64.NO_WRAP); // to bytes
        byte [] currentNoteBytes = KeyStore_subSystem.DecryptData(currentEncryptedNoteBytes,context,iv);// decrypt

        String note = null;
        note = convertToString(currentNoteBytes); // back to string
        return note;
    }
    public static String encryptNote(String note, Context context, byte[] iv)
    {
        byte [] noteBytes = new byte[0];
        try {
            noteBytes = note.getBytes("UTF-8"); // to bytes
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        EncryptHandler handler = KeyStore_subSystem.EncryptData2(context,noteBytes,iv); //encrypt
        byte [] noteEncryptedBytes = handler.getEncrypted(); //get encrypted bytes

        String noteEncrypted =  Base64.encodeToString(noteEncryptedBytes, Base64.NO_WRAP); //convert to string
        return noteEncrypted;

    }

    public static String convertToString(byte[] chars)
    {
        StringBuilder result = new StringBuilder();
        String temp = "";
        for(int i =0;i<chars.length;i++)
        {
            byte[] bytes = Arrays.copyOfRange(chars,i,i+1);
            try {
                temp = new String(bytes,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            result.append(temp);
        }
        return result.toString();

    }


}
