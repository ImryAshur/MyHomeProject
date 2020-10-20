package com.example.project.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.project.Objects.MyEvent;
import com.example.project.R;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.ArrayList;


public class Adapter_Event extends RecyclerView.Adapter<Adapter_Event.ViewHolder> {

    private ArrayList<com.github.sundeepk.compactcalendarview.domain.Event> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private EventItemClickListener eventItemClickListener;

    
    // data is passed into the constructor
    public Adapter_Event(Context context, ArrayList<com.github.sundeepk.compactcalendarview.domain.Event> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    public void setClickListeners(EventItemClickListener eventItemClickListener) {
        this.eventItemClickListener = eventItemClickListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_event, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        MyEvent myEvent = (MyEvent) mData.get(position);

        String familyLink = "https://banner2.cleanpng.com/20190201/tgy/kisspng-thrybergh-primary-school-ygk-family-child-school-parents-clipart-black-and-white-library-r-5c54bb88d74735.4146544615490569048818.jpg";
        String acivityLink = "https://www.nicepng.com/png/full/355-3557565_interactive-whiteboard-activities-learning-activity-icon.png";
        String pickLink = "https://toppng.com/uploads/preview/the-white-rabbit-clip-art-i-m-late-for-a-very-important-date-115630950721ncisqrs8o.png";
        String otherLink = "https://banner2.cleanpng.com/20180301/dhq/kisspng-illustration-men-go-to-work-5a97f83c538861.2988258615199089243422.jpg";


        if (myEvent.getEventType().charAt(0) == 'F') {
            glide(familyLink,holder.event_IMG_icon);
        }
        else if (myEvent.getEventType().charAt(0) == 'A') {
            glide(acivityLink,holder.event_IMG_icon);
        }
        else if (myEvent.getEventType().charAt(0) == 'P') {
            glide(pickLink,holder.event_IMG_icon);
        }
        else {
            glide(otherLink,holder.event_IMG_icon);
        }

        holder.event_LBL_key.setText(myEvent.getEventType());
        holder.event_LBL_name.setText(myEvent.getParticipants());
        holder.event_LBL_reps.setText(myEvent.getStartTime());

    }

    private void glide(String link, ShapeableImageView event_img_icon) {
        Glide
                .with(context)
                .load(link)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(event_img_icon);
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<Event> getmData() {
        return mData;
    }

    public void setmData(ArrayList<Event> mData) {
        this.mData = mData;
    }

    // convenience method for getting data at click position
    MyEvent getItem(int position) {
        return (MyEvent) mData.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ShapeableImageView event_IMG_icon;
        private TextView event_LBL_key;
        private TextView event_LBL_reps;
        private TextView event_LBL_name;

        ViewHolder(View itemView) {
            super(itemView);
            event_IMG_icon = itemView.findViewById(R.id.event_IMG_icon);
            event_LBL_key = itemView.findViewById(R.id.event_LBL_key);
            event_LBL_reps = itemView.findViewById(R.id.event_LBL_reps);
            event_LBL_name = itemView.findViewById(R.id.event_LBL_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (eventItemClickListener != null) {
                        eventItemClickListener.itemClicked(getItem(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });
        }

    }

    public interface EventItemClickListener {
        void itemClicked(MyEvent myEvent, int position);
    }
}

