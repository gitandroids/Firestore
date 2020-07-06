package gitandroid.com.firestoreexample;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String title;
    private String description;
    private String documentId;
    private int priority;
    public Note() {  // Firestore always needs Empty constructor . Otherwise it will crash..
        //public no-arg constructor needed
    }
    public Note(String _title, String _description , int _priority) {
        this.title = _title;
        this.description = _description;
        this.priority = _priority;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    @Exclude      // We dont want this id to appear in firestore so that we @exclude it. // There is an id anyways . It would be redundant
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
