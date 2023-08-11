package com.example.bilguessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilguessr.Models.Photo;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class LastManStanding extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().
            getReferenceFromUrl("https://bilguessr-default-rtdb.firebaseio.com/");

    private String playerUniqueId;


    private Photo[] photos;
    private String connectionId ;
    private String connectionUniqueId;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private boolean matched;
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
    private boolean entered;
    private  String playerName;
    private String playerpp;

    private  ImageView p1pp;
    private ImageView p2pp;
    private  ImageView p3pp;
    private ImageView p4pp;
    private  ImageView p5pp;

    private  boolean opponentLock ;

    private int turnNo;
    private int children;
    private int playercount = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_man_standing);
        turnNo=getIntent().getIntExtra("turn",0);

        p1pp = findViewById(R.id.pp);
        p2pp = findViewById(R.id.pp1);
        p3pp = findViewById(R.id.pp3);
        p4pp = findViewById(R.id.pp4);
        p5pp = findViewById(R.id.pp5);

        p1pp.setVisibility(View.INVISIBLE);
        p2pp.setVisibility(View.INVISIBLE);
        p3pp.setVisibility(View.INVISIBLE);
        p4pp.setVisibility(View.INVISIBLE);
        p5pp.setVisibility(View.INVISIBLE);

        photos = new Photo[50];


        pb=findViewById(R.id.pb);

        fstore = FirebaseFirestore.getInstance();
        isTimeFinished=false;

        imageView = findViewById(R.id.imageView);
        photoList = new ArrayList<>();

        connectionId= getIntent().getStringExtra("conID");
        playerUniqueId = getIntent().getStringExtra("player");

        matched = getIntent().getBooleanExtra("matched",false);

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
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lastconnections").child(connectionId);
                        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                children = (int) task.getResult().getChildrenCount();
                            }
                        });
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

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lastconnections").child(conId);
                        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                children = (int) task.getResult().getChildrenCount();
                            }
                        });
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
                    Intent intent = new Intent(LastManStanding.this, LastManResultScreen.class);
                    intent.putExtra("children",children);
                    intent.putExtra("conID",connectionId);
                    intent.putExtra("player",playerUniqueId);
                    intent.putExtra("pp",playerpp);
                    intent.putExtra("name",playerName);
                    intent.putExtra("photolat",currentPhoto.getLatitude());
                    intent.putExtra("photolong",currentPhoto.getLongitude());
                    startActivity(intent);
                    finish();
                }}

        };
        t.start();


        matched = getIntent().getBooleanExtra("matched",false);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        MyConstents info = new MyConstents();


        playerName = getIntent().getStringExtra("name");
        playerpp = String.valueOf(getIntent().getStringExtra("pp")) ;
        if(playerpp.equals("default"))
            Picasso.get().load(R.drawable.wavy).into(p1pp);
        else
            Picasso.get().load(playerpp).into(p1pp);
        p1pp.setVisibility(View.VISIBLE);
        isAnswerLocked =info.isLockClicked();





        if(!matched){
            if (MemoryData.getData("player_id", "", this).isEmpty()) {
                playerUniqueId = String.valueOf(System.currentTimeMillis());
                MemoryData.saveData("player_id", playerUniqueId, this);
            } else {
                playerUniqueId = MemoryData.getData("player_id", "", this);
            }
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Waiting for Opponent");
            progressDialog.show();
            databaseReference.child("lastconnections").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren())
                        {
                            for(DataSnapshot snapshots : snapshot.getChildren())
                            {
                                if(snapshots.child("isAvailable").getValue(Boolean.class))
                                {
                                    connectionId =snapshots.getKey();
                                    databaseReference.child("lastconnections").child(connectionId).child(playerUniqueId);
                                    databaseReference.child("lastconnections").child(connectionId).child(playerUniqueId).child("name").setValue(playerName);
                                    databaseReference.child("lastconnections").child(connectionId).child(playerUniqueId).child("pp").setValue(playerpp);
                                    entered = true;
                                    break;

                                }
                                if(!entered)
                                {
                                    connectionUniqueId  = String.valueOf(System.currentTimeMillis());
                                    connectionId =connectionUniqueId;
                                    ImageList();
                                    databaseReference.child("lastconnections").child(connectionUniqueId).child("isAvailable").setValue(true);
                                    databaseReference.child("lastconnections").child(connectionId).child(playerUniqueId).child("name").setValue(playerName);
                                    databaseReference.child("lastconnections").child(connectionUniqueId).child(playerUniqueId).child("pp").setValue(playerpp);
                                    entered = true;
                                }
                            }
                        }else{
                            connectionUniqueId  = String.valueOf(System.currentTimeMillis());
                            connectionId =connectionUniqueId;
                            ImageList();
                            databaseReference.child("lastconnections").child(connectionUniqueId).child("isAvailable").setValue(true);
                            databaseReference.child("lastconnections").child(connectionId).child(playerUniqueId).child("name").setValue(playerName);
                            databaseReference.child("lastconnections").child(connectionUniqueId).child(playerUniqueId).child("pp").setValue(playerpp);

                            entered = true;
                        }
                        if(entered)
                        {

                            if(snapshot.child(connectionId).getChildrenCount()==2+playercount)
                            {

                                matched=true;
                                databaseReference.child("lastconnections").child(connectionId).child("isAvailable").setValue(false);
                                if (progressDialog.isShowing()) {

                                    progressDialog.dismiss();
                                    pb();
                                    getImage();
                                    turnNo++;

                                }

                            }
                            if(matched){
                            int i = 0;
                            for(DataSnapshot players : snapshot.child(connectionId).getChildren())
                            {
                                if(!players.getKey().equals(playerUniqueId) && !players.getKey().equals("isAvailable") && !players.getKey().equals("removed")&& !players.getKey().equals("photos")){
                                    String pp = String.valueOf(players.child("pp").getValue());

                                    if(i == 0)
                                    {
                                        if(pp.equals("default"))
                                            Picasso.get().load(R.drawable.wavy).into(p2pp);
                                        else
                                            Picasso.get().load(playerpp).into(p2pp);
                                        p2pp.setVisibility(View.VISIBLE);
                                    }
                                    if(i == 1)
                                    {
                                        if(pp.equals("default"))
                                            Picasso.get().load(R.drawable.wavy).into(p3pp);
                                        else
                                            Picasso.get().load(playerpp).into(p3pp);
                                        p3pp.setVisibility(View.VISIBLE);
                                    }
                                    if(i == 2)
                                    {
                                        if(pp.equals("default"))
                                            Picasso.get().load(R.drawable.wavy).into(p4pp);
                                        else
                                            Picasso.get().load(playerpp).into(p4pp);
                                        p4pp.setVisibility(View.VISIBLE);
                                    }
                                    if(i == 3)
                                    {
                                        if(pp.equals("default"))
                                            Picasso.get().load(R.drawable.wavy).into(p5pp);
                                        else
                                            Picasso.get().load(playerpp).into(p5pp);
                                        p5pp.setVisibility(View.VISIBLE);
                                    }
                                    i++;
                                }}

                            }


                        }
                    }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            }else{
            turnNo++;
            pb();
            getImage();



            String conId= getIntent().getStringExtra("conID");
            String player = getIntent().getStringExtra("player");


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lastconnections").child(conId);

            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful() && task.getResult().exists())
                    {
                        int i = 0;
                        for(DataSnapshot players : task.getResult().getChildren())
                        {
                            if(!players.getKey().equals(player)&&!players.getKey().equals("isAvailable")&& !players.getKey().equals("removed")&& !players.getKey().equals("photos")){
                                String pp = String.valueOf(players.child("pp").getValue());

                                if(i == 0)
                                {
                                    if(pp.equals("default"))
                                        Picasso.get().load(R.drawable.wavy).into(p2pp);
                                    else
                                        Picasso.get().load(playerpp).into(p2pp);
                                    p2pp.setVisibility(View.VISIBLE);
                                }
                                if(i == 1)
                                {
                                    if(pp.equals("default"))
                                        Picasso.get().load(R.drawable.wavy).into(p3pp);
                                    else
                                        Picasso.get().load(playerpp).into(p3pp);
                                    p3pp.setVisibility(View.VISIBLE);
                                }
                                if(i == 2)
                                {
                                    if(pp.equals("default"))
                                        Picasso.get().load(R.drawable.wavy).into(p4pp);
                                    else
                                        Picasso.get().load(playerpp).into(p4pp);
                                    p4pp.setVisibility(View.VISIBLE);
                                }
                                if(i == 3)
                                {
                                    if(pp.equals("default"))
                                        Picasso.get().load(R.drawable.wavy).into(p5pp);
                                    else
                                        Picasso.get().load(playerpp).into(p5pp);
                                    p5pp.setVisibility(View.VISIBLE);
                                }
                                i++;
                            }}

                    }
                    }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }


    }
    private void ImageList()
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
                    databaseReference.child("lastconnections").child(connectionId).child("photos").child("photo"+i).setValue(photos[i]);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LastManStanding.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void getImage(){

        databaseReference.child("lastconnections").child(connectionId).child("photos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentPhoto = snapshot.child("photo"+turnNo).getValue(Photo.class);
                if(currentPhoto.getDownloadUrl()!=null)
                    Picasso.get().load(currentPhoto.getDownloadUrl()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void pb()
    {
        int period;


        if(turnNo==1)
             period =10000;
        else
            period = 100;

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
        timer.schedule(timerTask,0,period);

    }

    public void lock(View view)
    {
        /*System.out.println(turnNo);
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
        }*/

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