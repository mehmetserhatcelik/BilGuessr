package com.example.bilguessr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bilguessr.databinding.ActivityHotPursuitEndBinding;
import com.example.bilguessr.databinding.ActivityHotpursuitBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class HotPursuitEnd extends AppCompatActivity {
    private ActivityHotPursuitEndBinding binding;
    private String photUrl;
    private String name;
    private long score;
    private long record;
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHotPursuitEndBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        photUrl = getIntent().getStringExtra("pp");
        name = getIntent().getStringExtra("name");
        score = getIntent().getIntExtra("total",0);
        record = getIntent().getIntExtra("record",0);

        if(photUrl.equals("default"))
            Picasso.get().load(R.drawable.wavy).into(binding.pp);
        else
            Picasso.get().load(photUrl).into(binding.pp);

        binding.name.setText(name);
        binding.score.setText(""+score);
        fstore =  FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        if (score>record)
        {
            user = auth.getCurrentUser();
            DocumentReference documentReference = fstore.collection("Users").document(user.getUid());
            Map<String,Object> userInfo = new HashMap<>();
            userInfo.put("hotPursuitRecord",score);
            documentReference.update(userInfo);

            binding.record.setText(score+"");
            record = score;

        }else {
            binding.score.setText(record+"");
        }

    }
    public void main(View v)
    {
        Intent intent = new Intent(HotPursuitEnd.this, MainScreen.class);
        startActivity(intent);
        finish();
    }
    public void again(View v)
    {
        Intent intent = new Intent(HotPursuitEnd.this, HotPursuit.class);
        intent.putExtra("name",name);
        intent.putExtra("pp",photUrl);
        intent.putExtra("record",record);
        startActivity(intent);
        finish();
    }
}