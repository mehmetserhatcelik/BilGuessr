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

public class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.MyViewHolder> {
    private ArrayList<User> users;
    private Context context;
    private String[] colors = {"#E0B528","#C0C0C0","#CD7F32","#2E4E9B"};

    public LobbyAdapter(ArrayList<User> users, Context context)
    {
        this.users = users;
        this.context = context;
    }
    @NonNull
    @Override
    public LobbyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View v = layoutInflater.inflate(R.layout.player_card,parent,false);
        return new LobbyAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LobbyAdapter.MyViewHolder holder, int position) {

        User user = users.get(position);

        holder.userName.setText(user.getName());

        if(user.getUserPhotoUrl()!=null){
        if(user.getUserPhotoUrl().equals("default"))
            Picasso.get().load(R.drawable.wavy).into(holder.view);
        else
            Picasso.get().load(user.getUserPhotoUrl()).into(holder.view);}

        holder.itemView.setBackgroundColor(Color.parseColor(colors[3]));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName;

        CircleImageView view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            view = itemView.findViewById(R.id.pp);
        }
    }
}
