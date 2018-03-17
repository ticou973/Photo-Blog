package com.example.thierrycouilleault.blogphoto;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

    Toolbar setUpToolbar;
    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        setUpToolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(setUpToolbar);
        getSupportActionBar().setTitle("Account Setup");


        circleImageView = findViewById(R.id.set_up_image);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    //Pour demander la permission car après MArshmallow , elle est obligatoire

                    if(ContextCompat.checkSelfPermission(SetUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){


                        Toast.makeText(SetUpActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);




                    } else {

                        Toast.makeText(SetUpActivity.this, "You already have the permissions", Toast.LENGTH_LONG).show();

                    }




                }



            }
        });


    }
}
