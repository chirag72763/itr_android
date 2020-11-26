package com.codearlystudio.homedelivery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.models.CartItems;

import java.util.List;

public class AdapterConfirmItem extends RecyclerView.Adapter<AdapterConfirmItem.ViewHolder> {

    List<CartItems> cartItemsList;
    Context ctx;

    public AdapterConfirmItem(List<CartItems> listItems, Context ctx) {
        this.cartItemsList = listItems;
        this.ctx = ctx;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @NonNull
    @Override
    public AdapterConfirmItem.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_order_row, parent,false);
        return new AdapterConfirmItem.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final AdapterConfirmItem.ViewHolder holder, final int position) {
        final CartItems cartItems = cartItemsList.get(position);
        holder.productSize.setText(cartItems.getVariantSize());
        holder.productQuantity.setText(cartItems.getCartQuanity()+" x");
        holder.productName.setText(cartItems.getProductName());
    }

    @Override
    public int getItemCount() {
        return cartItemsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView productName, productQuantity, productSize;

        public ViewHolder(View itemView) {
            super(itemView);
            productName = (TextView)itemView.findViewById(R.id.product_name);
            productQuantity = (TextView)itemView.findViewById(R.id.product_quantity);
            productSize = (TextView)itemView.findViewById(R.id.product_size);
        }
    }

}
