package com.codearlystudio.homedelivery.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.codearlystudio.homedelivery.Cart;
import com.codearlystudio.homedelivery.PaymentActivity;
import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.models.PojoTimeSlot;
import java.util.List;

public class AdapterTimeSlot extends RecyclerView.Adapter<AdapterTimeSlot.ViewHolder> {

    List<PojoTimeSlot> listTimeSlots;
    Context ctx;
    boolean FROM;

    public AdapterTimeSlot(List<PojoTimeSlot> listItems, Context ctx, boolean FROM) {
        this.listTimeSlots = listItems;
        this.ctx = ctx;
        this.FROM = FROM;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(final AdapterTimeSlot.ViewHolder holder, final int position) {
        final PojoTimeSlot pojoTimeSlot = listTimeSlots.get(position);
        holder.date.setText(pojoTimeSlot.getDateSlot());
        holder.time.setText(pojoTimeSlot.getTimeSlot());
        holder.dateTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FROM){
                    holder.dateTimeLayout.setClickable(false);
                    holder.dateTimeLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.dateTimeLayout.setClickable(true);
                        }
                    }, 500);
                    Intent intent = new Intent(ctx, PaymentActivity.class);
                    intent.putExtra(Constants.ITEM_COUNT, "" + Cart.cartItems.size());
                    intent.putExtra(Constants.SCHEDULE_DATE, pojoTimeSlot.getDateSlot());
                    intent.putExtra(Constants.SCHEDULE_TIME, pojoTimeSlot.getTimeSlot());
                    ctx.startActivity(intent);
                    Cart.dialogTime.dismiss();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listTimeSlots.size();
    }

    @NonNull
    @Override
    public AdapterTimeSlot.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (FROM){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_slot_row, parent,false);
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_slot_row_show, parent,false);
        }
        return new AdapterTimeSlot.ViewHolder(v);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView date, time;
        RelativeLayout dateTimeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView)itemView.findViewById(R.id.date);
            time = (TextView)itemView.findViewById(R.id.time);
            dateTimeLayout = (RelativeLayout) itemView.findViewById(R.id.date_time_layout);
        }
    }

}
