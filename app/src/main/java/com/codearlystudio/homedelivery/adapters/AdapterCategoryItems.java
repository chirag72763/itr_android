package com.codearlystudio.homedelivery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codearlystudio.homedelivery.ExploreProducts;
import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.models.PojoCategoryItems;
import java.util.List;

public class AdapterCategoryItems extends RecyclerView.Adapter<AdapterCategoryItems.ViewHolder> {

    List<PojoCategoryItems> categoryItemsList;
    Context ctx;
    String sectionId;

    public AdapterCategoryItems(List<PojoCategoryItems> listItems, Context ctx, String sectionId) {
        this.categoryItemsList = listItems;
        this.ctx = ctx;
        this.sectionId = sectionId;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public AdapterCategoryItems.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items,parent,false);
        return new AdapterCategoryItems.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final AdapterCategoryItems.ViewHolder holder, final int position) {
        final PojoCategoryItems pojoCategoryItems = categoryItemsList.get(position);
        Glide.with(ctx).load(pojoCategoryItems.getSectionImage()).placeholder(R.drawable.ic_baseline_image_24).into(holder.imageView);
        holder.textView.setText(pojoCategoryItems.getSectionName());
        holder.itemCount.setText(pojoCategoryItems.getSectionCount() + Constants.PLUS_ITEMS);
        holder.mainContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, ExploreProducts.class);
                intent.putExtra(Constants.INTENT_SOURCE, "Master");
                intent.putExtra(Constants.CATEGORY_ID, sectionId);
                intent.putExtra(Constants.SECTION_ID, pojoCategoryItems.getSectionId());
                intent.putExtra(Constants.SECTION_NAME, pojoCategoryItems.getSectionName());
                intent.putExtra(Constants.POSITION, position);
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryItemsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView, itemCount;
        RelativeLayout mainContent;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_name);
            itemCount = itemView.findViewById(R.id.item_count);
            mainContent = itemView.findViewById(R.id.main_content);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
