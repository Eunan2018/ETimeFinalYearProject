package com.eunan.tracey.etimefinalyearproject.upload;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employer.EmpImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadActivity extends AppCompatActivity {

    private Button btnChooseFile;
    private Button btnUpload;
    private Button btnShowUploads;
    private EditText edtImageName;
    private StorageReference storageRef;
    private StorageTask uploadTask;
    private DatabaseReference imageRef;
    private ProgressDialog progressDialog;
    private String employeeKey;
    private String employerKey;
    private Toolbar toolbar;
    public static  final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        btnChooseFile = findViewById(R.id.button_choose_file);
        btnUpload = findViewById(R.id.button_upload);
        btnShowUploads = findViewById(R.id.button_show_uploads);
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        imageRef = FirebaseDatabase.getInstance().getReference("Images");
        imageView = findViewById(R.id.image_view_ts);
        edtImageName = findViewById(R.id.edit_text_image_name);

        toolbar = findViewById(R.id.time_sheet_app_bar);
        setSupportActionBar(toolbar);
        Drawable dr = ContextCompat.getDrawable(this, R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        getSupportActionBar().setLogo(d);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialise dialog
        progressDialog = new ProgressDialog(UploadActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        employeeKey = getIntent().getStringExtra("key2");
        employerKey = getIntent().getStringExtra("key1");

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChoser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(UploadActivity.this, " Upload in progress", Toast.LENGTH_SHORT).show();
                }else if (edtImageName.getText().toString().isEmpty()){
                    Toast.makeText(UploadActivity.this, "Need to set a file name.", Toast.LENGTH_SHORT).show();
                    edtImageName.findFocus();
                }else{
                    uploadFile();

                }
            }
        });

        btnShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();
            }
        });

    }

    private void openImagesActivity() {
        Intent intent = new Intent(UploadActivity.this, EmpImage.class);
        intent.putExtra("key1",employerKey);
        intent.putExtra("key2",employeeKey);
        startActivity(intent);
    }

    private void openFileChoser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST & resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            Picasso.with(this).load(imageUri).into(imageView);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));

    }

    private void uploadFile(){
        progressDialog.show();
        if(imageUri != null){
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask =  fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                        }
                    },1000);
                    Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_LONG).show();
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUrl = urlTask.getResult();
                    Upload upload = new Upload(edtImageName.getText().toString().trim(), downloadUrl.toString());
                    String uploadId = imageRef.push().getKey();
                    imageRef.child(employerKey).child(employeeKey).child(uploadId).setValue(upload);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            progressDialog.hide();
            Toast.makeText(UploadActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
