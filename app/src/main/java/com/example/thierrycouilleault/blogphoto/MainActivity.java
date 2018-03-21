package com.example.thierrycouilleault.blogphoto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private android.support.v7.widget.Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Photo Blog");

        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    protected void onStart() {
        super.onStart();



        mCurrentUser = mAuth.getCurrentUser();


        if (mCurrentUser == null) {

            sendToLogin();


        }else {

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){

            case R.id.action_logout_btn :

                logOut();

                return true;

            case R.id.action_settings_btn :

                Intent settingsIntent = new Intent(MainActivity.this, SetUpActivity.class);
                startActivity(settingsIntent);
                return true;

                default:
                    return false;



        }
    }

    private void logOut() {


        mAuth.signOut();
        sendToLogin();


    }


    private void sendToLogin() {

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

    }
}
