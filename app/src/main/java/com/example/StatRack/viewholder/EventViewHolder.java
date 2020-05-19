package com.example.StatRack.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.StatRack.R;
import com.example.StatRack.models.Event;

public class EventViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView locationView;
    public TextView dateView;
    public TextView timeView;

    public EventViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.eventTitle);
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.playerNumStars);
        locationView = itemView.findViewById(R.id.eventLocation);
        dateView = itemView.findViewById(R.id.eventDate);
        timeView = itemView.findViewById(R.id.eventTime);
    }

    public void bindToEvent(Event event, View.OnClickListener starClickListener) {
        titleView.setText(event.title);
        numStarsView.setText(String.valueOf(event.starCount));
        locationView.setText(event.location);
        dateView.setText(event.date);
        timeView.setText(event.time);

        starView.setOnClickListener(starClickListener);
    }
}

