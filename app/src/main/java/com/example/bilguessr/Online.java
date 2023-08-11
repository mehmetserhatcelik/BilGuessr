package com.example.bilguessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.bilguessr.Models.Photo;
import com.example.bilguessr.Models.Room;
import com.example.bilguessr.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Online extends AppCompatActivity {


    private boolean isGameOver;
    private FirebaseFirestore fstore;
    private boolean isAvailableRoom;

    private ProgressDialog pb;

    private User user;
    private FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);


        isGameOver = true;
        fstore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        fuser =auth.getCurrentUser();

        DocumentReference df = fstore.collection("Users").document(fuser.getUid());

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    user = new User(documentSnapshot.getString("name"),documentSnapshot.getString("userPhotoUrl"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Online.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        pb = new ProgressDialog(this);
        pb.setCancelable(false);
        pb.setMessage("Waiting for Opponent!");
        pb.show();


        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                while (isGameOver)
                {
                    try {

                        Thread.sleep(10);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Room room = isRoomExist();

                                if(!isAvailableRoom)
                                {
                                    DocumentReference df = fstore.collection("Rooms").document(fuser.getUid());
                                    Room r = new Room(fuser.getUid(),"Duel",2);
                                    r.addPlayer(user);
                                    Map<String,Object> roomInfo = new HashMap<>();

                                    roomInfo.put("ID",fuser.getUid());
                                    roomInfo.put("gameMode","Duel");
                                    roomInfo.put("maximumNumberOfPlayer",2);
                                    roomInfo.put("currentPlayer",r.getCurrentPlayerNo());

                                    df.set(roomInfo);
                                }
                                else{
                                    room.addPlayer(user);
                                    fstore.collection("Rooms").document(room.getID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                }
                            }
                        });


                    }catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        };
        t.start();
    }

    public Room isRoomExist()
    {
        final Room[] availableRoom = new Room[1];
        fstore.collection("Rooms").whereEqualTo("gameMode","Duel").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot d: queryDocumentSnapshots) {

                    Room room = d.toObject(Room.class);

                    if(room.isAvailable()) {
                        isAvailableRoom =true;
                        availableRoom[0] = room;
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Online.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });
        return availableRoom[0];
    }
}