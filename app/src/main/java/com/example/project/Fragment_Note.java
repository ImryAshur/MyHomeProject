package com.example.project;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


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
//        modelList = new ArrayList<>();
//        for (int i = 0; i < head.length; i++) {
//            NotesModel notesModel = new NotesModel();
//            notesModel.setHead(head[i]);
//            notesModel.setDesc(desc[i]);
//            notesModel.setTime(time[i]);
//            //if you want to use icons for different categories you can use the following line :
//            //notesModel.setView(PASS ICONS HERE LIKE WORK, PERSONAL, ETC);
//            modelList.add(notesModel);
//        }
        //notesAdapter = new NotesAdapter(modelList, getContext());
        //recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        //recyclerView.setAdapter(notesAdapter);

        //notesAdapter.setOnRecyclerItemClick(this);
        return view;
    }
    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

//    @Override
//    public void onClick(int pos) {
//        Toast.makeText(getContext(), "Pos is : " + pos, Toast.LENGTH_SHORT).show();
//    }

}
