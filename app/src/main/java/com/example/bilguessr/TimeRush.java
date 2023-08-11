package com.example.bilguessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bilguessr.Models.Photo;
import com.example.bilguessr.databinding.ActivityHotpursuitBinding;
import com.example.bilguessr.databinding.ActivityTimeRushBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TimeRush extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {


    private GoogleMap mMap;



    private FirebaseFirestore fstore;
    private FirebaseAuth auth;


    Timer timer;



    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private int totalPoints;

    List<Photo> photoList;

    private Photo currentPhoto;

    private double longitude;
    private double latitude;
    private ActivityTimeRushBinding binding;

    private ImageView imageView;
    private boolean isTimeFinished;

    private ProgressBar pb;
    private int counter;
    private GameBeginDialog calendarDialog;
    private String ppUrl;
    private String name;
    private int prevp;
    private long record;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimeRushBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideSystemUI();
        ppUrl = getIntent().getStringExtra("pp");

        Picasso.get().load(ppUrl).into(binding.pp);
        name = getIntent().getStringExtra("name");

        firebaseStorage = FirebaseStorage.getInstance();
        fstore = FirebaseFirestore.getInstance();




        totalPoints = getIntent().getIntExtra("point",0);
        binding.textView2.setText(totalPoints+"");

        openDialog();

        isTimeFinished=false;



        pb=binding.pb;
        imageView = binding.imageView;
        photoList = new ArrayList<>();

        counter = 100;

        getImage();

        record = getIntent().getLongExtra("record",0);

        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                while (isTimeFinished==false)
                {
                    try {

                        Thread.sleep(100);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });


                    }catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if( isTimeFinished==true) {

                        Intent intent = new Intent(TimeRush.this, TimeRushEnd.class);
                        int temp = calculateDistance();

                        intent.putExtra("totalpoints",(totalPoints+givePoint(temp)));
                        intent.putExtra("pp",ppUrl);
                        intent.putExtra("name",name);
                        intent.putExtra("record",record);

                        startActivity(intent);
                        finish();}
                }

        };
        t.start();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);



        //Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/bilguessr.appspot.com/o/images%2F01c9e59c-924a-4f38-8e7d-c1fec505fd48.png?alt=media&token=e9af9f53-3027-4b6f-aaa4-9b5a3389725f").into(binding.imageView);


    }

    public void openDialog()
    {
        calendarDialog = new GameBeginDialog(this);
        calendarDialog.show(getSupportFragmentManager(),"Calendar Dialog");
    }
    private void getImage(){

        fstore.collection("Photos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot d: queryDocumentSnapshots) {
                    Photo photo = d.toObject(Photo.class);

                    photoList.add(photo);




                }
                Random random = new Random();
                int i = random.nextInt(photoList.size());
                Photo photo = photoList.get(i);
                Picasso.get().load(photo.getDownloadUrl()).into(binding.imageView);
                currentPhoto = photo;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TimeRush.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }
    public int calculateDistance() {
        final double EARTH_RADIUS_KM = 6371.0;
        double dLat = Math.toRadians(currentPhoto.getLatitude() - latitude);
        double dLon = Math.toRadians(currentPhoto.getLongitude() - longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(currentPhoto.getLatitude())) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance in kilometers
        double distance = EARTH_RADIUS_KM * c;
        System.out.println(distance);
        return (int)((distance)*1000);
    }
    public int givePoint(int distance)
    {
        if(distance>1000)
        {
            return 0;
        }
        return 1000-distance;
    }

    public void pb()
    {
        System.out.println("asd");
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                counter--;
                pb.setProgress(counter);
                if(counter<=0) {
                    timer.cancel();
                    isTimeFinished=true;
                }
            }
        };
        timer.schedule(timerTask,0,1000);

    }

    public void lock(View view)
    {
        if(calculateDistance()>1000)
        {
            counter = counter - 5;
        }
        totalPoints+=givePoint(calculateDistance());
        binding.textView2.setText(""+totalPoints);
        getImage();
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
}