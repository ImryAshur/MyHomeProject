package com.example.project.Activities;
/*
Developer - Imry Ashur
*/
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import com.bumptech.glide.Glide;
import com.example.project.Adapters.Adapter_Notes;
import com.example.project.Fragments.Fragment_Note;
import com.example.project.CallBacks.GetDataListener;
import com.example.project.Others.LinedEditText;
import com.example.project.Others.MySharedPreferencesV4;
import com.example.project.Others.MySignalV2;
import com.example.project.Objects.Note;
import com.example.project.Objects.User;
import com.example.project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;


public class Activity_Notes extends AppCompatActivity {


    private String TAG = "pttt";
    private String SP_KEY_FAMILY_NOTES = "";
    private String SP_KEY_USER_NOTES = "";

    private String DB_USER_NOTES = "";
    private String DB_FAMILY_NOTES = "";
    private DrawerLayout notes_SPC_drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar notes_SPC_toolBar;
    private NavigationView notes_SPC_nav;
    private TextView navHeader_LBL_userName;
    private ProgressDialog progressDialog;
    private User user;
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
    private HashMap<String, Note> arrayIndexes = new HashMap<>();

    private HashMap<String, Note> hashMapFamilyNotes = new HashMap<>();
    private HashMap<String, Note> hashMapUserNotes = new HashMap<>();
    private Note lastNoteClicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        showProgressDialog();
        initViews();
        getUser();
        setKeys();
        hashMapFamilyNotes = getHashMapDataFromSP(SP_KEY_FAMILY_NOTES);
        hashMapUserNotes = getHashMapDataFromSP(SP_KEY_USER_NOTES);
        setSideMenu();
        initFragment();
        notes_BTN_newNote.setOnClickListener(newNoteBTN);
        notes_SPC_nav.setNavigationItemSelectedListener(menuListener);
        initNotes(DB_FAMILY_NOTES);
        initNotes(DB_USER_NOTES);

        Glide
                .with(this)
                .load(R.drawable.shaam)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(notes_IMG_background);



    }

    private void setKeys() {
        SP_KEY_FAMILY_NOTES = user.getKey() + "_FAMILY_NOTES";
        SP_KEY_USER_NOTES = user.getUserName() + "_USER_NOTES";
        DB_FAMILY_NOTES = "families/" + user.getKey() + "/familyNotes";
        DB_USER_NOTES = "users/" + user.getPhone() + "/userNotes";

    }

    @Override
    protected void onStop() {
        parseHashMapToStringAndUploadToSP(hashMapFamilyNotes, SP_KEY_FAMILY_NOTES);
        parseHashMapToStringAndUploadToSP(hashMapUserNotes, SP_KEY_USER_NOTES);
        super.onStop();
    }

    private void getUser() {
        Intent intent = getIntent();
        String tempUser = intent.getStringExtra(Activity_Main.EXTRA_KEY_USER);
        if (tempUser.length() > 0) {
            user = new Gson().fromJson(tempUser, User.class);
            navHeader_LBL_userName.setText(user.getUserName());
        } else {
            Log.d(TAG, "getUser: FAILD!!!!!!!!!!!");
        }

    }

    private HashMap<String, Note> getHashMapDataFromSP(String ref) {
        HashMap<String, Note> hashMap = new HashMap<>();
        String hashMapString = MySharedPreferencesV4.getInstance().getString(ref, "");
        if (hashMapString.length() > 0) {
            java.lang.reflect.Type type = new TypeToken<HashMap<String, Note>>() {}.getType();
            hashMap = new Gson().fromJson(hashMapString, type);
        }
        Log.d(TAG, "getDataFromSP: " + hashMap.size());
        return hashMap;
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(Activity_Notes.this);
        progressDialog.setMessage("Loading Please Wait...");
        progressDialog.show();
    }

    private void dismissDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void initViews() {
        notes_SPC_drawerLayout = findViewById(R.id.notes_SPC_drawerLayout);
        notes_SPC_toolBar = findViewById(R.id.notes_SPC_toolBar);
        notes_SPC_nav = findViewById(R.id.notes_SPC_nav);
        notes_BTN_newNote = findViewById(R.id.notes_BTN_newNote);
        notes_IMG_background = findViewById(R.id.notes_IMG_background);
        View headerView = notes_SPC_nav.getHeaderView(0);
        navHeader_LBL_userName = headerView.findViewById(R.id.navHeader_LBL_userName);
    }

    private void setSideMenu() {
        setSupportActionBar(notes_SPC_toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, notes_SPC_drawerLayout, notes_SPC_toolBar, R.string.open, R.string.close);
        notes_SPC_drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }
    private void parseHashMapToStringAndUploadToSP(HashMap<String, Note> hashMap, String path) {
        String hashMapString = new Gson().toJson(hashMap);
        MySharedPreferencesV4.getInstance().putString(path, hashMapString);
    }
    private NavigationView.OnNavigationItemSelectedListener menuListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.nav_LBL_calendar:
                    startNewActivity(Activity_Main.class, true);
                    break;
                case R.id.nav_LBL_settings:
                    startNewActivity(Activity_Settings.class, true);
                    break;
                case R.id.nav_LBL_logout:
                    startNewActivity(Activity_LogIn.class, false);
                    break;

                default:
                    return true;
            }
            return false;
        }

    };

    private void startNewActivity(Class newActivity, boolean parseUser) {
        Intent intent = new Intent(Activity_Notes.this, newActivity);
        if (parseUser) {
            String stringUser = new Gson().toJson(user);
            intent.putExtra(Activity_Main.EXTRA_KEY_USER, stringUser);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;

        return super.onOptionsItemSelected(item);
    }

    private void putNotesFromHashMaps(HashMap<String, Note> hashMap) {
        if (hashMap.size() > 0) {
            Log.d(TAG, "putEventsFromHashMaps: " + hashMap.size());
            for (Note note : hashMap.values()) {
                notesArray.add(note);
                arrayIndexes.put(note.getKey(), note);
            }
            if (notesArray.size() > 0)
                updatedData(notesArray);
        }

    }

    private void addNoteToArrayAndHashMap(DataSnapshot ds, HashMap<String, Note> hashMap) {
        Note note = ds.getValue(Note.class);
        hashMap.put(note.getKey(), note);
        notesArray.add(note);
        arrayIndexes.put(note.getKey(), note);
    }

    private void initNotes(String path) {
        readData(FirebaseDatabase.getInstance().getReference(path), new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                // init Notes From SP
                if (dataSnapshot.getKey().charAt(0) == 'f')
                    putNotesFromHashMaps(hashMapFamilyNotes);
                else putNotesFromHashMaps(hashMapUserNotes);

                // init Notes From DB That Not In SP
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (dataSnapshot.getKey().charAt(0) == 'f' && (!hashMapFamilyNotes.containsKey(ds.getKey()))) {
                            Log.d(TAG, "FAMILY initNotes: " + ds.getValue().toString());
                            addNoteToArrayAndHashMap(ds, hashMapFamilyNotes);
                        } else if (dataSnapshot.getKey().charAt(0) == 'u' && !(hashMapUserNotes.containsKey(ds.getKey()))) {
                            Log.d(TAG, "USER initNotes: " + ds.getValue().toString());
                            addNoteToArrayAndHashMap(ds, hashMapUserNotes);
                        }

                    }
                    if (notesArray.size() != 0) {
                        updatedData(notesArray);
                    }
                    if (dataSnapshot.getKey().charAt(0) == 'u') dismissDialog();
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
        if (noteClicked == null) {
            newNote_BTN_cancel.setOnClickListener(cancelBTN);
        } else {
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
    private void removeNoteFromArrayAndHashMapAndDB() {
        notesArray.remove(arrayIndexes.get(lastNoteClicked.getKey()));
        arrayIndexes.remove(lastNoteClicked.getKey());
        if (lastNoteClicked.isShare()) {
            hashMapFamilyNotes.remove(lastNoteClicked.getKey());
            removeFromDB(DB_FAMILY_NOTES);
        } else {
            hashMapUserNotes.remove(lastNoteClicked.getKey());
            removeFromDB(DB_USER_NOTES);
        }
        updatedData(notesArray);
    }
    private View.OnClickListener removeNote = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            removeNoteFromArrayAndHashMapAndDB();
            updatedData(notesArray);
            Log.d(TAG, "REMOVE NOTE: " + notesArray.size());
            MySignalV2.getInstance().showToast("Note Has Removed!");
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
                    removeNoteFromArrayAndHashMapAndDB();
                }

                createNewNote(title, text, repeat);
            }

        }
    };

    private void createNewNote(String title, String text, boolean repeat) {
        Note note = new Note(title, text, repeat);
        addNoteFromArrayAndHashMapAndDB(note);

        MySignalV2.getInstance().showToast("Done!");
        Log.d(TAG, "onClick: " + notesArray.size());
        dialog.dismiss();
    }

    private void addNoteFromArrayAndHashMapAndDB(Note note) {
        notesArray.add(note);
        arrayIndexes.put(note.getKey(), note);
        if (note.isShare()){
            uploadNewNoteToDB(note, DB_FAMILY_NOTES);
            hashMapFamilyNotes.put(note.getKey(),note);
        }
        else {
            uploadNewNoteToDB(note, DB_USER_NOTES);
            hashMapUserNotes.put(note.getKey(),note);
        }
        updatedData(notesArray);
    }



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
