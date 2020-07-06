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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    private CollectionReference notebookRef = db.collection("Notebook");
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
        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Note note = documentSnapshot.toObject(Note.class);
                    note.setDocumentId(documentSnapshot.getId());
                    String documentId = note.getDocumentId();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    data += "ID: "+documentId
                            + "\nTitle: " + title + "\nDescription: " + description + "\n\n";
                   // notebookRef.document(documentId).update   To get the id of the document and work on it..
                }
                textViewData.setText(data);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // noteListener.remove();  to remove listener manually
    }

    public void addNote(View v) {     // we have to set it public because we set it in .xml and it should take View in it.
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
        notebookRef.add(note);
    }


    public void loadNotes(View v) {
        notebookRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override

                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) { // we have querysnapshot now instead of documentsnapshot.
                        String data = "";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {   // QueryDocumentSnapshot always exist. No need to check it. DocumentSnapshot is the superclass of it.
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentId(documentSnapshot.getId());
                            String documentId = note.getDocumentId();
                            String title = note.getTitle();
                            String description = note.getDescription();
                            data += "ID: "+documentId
                                   + "\nTitle: " + title + "\nDescription: " + description + "\n\n";
                        }
                        textViewData.setText(data);
                    }
                });
    }
}
