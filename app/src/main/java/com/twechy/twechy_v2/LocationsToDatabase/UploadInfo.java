package com.twechy.twechy_v2.LocationsToDatabase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.twechy.twechy_v2.R;


public class UploadInfo extends AppCompatActivity {

    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 1;
    private static final int CAPTURE_PHOTO = 2;
    Button selectImage, uploadBtn;
    ImageView userImage;
    EditText title;
    TextView locationName, latitude, longitude;
    ProgressDialog dialog;
    private Firebase firebase;
    private Uri uri = null;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_layout);

        selectImage = findViewById(R.id.select_image);
        uploadBtn = findViewById(R.id.uploadBtn);
        userImage = findViewById(R.id.user_image);
        title = findViewById(R.id.editTitle);
        latitude = findViewById(R.id.uploadLatitude);
        longitude = findViewById(R.id.uploadLongitude);
        locationName = findViewById(R.id.uploadLocatioName);

        dialog = new ProgressDialog(UploadInfo.this);
        registerForContextMenu(selectImage);

        Intent i = getIntent();

        String name = i.getStringExtra("locationName");
        Double locationLatitude = i.getDoubleExtra("locationLatitude", -1);
        Double locationLongitude = i.getDoubleExtra("locationLongitude", -1);

        if (locationName.length() != 0 && locationLatitude != -1 && locationLongitude != -1) {

            locationName.setText(name);
            latitude.setText(String.valueOf(locationLatitude));
            longitude.setText(String.valueOf(locationLongitude));

        }


        /*databaseReference = FirebaseDatabase.getInstance().getReference();
        firebase = new Firebase("URL name from firebase console").child("User_Details").push();
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("URL name from firebase storage console");
*/

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mName = title.getText().toString();

                if (mName.isEmpty()) {
                    Toast.makeText(UploadInfo.this, "Fill all Fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                Firebase childRef_name = firebase.child("Image_Title");
                childRef_name.setValue(mName);

                Toast.makeText(UploadInfo.this, "Updated Info...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_picker_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.capture_Photo:

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, CAPTURE_PHOTO);
                break;
            case R.id.pick_Photo:

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_INTENT);
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callGallery();
                return;
        }
        Toast.makeText(this, "....", Toast.LENGTH_SHORT).show();
    }

    private void callGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            uri = data.getData();
            userImage.setImageURI(uri);
            StorageReference filePath = storageReference.child("User_Image").child(uri.getLastPathSegment());

            dialog.setMessage("Uploading Image...");
            dialog.show();

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downLoadUri = taskSnapshot.getDownloadUrl();
                    firebase.child("Image_URL").setValue(downLoadUri.toString());

                    Glide.with(getApplicationContext())
                            .load(downLoadUri)
                            .crossFade()
                            .placeholder(R.drawable.ic_menu_slideshow)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(userImage);
                    Toast.makeText(UploadInfo.this, "Updated", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            });

        }
    }
}
/*selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(UploadInfo.this, "Call for Permission", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                    }
                } else {
                    callGallery();
                }
            }
        });*/
