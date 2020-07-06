package gitandroid.com.firestoreexample;

public class Note {
    private String title;
    private String description;

    public Note() {  // Firestore always needs Empty constructor . Otherwise it will crash..
        //public no-arg constructor needed
    }
    public Note(String _title, String _description) {
        this.title = _title;
        this.description = _description;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
}
