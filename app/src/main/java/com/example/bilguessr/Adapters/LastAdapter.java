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

public class LastAdapter extends RecyclerView.Adapter<LastAdapter.MyViewHolder> {
    private ArrayList<User> users;
    private Context context;

    private String[] colors = {"#539165","#C74545"};

    public LastAdapter(ArrayList<User> users, Context context)
    {
        this.users = users;
        this.context = context;

    }
    @NonNull
    @Override
    public LastAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View v = layoutInflater.inflate(R.layout.leader_board_card,parent,false);
        return new LastAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LastAdapter.MyViewHolder holder, int position) {

        User user = users.get(position);

        holder.userName.setText(user.getName());
        System.out.println(user.getDistance());
        holder.score.setText(""+user.getDistance()+"m");


        if(user.getUserPhotoUrl().equals("default"))
            Picasso.get().load(R.drawable.wavy).into(holder.view);
        else
            Picasso.get().load(user.getUserPhotoUrl()).into(holder.view);
        if(position == getItemCount()-1)
            holder.itemView.setBackgroundColor(Color.parseColor(colors[1]));

        else {
            holder.itemView.setBackgroundColor(Color.parseColor(colors[0]));
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
