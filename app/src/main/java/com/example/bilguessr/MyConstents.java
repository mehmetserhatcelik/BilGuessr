package com.example.bilguessr;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class MyConstents {
    FirebaseFirestore fstore;
    FirebaseAuth auth;

    public String playerName;
    public int hp;
    public boolean lockClicked;
    public String playerPP;

    public MyConstents()
    {
        hp =100;

        lockClicked = false;
        fstore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        fstore.collection("Users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot =task.getResult();
                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        setPlayerName(documentSnapshot.getString("name"));
                        setPlayerPP(documentSnapshot.getString("downloadURL"));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void getPlayerName(TextView tv) {
        fstore.collection("Users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {

                    DocumentSnapshot documentSnapshot =task.getResult();

                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        tv.setText(documentSnapshot.getString("name"));

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public boolean isLockClicked() {
        return lockClicked;
    }

    public void setLockClicked(boolean lockClicked) {
        this.lockClicked = lockClicked;
    }

    public void getPlayerPP(ImageView v) {
        fstore.collection("Users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot =task.getResult();
                    if(documentSnapshot != null && documentSnapshot.exists())
                    {
                        Picasso.get().load(documentSnapshot.getString("userPhotoUrl")).into(v);

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void setPlayerPP(String playerPP) {
        this.playerPP = playerPP;
    }
}
