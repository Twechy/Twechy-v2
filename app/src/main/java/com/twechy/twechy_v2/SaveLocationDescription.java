package com.twechy.twechy_v2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.twechy.twechy_v2.Database.DbBitmapUtility;
import com.twechy.twechy_v2.Database.LocationModel;
import com.twechy.twechy_v2.Database.MarkerModel;

import java.io.IOException;

import static com.twechy.twechy_v2.MainActivity.helper;


public class SaveLocationDescription extends AppCompatActivity {

    private static final int GALLERY_INTENT = 1;
    private static final int CAPTURE_PHOTO = 2;
    ImageView locationImage;
    TextView pickImage, latitude, longitude, saveLocation;
    EditText locationName, locationDescription;
    String name, description;
    Double lat, lng;
    long time;
    Bitmap bitmapImage;
    byte[] image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_location_layout);

        locationImage = findViewById(R.id.saveLocationImage);
        pickImage = findViewById(R.id.savePickImage);
        latitude = findViewById(R.id.saveLatitude);
        longitude = findViewById(R.id.saveLongitude);
        saveLocation = findViewById(R.id.saveLocation);
        locationName = findViewById(R.id.saveLocationName);
        locationDescription = findViewById(R.id.saveLocationDescription);
        registerForContextMenu(pickImage);

        Intent i = getIntent();
        name = i.getStringExtra("locationName");
        lat = i.getDoubleExtra("locationLatitude", -1);
        lng = i.getDoubleExtra("locationLongitude", -1);
        time = i.getLongExtra("locationTime", -1);

        if (name.length() != 0 && lat != -1 && lng != -1 && time != -1) {

            locationName.setText(name);
            latitude.setText(String.valueOf(lat));
            longitude.setText(String.valueOf(lng));

            Log.d("locationInfo: ", name);
            Log.d("locationInfo: ", String.valueOf(lat));
            Log.d("locationInfo: ", String.valueOf(lng));
        }

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

    public void saveLocationData(View view) {

        image = DbBitmapUtility.getBytes(bitmapImage, locationImage);
        description = locationDescription.getText().toString();

        LocationModel myLocationModel;

        if (image != null) {
            myLocationModel = new LocationModel(name, lng, lat, time, description, image);

        } else {
            myLocationModel = new LocationModel(name, lng, lat, time, description);
        }
        MarkerModel markerModel = new MarkerModel(name, lat, lng);

        helper.addLocation(myLocationModel);
        helper.addMarker(markerModel);

        AlertDialog.Builder builder = new AlertDialog.Builder(SaveLocationDescription.this);
        builder.setMessage("Do you want to go location List ?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        startActivity(new Intent(getApplicationContext(), LocationList.class));
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                finish();
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setTitle("Confirm");
        dialog.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();


            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                locationImage.setImageBitmap(bitmapImage);

                Log.i("info", "saveLocationData: success");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAPTURE_PHOTO && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();

            try {

                bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                locationImage.setImageBitmap(bitmapImage);

                Log.i("info", "saveLocationData: success");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
