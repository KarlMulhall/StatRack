package com.example.StatRack.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.StatRack.R;
import com.example.StatRack.models.Player;

public class PlayerViewHolder extends RecyclerView.ViewHolder {

    public TextView nameView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView positionView;

    public PlayerViewHolder(View itemView) {
        super(itemView);

        nameView = itemView.findViewById(R.id.playerName);
        authorView = itemView.findViewById(R.id.playerAuthor);
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.playerNumStars);
        positionView = itemView.findViewById(R.id.playerPosition);
    }

    public void bindToPlayer(Player player, View.OnClickListener starClickListener) {
        nameView.setText(player.name);
        authorView.setText(player.author);
        numStarsView.setText(String.valueOf(player.starCount));
        positionView.setText(player.position);

        starView.setOnClickListener(starClickListener);
    }
}
