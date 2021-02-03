import android.content.Context;
import android.util.Base64;

import com.example.notes.EncryptHandler;
import com.example.notes.KeyStore_subSystem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class NotesSecurity {

    public static ArrayList<String>DecryptNotes(ArrayList<String> EncryptedNotes, Context context, byte [] iv)
    {
        ArrayList<String> Notes = new ArrayList<String>();
        for (int i = 0;i<EncryptedNotes.size();i++)
        {
            String CurrentEncryptedNote = EncryptedNotes.get(i);

           String CurrentNote = DecryptNote(CurrentEncryptedNote,context,iv);
            Notes.add(CurrentNote);
        }
        return Notes;
    }

    public static String DecryptNote(String EncryptedNote,Context context, byte [] iv)
    {
        byte [] CurrentEncryptedNoteBytes = new byte[0]; // get bytes
        CurrentEncryptedNoteBytes = Base64.decode(EncryptedNote,Base64.NO_WRAP); // to bytes
        byte [] CurrentNoteBytes = KeyStore_subSystem.DecryptData(CurrentEncryptedNoteBytes,context,iv);// decrypt

        String Note = null;
        try {
            Note = new String(CurrentNoteBytes,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Note;
    }
    public static String EncryptNote(String Note, Context context, byte[] iv)
    {
        byte [] NoteBytes = new byte[0];
        try {
            NoteBytes = Note.getBytes("UTF-8"); // to bytes
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        EncryptHandler Handler = KeyStore_subSystem.EncryptData2(context,NoteBytes,iv);
        byte [] NoteEncryptedBytes = Handler.getEncrypted();
        String NoteEncrypted =  Base64.encodeToString(NoteEncryptedBytes, Base64.NO_WRAP);
        return NoteEncrypted;

    }
    public static ArrayList<String> UpdateEncryptedNotes(ArrayList<String> EncryptedNotes,byte [] iv, int index,Context context)
    {
        String NoteToUpdate = EncryptedNotes.get(index);
        String UpdatedNote = EncryptNote(NoteToUpdate,context,iv);
        EncryptedNotes.set(index, NoteToUpdate);

        return EncryptedNotes;
    }

}
