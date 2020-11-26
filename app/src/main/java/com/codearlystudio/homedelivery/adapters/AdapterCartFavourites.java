package com.codearlystudio.homedelivery.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codearlystudio.homedelivery.Cart;
import com.codearlystudio.homedelivery.LoginActivity;
import com.codearlystudio.homedelivery.ProductDetails;
import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.data.Api;
import com.codearlystudio.homedelivery.data.AppConfig;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.data.ServerResponse;
import com.codearlystudio.homedelivery.models.PojoProducts;
import com.codearlystudio.homedelivery.models.PojoVariants;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class AdapterCartFavourites extends RecyclerView.Adapter<AdapterCartFavourites.ViewHolder>{

    List<PojoProducts> listItems;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Intent intent;
    Vibrator vibe;

    public AdapterCartFavourites(List<PojoProducts> listItems, Context ctx) {
        this.listItems = listItems;
        context = ctx;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("CommitPrefEdits")
    @NonNull
    @Override
    public AdapterCartFavourites.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.products_row_cart,parent,false);
        intent = new Intent(context, LoginActivity.class);
        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        preferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
        return new AdapterCartFavourites.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final AdapterCartFavourites.ViewHolder holder, final int position) {
        final PojoProducts pojoProducts = listItems.get(position);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.parentLayout.setClickable(false);
                holder.parentLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.parentLayout.setClickable(true);
                    }
                }, 500);
                if (pojoProducts.getProductAvailable().equals("1")){
                    InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                    showVariants(holder, pojoProducts);
                }
                else {
                    Cart.recylcerViewFavouritesState = Cart.recyclerViewFavourites.getLayoutManager().onSaveInstanceState();
                    Intent intent = new Intent(context, ProductDetails.class);
                    intent.putExtra(Constants.PRODUCT_ID, pojoProducts.getProductId());
                    context.startActivity(intent);
                }
            }
        });

        if (pojoProducts.getProductAvailable().equals("0")){
            holder.btnOutOfStock.setVisibility(View.VISIBLE);
            holder.btnAdd.setVisibility(View.GONE);
            holder.cartCounter.setVisibility(View.GONE);
            holder.imAddProduct.setAlpha(0.2f);
        }
        else {
            if (!checkUserLogin()) {
                holder.imAddProduct.setAlpha(1f);
                boolean temp = Integer.parseInt(pojoProducts.getProductCartCount()) > 0;
                holder.cartCounter.setVisibility(temp ? View.VISIBLE : View.GONE);
                holder.btnAdd.setVisibility(temp ? View.GONE : View.VISIBLE);
                AdapterCartFavourites.ViewHolder.counter.setText(pojoProducts.getProductCartCount());
            }

        }
        holder.listVariants = new ArrayList<>();
        try {
            JSONArray jsonArray = pojoProducts.getProductVariants();
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject o = jsonArray.getJSONObject(i);
                PojoVariants pojoVariants = new PojoVariants(
                        o.getString("variant_id"),
                        o.getString("variant_size"),
                        o.getString("variant_price"),
                        o.getString("variant_original"),
                        o.getString("variant_available")
                );
                holder.listVariants.add(pojoVariants);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        Glide.with(context).load(pojoProducts.getProductImage()).placeholder(R.drawable.ic_baseline_image_24).override(512,512).into(holder.imAddProduct);
        AdapterCartFavourites.ViewHolder.tvName.setText(pojoProducts.getProductName());
        AdapterCartFavourites.ViewHolder.tvSize.setText(holder.listVariants.get(0).getVariantSize());
        AdapterCartFavourites.ViewHolder.tvCurrentPrice.setText("₹ "+holder.listVariants.get(0).getVariantPrice());
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnAdd.setClickable(false);
                holder.btnAdd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.btnAdd.setClickable(true);
                    }
                }, 500);
                if (checkUserLogin()) {
                    context.startActivity(intent);
                } else {
                    if (holder.listVariants.size() > 1){
                        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                        showVariants(holder, pojoProducts);
                    }
                    else if (holder.listVariants.size() == 1) {
                        addToCart(pojoProducts, holder.listVariants.get(0).getVariantId(), holder.listVariants.get(0).getVariantSize(), holder.listVariants.get(0).getVariantPrice());
                    }
                }
            }
        });

        AdapterCartFavourites.ViewHolder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdapterCartFavourites.ViewHolder.addToCart.setClickable(false);
                AdapterCartFavourites.ViewHolder.addToCart.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AdapterCartFavourites.ViewHolder.addToCart.setClickable(true);
                    }
                }, 500);
                if (holder.listVariants.size() > 1){
                    InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                    showVariants(holder, pojoProducts);
                }
                else if (holder.listVariants.size() == 1) {
                    addToCart(pojoProducts, holder.listVariants.get(0).getVariantId(), holder.listVariants.get(0).getVariantSize(), holder.listVariants.get(0).getVariantPrice());
                }
            }
        });
        AdapterCartFavourites.ViewHolder.removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdapterCartFavourites.ViewHolder.removeFromCart.setClickable(false);
                AdapterCartFavourites.ViewHolder.removeFromCart.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AdapterCartFavourites.ViewHolder.removeFromCart.setClickable(true);
                    }
                }, 500);
                removeFromCart(pojoProducts);
            }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        static BottomSheetDialog dialog;
        List<PojoVariants> listVariants;
        @SuppressLint("StaticFieldLeak")
        public static TextView tvName, tvCurrentPrice,
                addToCart, removeFromCart, counter, tvSize;
        Button btnAdd, btnOutOfStock;
        ImageView imAddProduct;
        LinearLayout cartCounter, parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCurrentPrice = itemView.findViewById(R.id.tv_current_price);
            tvSize = itemView.findViewById(R.id.tv_size);
            imAddProduct = itemView.findViewById(R.id.im_product_image);
            btnAdd = itemView.findViewById(R.id.btn_add);
            btnOutOfStock = itemView.findViewById(R.id.btn_outofstock);
            cartCounter = itemView.findViewById(R.id.cart_counter);
            addToCart = itemView.findViewById(R.id.add_to_cart);
            removeFromCart = itemView.findViewById(R.id.remove_from_cart);
            counter = itemView.findViewById(R.id.counter);
        }
    }
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void showVariants(final AdapterCartFavourites.ViewHolder holder, final PojoProducts pojoProducts) {
        AdapterCartFavourites.ViewHolder.dialog = new BottomSheetDialog(context);
        AdapterCartFavourites.ViewHolder.dialog.setContentView(R.layout.variant_layout);
        ImageView variantImage = AdapterCartFavourites.ViewHolder.dialog.findViewById(R.id.variant_image);
        TextView variantName = AdapterCartFavourites.ViewHolder.dialog.findViewById(R.id.variant_name);
        TextView variantDesc = AdapterCartFavourites.ViewHolder.dialog.findViewById(R.id.variant_desc);
        RecyclerView recyclerViewVariant = AdapterCartFavourites.ViewHolder.dialog.findViewById(R.id.recyclerViewVariants);
        recyclerViewVariant.setHasFixedSize(true);
        recyclerViewVariant.setLayoutManager(new LinearLayoutManager(context));
        Glide.with(context).load(pojoProducts.getProductImage()).placeholder(R.drawable.ic_baseline_image_24).into(variantImage);
        variantName.setText(pojoProducts.getProductName());
        variantDesc.setText(pojoProducts.getProductDescription());
        AdapterCartFavourites.ViewHolder.dialog.findViewById(R.id.variant_see_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart.recylcerViewFavouritesState = Objects.requireNonNull(Cart.recyclerViewFavourites.getLayoutManager()).onSaveInstanceState();
                Intent intent = new Intent(context, ProductDetails.class);
                intent.putExtra(Constants.PRODUCT_ID, pojoProducts.getProductId());
                context.startActivity(intent);
            }
        });
        variantDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart.recylcerViewFavouritesState = Objects.requireNonNull(Cart.recyclerViewFavourites.getLayoutManager()).onSaveInstanceState();
                Intent intent = new Intent(context, ProductDetails.class);
                intent.putExtra(Constants.PRODUCT_ID, pojoProducts.getProductId());
                context.startActivity(intent);
            }
        });
        AdapterCartFavouriteVariants adapterRecyclerView = new AdapterCartFavouriteVariants(holder.listVariants, context, pojoProducts.getProductId());
        recyclerViewVariant.setAdapter(adapterRecyclerView);
        adapterRecyclerView.notifyDataSetChanged();
        AdapterCartFavourites.ViewHolder.dialog.show();
    }

    void addToCart(final PojoProducts pojoProducts, final String variant_id, String variantSize, String variantPrice) {
        vibe.vibrate(5);
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.addToCart(
                preferences.getString(Constants.USER_ID, null),
                pojoProducts.getProductId(), variant_id, variantSize, variantPrice);
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        Cart.recylcerViewFavouritesState = Objects.requireNonNull(Cart.recyclerViewFavourites.getLayoutManager()).onSaveInstanceState();
                        Cart.getFavourites();
                        if (false) {
                            AdapterCartFavourites.ViewHolder.dialog.dismiss();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) { }
        });
    }

    void removeFromCart(final PojoProducts pojoProducts) {

        vibe.vibrate(5);
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.removeFromCart(preferences.getString(Constants.USER_ID, null), pojoProducts.getProductId());
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
            public void onFailure(Call<ServerResponse> call, Throwable t) { }
        });
    }

    boolean checkUserLogin(){
        return preferences.getInt(Constants.LOGIN_FLAG,0) == 0;
    }

}
