package com.codearlystudio.homedelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codearlystudio.homedelivery.ProductDetails;
import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.models.PojoSimilarProducts;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AdapterSimilarProducts extends RecyclerView.Adapter<AdapterSimilarProducts.ViewHolder> {

    List<PojoSimilarProducts> listSimilar;
    Context ctx;

    public AdapterSimilarProducts(List<PojoSimilarProducts> listItems, Context ctx) {
        this.listSimilar = listItems;
        this.ctx = ctx;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @NonNull
    @Override
    public AdapterSimilarProducts.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_row,parent,false);
        return new AdapterSimilarProducts.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AdapterSimilarProducts.ViewHolder holder, final int position) {
        final PojoSimilarProducts pojoSimilarProducts = listSimilar.get(position);
        Glide.with(ctx).load(pojoSimilarProducts.getProductImage()).placeholder(R.drawable.ic_baseline_image_24).override(250,250).into(holder.similarImage);
        holder.similarText.setText(pojoSimilarProducts.getProductName());
        holder.similarRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.similarRow.setClickable(false);
                holder.similarRow.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.similarRow.setClickable(true);
                    }
                }, 500);
                Intent intent = new Intent(ctx, ProductDetails.class);
                intent.putExtra(Constants.PRODUCT_ID, pojoSimilarProducts.getProductId());
                ctx.startActivity(intent);
//                ((Activity)ctx).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listSimilar.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView similarImage;
        TextView similarText;
        LinearLayout similarRow;

        public ViewHolder(View itemView) {
            super(itemView);
            similarRow = (LinearLayout) itemView.findViewById(R.id.similar_row);
            similarImage = (ImageView) itemView.findViewById(R.id.similar_image);
            similarText = (TextView) itemView.findViewById(R.id.similar_text);
        }
    }

}
