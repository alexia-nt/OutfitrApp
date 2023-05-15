package com.example.outfitrapp;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadActivity extends AppCompatActivity {

    private FloatingActionButton uploadButton;
    EditText uploadCaption;

    /*String[] colors=new String[]{"blue","red","orange"};
    AutoCompleteTextView uploadCaption;*/
    private ImageView uploadImage;


    ProgressBar progressBar;
    private Uri imageUri;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String userId = currentUser.getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    // Get the reference to the current user's node
    DatabaseReference userRef = database.getReference("Users").child(userId);

    // Create a new node inside the current user's node
    DatabaseReference databaseReference = userRef.child("TopsSlider");
//    final private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("TopsSlider");
    final private StorageReference storageReference= FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadButton=findViewById(R.id.uploadButton);
        uploadCaption=findViewById(R.id.uploadCaption);
        uploadImage=findViewById(R.id.uploadImage);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()== Activity.RESULT_OK){
                            Intent data=result.getData();
                            imageUri=data.getData();
                            uploadImage.setImageURI(imageUri);

                        }else {
                            Toast.makeText(UploadActivity.this,"No Image",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photo= new Intent();
                photo.setAction(Intent.ACTION_GET_CONTENT);
                photo.setType("image/*");
                activityResultLauncher.launch(photo);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri !=null){
                    uploadToFirebase(imageUri);
                }else{
                    Toast.makeText(UploadActivity.this,"Select image",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private  void uploadToFirebase(Uri uri){
        String caption=uploadCaption.getText().toString();
        final StorageReference imgref=storageReference.child(System.currentTimeMillis()+ "."+ getFileExtension(uri));

        imgref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DataClass dataClass=new DataClass(uri.toString(),caption);
                        String key=databaseReference.push().getKey();
                        databaseReference.child(key).setValue(dataClass);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(UploadActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(UploadActivity.this,"failed",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExtension(Uri file1){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mine=MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(contentResolver.getType(file1));
    }
}
