package com.example.nightlife.nightlife;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mCtx;
    private List<PlacesPOJO.CustomA> stLstStores;
    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_NAME = "Name";
    public static final String EXTRA_ADD = "add";
    public static final String EXTRA_ID = "add";
    public static final String EXTRA_LAT="Latitude";
    public static final String EXTRA_LON = "longitude";
    public static final String EXTRA_photoref = "photo";




    public RecyclerViewAdapter(Context ctx,List<PlacesPOJO.CustomA> results) {
        mCtx=ctx;
        stLstStores=results;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("hey", "onComplete:  holder!");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_list, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        Log.d("hey", "onComplete: create holder!");

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final PlacesPOJO.CustomA product = stLstStores.get(position);

            holder.txtStoreName.setText(product.name);
            Glide.with(mCtx).load(product.icon).into(holder.img);
            holder.txtStoreAddr.setText(product.vicinity);
            if (product.opennow!=null) {
                if (product.opennow.open_now) {
                    holder.txtopen.setText("Open Now");
                } else {
                    holder.txtopen.setText("Not Open Now");
                }
            }

            holder.rate.setText(product.rating);
            Log.d("hey", "onComplete: found holder!");


            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("bind", "onClick: clicked on: " + product.name);



                    Intent detailIntent = new Intent(mCtx, detail_place.class);
                    //detailIntent.putExtra(EXTRA_URL, icon);
                    detailIntent.putExtra(EXTRA_NAME, product.name);
                    detailIntent.putExtra("EXTRA_ADD", product.vicinity);
                    detailIntent.putExtra(EXTRA_ID, product.placeid);
                    detailIntent.putExtra("EXTRA_RATING",product.rating);
                    detailIntent.putExtra(EXTRA_LAT,String.valueOf( product.geo.locationA.lat));
                    detailIntent.putExtra(EXTRA_LON,String.valueOf( product.geo.locationA.lng));


                    mCtx.startActivity(detailIntent);
                }
            });

    }


    @Override
    public int getItemCount() {
        return stLstStores.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txtStoreName;
        TextView txtStoreAddr;
        TextView txtStoreDist;
        TextView txtopen,rate;
        ImageView img;
        CardView parentLayout;



        public MyViewHolder(View itemView) {
            super(itemView);

            //this.txtStoreDist = (TextView) itemView.findViewById(R.id.txtStoreDist);

            this.txtStoreName = (TextView) itemView.findViewById(R.id.textViewTitle);
            this.txtStoreAddr = (TextView) itemView.findViewById(R.id.textViewShortDesc);
            //this.txttime = (TextView) itemView.findViewById(R.id.texttime);
           // this.txtkm = (TextView) itemView.findViewById(R.id.textkm);
            this.img=(ImageView)itemView.findViewById(R.id.imageView1);
            this.rate=itemView.findViewById(R.id.textViewRating);
            this.txtopen=itemView.findViewById(R.id.textopen);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }

    }
}
