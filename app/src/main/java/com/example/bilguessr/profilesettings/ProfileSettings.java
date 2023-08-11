package com.example.bilguessr.profilesettings;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bilguessr.R;
import com.example.bilguessr.databinding.ActivityProfileSettingsBinding;


public class ProfileSettings extends AppCompatActivity {
    ActivityProfileSettingsBinding binding;
    PasswordFragment passwordFragment;
    EmailFragment emailFragment;
    IconFragment iconFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityProfileSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        passwordFragment = new PasswordFragment();
        emailFragment = new EmailFragment();
        iconFragment  =new IconFragment();

        Intent intent = getIntent();
        String setting  = intent.getStringExtra("setting");
        switch (setting){
            case "password":
                replaceFragment(passwordFragment);
                break;
            case "icon":
                replaceFragment(iconFragment);
                break;
            case "email":
                replaceFragment(emailFragment);
                break;
        }
    }


    public void replaceFragment(Fragment fragment){
        FragmentManager manager= getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frameLayoutSettings,fragment).commit();
    }
}