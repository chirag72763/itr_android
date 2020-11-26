package com.codearlystudio.homedelivery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.models.OrderProducts;
import com.squareup.picasso.Picasso;
import java.util.List;

public class AdapterOrderProducts extends RecyclerView.Adapter<AdapterOrderProducts.ViewHolder> {

    List<OrderProducts> listOrderProducts;
    Context ctx;

    public AdapterOrderProducts(List<OrderProducts> listItems, Context ctx) {
        this.listOrderProducts = listItems;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public AdapterOrderProducts.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_product_row,parent,false);
        return new AdapterOrderProducts.ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final AdapterOrderProducts.ViewHolder holder, final int position) {
        final OrderProducts pojoOrderProducts = listOrderProducts.get(position);
        holder.productName.setText(pojoOrderProducts.getProductName());
        holder.productPrice.setText("₹ "+pojoOrderProducts.getVariantPrice()+"    ("+pojoOrderProducts.getVariantSize()+")");
        holder.productQuantity.setText(pojoOrderProducts.getProductQuantity());
        holder.totalPrice.setText("₹ "+pojoOrderProducts.getTotalPrice());
        Glide.with(ctx).load(pojoOrderProducts.getProductImage()).placeholder(R.drawable.ic_baseline_image_24).override(100,100).into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return listOrderProducts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView productName, productQuantity, productPrice, totalPrice;
        ImageView productImage;

        public ViewHolder(View itemView) {
            super(itemView);
            productImage = (ImageView) itemView.findViewById(R.id.product_image);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            productQuantity = (TextView) itemView.findViewById(R.id.product_quantity);
            productPrice = (TextView) itemView.findViewById(R.id.product_price);
            totalPrice = (TextView) itemView.findViewById(R.id.total_price);
        }

    }
}
