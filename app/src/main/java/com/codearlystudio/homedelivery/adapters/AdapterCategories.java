package com.codearlystudio.homedelivery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codearlystudio.homedelivery.ExploreProducts;
import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.models.PojoCategoryItems;
import com.codearlystudio.homedelivery.models.PojoExProducts;
import com.codearlystudio.homedelivery.models.SectionCategories;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AdapterCategories extends RecyclerView.Adapter<AdapterCategories.ViewHolder> {

    List<SectionCategories> sectionCategoriesList;
    @SuppressLint("StaticFieldLeak")
    static Context ctx;

    public AdapterCategories(List<SectionCategories> listItems, Context ctx) {
        this.sectionCategoriesList = listItems;
        AdapterCategories.ctx = ctx;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public AdapterCategories.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_row,parent,false);
        return new AdapterCategories.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AdapterCategories.ViewHolder holder, final int position) {
        final SectionCategories pojoSectionCategories = sectionCategoriesList.get(position);
        holder.categoryItemsList = new ArrayList<>();
        try {
            for (int i = 0; i < pojoSectionCategories.getSectionItems().length(); i++){
                JSONObject o = pojoSectionCategories.getSectionItems().getJSONObject(i);
                PojoCategoryItems pojoCategoryItems = new PojoCategoryItems(
                        o.getString("section_id"),
                        o.getString("section_name"),
                        o.getString("section_image"),
                        o.getString("section_count"));
                holder.categoryItemsList.add(pojoCategoryItems);
            }
            holder.adapterCategoryItems = new AdapterCategoryItems(holder.categoryItemsList, ctx, pojoSectionCategories.getSectionCategory());
            ViewHolder.recyclerView.setAdapter(holder.adapterCategoryItems);
            holder.adapterCategoryItems.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ViewHolder.listExclusive = new ArrayList<>();
        try {
            for (int i = 0; i < pojoSectionCategories.getSectionExclusive().length(); i++){
                JSONObject o = pojoSectionCategories.getSectionExclusive().getJSONObject(i);
                PojoExProducts pojoProducts = new PojoExProducts(
                        o.getString("product_id"),
                        o.getString("product_name"),
                        o.getString("product_image"));
                ViewHolder.listExclusive.add(pojoProducts);
            }
            ViewHolder.adapterExclusive = new AdapterExclusive(ViewHolder.listExclusive, ctx);
            ViewHolder.recyclerViewExclusive.setAdapter(ViewHolder.adapterExclusive);
            ViewHolder.adapterExclusive.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int[] androidColors = ctx.getResources().getIntArray(R.array.androidcolors);
        holder.banner.setBackgroundColor(androidColors[position]);
        holder.bannerTitle.setText(pojoSectionCategories.getSectionBannerTitle());
        holder.bannerSubtitle.setText(pojoSectionCategories.getSectionBannerSubTitle());
        holder.bannerMessage.setText(pojoSectionCategories.getSectionBannerMessage());
        Glide.with(ctx).load(pojoSectionCategories.getSectionBannerImage()).placeholder(R.drawable.ic_baseline_image_24).into(holder.bannerImage);
        holder.textView.setText(pojoSectionCategories.getSectionCategoryName());
        holder.textViewDesc.setText(pojoSectionCategories.getSectionDescription());
        holder.all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, ExploreProducts.class);
                intent.putExtra(Constants.INTENT_SOURCE, "Master");
                intent.putExtra(Constants.CATEGORY_ID, pojoSectionCategories.getSectionCategory());
                intent.putExtra(Constants.SECTION_ID, "");
                intent.putExtra(Constants.SECTION_NAME, holder.categoryItemsList.get(0).getSectionName());
                intent.putExtra(Constants.POSITION, 0);
                ctx.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return sectionCategoriesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView, textViewDesc, bannerTitle, bannerSubtitle, bannerMessage;
        ImageView bannerImage;
        public static RecyclerView recyclerView, recyclerViewExclusive;
        LinearLayout all;
        RelativeLayout banner;
        List<PojoCategoryItems> categoryItemsList;
        AdapterCategoryItems adapterCategoryItems;

        public static List<PojoExProducts> listExclusive;
        @SuppressLint("StaticFieldLeak")
        public static AdapterExclusive adapterExclusive;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            bannerTitle = itemView.findViewById(R.id.banner_title);
            bannerSubtitle = itemView.findViewById(R.id.banner_subtitle);
            bannerMessage = itemView.findViewById(R.id.banner_message);
            bannerImage = itemView.findViewById(R.id.banner_image);
            banner = itemView.findViewById(R.id.banner);
            all = itemView.findViewById(R.id.all);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false));
            recyclerViewExclusive = itemView.findViewById(R.id.recyclerViewExclusive);
            recyclerViewExclusive.setHasFixedSize(true);
            recyclerViewExclusive.setNestedScrollingEnabled(true);
            recyclerViewExclusive.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));

        }
    }
}