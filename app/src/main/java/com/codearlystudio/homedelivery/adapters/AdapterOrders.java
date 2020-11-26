package com.codearlystudio.homedelivery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.codearlystudio.homedelivery.MyOrders;
import com.codearlystudio.homedelivery.OrderDetails;
import com.codearlystudio.homedelivery.R;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.models.Orders;
import java.util.List;
import java.util.Objects;

public class AdapterOrders extends RecyclerView.Adapter<AdapterOrders.ViewHolder> {

    List<Orders> listOrders;
    Context ctx;
    String FROM;

    public AdapterOrders(List<Orders> listItems, Context ctx, String FROM) {
        this.listOrders = listItems;
        this.ctx = ctx;
        this.FROM = FROM;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @NonNull
    @Override
    public AdapterOrders.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row,parent,false);
        return new AdapterOrders.ViewHolder(v);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final AdapterOrders.ViewHolder holder, final int position) {
        final Orders pojoOrders = listOrders.get(position);
        holder.orderId.setText("#" +pojoOrders.getOrderId());
        switch (pojoOrders.getOrderStatus()){
            case "0":
                showOrderPlaced(holder);
                break;
            case "1":
                showOrderAccepted(holder);
                break;
            case "2":
                showOrderCancelledByAdmin(holder);
                break;
            case "3":
                showOrderDelivered(holder);
                break;
            case "4":
                showOrderCancelledByUser(holder);
                break;
            case "5":
                showReturnScreen(holder);
                break;
            case "6":
                showIssueResolved(holder);
                break;
        }
        holder.orderProducts.setText(pojoOrders.getProductName());
        holder.orderedAt.setText(pojoOrders.getOrderedOn());
        holder.orderTotal.setText("₹ " + Float.parseFloat(pojoOrders.getOrderTotal()));
        holder.orderCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FROM.equals("ORDERS")){
                    MyOrders.recylcerViewState = Objects.requireNonNull(MyOrders.recyclerView.getLayoutManager()).onSaveInstanceState();
                }
                else {
                    MyOrders.recylcerViewState = Objects.requireNonNull(MyOrders.recyclerViewPast.getLayoutManager()).onSaveInstanceState();
                }
                Intent intent = new Intent(ctx, OrderDetails.class);
                intent.putExtra(Constants.RIDE_ID, pojoOrders.getOrderId());
                intent.putExtra(Constants.INTENT_SOURCE, "MyOrders");
                ctx.startActivity(intent);
            }
        });
    }

    void showOrderPlaced(AdapterOrders.ViewHolder holder) {
        holder.order1.setVisibility(View.VISIBLE);
        holder.order2.setVisibility(View.GONE);
        holder.check1.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.status1.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        holder.status1.setTypeface(null, Typeface.BOLD);
        holder.status1.setTextSize(16.0f);
    }

    void showOrderAccepted(AdapterOrders.ViewHolder holder) {
        holder.order1.setVisibility(View.VISIBLE);
        holder.order2.setVisibility(View.GONE);
        holder.check2.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.status2.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        holder.status2.setTypeface(null, Typeface.BOLD);
        holder.status2.setTextSize(16.0f);
        holder.line1.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        holder.status1.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        holder.check1.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    void showOrderDelivered(AdapterOrders.ViewHolder holder) {
        holder.order1.setVisibility(View.VISIBLE);
        holder.order2.setVisibility(View.GONE);
        holder.status3.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        holder.check3.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.status3.setTextSize(16.0f);
        holder.status3.setTypeface(null, Typeface.BOLD);
        holder.check2.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.check1.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.status2.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        holder.status1.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        holder.line1.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        holder.line2.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
    }

    void showOrderCancelledByAdmin(AdapterOrders.ViewHolder holder) {
        holder.order1.setVisibility(View.VISIBLE);
        holder.order2.setVisibility(View.GONE);
        holder.check2.setVisibility(View.GONE);
        holder.cancel2.setVisibility(View.VISIBLE);
        holder.status2.setTextColor(ctx.getResources().getColor(R.color.Red));
        holder.status2.setText("Order\nDeclined");
        holder.line1.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        holder.status1.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        holder.check1.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    void showOrderCancelledByUser(AdapterOrders.ViewHolder holder) {
        holder.order1.setVisibility(View.VISIBLE);
        holder.order2.setVisibility(View.GONE);
        holder.check1.setVisibility(View.GONE);
        holder.cancel1.setVisibility(View.VISIBLE);
        holder.status1.setTextColor(ctx.getResources().getColor(R.color.red_color));
        holder.status1.setText("Order\nCancelled");
    }

    void showReturnScreen(AdapterOrders.ViewHolder holder) {
        holder.order1.setVisibility(View.GONE);
        holder.order2.setVisibility(View.VISIBLE);
    }

    void showIssueResolved(AdapterOrders.ViewHolder holder) {
        holder.order1.setVisibility(View.GONE);
        holder.order2.setVisibility(View.VISIBLE);
        holder.status4.setText("Issue\nResolved");
        holder.status4.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
    }





    @Override
    public int getItemCount() {
        return listOrders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        CardView orderCardview;
        TextView orderId, orderProducts, orderedAt, orderTotal,
                status1, status2, status3, status4;
        ImageView check1, check2, check3, cancel1, cancel2, cancel3;
        View line1, line2;
        LinearLayout order1, order2;

        public ViewHolder(View itemView) {
            super(itemView);
            order1 = (LinearLayout) itemView.findViewById(R.id.order_layout1);
            order2 = (LinearLayout) itemView.findViewById(R.id.order_layout2);
            orderCardview = (CardView) itemView.findViewById(R.id.order_cardview);
            orderId = (TextView) itemView.findViewById(R.id.tv_order_id);
            orderProducts = (TextView) itemView.findViewById(R.id.tv_order_items);
            orderedAt = (TextView) itemView.findViewById(R.id.tv_order_date);
            orderTotal = (TextView) itemView.findViewById(R.id.tv_order_total);
            status1 = (TextView)itemView.findViewById(R.id.status1);
            status2 = (TextView)itemView.findViewById(R.id.status2);
            status3 = (TextView)itemView.findViewById(R.id.status3);
            status4 = (TextView)itemView.findViewById(R.id.status4);
            check1 = (ImageView)itemView.findViewById(R.id.check1);
            check2 = (ImageView)itemView.findViewById(R.id.check2);
            check3 = (ImageView)itemView.findViewById(R.id.check3);
            cancel1 = (ImageView)itemView.findViewById(R.id.cancel1);
            cancel2 = (ImageView)itemView.findViewById(R.id.cancel2);
            cancel3 = (ImageView)itemView.findViewById(R.id.cancel3);
            line1 = (View)itemView.findViewById(R.id.line1);
            line2 = (View)itemView.findViewById(R.id.line2);

        }

    }
}
