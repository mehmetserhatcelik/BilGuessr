package com.example.bilguessr.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilguessr.Models.User;
import com.example.bilguessr.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.MyViewHolder>{

    private ArrayList<User> users;
    private Context context;
    private String gameMode;
    private String[] colors = {"#E0B528","#C0C0C0","#CD7F32","#2E4E9B"};

    public LeaderBoardAdapter(ArrayList<User> users, Context context, String gameMode)
    {
        this.users = users;
        this.context = context;
        this.gameMode=gameMode;
    }
    @NonNull
    @Override
    public LeaderBoardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View v = layoutInflater.inflate(R.layout.leader_board_card,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderBoardAdapter.MyViewHolder holder, int position) {

        User user = users.get(position);

        holder.userName.setText(user.getName());
        if(gameMode.equals("HotPursuit"))
            holder.score.setText(""+user.getHotPursuitRecord());
        else if(gameMode.equals("TimeRush"))
            holder.score.setText(""+user.getTimeRushRecord());

        if(user.getUserPhotoUrl().equals("default"))
            Picasso.get().load(R.drawable.wavy).into(holder.view);
        else
            Picasso.get().load(user.getUserPhotoUrl()).into(holder.view);
        if(position == 0)
            holder.itemView.setBackgroundColor(Color.parseColor(colors[0]));
        else if(position == 1)
            holder.itemView.setBackgroundColor(Color.parseColor(colors[1]));
        else if(position == 2)
            holder.itemView.setBackgroundColor(Color.parseColor(colors[2]));
        else {
            holder.itemView.setBackgroundColor(Color.parseColor(colors[3]));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName;
        TextView score;
        CircleImageView view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            score = itemView.findViewById(R.id.score);
            view = itemView.findViewById(R.id.pp);
        }
    }
}
