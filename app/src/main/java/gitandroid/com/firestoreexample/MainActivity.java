package gitandroid.com.firestoreexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";    // logt is a live template
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewData;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();    // Referance for firestore database.
    private DocumentReference noteRef = db.document("Notebook/My First Note");
    //private ListenerRegistration noteListener;  if we want to detach the listener manually..

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextTitle = findViewById(R.id.editTextTitle);   // if cannot recognise go to build and rebuild the project
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewData = findViewById(R.id.text_load_data);

    }

    @Override
    protected void onStart() {
        super.onStart();
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() { // just put "this" to detach it on activity stop. We could also do detach onStop manually -- noteListener = noteRef.addSnapshotListener...  and remove listener onStop()..
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "Could not load firebase exception!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                    return;
                } else {
                    if (documentSnapshot.exists()) {
/*                        String title = documentSnapshot.getString(KEY_TITLE);
                        String description = documentSnapshot.getString(KEY_DESCRIPTION);
                        textViewData.setText("Title:" + title + "\n" + "Description:" + description);*/
                        Note note = documentSnapshot.toObject(Note.class);   // variable names should match!
                        String title = note.getTitle();
                        String description = note.getDescription();
                        textViewData.setText("Title:" + title + "\n" + "Description:" + description);
                    } else {
                        Toast.makeText(MainActivity.this, "Document does not exist!", Toast.LENGTH_SHORT).show();
                        textViewData.setText("");
                    }

                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // noteListener.remove();  to remove listener manually
    }

    public void saveNote(View v) {     // we have to set it public because we set it in .xml and it should take View in it.
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        // we need to use container to send the variables . We cannot just pass them directly.
        // we can use java map.(key -value pairs) or we can create our own java object -- Find detailed description on json video series.
        // we set it as object because we can pass different types other than strings
        // Map is an interface and Hashmap is a specific implementation of Map interface . We can create it .
/*        Map<String, Object> note = new HashMap<>();
        note.put(KEY_TITLE, title);
        note.put(KEY_DESCRIPTION, description);*/
        Note note = new Note(title, description);  // we could use MAP object instead of our Java Object
        //db.document("Notebook/My First Note");  shorter version to name collection and document but the other one is better to use
        // We could create sub collections      db.collection("Notebook").document("My First Note").collection()set(note) . .
        noteRef.set(note)  // db.collection("Notebook").document("My First Note").set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

    }

    public void updateDescription(View view) {  // This updates description but also all the document so the title would be null.
        String description = editTextDescription.getText().toString();
        //Map<String, Object> note = new HashMap<>();
        //note.put(KEY_DESCRIPTION, description);
        //noteRef.set(note, SetOptions.merge()); this will update the field , but if the document doesn't exist this also create one..
        noteRef.update(KEY_DESCRIPTION, description); // we could use Map as above instead. This wont create the document , if does not exist.
    }

    public void deleteDescription(View v) {
        //Map<String, Object> note = new HashMap<>();
        //note.put(KEY_DESCRIPTION, FieldValue.delete());
        //noteRef.update(note);
        noteRef.update(KEY_DESCRIPTION, FieldValue.delete());
    }

    public void deleteNote(View v) {
        noteRef.delete();
    }

    public void loadNote(View v) {
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
/*                          String title = documentSnapshot.getString(KEY_TITLE);
                            String description = documentSnapshot.getString(KEY_DESCRIPTION);*/
                            Note note = documentSnapshot.toObject(Note.class);   // variable names should match!
                            String title = note.getTitle();
                            String description = note.getDescription();
                            //Map<String, Object> note = documentSnapshot.getData();
                            textViewData.setText("Title:" + title + "\n" + "Description:" + description);
                        } else {
                            Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }
}
