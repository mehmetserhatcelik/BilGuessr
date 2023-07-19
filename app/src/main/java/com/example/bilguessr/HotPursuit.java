package com.example.bilguessr;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bilguessr.Models.Photo;
import com.example.bilguessr.databinding.ActivityHotpursuitBinding;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;


import com.example.bilguessr.databinding.ActivityHotpursuitBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HotPursuit extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    ActivityResultLauncher<String> permissionLauncher;

    private GoogleMap mMap;



    private FirebaseFirestore fstore;
    private FirebaseAuth auth;
    private LocationManager locationManager;
    private LocationListener locationListener;

    Bitmap selectedImage;
    Uri imageData;

    Timer timer;

    private int currentQuestionNumber;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private int totalPoints;

    List<Photo> photoList;

    private Photo currentPhoto;

    private double longitude;
    private double latitude;
    private ActivityHotpursuitBinding binding;

    private ImageView imageView;
    private boolean isTimeFinished;
    private boolean isAnswerLocked;
    private ProgressBar pb;
    private int counter;
    private GameBeginDialog calendarDialog;

    int i;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHotpursuitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        firebaseStorage = FirebaseStorage.getInstance();
        fstore = FirebaseFirestore.getInstance();

        currentQuestionNumber=getIntent().getIntExtra("q",1);

        binding.textView.setText(currentQuestionNumber+"/10");

        if(currentQuestionNumber == 10) {
            Intent intent = new Intent(HotPursuit.this, HotPursuitResult.class);
            intent.putExtra("p", totalPoints);
            startActivity(intent);
            finish();
        }
        else if(currentQuestionNumber == 1)
            openDialog();
        else
            pb();
        isTimeFinished=false;
        isAnswerLocked =false;


        pb=binding.pb;
        imageView = binding.imageView;
        photoList = new ArrayList<>();

        counter = 100;

        getImage();

        //Toast.makeText(context, photoList.size()+"", Toast.LENGTH_SHORT).show();
        i=0;

        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                while (isAnswerLocked ==false && isTimeFinished==false)
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
                if(isAnswerLocked == true || isTimeFinished==true) {
                    currentQuestionNumber++;

                    Intent intent = new Intent(HotPursuit.this, HotPursuitResult.class);
                    System.out.println(distance());
                    intent.putExtra("q",currentQuestionNumber);
                    intent.putExtra("d",distance());
                    startActivity(intent);
                    finish();
                }
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

                    Picasso.get().load(photo.getDownloadUrl()).into(binding.imageView);
                    currentPhoto = photo;

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HotPursuit.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });


    }
    public double distance() {
        float[] results = new float[1];
        Location.distanceBetween(currentPhoto.getLatitude(), currentPhoto.getLongitude(),
                latitude, longitude, results);

        System.out.println(results[0]/1000);
        return results[0]/1000;
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
        isAnswerLocked=true;
        timer.cancel();
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