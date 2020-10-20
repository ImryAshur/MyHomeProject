package com.example.project.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.CallBacks.CallBack_List;
import com.example.project.R;


public class Fragment_List extends Fragment {
    protected View view;
    private RecyclerView recyclerView;
    private CallBack_List callBack_list;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public static Fragment_List newInstance() {
        Fragment_List fragment = new Fragment_List();
        return fragment;
    }
    public void setActivityCallBack(CallBack_List callBack_list) {
        this.callBack_list = callBack_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_list, container, false);
        }
        findViews(view);

//        list_LST_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
//            {
//                callBack_list.getEventFromList(itemPosition);
//            }
//        });

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
