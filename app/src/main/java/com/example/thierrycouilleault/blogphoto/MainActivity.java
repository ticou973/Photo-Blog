package com.example.thierrycouilleault.blogphoto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore firebaseFirestore;

    private android.support.v7.widget.Toolbar mainToolbar;
    private Button addPostBtn;
    private BottomNavigationView mainBottomNav;
    private FrameLayout mainContainer;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Photo Blog");

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(mAuth.getCurrentUser()!=null) {

            mainBottomNav = findViewById(R.id.mainBottomNav);

            //FRAGMENTS

            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();


            replaceFragment(homeFragment);

            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.bottom_action_home:

                            replaceFragment(homeFragment);
                            return true;

                        case R.id.bottom_action_account:

                            replaceFragment(accountFragment);
                            return true;

                        case R.id.bottom_action_notifications:
                            replaceFragment(notificationFragment);
                            return true;

                        default:
                            return false;

                    }

                }
            });

            addPostBtn = findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                    startActivity(newPostIntent);
                }
            });

        }



    }


    @Override
    protected void onStart() {
        super.onStart();

        mCurrentUser = mAuth.getCurrentUser();


        if (mCurrentUser == null) {

            sendToLogin();


        }else {

            current_user_id = mAuth.getCurrentUser().getUid();
                    firebaseFirestore.collection("users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()){
                                if(!task.getResult().exists()){

                                    Intent setUpIntent = new Intent(MainActivity.this, SetUpActivity.class);
                                    startActivity(setUpIntent);
                                    finish();

                                }

                            }else{

                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

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

    private void replaceFragment (Fragment fragment){

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainContainer, fragment).commit();

    }
}
