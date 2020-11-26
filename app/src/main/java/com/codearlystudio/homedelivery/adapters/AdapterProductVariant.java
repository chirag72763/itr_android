package com.codearlystudio.homedelivery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.codearlystudio.homedelivery.LoginActivity;
import com.codearlystudio.homedelivery.ProductDetails;
import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.data.Api;
import com.codearlystudio.homedelivery.data.AppConfig;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.data.ServerResponse;
import com.codearlystudio.homedelivery.models.PojoVariantDetails;

import java.util.List;

import retrofit2.Call;

public class AdapterProductVariant extends RecyclerView.Adapter<AdapterProductVariant.ViewHolder> {

    List<PojoVariantDetails> listVariants;
    Context ctx;
    String productId, productAvailable;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Vibrator vibe;

    public AdapterProductVariant(List<PojoVariantDetails> listItems, Context ctx, String productId, String prodductAvailable) {
        this.listVariants = listItems;
        this.ctx = ctx;
        this.productId = productId;
        this.productAvailable = prodductAvailable;
    }

    @SuppressLint("CommitPrefEdits")
    @NonNull
    @Override
    public AdapterProductVariant.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_variant_row,parent,false);
        vibe = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        preferences = ctx.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
        return new AdapterProductVariant.ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    boolean checkUserLogin(){
        return preferences.getInt(Constants.LOGIN_FLAG, 0) != 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final AdapterProductVariant.ViewHolder holder, final int position) {
        final PojoVariantDetails pojoVariants = listVariants.get(position);
        if (pojoVariants.getVariantAvailable().equals("0")){
            holder.outOfStock.setVisibility(View.VISIBLE);
            holder.add.setVisibility(View.GONE);
            holder.counterRow.setVisibility(View.GONE);
        }
        else if (pojoVariants.getVariantAvailable().equals("1")){
            if (productAvailable.equals("0")){
                holder.outOfStock.setVisibility(View.VISIBLE);
                holder.add.setVisibility(View.GONE);
                holder.counterRow.setVisibility(View.GONE);
            }
            else {
                holder.outOfStock.setVisibility(View.GONE);
                if (checkUserLogin()){
                    if (Integer.parseInt(pojoVariants.getVariantCount()) > 0){
                        holder.add.setVisibility(View.GONE);
                        holder.counterRow.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.add.setVisibility(View.VISIBLE);
                        holder.counterRow.setVisibility(View.GONE);
                    }
                }
            }
        }
        holder.variantCount.setText(pojoVariants.getVariantCount());
        holder.variantSize.setText("Size: "+pojoVariants.getVariantSize());
        holder.variantPrice.setText("₹ "+pojoVariants.getVariantPrice());
        holder.variantOriginal.setText("MRP ₹"+pojoVariants.getVariantOriginal());
        holder.variantOriginal.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.add.setClickable(false);
                holder.add.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.add.setClickable(true);
                    }
                }, 500);
                if (checkUserLogin()){
                    addToCart(pojoVariants.getVariantId(), pojoVariants.getVariantSize(), pojoVariants.getVariantPrice());
                }
                else {
                    ctx.startActivity(new Intent(ctx, LoginActivity.class));
                }

            }
        });
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.addToCart.setClickable(false);
                holder.addToCart.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.addToCart.setClickable(true);
                    }
                }, 500);
                addToCart(pojoVariants.getVariantId(), pojoVariants.getVariantSize(), pojoVariants.getVariantPrice());
            }
        });
        holder.removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.removeFromCart.setClickable(false);
                holder.removeFromCart.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.removeFromCart.setClickable(true);
                    }
                }, 500);
                removeFromCart(pojoVariants.getVariantId());
            }
        });
    }

    void addToCart(final String variantId, String variantSize, String variantPrice) {
        vibe.vibrate(5);
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.addToCart(
                preferences.getString(Constants.USER_ID, null),
                productId, variantId, variantSize, variantPrice);
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        ProductDetails.getProduct(false);
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) { }
        });
    }

    void removeFromCart(String variantId) {
        vibe.vibrate(5);
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.removeProductFromCart(preferences.getString(Constants.USER_ID, null), productId, variantId);
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @SuppressLint("Assert")
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        ProductDetails.getProduct(false);
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) { }
        });
    }

    @Override
    public int getItemCount() {
        return listVariants.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView variantPrice, variantOriginal;
        TextView variantSize, variantCount, addToCart, removeFromCart;
        LinearLayout counterRow;
        RelativeLayout variantDetailRow;
        Button add, outOfStock;

        public ViewHolder(View itemView) {
            super(itemView);
            variantDetailRow = (RelativeLayout) itemView.findViewById(R.id.variant_detail_row);
            variantPrice = (TextView) itemView.findViewById(R.id.variant_price);
            variantOriginal = (TextView) itemView.findViewById(R.id.variant_original);
            variantSize = (TextView) itemView.findViewById(R.id.variant_size);
            counterRow = (LinearLayout) itemView.findViewById(R.id.cart_counter);
            variantCount = (TextView) itemView.findViewById(R.id.counter);
            addToCart = (TextView) itemView.findViewById(R.id.add_to_cart);
            removeFromCart = (TextView) itemView.findViewById(R.id.remove_from_cart);
            add = (Button) itemView.findViewById(R.id.add);
            outOfStock = (Button) itemView.findViewById(R.id.out_of_stock);
        }
    }

}
