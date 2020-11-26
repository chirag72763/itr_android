package com.codearlystudio.homedelivery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codearlystudio.homedelivery.adapters.AdapterOrderProducts;
import com.codearlystudio.homedelivery.data.Api;
import com.codearlystudio.homedelivery.data.AppConfig;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.data.ServerResponse;
import com.codearlystudio.homedelivery.models.OrderProducts;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

public class OrderDetails extends AppCompatActivity {

    Intent intent;
    TextView orderId, orderPaymentMode, orderDate, orderContact, orderAddress, itemTotal,
            itemDeliveryFee, grandTotal, orderMessage, cancelOrder, returnOrder, status1, status2, status3, status4;
    ImageView check1, check2, check3, cancel1, cancel2, cancel3;
    View line1, line2;
    LinearLayout order1, order2;
    String rideId = "";
    List<OrderProducts> listOrderProducts;
    AdapterOrderProducts adapterOrderProducts;
    RecyclerView recyclerView;
    @SuppressLint("StaticFieldLeak")
    static Context ctx;
    @SuppressLint("StaticFieldLeak")
    static RelativeLayout mainContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        ctx = this;
        declareComponents();
    }

    void declareComponents() {
        mainContent = findViewById(R.id.main_content);
        recyclerView = findViewById(R.id.recyclerViewOrderDetails);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        intent = getIntent();
        rideId = intent.getStringExtra(Constants.RIDE_ID);
        orderId = findViewById(R.id.order_id);
        cancelOrder = findViewById(R.id.cancel_order);
        returnOrder = findViewById(R.id.return_order);
        orderPaymentMode = findViewById(R.id.payment_mode);
        orderDate = findViewById(R.id.order_date);
        orderContact = findViewById(R.id.order_contact);
        orderAddress = findViewById(R.id.order_address);
        orderMessage = findViewById(R.id.order_message);
        itemTotal = findViewById(R.id.item_total);
        itemDeliveryFee = findViewById(R.id.delivery_fee);
        grandTotal = findViewById(R.id.to_pay);
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelAlert();
            }
        });
        returnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReturnAlert();
            }
        });
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.call_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:+917276359585"));
                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OrderDetails.this, new String[]{Manifest.permission.CALL_PHONE}, 741);
                }
                else { startActivity(phoneIntent); }
            }
        });
        order1 = findViewById(R.id.order_layout1);
        order2 = findViewById(R.id.order_layout2);
        status1 = findViewById(R.id.status1);
        status2 = findViewById(R.id.status2);
        status3 = findViewById(R.id.status3);
        status4 = findViewById(R.id.status4);
        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        cancel1 = findViewById(R.id.cancel1);
        cancel2 = findViewById(R.id.cancel2);
        cancel3 = findViewById(R.id.cancel3);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
    }

    void getOrderDetails() {
        listOrderProducts = new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                Constants.FETCH_ORDER_DETAILS + rideId, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("text");
                    orderId.setText(rideId);
                    orderDate.setText(jsonArray.getJSONObject(0).getString("order_date"));
                    orderContact.setText(jsonArray.getJSONObject(0).getString("ride_time"));
                    orderAddress.setText(jsonArray.getJSONObject(0).getString("order_contact") +"\n"+jsonArray.getJSONObject(0).getString("order_address"));
                    itemTotal.setText("₹ "+Float.parseFloat(jsonArray.getJSONObject(0).getString("item_total")));
                    itemDeliveryFee.setText("₹ "+Float.parseFloat(jsonArray.getJSONObject(0).getString("delivery_fee")));
                    float grand = Float.parseFloat(jsonArray.getJSONObject(0).getString("item_total")) + Float.parseFloat(jsonArray.getJSONObject(0).getString("delivery_fee"));
                    grandTotal.setText("₹ "+grand);
                    orderMessage.setText(jsonArray.getJSONObject(0).getString("order_message"));
                    JSONArray jsonArray1 = jsonArray.getJSONObject(0).getJSONArray("product_details");
                    for (int i = 0; i < jsonArray1.length(); i++){
                        JSONObject o = jsonArray1.getJSONObject(i);
                        OrderProducts pojoProducts = new OrderProducts(
                                o.getString("product_name"),
                                o.getString("product_image"),
                                o.getString("variant_size"),
                                o.getString("variant_price"),
                                o.getString("product_quantity"),
                                o.getString("total_price")
                        );
                        listOrderProducts.add(pojoProducts);
                    }
                    adapterOrderProducts = new AdapterOrderProducts(listOrderProducts, ctx);
                    recyclerView.setAdapter(adapterOrderProducts);
                    adapterOrderProducts.notifyDataSetChanged();
                    cancelOrder.setVisibility(View.GONE);
                    returnOrder.setVisibility(View.GONE);
                    switch (jsonArray.getJSONObject(0).getString("order_status")){
                        case "0":
                            showOrderPlaced();
                            cancelOrder.setVisibility(View.VISIBLE);
                            break;
                        case "1":
                            cancelOrder.setVisibility(View.VISIBLE);
                            showOrderAccepted();
                            break;
                        case "2":
                            showOrderCancelledByAdmin();
                            break;
                        case "3":
                            showOrderDelivered();
                            returnOrder.setVisibility(View.VISIBLE);
                            break;
                        case "4":
                            showOrderCancelledByUser();
                            break;
                        case "5":
                            showReturnScreen();
                            break;
                        case "6":
                            showIssueResolved();
                            break;
                    }
                }catch (JSONException ignored){ }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    void showOrderPlaced() {
        order1.setVisibility(View.VISIBLE);
        order2.setVisibility(View.GONE);
        check1.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
        status1.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        status1.setTypeface(null, Typeface.BOLD);
        status1.setTextSize(16.0f);
    }

    void showOrderAccepted() {
        order1.setVisibility(View.VISIBLE);
        order2.setVisibility(View.GONE);
        check2.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
        status2.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        status2.setTypeface(null, Typeface.BOLD);
        status2.setTextSize(16.0f);
        line1.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        status1.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        check1.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    void showOrderDelivered() {
        order1.setVisibility(View.VISIBLE);
        order2.setVisibility(View.GONE);
        status3.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        check3.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
        status3.setTextSize(16.0f);
        status3.setTypeface(null, Typeface.BOLD);
        check2.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
        check1.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
        status2.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        status1.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        line1.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        line2.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
    }

    void showOrderCancelledByAdmin() {
        order1.setVisibility(View.VISIBLE);
        order2.setVisibility(View.GONE);
        check2.setVisibility(View.GONE);
        cancel2.setVisibility(View.VISIBLE);
        status2.setTextColor(ctx.getResources().getColor(R.color.Red));
        status2.setText("Order\nDeclined");
        line1.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        status1.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        check1.setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    void showOrderCancelledByUser() {
        order1.setVisibility(View.VISIBLE);
        order2.setVisibility(View.GONE);
        check1.setVisibility(View.GONE);
        cancel1.setVisibility(View.VISIBLE);
        status1.setTextColor(ctx.getResources().getColor(R.color.red_color));
        status1.setText("Order\nCancelled");
        status2.setTextSize(13.0f);
        status2.setTypeface(null, Typeface.NORMAL);
        status2.setTextColor(ctx.getResources().getColor(R.color.grey_medium));
        check2.setColorFilter(ContextCompat.getColor(ctx, R.color.grey_medium), android.graphics.PorterDuff.Mode.SRC_IN);
        line1.setBackgroundColor(ctx.getResources().getColor(R.color.grey_bg));
    }

    void showReturnScreen() {
        order1.setVisibility(View.GONE);
        order2.setVisibility(View.VISIBLE);
    }

    void showIssueResolved() {
        order1.setVisibility(View.GONE);
        order2.setVisibility(View.VISIBLE);
        status4.setText("Issue\nResolved");
        status4.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
    }

    void showCancelAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(Constants.CANCEL_ORDER);
        builder.setMessage(Constants.CANCEL_ORDER_CONFIRM);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelOrderFromServer();
            }
        });
        builder.show();
    }

    void showReturnAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(Constants.RETURN_ORDER);
        builder.setMessage(Constants.RETURN_ORDER_CONFIRM);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnOrderFromServer();
            }
        });
        builder.show();
    }


    void cancelOrderFromServer(){
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.cancelOrder(rideId);
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @SuppressLint("Assert")
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        getOrderDetails();
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    void returnOrderFromServer(){
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.returnOrder(rideId);
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @SuppressLint("Assert")
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        getOrderDetails();
                        final Snackbar snackbar = Snackbar.make(mainContent, serverResponse.getMessage(), Snackbar.LENGTH_LONG);
                        snackbar.setAction(Constants.OK, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.setActionTextColor(Color.WHITE);
                        snackbar.show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) { }
        });
    }

    @Override
    public void onBackPressed() {
        if (intent != null){
            if (intent.getStringExtra(Constants.INTENT_SOURCE).equals("FCM")){
                Intent intent = new Intent(ctx, MasterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            else {
                super.onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isOnline();
    }

    void isOnline() {
        final BottomSheetDialog dialogInternet = new BottomSheetDialog(this);
        dialogInternet.setContentView(R.layout.no_internet_layout);
        dialogInternet.setCancelable(false);
        dialogInternet.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInternet.dismiss();
                isOnline();
            }
        });
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            dialogInternet.dismiss();
            getOrderDetails();
        }
        else {
            dialogInternet.dismiss();
            dialogInternet.show();
        }
    }

}
