package com.example.StatRack.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.StatRack.R;
import com.example.StatRack.models.Note;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public TextView descriptionView;

    public NoteViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.noteTitle);
        authorView = itemView.findViewById(R.id.noteAuthor);
        descriptionView = itemView.findViewById(R.id.noteDescription);
    }

    public void bindToNote(Note note, View.OnClickListener starClickListener) {
        titleView.setText(note.title);
        authorView.setText(note.author);
        descriptionView.setText(note.description);
    }
}
