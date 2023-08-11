package com.example.bilguessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilguessr.Models.Photo;
import com.example.bilguessr.Models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Duel extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener{
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().
            getReferenceFromUrl("https://bilguessr-default-rtdb.firebaseio.com/");
    ValueEventListener wonEventListener;
    private LinearLayout player1Layout, player2Layout;
    private TextView player1TV, player2TV;
    private String playerUniqueId;
    private boolean opponentFound = false;
    private String opponentUniqueId;
    private String status = "matching";
    private String connectionId ;
    private String connectionUniqueId;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private GoogleMap mMap;
    Timer timer;
    private ProgressBar pb;
    private int counter;
    private boolean isTimeFinished;
    private boolean isAnswerLocked;
    private double longitude;
    private double latitude;
    private ImageView imageView;
    private List<Photo> photoList;

    private Photo currentPhoto;
    private boolean matched;
    private  String playerName;
    private String playerpp;
    private int hp;
    private String pp;
    private  ImageView p1pp;
    private ImageView p2pp;
    private  boolean opponentLock ;

    private int turnNo;
    private TextView p1can;
    private TextView p2can;
    private long p1hp;
    private long p2hp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel);
        turnNo=getIntent().getIntExtra("turn",0);
        player1Layout = findViewById(R.id.player1Layout);
        player2Layout = findViewById(R.id.player2Layout);
        player1TV = findViewById(R.id.player1TV);
        player2TV = findViewById(R.id.player2TV);
        p1pp = findViewById(R.id.player1pp);
        p2pp = findViewById(R.id.player2pp);
        p1can = findViewById(R.id.pcan1);
        p2can = findViewById(R.id.pcan2);


        pb=findViewById(R.id.pb);

        fstore = FirebaseFirestore.getInstance();
        isTimeFinished=false;

        imageView = findViewById(R.id.imageView);
        photoList = new ArrayList<>();
        matched = getIntent().getBooleanExtra("matched",false);
        connectionId= getIntent().getStringExtra("conID");
        playerUniqueId = getIntent().getStringExtra("player");
        opponentUniqueId = getIntent().getStringExtra("opponent");


        counter = 100;
        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                while ((isAnswerLocked ==false || opponentLock==false) && isTimeFinished==false)
                {
                    try {

                        Thread.sleep(10);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });


                    }catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if((isAnswerLocked==true&&opponentLock == true) || isTimeFinished == true) {

                    if(turnNo==1)
                    {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("connections").child(connectionId);
                    ref.child(playerUniqueId).child("distance").setValue(calculateDistance(latitude,longitude))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Data updated successfully
                                    // Do something on success if needed
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to update data
                                    // Handle the error
                                }
                            });
                    }
                    else{

                        String conId= getIntent().getStringExtra("conID");
                        String player = getIntent().getStringExtra("player");
                        String opponent = getIntent().getStringExtra("opponent");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("connections").child(conId);
                        ref.child(player).child("distance").setValue(calculateDistance(latitude,longitude))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Data updated successfully
                                        // Do something on success if needed
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to update data
                                        // Handle the error
                                    }
                                });

                    }
                    Intent intent = new Intent(Duel.this, DuelResultScreen.class);
                    intent.putExtra("conID",connectionId);
                    intent.putExtra("matched",matched);
                    intent.putExtra("opponent",opponentUniqueId);
                    intent.putExtra("player",playerUniqueId);
                    intent.putExtra("playerpp",playerpp);
                    intent.putExtra("p1name",playerName);
                    intent.putExtra("turn",turnNo);
                    intent.putExtra("p1hp",p1hp);
                    intent.putExtra("p2hp",p2hp);
                    intent.putExtra("photolat",currentPhoto.getLatitude());
                    intent.putExtra("photolong",currentPhoto.getLongitude());
                    startActivity(intent);
                    finish();
                }}

        };
        t.start();






        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        MyConstents info = new MyConstents();

        info.getPlayerName(player1TV);
        playerName = getIntent().getStringExtra("name");
        playerpp = getIntent().getStringExtra("pp")+"";
        if(playerpp.equals("default"))
            Picasso.get().load(R.drawable.wavy).into(p1pp);
        else
            Picasso.get().load(playerpp).into(p1pp);
        isAnswerLocked =info.isLockClicked();
        hp = info.getHp();


        if (MemoryData.getData("player_id", "", this).isEmpty()) {
            playerUniqueId = String.valueOf(System.currentTimeMillis());
            MemoryData.saveData("player_id", playerUniqueId, this);
        } else {
            playerUniqueId = MemoryData.getData("player_id", "", this);
        }
        player1TV.setText(playerName);
        if(!matched){
            p1hp = 100;
            p1can.setText(p1hp+"");
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Waiting for Opponent");
            progressDialog.show();
        databaseReference.child("connections").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                // check if opponent found or not. If not then look for the opponent
                if (!opponentFound) {


                    // checking if there are others in the firebase realtime database
                    if (snapshot.hasChildren()) {


                        // checking all connections if other users are also waiting for a user to play the match
                        for (DataSnapshot connections : snapshot.getChildren()) {

                            // getting connection unique id
                            String conId = connections.getKey();

                            // 2 players are required to play the game.
                            // If getPlayersCount is 1 it means other player is waiting for a opponent to play the game.
                            // else if getPlayersCount is 2 it means this connection has completed with 2 players.
                            int getPlayersCount = (int) connections.getChildrenCount();

                            // after created a new connection waiting for other to join
                            if (status.equals("waiting")) {


                                // if getPlayersCount is 2 means other player joined the match
                                if (getPlayersCount == 2) {



                                    // true when player found in connections
                                    boolean playerFound = false;

                                    // getting players in connection
                                    for (DataSnapshot players : connections.getChildren()) {

                                        String getPlayerUniqueId = players.getKey();

                                        // check if player id match with user who created connection(this user). If match then get opponent details
                                        if (getPlayerUniqueId.equals(playerUniqueId)) {

                                            playerFound = true;
                                        } else if (playerFound) {

                                            String getOpponentPlayerName = players.child("player_name").getValue(String.class);
                                            String getOpponenPP = players.child("pp").getValue(String.class);
                                            String getLocked = players.child("lock").getValue()+"";
                                            opponentUniqueId = players.getKey();

                                            // set opponent playername to the TextView
                                            p2hp = 100;
                                            p2can.setText(p2hp+"");
                                            player2TV.setText(getOpponentPlayerName);
                                            if(getOpponenPP!=null)
                                            {if(getOpponenPP.equals("default"))
                                                Picasso.get().load(R.drawable.wavy).into(p2pp);
                                            else
                                                Picasso.get().load(playerpp).into(p2pp);}
                                            else{
                                                Picasso.get().load(R.drawable.wavy).into(p2pp);
                                            }
                                            if(getLocked.equals("true"))
                                                opponentLock = true;
                                            else
                                                opponentLock = false;


                                            // assigning connection id
                                            connectionId = conId;
                                            opponentFound = true;

                                            // adding turns listener and won listener to the database reference.
                                            //databaseReference.child("turns").child(connectionId).addValueEventListener(turnsEventListener);
                                            //databaseReference.child("won").child(connectionId).addValueEventListener(wonEventListener);

                                            // hide progress dialog if showing
                                            if (progressDialog.isShowing()) {

                                                progressDialog.dismiss();
                                                getImage();
                                                pb();
                                                matched = true;
                                                turnNo++;
                                            }

                                            // once the connection has made remove connectionlistener from Database Reference
                                            databaseReference.child("connections").removeEventListener(this);

                                        }
                                    }
                                }
                            }

                            // in case user has not created the connection/room because of other rooms are available to join
                            else {


                                // checking if the connection has 1 player and need 1 more player to play the match then join this connection
                                if (getPlayersCount == 1) {

                                    boolean samePlayer = false;
                                    // getting both players
                                    for (DataSnapshot players : connections.getChildren()) {
                                        if (players.getKey().equals(playerUniqueId)) {

                                            samePlayer = true;
                                            break;
                                        }
                                    }

                                    if (!samePlayer) {
                                        // add player to the connection

                                        connections.child(playerUniqueId).child("player_name").getRef().setValue(playerName);
                                        connections.child(playerUniqueId).child("pp").getRef().setValue(playerpp);
                                        connections.child(playerUniqueId).child("lock").getRef().setValue(isAnswerLocked);

                                        // getting both players
                                        for (DataSnapshot players : connections.getChildren()) {

                                            String getOpponentName = players.child("player_name").getValue(String.class);
                                            String getOpponenPP = players.child("pp").getValue(String.class);
                                            String getLock = players.child("lock").getValue(String.class);
                                            opponentUniqueId = players.getKey();

                                            // first turn will be of who created the connection / room
                                            //playerTurn = opponentUniqueId;
                                            //applyPlayerTurn(playerTurn);

                                            // setting playername to the TextView
                                            p2hp = 100;
                                            p2can.setText(p2hp+"");
                                            player2TV.setText(getOpponentName);
                                            if(getOpponenPP.equals("default"))
                                                Picasso.get().load(R.drawable.wavy).into(p2pp);
                                            else
                                                Picasso.get().load(playerpp).into(p2pp);
                                            if(getLock.equals("true"))
                                                opponentLock = true;
                                            else
                                                opponentLock = false;

                                            // assigning connection id
                                            connectionId = conId;
                                            opponentFound = true;

                                            // adding turns listener and won listener to the database reference.
                                            //databaseReference.child("turns").child(connectionId).addValueEventListener(turnsEventListener);
                                            //databaseReference.child("won").child(connectionId).addValueEventListener(wonEventListener);

                                            // hide progress dialog if showing
                                            if (progressDialog.isShowing()) {

                                                progressDialog.dismiss();
                                                getImage();
                                                pb();
                                                matched = true;
                                                turnNo++;
                                            }

                                            // once the connection has made remove connection listener from Database Reference
                                            databaseReference.child("connections").removeEventListener(this);

                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        // check if opponent is not found and user is not waiting for the opponent anymore then create a new connection
                        if (!opponentFound && !status.equals("waiting")) {


                            // generating unique id for the connection
                            connectionUniqueId  = String.valueOf(System.currentTimeMillis());

                            // adding first player to the connection and waiting for other to complete the connection and play the game
                            snapshot.child(connectionUniqueId).child(playerUniqueId).child("player_name").getRef().setValue(playerName);
                            snapshot.child(connectionUniqueId).child(playerUniqueId).child("pp").getRef().setValue(playerpp+"");
                            snapshot.child(connectionUniqueId).child(playerUniqueId).child("lock").getRef().setValue(isAnswerLocked+"");

                            status = "waiting";
                        }
                    }

                    // if there is no connection available in the firebase database then create a new connection.
                    // It is like creating a room and waiting for other players to join the room.
                    else {

                        // generating unique id for the connection
                        connectionUniqueId = String.valueOf(System.currentTimeMillis());

                        // adding first player to the connection and waiting for other to complete the connection and play the game
                        snapshot.child(connectionUniqueId).child(playerUniqueId).child("player_name").getRef().setValue(playerName);
                        snapshot.child(connectionUniqueId).child(playerUniqueId).child("pp").getRef().setValue(playerpp+"");
                        snapshot.child(connectionUniqueId).child(playerUniqueId).child("lock").getRef().setValue(isAnswerLocked+"");

                        status = "waiting";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });}else{
            turnNo++;
            pb();
            getImage();
            p1hp = getIntent().getLongExtra("p1hp",100);
            p2hp = getIntent().getLongExtra("p2hp",100);
            p1can.setText(p1hp+"");
            p2can.setText(p2hp+"");

            String conId= getIntent().getStringExtra("conID");
            String player = getIntent().getStringExtra("player");
            String opponent = getIntent().getStringExtra("opponent");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("connections").child(conId);

            ref.child(opponent).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        if(task.getResult().exists())
                        {
                            DataSnapshot snapshot = task.getResult();
                            String nameOppo = String.valueOf(snapshot.child("player_name").getValue());
                            String ppOppo = String.valueOf(snapshot.child("pp").getValue());
                            player2TV.setText(nameOppo);
                            if(ppOppo.equals("default"))
                                Picasso.get().load(R.drawable.wavy).into(p2pp);
                            else
                                Picasso.get().load(playerpp).into(p2pp);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });



        }


    }private void getImage(){

        fstore.collection("Photos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot d: queryDocumentSnapshots) {
                    Photo photo = d.toObject(Photo.class);

                    photoList.add(photo);

                    Picasso.get().load(photo.getDownloadUrl()).into(imageView);
                    currentPhoto = photo;

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Duel.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void pb()
    {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                counter--;
                pb.setProgress(counter);
                if(counter==0) {
                    timer.cancel();
                    isTimeFinished=true;
                }
            }
        };
        timer.schedule(timerTask,0,100);

    }

    public void lock(View view)
    {
        System.out.println(turnNo);
        if(turnNo!=1)
        {

            String conId= getIntent().getStringExtra("conID");
            String player = getIntent().getStringExtra("player");
            String opponent = getIntent().getStringExtra("opponent");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("connections").child(conId);

        isAnswerLocked=true;
        ref.child(player).child("lock").setValue("true")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data updated successfully
                        // Do something on success if needed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to update data
                        // Handle the error
                    }
                });}
        else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("connections").child(connectionId);

            isAnswerLocked=true;
            ref.child(playerUniqueId).child("lock").setValue("true")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Data updated successfully
                            // Do something on success if needed
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to update data
                            // Handle the error
                        }
                    });
        }

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        LatLng bilkent = new LatLng(39.87462344844274, 32.747621680995884);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bilkent,15));
    }
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear();
        latitude = latLng.latitude;
        longitude = latLng.longitude;

        mMap.addMarker(new MarkerOptions().position(latLng));

    }
    public int calculateDistance(double latitude,double longitude) {
        final double EARTH_RADIUS_KM = 6371.0;
        double dLat = Math.toRadians(currentPhoto.getLatitude() - latitude);
        double dLon = Math.toRadians(currentPhoto.getLongitude() - longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(currentPhoto.getLatitude())) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance in kilometers
        double distance = EARTH_RADIUS_KM * c;

        return (int)((distance)*1000);
    }
}