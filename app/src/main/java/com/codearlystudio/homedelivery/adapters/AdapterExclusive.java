package com.codearlystudio.homedelivery.adapters;

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
import com.codearlystudio.homedelivery.models.PojoExProducts;
import java.util.List;

public class AdapterExclusive extends RecyclerView.Adapter<AdapterExclusive.ViewHolder>{

    List<PojoExProducts> listItems;
    Context context;

    public AdapterExclusive(List<PojoExProducts> listItems, Context ctx) {
        this.listItems = listItems;
        context = ctx;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public AdapterExclusive.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.products_row_master,parent,false);
        return new AdapterExclusive.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AdapterExclusive.ViewHolder holder, final int position) {
        final PojoExProducts pojoProducts = listItems.get(position);
        Glide.with(context).load(pojoProducts.getProductImage()).override(500,500).placeholder(R.drawable.ic_baseline_image_24).into(holder.imAddProduct);
        holder.tvName.setText(pojoProducts.getProductName());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetails.class);
                intent.putExtra(Constants.PRODUCT_ID, pojoProducts.getProductId());
                context.startActivity(intent);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView imAddProduct;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            tvName = itemView.findViewById(R.id.tv_name);
            imAddProduct = itemView.findViewById(R.id.im_product_image);
        }
    }
    @Override
    public int getItemCount() {
        return listItems.size();
    }

}
