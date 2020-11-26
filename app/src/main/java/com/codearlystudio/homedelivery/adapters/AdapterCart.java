package com.codearlystudio.homedelivery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codearlystudio.homedelivery.Cart;
import com.codearlystudio.homedelivery.ProductDetails;
import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.data.Api;
import com.codearlystudio.homedelivery.data.AppConfig;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.data.ServerResponse;
import com.codearlystudio.homedelivery.models.CartItems;
import java.util.List;
import retrofit2.Call;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.ViewHolder> {

    List<CartItems> cartItemsList;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Vibrator vibe;
    Context ctx;

    public AdapterCart(List<CartItems> listItems, Context ctx) {
        this.cartItemsList = listItems;
        this.ctx = ctx;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }



    @SuppressLint("CommitPrefEdits")
    @NonNull
    @Override
    public AdapterCart.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_list,parent,false);
        vibe = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        preferences = ctx.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
        return new AdapterCart.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final AdapterCart.ViewHolder holder, final int position) {
        final CartItems cartItems = cartItemsList.get(position);
        if (cartItems.getVariantAvailable().equals("0")){
            holder.cartCounter.setVisibility(View.GONE);
            holder.outOfStock.setVisibility(View.VISIBLE);
        }
        Glide.with(ctx)
                .load(cartItems.getProductImage())
                .placeholder(R.drawable.ic_baseline_image_24)
                .override(512,512)
                .into(holder.itemImage);
        holder.itemName.setText(cartItems.getProductName());
        holder.itemSize.setText(cartItems.getVariantSize());
        holder.itemPrice.setText("₹ " + cartItems.getTotalProductPrice());
        holder.counter.setText(cartItems.getCartQuanity());
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
                addToCart(cartItems);
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
                removeFromCart(cartItems);
            }
        });
        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemImage.setClickable(false);
                holder.itemImage.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.itemImage.setClickable(true);
                    }
                }, 500);
                Intent intent = new Intent(ctx, ProductDetails.class);
                intent.putExtra(Constants.PRODUCT_ID, cartItems.getProductId());
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemName, itemSize, itemPrice, counter, addToCart, removeFromCart;
        ImageView itemImage;
        Button outOfStock;
        LinearLayout mainContent, cartCounter;

        public ViewHolder(View itemView) {
            super(itemView);
            mainContent = (LinearLayout) itemView.findViewById(R.id.main_content) ;
            cartCounter = (LinearLayout) itemView.findViewById(R.id.cart_counter) ;
            itemImage = (ImageView)itemView.findViewById(R.id.item_image) ;
            itemName = (TextView)itemView.findViewById(R.id.cart_item_name);
            itemSize = (TextView)itemView.findViewById(R.id.cart_item_size);
            itemPrice = (TextView)itemView.findViewById(R.id.cart_item_price);
            counter = (TextView)itemView.findViewById(R.id.counter);
            addToCart = (TextView)itemView.findViewById(R.id.add_to_cart);
            removeFromCart = (TextView)itemView.findViewById(R.id.remove_from_cart);
            outOfStock = (Button) itemView.findViewById(R.id.btn_outofstock);
        }
    }

    void addToCart(final CartItems cartItems) {
        vibe.vibrate(1);
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.addToCart(
                preferences.getString(Constants.USER_ID, null),
                cartItems.getProductId(), cartItems.getVariantId(), cartItems.getVariantSize(), cartItems.getVariantPrice());
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        Cart.getFavourites();
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    void removeFromCart(final CartItems cartItems) {

        vibe.vibrate(1);
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.removeFromCart(preferences.getString(Constants.USER_ID, null), cartItems.getProductId());
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @SuppressLint("Assert")
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        Cart.getFavourites();
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }


}