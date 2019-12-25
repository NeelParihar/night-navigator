package com.example.nightlife.nightlife;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nightlife.nightlife.model.Results;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class placeadapter extends RecyclerView.Adapter<placeadapter.placeholder> {
    private Context mCtx;
    private List<Results> productList;
    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_NAME = "Name";
    public static final String EXTRA_ADD = "add";


    public placeadapter(Context mCtx, List<Results> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public placeholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_list, parent, false);
        placeholder holder = new placeholder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull placeholder placeholder,final int i) {
        Results  product = productList.get(i);
        //product.get
        //final String icon =product.get;
        final String name= (String) product.getName();
        final String add = (String) product.getVicinity();
        //loading the image
        Log.d("hey", "onComplete: found holder!");
        //Glide.with(mCtx).load(product.getIcon()).into(placeholder.imageView);

        placeholder.textViewTitle.setText(product.getName());
        placeholder.textViewShortDesc.setText(product.getVicinity());
        placeholder.textViewRating.setText(String.valueOf(product.getRating()));
        //placeholder.textViewPrice.setText(String.valueOf(product.getOpening_hours().getOpen_now()));

        placeholder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("bind", "onClick: clicked on: " + productList.get(i));

                Toast.makeText(mCtx,"hdhdhhdhhdd", Toast.LENGTH_SHORT).show();

                Intent detailIntent = new Intent(mCtx, detail_place.class);
                //detailIntent.putExtra(EXTRA_URL, icon);
                detailIntent.putExtra(EXTRA_NAME, name);
                detailIntent.putExtra(EXTRA_ADD,add );

                mCtx.startActivity(detailIntent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return productList.size();
    }
    
    class placeholder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        ImageView imageView;
        AppCompatButton call,map,uber;
        CardView parentLayout;

        public placeholder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            //textViewPrice = itemView.findViewById(R.id.te);
            //imageView = itemView.findViewById(R.id.imageView);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }

    }




}
