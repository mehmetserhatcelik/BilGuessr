package com.example.bilguessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilguessr.Adapters.LeaderBoardAdapter;
import com.example.bilguessr.Adapters.LobbyAdapter;
import com.example.bilguessr.Models.Photo;
import com.example.bilguessr.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class CreateLobby extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private DatabaseReference database;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().
            getReferenceFromUrl("https://bilguessr-default-rtdb.firebaseio.com/");
    private String name;
    private String pp;
    private String conID;

    private String playerUniqueId;
    private int type;
    private int maxPlayer;
    private int currentPlayer;

    private ArrayList<User> list;
    private String lock;
    private RecyclerView recyclerView;
    private LobbyAdapter myAdapter;
    private TextView textView;
    private TextView textView2;
    private Spinner spinner;
    private boolean isCreate;
    private FirebaseFirestore fstore;
    private ArrayList<Photo> photoList;
    private Photo[] photos;

    boolean showed;
    private boolean entered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        isCreate = getIntent().getBooleanExtra("jc",true);
        fstore = FirebaseFirestore.getInstance();
        photoList = new ArrayList<>();
        photos = new Photo[50];
        name = getIntent().getStringExtra("name");
        pp = getIntent().getStringExtra("pp");
        textView = findViewById(R.id.playerNo);
        textView2 = findViewById(R.id.textView10);
        spinner = findViewById(R.id.ddmenu);
        list = new ArrayList<>();
        currentPlayer = 1;
        recyclerView = findViewById(R.id.recVew);
        myAdapter = new LobbyAdapter( list,CreateLobby.this );
        entered = false;
        recyclerView.setAdapter(myAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        showed = false;

        if(type == 0)
        {
            maxPlayer = 2;

        }else{
            maxPlayer = 5;
        }

        if(isCreate) {

            String[] gameModes = new String[2];
            gameModes[0] = "Duel";
            gameModes[1] = "LastManStanding";
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gameModes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(this);


            lock = "false";
            conID = String.valueOf(System.currentTimeMillis());
            textView2.setText(conID);

            createRoom();

        }else{
            type = getIntent().getIntExtra("type",0);

            if(type==0){
                maxPlayer = 2;
            conID = getIntent().getStringExtra("conID");
            textView2.setText(conID);
            if (MemoryData.getData("player_id", "", this).isEmpty()) {
                playerUniqueId = String.valueOf(System.currentTimeMillis());
                MemoryData.saveData("player_id", playerUniqueId, this);
            } else {
                playerUniqueId = MemoryData.getData("player_id", "", this);
            }
            database = FirebaseDatabase.getInstance().getReference().child("connections");
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    snapshot.child(conID).child(playerUniqueId).child("hp").getRef().setValue(100);
                    snapshot.child(conID).child(playerUniqueId).child("name").getRef().setValue(name);
                    snapshot.child(conID).child(playerUniqueId).child("pp").getRef().setValue(pp);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });}else{
                maxPlayer = 5;
                textView.setText(currentPlayer+"/"+5);
                conID = getIntent().getStringExtra("conID");
                textView2.setText(conID);
                if (MemoryData.getData("player_id", "", this).isEmpty()) {
                    playerUniqueId = String.valueOf(System.currentTimeMillis());
                    MemoryData.saveData("player_id", playerUniqueId, this);
                } else {
                    playerUniqueId = MemoryData.getData("player_id", "", this);
                }
                database = FirebaseDatabase.getInstance().getReference().child("lastconnections");
                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.child(conID).child(playerUniqueId).child("name").getRef().setValue(name);
                        snapshot.child(conID).child(playerUniqueId).child("pp").getRef().setValue(pp);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
        EventChangeListener();
    }
    public void EventChangeListener()
    {
        if(type==0){
        databaseReference.child("connections").child(conID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot players : snapshot.getChildren()) {
                    if(!players.getKey().equals("photos") && !players.getKey().equals("isAvailable") ){
                    String nme = players.child("name").getValue(String.class);
                    String pp = players.child("pp").getValue(String.class);

                    User user = new User(nme,pp);
                    list.add(user);

                    }
                }
                currentPlayer = list.size();
                textView.setText(currentPlayer+"/"+maxPlayer);
                myAdapter.notifyDataSetChanged();
                if(currentPlayer>=maxPlayer)
                {
                    snapshot.child("isAvailable").getRef().setValue(false);
                    if(!showed)
                        showCountdownDialog();
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });}
        else{
            databaseReference.child("lastconnections").child(conID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();

                    for (DataSnapshot players : snapshot.getChildren()) {
                        if(!players.getKey().equals("photos") && !players.getKey().equals("isAvailable") ){
                            String nme = players.child("name").getValue(String.class);
                            String pp = players.child("pp").getValue(String.class);

                            User user = new User(nme,pp);
                            list.add(user);

                        }
                    }
                    currentPlayer = list.size();
                    textView.setText(currentPlayer+"/"+maxPlayer);
                    myAdapter.notifyDataSetChanged();
                    if(currentPlayer>=maxPlayer)
                    {
                        snapshot.child("isAvailable").getRef().setValue(false);
                        if(!showed)
                            showCountdownDialog();
                    }


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getId() == R.id.ddmenu)
        {
            String valueFromSpinner = parent.getItemAtPosition(position).toString();

            if(valueFromSpinner.equals("Duel"))
            {
                type = 0;
                String temp = textView.getText().toString();
                maxPlayer = 2;
                for (int i = 0; i < temp.length(); i++) {
                    if(temp.charAt(i) == '/')
                    {
                        temp = temp.substring(0,i+1)+maxPlayer;
                        break;
                    }
                }
                textView.setText(temp);


            }else{
                type = 1;
                String temp = textView.getText().toString();
                maxPlayer = 5;
                for (int i = 0; i < temp.length(); i++) {
                    if(temp.charAt(i) == '/')
                    {
                        temp = temp.substring(0,i+1)+maxPlayer;
                        break;
                    }
                }
                textView.setText(temp);
            }

            createRoom();
            deleteRoom();
            EventChangeListener();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        type=0;
    }
    private void showCountdownDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_countdown, null);
        showed =true;
        // Create the dialog
        final Dialog countdownDialog = new Dialog(CreateLobby.this);
        countdownDialog.setContentView(dialogView);
        countdownDialog.setCancelable(false);

        // Initialize the TextView for countdown display
        final TextView countdownTextView = dialogView.findViewById(R.id.countdown_text);

        // Set up a CountDownTimer
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                // Update the countdownTextView text
                countdownTextView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                countdownDialog.dismiss();
                if(type ==0)
                {
                Intent intent = new Intent(CreateLobby.this, DuelDeneme.class);

                intent.putExtra("conID", conID);
                intent.putExtra("matched", true);
                intent.putExtra("player", playerUniqueId);
                intent.putExtra("pp", pp);
                intent.putExtra("name",name);
                intent.putExtra("turn", 1);
                intent.putExtra("p1hp", 100);
                startActivity(intent);
                finish();}
                else{
                    Intent intent = new Intent(CreateLobby.this, LastManStanding.class);

                    intent.putExtra("conID", conID);
                    intent.putExtra("matched", true);
                    intent.putExtra("player", playerUniqueId);
                    intent.putExtra("pp", pp);
                    intent.putExtra("name",name);
                    intent.putExtra("turn", 1);

                    startActivity(intent);
                    finish();
                }
            }
        }.start();

        // Show the dialog
        countdownDialog.show();
    }
    private void deleteRoom()
    {

        if(type==1)
        {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("connections");
        if(conID != null && !conID.isEmpty())
        {
            ref.child(conID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // The onDataChange method is triggered when the data at the reference changes.
                    // If the reference exists, dataSnapshot.exists() will be true.
                    if (dataSnapshot.exists()) {
                        // The reference exists.
                        // Remove the reference from the database.
                        ref.child(conID).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    System.out.println("kalk");
                                })
                                .addOnFailureListener(e -> {
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any potential error during the database operation.
                }
            });}}
        else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lastconnections");
            if(conID != null && !conID.isEmpty())
            {
                ref.child(conID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // The onDataChange method is triggered when the data at the reference changes.
                        // If the reference exists, dataSnapshot.exists() will be true.
                        if (dataSnapshot.exists()) {
                            // The reference exists.
                            // Remove the reference from the database.
                            ref.child(conID).removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                    })
                                    .addOnFailureListener(e -> {
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any potential error during the database operation.
                    }
                });}
        }
    }
    private void ImageList(String path)
    {
        fstore.collection("Photos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot d: queryDocumentSnapshots) {
                    Photo photo = d.toObject(Photo.class);
                    photoList.add(photo);
                }
                Random random = new Random();

                for (int j = 0; j < 50 ; j++) {
                    int i = random.nextInt(photoList.size());
                    photos[j]=photoList.get(i);
                }
                for (int i = 0; i < photos.length; i++) {
                    databaseReference.child(path).child(conID).child("photos").child("photo"+i).setValue(photos[i]);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateLobby.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void createRoom()
    {

        entered = false;
        if(type==0)
        {
            if (MemoryData.getData("player_id", "", this).isEmpty()) {
                playerUniqueId = String.valueOf(System.currentTimeMillis());
                MemoryData.saveData("player_id", playerUniqueId, this);
            } else {
                playerUniqueId = MemoryData.getData("player_id", "", this);
            }



            database = FirebaseDatabase.getInstance().getReference().child("connections");
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!entered){
                        snapshot.child(conID).child("isAvailable").getRef().setValue(true);
                        ImageList("connections");
                        snapshot.child(conID).child(playerUniqueId).child("hp").getRef().setValue(100);
                        snapshot.child(conID).child(playerUniqueId).child("name").getRef().setValue(name);
                        snapshot.child(conID).child(playerUniqueId).child("pp").getRef().setValue(pp);

                        entered = true;}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }else{
            if (MemoryData.getData("player_id", "", this).isEmpty()) {
                playerUniqueId = String.valueOf(System.currentTimeMillis());
                MemoryData.saveData("player_id", playerUniqueId, this);
            } else {
                playerUniqueId = MemoryData.getData("player_id", "", this);
            }


            database = FirebaseDatabase.getInstance().getReference().child("lastconnections");
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!entered){
                        snapshot.child(conID).child("isAvailable").getRef().setValue(true);
                        ImageList("lastconnections");
                        snapshot.child(conID).child(playerUniqueId).child("name").getRef().setValue(name);
                        snapshot.child(conID).child(playerUniqueId).child("pp").getRef().setValue(pp);

                        entered = true;}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

    }

}