package com.example.project;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class Activity_notes extends AppCompatActivity {


    private ImageView notes_BTN_newNote;
    private ImageView notes_IMG_background;
    private Dialog dialog;
    private TextView newNote_LBL_header;
    private TextInputEditText newNote_EDT_title;
    private LinedEditText newNote_EDT_text;
    private MaterialButton newNote_BTN_go;
    private MaterialButton newNote_BTN_cancel;
    private MaterialCheckBox newNote_BTN_share;
    private Fragment_Note fragment_note;
    private Adapter_Notes adapterNotes;
    private ArrayList<Note> notesArray = new ArrayList<>();
    private HashMap<String,Note> arrayIndexes = new HashMap<>();
    private Note lastNoteClicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        initViews();
        initFragment();
        notes_BTN_newNote.setOnClickListener(newNoteBTN);

        initNotes("families/ashur/familyNotes");
        initNotes("users/0/userNotes");


        Glide
                .with(this)
                .load(R.drawable.shaam)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(notes_IMG_background);


    }

    private void initViews() {
        notes_BTN_newNote = findViewById(R.id.notes_BTN_newNote);
        notes_IMG_background = findViewById(R.id.notes_IMG_background);
    }

    private void initNotes(String path) {
        readData(FirebaseDatabase.getInstance().getReference(path), new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Note note = ds.getValue(Note.class);
                        notesArray.add(note);
                        arrayIndexes.put(note.getKey(),note);
                    }
                    if (notesArray.size() != 0) {
                        updatedData(notesArray);
                    }
                }

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void readData(DatabaseReference ref, final GetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure();
            }

        });

    }

    private View.OnClickListener newNoteBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openDialog(null);
        }
    };

    private void openDialog(Note noteClicked) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_new_note);
        initDialog();
        newNote_BTN_go.setOnClickListener(newNoteGoBTN);
        if (noteClicked == null){
            newNote_BTN_cancel.setOnClickListener(cancelBTN);
        }
        else {
            setProperties(noteClicked);
        }
        dialog.show();
    }

    private void setProperties(Note note) {
        newNote_BTN_go.setText("edit");
        newNote_BTN_cancel.setText("Remove");

        newNote_LBL_header.setText("Edit Event");
        newNote_EDT_title.setText(note.getHead());
        newNote_EDT_text.setText(note.getDesc());
        newNote_BTN_share.setChecked(note.isShare());

        newNote_BTN_cancel.setOnClickListener(removeNote);


    }

    private View.OnClickListener removeNote = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            notesArray.remove(arrayIndexes.get(lastNoteClicked.getKey()));
            arrayIndexes.remove(lastNoteClicked.getKey());
            updatedData(notesArray);
            if (lastNoteClicked.isShare()) removeFromDB("families/ashur/familyNotes");
            else removeFromDB("users/0/userNotes");

            dialog.dismiss();
        }
    };

    private View.OnClickListener cancelBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.dismiss();
        }
    };

    private View.OnClickListener newNoteGoBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean titleInput, textInput;
            String title = newNote_EDT_title.getText().toString().trim();
            String text = newNote_EDT_text.getText().toString().trim();
            boolean repeat = newNote_BTN_share.isChecked();
            titleInput = makeError(newNote_EDT_title, "Title");
            textInput = makeError(newNote_EDT_text, "Text");

            if (titleInput && textInput) {
                if (newNote_BTN_go.getText().charAt(0) == 'e') {
                    notesArray.remove(arrayIndexes.get(lastNoteClicked.getKey()));
                    arrayIndexes.remove(lastNoteClicked.getKey());
                    if (lastNoteClicked.isShare()) {
                        removeFromDB("families/ashur/familyNotes");
                    } else{
                        removeFromDB("users/0/userNotes");
                    }
                }


                Note note = new Note(title, text, repeat);
                notesArray.add(note);
                arrayIndexes.put(note.getKey(),note);
                updatedData(notesArray);
                if (note.isShare()) uploadNewNoteToDB(note, "families/ashur/familyNotes");
                else uploadNewNoteToDB(note, "users/0/userNotes");

                dialog.dismiss();
            }
        }
    };

    private void uploadNewNoteToDB(Note note, String path) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.child(note.getKey()).setValue(note);
    }

    private void removeFromDB(String path) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.child(lastNoteClicked.getKey()).removeValue();
    }


    private void updatedData(ArrayList itemsArrayList) {
        if (adapterNotes == null) {
            adapterNotes = new Adapter_Notes(itemsArrayList, this);
            adapterNotes.setClickListeners(noteItemClickListener);
            fragment_note.getRecyclerView().setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            fragment_note.getRecyclerView().setAdapter(adapterNotes);
        } else {
            adapterNotes.notifyDataSetChanged();
        }
    }

    Adapter_Notes.NoteItemClickListener noteItemClickListener = new Adapter_Notes.NoteItemClickListener() {
        @Override
        public void itemClicked(Note note, int position) {
            lastNoteClicked = note;
            openDialog(note);
        }
    };

    private void initDialog() {
        newNote_LBL_header = dialog.findViewById(R.id.newNote_LBL_header);
        newNote_EDT_title = dialog.findViewById(R.id.newNote_EDT_title);
        newNote_EDT_text = dialog.findViewById(R.id.newNote_EDT_text);
        newNote_BTN_go = dialog.findViewById(R.id.newNote_BTN_go);
        newNote_BTN_cancel = dialog.findViewById(R.id.newNote_BTN_cancel);
        newNote_BTN_share = dialog.findViewById(R.id.newNote_BTN_share);
    }

    private void initFragment() {
        fragment_note = new Fragment_Note();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment_note);
        transaction.commit();
    }

    private boolean makeError(EditText inputLayout, String label) {
        if (inputLayout.length() == 0) {
            inputLayout.setError(label + " should not be empty");
            return false;
        } else {
            inputLayout.setError(null);
            return true;
        }
    }

}
