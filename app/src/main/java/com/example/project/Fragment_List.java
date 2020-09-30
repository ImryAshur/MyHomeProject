package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;


public class Fragment_List extends Fragment {
    protected View view;
    private ListView list_LST_list;
    private CallBack_List callBack_list;

    public ListView getList_LST_list() {
        return list_LST_list;
    }

    public void setList_LST_list(ListView list_LST_list) {
        this.list_LST_list = list_LST_list;
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

        list_LST_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
            {
                callBack_list.getEventFromList(itemPosition);
            }
        });

        return view;
    }

    private void findViews(View view) {
        list_LST_list = view.findViewById(R.id.list_LST_list);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
