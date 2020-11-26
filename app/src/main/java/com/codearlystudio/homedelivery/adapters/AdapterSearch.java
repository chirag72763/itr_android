package com.codearlystudio.homedelivery.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codearlystudio.homedelivery.ProductDetails;
import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.models.PojoSearch;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.ViewHolder> {

    List<PojoSearch> listSearches;
    Context ctx;

    public AdapterSearch(List<PojoSearch> listItems, Context ctx) {
        this.listSearches = listItems;
        this.ctx = ctx;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public AdapterSearch.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, parent,false);
        return new AdapterSearch.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterSearch.ViewHolder holder, int position) {
        final PojoSearch pojoSearch = listSearches.get(position);
        holder.tvTitle.setText(pojoSearch.getProductName());
        holder.tvSubtitle.setText(pojoSearch.getSectionName());
        Glide.with(ctx).load(pojoSearch.getProductImage()).placeholder(R.drawable.ic_baseline_image_24).override(200,200).into(holder.imProductImage);
        holder.llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)ctx.getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                holder.llSearch.setClickable(false);
                holder.llSearch.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.llSearch.setClickable(true);
                    }
                }, 500);
                Intent intent = new Intent(ctx, ProductDetails.class);
                intent.putExtra(Constants.PRODUCT_ID, pojoSearch.getProductId());
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listSearches.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imProductImage;
        TextView tvTitle, tvSubtitle;
        LinearLayout llSearch;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView)itemView.findViewById(R.id.tv_title);
            tvSubtitle = (TextView)itemView.findViewById(R.id.tv_subtitle);
            imProductImage = (ImageView)itemView.findViewById(R.id.im_product_image);
            llSearch = (LinearLayout) itemView.findViewById(R.id.ll_search);
        }
    }

}
