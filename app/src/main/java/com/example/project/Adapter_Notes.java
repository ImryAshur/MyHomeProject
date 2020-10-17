package com.example.project;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class Adapter_Notes extends RecyclerView.Adapter<Adapter_Notes.NotesViewHolder> {

    private ArrayList<Note> noteList;
    private LayoutInflater mInflater;
    private Context context;

    private Adapter_Notes.NoteItemClickListener noteItemClickListener;

    public Adapter_Notes(ArrayList<Note> noteList, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.noteList = noteList;
        this.context = context;
    }
    public void setClickListeners(Adapter_Notes.NoteItemClickListener noteItemClickListener) {
        this.noteItemClickListener = noteItemClickListener;
    }


    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.notes_items, parent, false);
        return new NotesViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.headText.setText(noteList.get(position).getHead());
        holder.descText.setText(noteList.get(position).getDesc());
        holder.timeText.setText(noteList.get(position).getTime());

        switch (position) {
            case 1:
                holder.view.setBackgroundColor(context.getResources().getColor(R.color.color2));
                break;
            case 2:
                holder.view.setBackgroundColor(context.getResources().getColor(R.color.color3));
                break;
            case 3:
                holder.view.setBackgroundColor(context.getResources().getColor(R.color.color4));
                break;

            case 4:
                holder.view.setBackgroundColor(context.getResources().getColor(R.color.color5));
                break;

            case 0:

            default:
                holder.view.setBackgroundColor(context.getResources().getColor(R.color.color1));
                break;
        }
    }

    public ArrayList<Note> getNoteList() {
        return noteList;
    }

    Note getItem(int position) {
        return (Note) noteList.get(position);
    }

    public void setNoteList(ArrayList<Note> noteList) {
        this.noteList = noteList;
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {

        private TextView headText, descText, timeText;
        private View view;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            headText = itemView.findViewById(R.id.head1);
            descText = itemView.findViewById(R.id.desc1);
            timeText = itemView.findViewById(R.id.time1);
            view = itemView.findViewById(R.id.view1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (noteItemClickListener != null) {
                        noteItemClickListener.itemClicked(getItem(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });
        }
    }
    public interface NoteItemClickListener {
        void itemClicked(Note note, int position);
    }
}
