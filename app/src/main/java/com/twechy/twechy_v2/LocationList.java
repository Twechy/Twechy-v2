package com.twechy.twechy_v2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.twechy.twechy_v2.Database.LocationModel;

import java.util.List;

import static com.twechy.twechy_v2.MainActivity.helper;
import static com.twechy.twechy_v2.MainActivity.locationModel;


public class LocationList extends AppCompatActivity implements TwechyAdapter.ClickListener {

    static TwechyAdapter twechyAdapter;
    RecyclerView recyclerView;
    Context ctx;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list);

        ctx = getApplicationContext();

        locationModel = helper.locationToListView();

        recyclerView = findViewById(R.id.recyclerView_Locations);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setHasFixedSize(true);

        twechyAdapter = new TwechyAdapter(ctx, locationModel);
        recyclerView.setAdapter(twechyAdapter);
        recyclerView.setVerticalFadingEdgeEnabled(true);

        twechyAdapter.setClickListener(this);
        registerForContextMenu(recyclerView);

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(ctx, R.drawable.item_divider);
        assert horizontalDivider != null;
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback
                (0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition(); //get position which is swipe
                if (direction == ItemTouchHelper.LEFT) {    //if swipe left

                    AlertDialog.Builder builder = new AlertDialog.Builder(LocationList.this); //alert for confirm to delete
                    builder.setMessage("Are you sure to delete?");    //set message

                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            twechyAdapter.notifyItemRemoved(position);    //item removed from recylcerview
                            helper.deleteLocation(locationModel.get(position + 1).getLocationName());
                            //sqlDatabase.execSQL("delete from markers where id='" + (position + 1) + "'"); //query for delete
                            locationModel.remove(position);  //then remove item
                            twechyAdapter.update(locationModel);

                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            twechyAdapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                            twechyAdapter.notifyItemRangeChanged(position, twechyAdapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                        }
                    }).show();  //show alert dialog
                } else if (direction == ItemTouchHelper.RIGHT) {
                    //if swipe right

                    AlertDialog.Builder builder = new AlertDialog.Builder(LocationList.this); //alert for confirm to delete
                    builder.setMessage("Are you sure to delete?");    //set message

                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            twechyAdapter.notifyItemRemoved(position);    //item removed from recylcerview
                            helper.deleteLocation(locationModel.get(position).getLocationName());
                            //sqlDatabase.execSQL("delete from markers where id='" + (position + 1) + "'"); //query for delete
                            locationModel.remove(position);  //then remove item

                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            twechyAdapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                            twechyAdapter.notifyItemRangeChanged(position, twechyAdapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                        }
                    }).show();  //show alert dialog

                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.hide();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        twechyAdapter.update(locationModel);

        int position;
        try {
            position = twechyAdapter.getItemPosition();
        } catch (Exception e) {
            Log.d("RecyclerInfo", e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {

            case R.id.ctx_menu_delete_location:

                twechyAdapter.notifyItemRemoved(position);    //item removed from recylcerview
                helper.deleteLocation(locationModel.get(position).getLocationName());
                locationModel.remove(position);  //then remove item
                twechyAdapter.update(locationModel);
                Toast.makeText(ctx, "Delete the " + position, Toast.LENGTH_SHORT).show();
                break;
            case R.id.ctx_menu_location_info:

                String name = locationModel.get(position).getLocationName();
                String lat = String.valueOf(locationModel.get(position).getLatitude());
                String lng = String.valueOf(locationModel.get(position).getLongitude());
                Toast.makeText(ctx, "position " + position + " " + name + "\n" + lat + "\n" + lng, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.location_count) {

            Toast.makeText(ctx, "got " + twechyAdapter.getItemCount() + " items in Database", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.marker_list) {

            Toast.makeText(ctx, "My Markers", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(ctx, MarkersList.class));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void itemClicked(View view, int position, List<LocationModel> locationModels) {

        Intent i = new Intent(ctx, MapsActivity.class);
        i.putExtra("LocationName", locationModels.get(position).getLocationName());

        i.putExtra("locationLatitude", locationModels.get(position).getLatitude());
        i.putExtra("locationLongitude", locationModels.get(position).getLongitude());

        Log.i("LocationLat", String.valueOf(locationModels.get(position).getLatitude()));
        Log.i("LocationLng", String.valueOf(locationModels.get(position).getLongitude()));
        startActivity(i);
    }

    @Override
    public void itemLongClicked(View view, int position, List<LocationModel> locationModels, TwechyAdapter twechyAdapter) {

        Toast.makeText(this, "Long press at " + position, Toast.LENGTH_SHORT).show();
    }
}
