package com.twechy.twechy_v2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.twechy.twechy_v2.Database.LocationModel;
import com.twechy.twechy_v2.Database.Location_Helper;

import java.util.Collections;
import java.util.List;

class TwechyAdapter extends RecyclerView.Adapter<TwechyAdapter.MyViewHolder> {

    private int count;
    private int itemPosition;
    private List<LocationModel> locationModels = Collections.emptyList();
    private Context context;
    private ClickListener clickListener;
    private TwechyAdapter twechyAdapter;
    private Location_Helper location_helper;


    TwechyAdapter(Context context, List<LocationModel> locationModels) {

        this.context = context;
        this.locationModels = locationModels;
        location_helper = new Location_Helper(context);
        locationModels = location_helper.locationToListView();
        count = locationModels.size();
    }

    void update(List<LocationModel> locationModel) {

        locationModel.clear();
        locationModels = location_helper.locationToListView();
        locationModel.addAll(locationModels);
        notifyDataSetChanged();
    }

    void remove(int position) {

        notifyItemRemoved(position);

        notifyItemRangeChanged(position, getItemCount());
        notifyItemRangeChanged(position, locationModels.size());
        notifyDataSetChanged();
    }

    public void add(int position, LocationModel locationModel) {

        locationModels.add(position, locationModel);
        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_card_view, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.locationName.setText(locationModels.get(position).getLocationName());
        holder.locationDescription.setText(locationModels.get(position).getLocationDescription());
        holder.locationLatitude.setText(String.format("Lat: %s", locationModels.get(position).getLatitude()));
        holder.locationLongitude.setText(String.format("Lng: %s", locationModels.get(position).getLongitude()));

        byte[] image = locationModels.get(position).getImage();
        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.locationImage.setImageBitmap(bmp);
        //holder.locationImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, 200, 200, false));

        //holder.locationImage.setImageBitmap(DbBitmapUtility.getImage());

        /*int images = R.mipmap.ic_location_icon;
        holder.locationImage.setImageResource(images);
*/
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setItemPosition(holder.getAdapterPosition());
                return false;
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return locationModels == null ? 0 : locationModels.size();
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {

        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return locationModels == null ? 0 : locationModels.size();
    }

    int getItemPosition() {
        return itemPosition;
    }

    public void setItemPosition(int itemPosition) {
        this.itemPosition = itemPosition;
    }

    interface ClickListener {

        void itemClicked(View view, int position, List<LocationModel> locationModels);

        void itemLongClicked(View view, int position, List<LocationModel> locationModels, TwechyAdapter twechyAdapter);
    }

    @TargetApi(Build.VERSION_CODES.M)
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener/*, View.OnContextClickListener*/ {

        ImageView locationImage;
        TextView locationName;
        TextView locationDescription;
        TextView locationLatitude;
        TextView locationLongitude;

        MyViewHolder(final View itemView) {
            super(itemView);

            itemView.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {

                        clickListener.itemClicked(v, getAdapterPosition(), locationModels);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (clickListener != null) {
                        clickListener.itemLongClicked(v, getAdapterPosition(), locationModels, twechyAdapter);
                    }
                    return false;
                }
            });

            locationImage = itemView.findViewById(R.id.item_image);
            locationName = itemView.findViewById(R.id.location_name);
            locationDescription = itemView.findViewById(R.id.location_description);
            locationLatitude = itemView.findViewById(R.id.location_latitude);
            locationLongitude = itemView.findViewById(R.id.location_longitude);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            contextMenu.add(Menu.NONE, R.id.ctx_menu_delete_location, Menu.NONE, R.string.delete_location);
            contextMenu.add(Menu.NONE, R.id.ctx_menu_location_info, Menu.NONE, R.string.location_info);

            /*contextMenu.setHeaderTitle("Select The Action");
            contextMenu.add(0, itemView.getId(), 0, "View Details");//groupId, itemId, order, title
            contextMenu.add(0, itemView.getId(), 0, "Delete");
*/
        }


    }
}
