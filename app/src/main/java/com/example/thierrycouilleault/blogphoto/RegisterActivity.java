package com.example.thierrycouilleault.blogphoto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private Button mRegCreateAccountBtn, mRegLoginBtn;
    private EditText mRegEmail, mRegPassword, mRegConfirmPassword;
    private ProgressBar mRegProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegCreateAccountBtn = findViewById(R.id.reg_btn);
        mRegLoginBtn = findViewById(R.id.reg_login_btn);
        mRegEmail = findViewById(R.id.reg_email);
        mRegPassword = findViewById(R.id.reg_password);
        mRegConfirmPassword = findViewById(R.id.reg_confirm_password);
        mRegProgress = findViewById(R.id.reg_progress);

        mAuth = FirebaseAuth.getInstance();

        mRegLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


        mRegCreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mRegEmail.getText().toString();
                String password = mRegPassword.getText().toString();
                String confirmPassword = mRegConfirmPassword.getText().toString();


                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)){

                    if (password.equals(confirmPassword)) {

                        mRegProgress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){

                                    Intent setUpIntent = new Intent(RegisterActivity.this, SetUpActivity.class);
                                    startActivity(setUpIntent);
                                    finish();

                                } else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error : " + errorMessage, Toast.LENGTH_SHORT).show();
                                }

                                mRegProgress.setVisibility(View.INVISIBLE);
                            }
                        });

                    }else{

                        Toast.makeText(RegisterActivity.this, R.string.password_confirm_doesnt_work, Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){

            sendtoMain();

        }
    }



    private void sendtoMain() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }
}
