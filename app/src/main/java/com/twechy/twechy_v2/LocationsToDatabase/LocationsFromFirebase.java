package com.twechy.twechy_v2.LocationsToDatabase;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.twechy.twechy_v2.R;

public class LocationsFromFirebase extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseRecyclerAdapter<ShowLocationItems, LocationsDataViewHolder> recyclerAdapter;

    public LocationsFromFirebase() {
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_data_layout);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Location_List_Details");

        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(LocationsFromFirebase.this));
        Toast.makeText(this, "Wait ... Fetching List...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        recyclerAdapter = new FirebaseRecyclerAdapter<ShowLocationItems, LocationsDataViewHolder>
                (ShowLocationItems.class, R.layout.location_card_view, LocationsDataViewHolder.class, myRef) {
            @Override
            protected void populateViewHolder(final LocationsDataViewHolder viewHolder, ShowLocationItems model, int position) {

                viewHolder.Location_Name(model.getLocationName());
                viewHolder.Location_Description(model.getLocationDescription());
                viewHolder.Location_Latitude(model.getLatitude());
                viewHolder.Location_Longitude(model.getLongitude());
                viewHolder.Image_URL(model.getLocationImage());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LocationsFromFirebase.this);
                        builder.setMessage("Do you want to delete this data ?").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        int selectedItem = viewHolder.getAdapterPosition();

                                        recyclerAdapter.getRef(selectedItem).removeValue();
                                        recyclerAdapter.notifyItemRemoved(selectedItem);
                                        recyclerView.invalidate();
                                        onStart();
                                    }
                                }).setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.setTitle("Confirm");
                        dialog.show();
                    }
                });
                recyclerView.setAdapter(recyclerAdapter);
            }
        };


    }



    public static class LocationsDataViewHolder extends RecyclerView.ViewHolder {

        ImageView locationImage;
        TextView locationName;
        TextView locationDescription;
        TextView locationLatitude;
        TextView locationLongitude;

        public LocationsDataViewHolder(View itemView) {
            super(itemView);

            locationImage = itemView.findViewById(R.id.item_image);
            locationName = itemView.findViewById(R.id.location_name);
            locationDescription = itemView.findViewById(R.id.location_description);
            locationLatitude = itemView.findViewById(R.id.location_latitude);
            locationLongitude = itemView.findViewById(R.id.location_longitude);
        }

        private void Location_Name(String locationName) {
            this.locationName.setText(locationName);
        }

        private void Location_Description(String LocationDescription) {
            this.locationDescription.setText(LocationDescription);
        }

        private void Location_Latitude(String LocationLatitude) {
            locationLatitude.setText(LocationLatitude);
        }

        private void Location_Longitude(String LocationLongitude) {
            locationLongitude.setText(LocationLongitude);
        }

        private void Image_URL(String title) {

            Glide.with(itemView.getContext())
                    .load(title)
                    .crossFade()
                    .placeholder(R.drawable.ic_menu_slideshow)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(locationImage);
        }

    }
}
