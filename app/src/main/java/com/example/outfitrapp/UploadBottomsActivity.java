package com.example.outfitrapp;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
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

public class UploadBottomsActivity extends AppCompatActivity{
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
    DatabaseReference databaseReference = userRef.child("BottomsSlider");
    //final private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("TopsSlider");
    final private StorageReference storageReference= FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bottoms);

        uploadButton=findViewById(R.id.uploadButton);
        uploadCaption=findViewById(R.id.uploadCaption);
        uploadImage=findViewById(R.id.uploadImage);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    /*
                     * First checks if any image is selected from the gallery. Else if the user goes
                     * in the gallery but return to the main app without selecting an image or without changing
                     * the already existing image, the message “No image” is displayed.
                     */
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()== Activity.RESULT_OK){
                            Intent data=result.getData();
                            imageUri=data.getData();
                            uploadImage.setImageURI(imageUri);

                        }else {
                            Toast.makeText(UploadBottomsActivity.this,"No Image",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        /*
         * We check if any image has been selected by the user. In case when the user has
         * selected a photo, the upload process continues normally. However if he tries to
         * upload without having selected a photo for the corresponding activity then the
         * message "select image" is displayed without being executed some upload.
         */
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
                    Toast.makeText(UploadBottomsActivity.this,"Select image",Toast.LENGTH_SHORT).show();

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
                /*
                 * The upload can be done whether we have a description of the garment
                 * or not, by pressing the arrow on the bottom right and if you upload it
                 * done successfully then the message “Uploaded” will appear and it will
                 * all relevant data is stored, such as description, and then we will be
                 * transferred to MainActivity.
                 */
                imgref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DataClass dataClass=new DataClass(uri.toString(),caption);
                        String key=databaseReference.push().getKey();
                        databaseReference.child(key).setValue(dataClass);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadBottomsActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(UploadBottomsActivity.this,MainActivity.class);
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
            // if there is an error in the process, the message “failed” will be displayed
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(UploadBottomsActivity.this,"failed",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExtension(Uri file1){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mine=MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(contentResolver.getType(file1));
    }
}
