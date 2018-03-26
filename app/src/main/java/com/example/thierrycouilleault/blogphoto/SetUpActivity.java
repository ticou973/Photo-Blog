package com.example.thierrycouilleault.blogphoto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


//Attention pour avoir les autorisations sur firestore, il faut aller sur la consle et dans les règles mettre : if request.auth != null; à la place de false.



public class SetUpActivity extends AppCompatActivity {

    private Toolbar setUpToolbar;
    private CircleImageView circleImageView;
    private Uri mainImageURI = null;
    private EditText settingsName;
    private Button saveSettingsBtn;
    private ProgressBar setUpProgress;

    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private String user_id;
    private Boolean isChanged = false;


    private FirebaseFirestore firebaseFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        setUpToolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(setUpToolbar);
        getSupportActionBar().setTitle("Account Setup");


        //Initialisation de la DB
        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setUpProgress = findViewById(R.id.set_up_progress);
        circleImageView = findViewById(R.id.set_up_image);
        settingsName = findViewById(R.id.settings_name);
        saveSettingsBtn = findViewById(R.id.save_settings_btn);

        setUpProgress.setVisibility(View.VISIBLE);
        saveSettingsBtn.setEnabled(false);

        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        mainImageURI = Uri.parse(image);

                        settingsName.setText(name);

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.default_avatar);

                        Glide.with(SetUpActivity.this).setDefaultRequestOptions(requestOptions).load(image).into(circleImageView);




                    }else{

                        Toast.makeText(SetUpActivity.this, "Data doesn't exist : " , Toast.LENGTH_LONG).show();


                    }




                }else{

                    String error = task.getException().getMessage();
                    Toast.makeText(SetUpActivity.this, "Firestore retrieve Error : " + error, Toast.LENGTH_LONG).show();


                }

                setUpProgress.setVisibility(View.INVISIBLE);
                saveSettingsBtn.setEnabled(true);

            }
        });

        saveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userName = settingsName.getText().toString();

                if (!TextUtils.isEmpty(userName) && mainImageURI != null) {

                setUpProgress.setVisibility(View.VISIBLE);

                    if (isChanged) {


                        Toast.makeText(SetUpActivity.this, userName, Toast.LENGTH_SHORT).show();

                        user_id = mAuth.getCurrentUser().getUid();

                        setUpProgress.setVisibility(View.VISIBLE);


                        StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");

                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    storeFireStore(task, userName);


                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(SetUpActivity.this, "Error : " + error, Toast.LENGTH_LONG).show();

                                    setUpProgress.setVisibility(View.INVISIBLE);
                                }


                            }
                        });


                    }else{

                        storeFireStore(null, userName);


                    }


                }

            }
        });





        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    //Pour demander la permission car après MArshmallow , elle est obligatoire

                    if(ContextCompat.checkSelfPermission(SetUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){


                        Toast.makeText(SetUpActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);


                    } else {

                        bringImagePicker();

                    }

                }else {

                    bringImagePicker();

                }



            }
        });


    }

    private void storeFireStore(Task<UploadTask.TaskSnapshot> task, String userName) {

        Uri download_uri;


        if (task != null){

            download_uri = task.getResult().getDownloadUrl();


        } else {

            download_uri = mainImageURI;

        }



        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", userName);
        userMap.put("image", download_uri.toString());

        firebaseFirestore.collection("users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){


                    Toast.makeText(SetUpActivity.this, "The user settings are updated", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(SetUpActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(SetUpActivity.this, "Firestore Error : " + error, Toast.LENGTH_LONG).show();

                }
                setUpProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void bringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetUpActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();

                circleImageView.setImageURI(mainImageURI);

                isChanged = true;


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }
}
