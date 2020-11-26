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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codearlystudio.homedelivery.ExploreProducts;
import com.codearlystudio.homedelivery.LoginActivity;
import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.data.Api;
import com.codearlystudio.homedelivery.data.AppConfig;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.data.ServerResponse;
import com.codearlystudio.homedelivery.models.PojoVariants;
import java.util.List;
import retrofit2.Call;

public class AdapterVariants extends RecyclerView.Adapter<AdapterVariants.ViewHolder> {

    List<PojoVariants> listVariants;
    Context ctx;
    String productId;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Vibrator vibe;

    public AdapterVariants(List<PojoVariants> listItems, Context ctx, String productId) {
        this.listVariants = listItems;
        this.ctx = ctx;
        this.productId = productId;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("CommitPrefEdits")
    @NonNull
    @Override
    public AdapterVariants.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.variant_row,parent,false);
        preferences = ctx.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
        vibe = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        return new AdapterVariants.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final AdapterVariants.ViewHolder holder, final int position) {
        final PojoVariants pojoVariants = listVariants.get(position);
        if (pojoVariants.getVariantAvailable().equals("0")){
            holder.addButton.setEnabled(false);
            holder.addButton.setVisibility(View.GONE);
            holder.outButton.setVisibility(View.VISIBLE);
            holder.variantRadio.setTextColor(ctx.getResources().getColor(R.color.grey_medium));
            holder.variantOriginal.setTextColor(ctx.getResources().getColor(R.color.grey_medium));
            holder.variantPrice.setTextColor(ctx.getResources().getColor(R.color.grey_medium));
        }
        holder.variantRadio.setText(pojoVariants.getVariantSize());
        holder.variantPrice.setText("₹ "+Float.parseFloat(pojoVariants.getVariantPrice()));
        holder.variantOriginal.setText("₹ "+Float.parseFloat(pojoVariants.getVariantOriginal()));
        holder.variantOriginal.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.addButton.setClickable(false);
                holder.addButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.addButton.setClickable(true);
                    }
                }, 500);
                if (checkUserLogin()) {
                    ctx.startActivity(new Intent(ctx, LoginActivity.class));
                } else {
                    addToCart(productId, pojoVariants.getVariantId(), pojoVariants.getVariantSize(), pojoVariants.getVariantPrice());
                }
            }
        });
    }

    boolean checkUserLogin(){
        return preferences.getInt(Constants.LOGIN_FLAG,0) == 0;
    }

    void addToCart(String productId, String variantId, String variantSize, String variantPrice) {
        vibe.vibrate(5);
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.addToCart(preferences.getString(Constants.USER_ID, null),
                productId, variantId, variantSize, variantPrice);
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        ExploreProducts.recylcerViewState = ExploreProducts.recyclerView.getLayoutManager().onSaveInstanceState();
                        ExploreProducts.getProducts();
                        AdapterProducts.ViewHolder.dialog.dismiss();
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {}
        });
    }

    @Override
    public int getItemCount() {
        return listVariants.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView variantPrice, variantOriginal, variantPercentage, variantRadio;
        Button addButton, outButton;

        public ViewHolder(View itemView) {
            super(itemView);
            addButton = itemView.findViewById(R.id.add_button);
            outButton = itemView.findViewById(R.id.out_button);
            variantPrice = itemView.findViewById(R.id.variant_price);
            variantOriginal = itemView.findViewById(R.id.variant_original);
            variantPercentage = itemView.findViewById(R.id.variant_percentage);
            variantRadio = itemView.findViewById(R.id.variant_radio);
        }
    }

}