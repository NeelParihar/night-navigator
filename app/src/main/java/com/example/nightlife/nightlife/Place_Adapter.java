package com.example.nightlife.nightlife;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.example.nightlife.nightlife.model.Place_model;

import java.util.List;

public class Place_Adapter extends RecyclerView.Adapter<Place_Adapter.MyViewHolder> {

private Context mCtx;
private List<Place_model> stLstStores;
public static final String EXTRA_URL = "imageUrl";
public static final String EXTRA_NAME = "Name";
public static final String EXTRA_ADD = "add";
public static final String EXTRA_ID = "add";
public static final String EXTRA_LAT="Latitude";
public static final String EXTRA_LON = "longitude";
public static final String EXTRA_photoref = "photo";




public Place_Adapter (Context ctx,List<Place_model> results) {
        mCtx=ctx;
        stLstStores=results;
        }

@NonNull
@Override
public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("hey", "onComplete:  holder!");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedlayout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        Log.d("hey", "onComplete: create holder!");

        return holder;
        }

@Override
public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
final Place_model product = stLstStores.get(position);

        holder.txtStoreName.setText(product.name);
        holder.txtStoreAddr.setText(product.address);
        holder.txtStorephone.setText("+91 "+product.phoneno);
        holder.txtaddname.setText("Added By :"+product.addedby);
        holder.txttype.setText("Type : "+product.type);
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                // Send phone number to intent as data
                intent.setData(Uri.parse("tel:" + product.phoneno));
                // Start the dialer app activity with number
                mCtx.startActivity(intent);
            }
        });
        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("www.google.com").appendPath("maps").appendPath("dir").appendPath("").appendQueryParameter("api", "1")
                        .appendQueryParameter("destination",product.lat+ "," + product.lon);
                String url = builder.build().toString();
                Log.d("Directions", url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                mCtx.startActivity(i);
            }
        });
        holder.uber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(mCtx,MapsActivity.class);
                intent1.setAction("feed");
                intent1.putExtra("EXTRA_LAT",product.lat);
                intent1.putExtra("EXTRA_LON",product.lon);
                mCtx.startActivity(intent1);
            }
        });

        Log.d("hey", "onComplete: found holder!");

        }


@Override
public int getItemCount() {
        return stLstStores.size();
        }


public class MyViewHolder extends RecyclerView.ViewHolder {


    TextView txtStoreName;
    TextView txtStoreAddr;
    TextView txtStorephone,txttype;
    TextView txtaddname;
    AppCompatButton call,map,uber;





    public MyViewHolder(View itemView) {
        super(itemView);

        //this.txtStoreDist = (TextView) itemView.findViewById(R.id.txtStoreDist);

        this.txtStoreName = (TextView) itemView.findViewById(R.id.textname);
        this.txtStoreAddr = (TextView) itemView.findViewById(R.id.textadd);
        this.txtStorephone=itemView.findViewById(R.id.contno);
        this.txtaddname=itemView.findViewById(R.id.addname);
        this.call=itemView.findViewById(R.id.call);
        txttype=itemView.findViewById(R.id.type);
        map=itemView.findViewById(R.id.gotomap);
        uber=itemView.findViewById(R.id.gotouber);




    }

}
}