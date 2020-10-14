package com.example.project;

import com.google.firebase.database.DataSnapshot;

public interface GetDataListener {
    void onSuccess(DataSnapshot dataSnapshot);
    void onStart();
    void onFailure();
}
