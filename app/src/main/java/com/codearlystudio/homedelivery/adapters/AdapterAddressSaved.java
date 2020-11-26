package com.codearlystudio.homedelivery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codearlystudio.homedelivery.MasterActivity;
import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.data.Api;
import com.codearlystudio.homedelivery.data.AppConfig;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.data.ServerResponse;
import com.codearlystudio.homedelivery.models.PojoAddress;

import java.util.List;

import retrofit2.Call;

public class AdapterAddressSaved extends RecyclerView.Adapter<AdapterAddressSaved.ViewHolder> {

    List<PojoAddress> pojoAddresses;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context ctx;

    public AdapterAddressSaved(List<PojoAddress> listItems, Context ctx) {
        this.pojoAddresses = listItems;
        this.ctx = ctx;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @NonNull
    @SuppressLint("CommitPrefEdits")
    @Override
    public AdapterAddressSaved.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.address_row,parent,false);
        preferences = ctx.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
        return new AdapterAddressSaved.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final AdapterAddressSaved.ViewHolder holder, final int position) {
        final PojoAddress pojoAddress = pojoAddresses.get(position);
        holder.addressName.setText(pojoAddress.getAddressName());
        holder.addressLine1.setText(pojoAddress.getAddress()+", "+pojoAddress.getAddressLandmark());
        holder.addressLine2.setText(pojoAddress.getAddressCity()+", "+pojoAddress.getAddressState()+" - "+pojoAddress.getAddresPincode());
        holder.addressLine3.setText(pojoAddress.getAddressPhone()+" | "+pojoAddress.getAddressEmail());
        holder.radio.setClickable(false);
        holder.radio.setChecked(Integer.parseInt(pojoAddress.getAddressDefault()) == 1);
        holder.deleteAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.deleteAddress.setClickable(false);
                holder.deleteAddress.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.deleteAddress.setClickable(true);
                    }
                }, 500);
                deleteAddress(pojoAddress.getAddressId());
            }
        });
        holder.addressRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.addressRow.setClickable(false);
                holder.addressRow.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.addressRow.setClickable(true);
                    }
                }, 500);
                updateDefaultAddress(pojoAddress.getAddressId());
            }
        });
    }

    void updateDefaultAddress(String addressId) {
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.updateDefaultAddress(addressId, preferences.getString(Constants.USER_ID, null));
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @SuppressLint("Assert")
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        MasterActivity.showAddresses();
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) { }
        });
    }

    void deleteAddress(String addressId) {
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.removeAddress(addressId);
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @SuppressLint("Assert")
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        MasterActivity.showAddresses();
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) { }
        });
    }

    @Override
    public int getItemCount() {
        return pojoAddresses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        RadioButton radio;
        TextView addressName, addressLine1, addressLine2, addressLine3;
        ImageView deleteAddress;
        RelativeLayout addressRow;

        public ViewHolder(View itemView) {
            super(itemView);
            addressName = (TextView)itemView.findViewById(R.id.address_name);
            addressLine1 = (TextView)itemView.findViewById(R.id.address_line1);
            addressLine2 = (TextView)itemView.findViewById(R.id.address_line2);
            addressLine3 = (TextView)itemView.findViewById(R.id.address_line3);
            deleteAddress = (ImageView)itemView.findViewById(R.id.delete_address);
            radio = (RadioButton)itemView.findViewById(R.id.radio);
            addressRow = (RelativeLayout)itemView.findViewById(R.id.address_row);
        }
    }
}
