package com.example.project.Fragments;
/*
Developer - Imry Ashur
*/

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Adapters.Adapter_Notes;
import com.example.project.R;


public class Fragment_Note extends Fragment
       // implements NotesAdapter.OnRecyclerItemClick
{
    protected View view;
    private RecyclerView recyclerView;
    private Adapter_Notes adapterNotes;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
    public static Fragment_Note newInstance() {
        Fragment_Note fragment = new Fragment_Note();
        return fragment;
    }


    public Fragment_Note() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if(view==null){
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }
        findViews(view);

        return view;
    }
    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



}
