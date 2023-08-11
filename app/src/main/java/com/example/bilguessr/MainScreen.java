package com.example.bilguessr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bilguessr.profilesettings.ProfileSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainScreen extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().
            getReferenceFromUrl("https://bilguessr-default-rtdb.firebaseio.com/");
    private FirebaseFirestore fstore;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Intent DuelIntent;
    private Intent HotPursuitIntent ;
    private Intent TimeRushIntent ;
    private Intent CreateLobbyIntent ;
    private Intent SettingsIntent ;
    private String lobbyID;
    private EditText lobbyIDet;
    private Intent JoinLobbyIntent ;
    private Intent LastManIntent ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        lobbyIDet = findViewById(R.id.lobbyID);
        DuelIntent =new Intent(MainScreen.this, DuelDeneme.class);
        TimeRushIntent =new Intent(MainScreen.this, TimeRush.class);
        CreateLobbyIntent =new Intent(MainScreen.this, CreateLobby.class);
        SettingsIntent =new Intent(MainScreen.this, ProfileSetting.class);
        JoinLobbyIntent =new Intent(MainScreen.this, CreateLobby.class);
        fstore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        HotPursuitIntent = new Intent(MainScreen.this, HotPursuit.class);
        LastManIntent = new Intent(MainScreen.this,LastManStanding.class);


        fstore.collection("Users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {

                    DocumentSnapshot documentSnapshot =task.getResult();

                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        DuelIntent.putExtra("name",documentSnapshot.getString("name"));
                        DuelIntent.putExtra("pp",documentSnapshot.getString("userPhotoUrl"));


                        DuelIntent.putExtra("conID","");
                        DuelIntent.putExtra("player","0");
                        DuelIntent.putExtra("opponent","0");

                        LastManIntent.putExtra("name",documentSnapshot.getString("name"));
                        LastManIntent.putExtra("pp",documentSnapshot.getString("userPhotoUrl"));


                        LastManIntent.putExtra("conID","");
                        LastManIntent.putExtra("player","0");
                        LastManIntent.putExtra("matched",false);
                        LastManIntent.putExtra("turn",0);

                        HotPursuitIntent.putExtra("name",documentSnapshot.getString("name"));
                        HotPursuitIntent.putExtra("pp",documentSnapshot.getString("userPhotoUrl"));
                        HotPursuitIntent.putExtra("record",(long)documentSnapshot.get("hotPursuitRecord"));

                        TimeRushIntent.putExtra("name",documentSnapshot.getString("name"));
                        TimeRushIntent.putExtra("pp",documentSnapshot.getString("userPhotoUrl"));
                        TimeRushIntent.putExtra("record",(long)documentSnapshot.get("timeRushRecord"));

                        CreateLobbyIntent.putExtra("name",documentSnapshot.getString("name"));
                        CreateLobbyIntent.putExtra("pp",documentSnapshot.getString("userPhotoUrl"));

                        SettingsIntent.putExtra("name",documentSnapshot.getString("name"));
                        SettingsIntent.putExtra("pp",documentSnapshot.getString("userPhotoUrl"));

                        JoinLobbyIntent.putExtra("name",documentSnapshot.getString("name"));
                        JoinLobbyIntent.putExtra("pp",documentSnapshot.getString("userPhotoUrl"));




                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    public void HotPursuit(View view)
    {

        startActivity(HotPursuitIntent);
        finish();
    }
    public void lobby(View view)
    {
        lobbyID = lobbyIDet.getText().toString();
        JoinLobbyIntent.putExtra("lobby",lobbyID);
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().child("connections").exists())
                    {
                        System.out.println("ConExist");
                        if(task.getResult().child("connections").child(lobbyID).exists()) {
                            System.out.println("conenter");
                            JoinLobbyIntent.putExtra("conID", lobbyID);
                            JoinLobbyIntent.putExtra("jc", false);
                            JoinLobbyIntent.putExtra("type",0);
                            startActivity(JoinLobbyIntent);
                            finish();
                        } else if (task.getResult().child("lastconnections").child(lobbyID).exists()) {
                            System.out.println("lastenter");
                            JoinLobbyIntent.putExtra("conID", lobbyID);
                            JoinLobbyIntent.putExtra("jc", false);
                            JoinLobbyIntent.putExtra("type",1);
                            startActivity(JoinLobbyIntent);
                            finish();
                        }
                        else {
                            Toast.makeText(MainScreen.this, "Not Valid Lobby ID", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if(task.getResult().child("lastconnections").exists()) {
                        System.out.println("lastexist");

                        if (task.getResult().child("lastconnections").child(lobbyID).exists()) {
                            System.out.println("lastenter");
                            JoinLobbyIntent.putExtra("conID", lobbyID);
                            JoinLobbyIntent.putExtra("jc", false);
                            JoinLobbyIntent.putExtra("type",1);
                            startActivity(JoinLobbyIntent);
                            finish();
                        }
                        else {
                            Toast.makeText(MainScreen.this, "Not Valid Lobby ID", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MainScreen.this, "Not Valid Lobby ID", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }
    public void timeRush(View view)
    {

        startActivity(TimeRushIntent);
        finish();
    }

    public void Duel(View view)
    {
        startActivity(DuelIntent);
        finish();
    }

    public void lastMan(View view)
    {
        startActivity(LastManIntent);
        finish();
    }
    public void CreateLobby(View view)
    {
        startActivity(CreateLobbyIntent);
        finish();
    }
    public void HotPursuitLeaderboard(View view)
    {
        Intent intent = new Intent(MainScreen.this, HotPursuitLeaderBoard.class);
        startActivity(intent);
        finish();
    }
    public void Settings(View view)
    {
        startActivity(SettingsIntent);
        finish();
    }
    public void TimeRushLeaderboard(View view)
    {
        Intent intent = new Intent(MainScreen.this, TimeRushLeaderboard.class);
        startActivity(intent);
        finish();
    }

}