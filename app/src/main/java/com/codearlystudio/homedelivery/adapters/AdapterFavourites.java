package com.codearlystudio.homedelivery.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codearlystudio.homedelivery.Favourites;
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
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class AdapterFavourites extends RecyclerView.Adapter<AdapterFavourites.ViewHolder>{

    List<PojoProducts> listItems;
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Intent intent;
    Vibrator vibe;

    public AdapterFavourites(List<PojoProducts> listItems, Context ctx) {
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
    public AdapterFavourites.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.products_row,parent,false);
        intent = new Intent(context, LoginActivity.class);
        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        preferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
        return new AdapterFavourites.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final AdapterFavourites.ViewHolder holder, final int position) {
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
                    Favourites.recylcerViewState = Favourites.recyclerView.getLayoutManager().onSaveInstanceState();
                    Intent intent = new Intent(context, ProductDetails.class);
                    intent.putExtra(Constants.PRODUCT_ID, pojoProducts.getProductId());
                    context.startActivity(intent);
                }
            }
        });

        holder.saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.saved.setClickable(false);
                holder.saved.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.saved.setClickable(true);
                    }
                }, 500);
                addRemoveFromSave(pojoProducts.getProductId(), pojoProducts.getProductSaved());
            }
        });
        holder.removed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.saved.setClickable(false);
                holder.saved.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.saved.setClickable(true);
                    }
                }, 500);
                addRemoveFromSave(pojoProducts.getProductId(), pojoProducts.getProductSaved());
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
                holder.counter.setText(pojoProducts.getProductCartCount());
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
        Glide.with(context).load(pojoProducts.getProductImage()).placeholder(R.drawable.ic_baseline_image_24).into(holder.imAddProduct);
        holder.tvName.setText(pojoProducts.getProductName());
        holder.tvDesc.setText(pojoProducts.getProductDescription());
        holder.tvCurrentPrice.setText("₹ "+holder.listVariants.get(0).getVariantPrice());
        if (pojoProducts.getProductSaved().equals("1")){
            holder.saved.setVisibility(View.VISIBLE);
            holder.removed.setVisibility(View.GONE);
        }
        else {
            holder.saved.setVisibility(View.GONE);
            holder.removed.setVisibility(View.VISIBLE);
        }
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
                        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                        showVariants(holder, pojoProducts);
                    }
                    else if (holder.listVariants.size() == 1) {
                        addToCart(pojoProducts, holder.listVariants.get(0).getVariantId(), holder.listVariants.get(0).getVariantSize(), holder.listVariants.get(0).getVariantPrice());
                    }
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
                if (holder.listVariants.size() > 1){
                    InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                    showVariants(holder, pojoProducts);
                }
                else if (holder.listVariants.size() == 1) {
                    addToCart(pojoProducts, holder.listVariants.get(0).getVariantId(), holder.listVariants.get(0).getVariantSize(), holder.listVariants.get(0).getVariantPrice());
                }
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
                removeFromCart(pojoProducts);
            }
        });
    }

    void addRemoveFromSave(final String productId, final String state){
        vibe.vibrate(5);
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.addRemoveFromSave(preferences.getString(Constants.USER_ID, null), productId, state);
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        switch (state) {
                            case "0":
                                Snackbar.make(Favourites.coordinatorLayout, Constants.ADDED_TO_FAVOURITES, Snackbar.LENGTH_SHORT).show();
                                break;
                            case "1":
                                Snackbar.make(Favourites.coordinatorLayout, Constants.REMOVED_FROM_FAVOURITES, Snackbar.LENGTH_SHORT).show();
                                break;
                        }
                        Favourites.getProducts();
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        static BottomSheetDialog dialog;
        List<PojoVariants> listVariants;
        TextView tvName, tvCurrentPrice, addToCart, removeFromCart, counter, tvDesc;
        Button btnAdd, btnOutOfStock;
        ImageView imAddProduct, saved, removed;
        LinearLayout parentLayout;
        RelativeLayout cartCounter;

        public ViewHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            tvName = itemView.findViewById(R.id.tv_name);
            saved = itemView.findViewById(R.id.saved);
            removed = itemView.findViewById(R.id.removed);
            tvCurrentPrice = itemView.findViewById(R.id.tv_current_price);
            tvDesc = itemView.findViewById(R.id.tv_desc);
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
    String calculateDiscountPercentage(String original, String current){
        double difference = Double.parseDouble(original) - Double.parseDouble(current);
        int temp = (int) ((difference/(Integer.parseInt(original))) * 100);
        return ""+ temp + "% OFF";
    }
    public void showVariants(final AdapterFavourites.ViewHolder holder, final PojoProducts pojoProducts) {
        AdapterFavourites.ViewHolder.dialog = new BottomSheetDialog(context);
        AdapterFavourites.ViewHolder.dialog.setContentView(R.layout.variant_layout);
        ImageView variantImage = AdapterFavourites.ViewHolder.dialog.findViewById(R.id.variant_image);
        TextView variantName = AdapterFavourites.ViewHolder.dialog.findViewById(R.id.variant_name);
        TextView variantDesc = AdapterFavourites.ViewHolder.dialog.findViewById(R.id.variant_desc);
        RecyclerView recyclerViewVariant = AdapterFavourites.ViewHolder.dialog.findViewById(R.id.recyclerViewVariants);
        recyclerViewVariant.setHasFixedSize(true);
        recyclerViewVariant.setLayoutManager(new LinearLayoutManager(context));
        Glide.with(context).load(pojoProducts.getProductImage()).placeholder(R.drawable.ic_baseline_image_24).into(variantImage);
        variantName.setText(pojoProducts.getProductName());
        variantDesc.setText(pojoProducts.getProductDescription());
        AdapterFavourites.ViewHolder.dialog.findViewById(R.id.variant_see_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favourites.recylcerViewState = Favourites.recyclerView.getLayoutManager().onSaveInstanceState();
                Intent intent = new Intent(context, ProductDetails.class);
                intent.putExtra(Constants.PRODUCT_ID, pojoProducts.getProductId());
                context.startActivity(intent);
            }
        });
        variantDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favourites.recylcerViewState = Favourites.recyclerView.getLayoutManager().onSaveInstanceState();
                Intent intent = new Intent(context, ProductDetails.class);
                intent.putExtra(Constants.PRODUCT_ID, pojoProducts.getProductId());
                context.startActivity(intent);
            }
        });
        AdapterFavouriteVariants adapterRecyclerView = new AdapterFavouriteVariants(holder.listVariants, context, pojoProducts.getProductId());
        recyclerViewVariant.setAdapter(adapterRecyclerView);
        adapterRecyclerView.notifyDataSetChanged();
        AdapterFavourites.ViewHolder.dialog.show();
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
                        Favourites.recylcerViewState = Favourites.recyclerView.getLayoutManager().onSaveInstanceState();
                        Favourites.getProducts();
                        if (false) {
                            AdapterFavourites.ViewHolder.dialog.dismiss();
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
                        Favourites.getProducts();
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
