package com.codearlystudio.homedelivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codearlystudio.homedelivery.adapters.AdapterProductVariant;
import com.codearlystudio.homedelivery.adapters.AdapterSimilarProducts;
import com.codearlystudio.homedelivery.data.Api;
import com.codearlystudio.homedelivery.data.AppConfig;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.data.ServerResponse;
import com.codearlystudio.homedelivery.models.PojoSimilarProducts;
import com.codearlystudio.homedelivery.models.PojoVariantDetails;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

@SuppressLint("StaticFieldLeak")
public class ProductDetails extends AppCompatActivity {
    static Vibrator vibe;
    static String saved = "";
    static String productId = "";
    public static SharedPreferences preferences;
    SharedPreferences.Editor editor;
    static Context ctx;
    static List<PojoVariantDetails> listVariants;
    static List<PojoSimilarProducts> listSimilar;
    static AdapterProductVariant adapterProductVariant;
    static AdapterSimilarProducts adapterSimilarProducts;
    static RecyclerView recyclerViewVariant, recyclerViewSimilar;
    static ImageView productImage, imgSave;
    static TextView productName, productDescription, sectionName, textCount;
    static FloatingActionButton fabCart;
    LinearLayoutManager horizontalLayout;
    public static RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ctx = this;
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Intent intent = getIntent();
        productId = intent.getStringExtra(Constants.PRODUCT_ID);
        declareComponents();
    }

    @SuppressLint("CommitPrefEdits")
    void declareComponents(){
        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        relativeLayout = findViewById(R.id.main_content);
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        sectionName = findViewById(R.id.section_name);
        imgSave = findViewById(R.id.img_saved);
        fabCart = findViewById(R.id.fab_cart);
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, Cart.class);
                intent.putExtra(Constants.INTENT_SOURCE, "Product Details");
                startActivity(intent); }});
        textCount = findViewById(R.id.text_count);
        recyclerViewVariant = findViewById(R.id.recyclerViewVariants);
        recyclerViewVariant.setHasFixedSize(true);
        recyclerViewVariant.setNestedScrollingEnabled(true);
        recyclerViewVariant.setLayoutManager(new LinearLayoutManager(ctx));
        horizontalLayout = new GridLayoutManager(ctx, 3);
        recyclerViewSimilar = findViewById(R.id.recyclerViewSimilar);
        recyclerViewSimilar.setHasFixedSize(true);
        recyclerViewSimilar.setNestedScrollingEnabled(true);
        recyclerViewSimilar.setLayoutManager(horizontalLayout);
        preferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        editor = preferences.edit();
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgSave.setClickable(false);
                imgSave.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgSave.setClickable(true);
                    }
                }, 500);
                if (preferences.getInt(Constants.LOGIN_FLAG, 0 ) == 1){
                    addRemoveFromSave(saved);
                }
                else {
                    startActivity(new Intent(ctx, LoginActivity.class));
                }
            }
        });
    }

    static void addRemoveFromSave(final String state){
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
                                Snackbar.make(relativeLayout, Constants.ADDED_TO_FAVOURITES, Snackbar.LENGTH_SHORT).show();
                                break;
                            case "1":
                                Snackbar.make(relativeLayout, Constants.REMOVED_FROM_FAVOURITES, Snackbar.LENGTH_SHORT).show();
                                break;
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getProduct(false);
                            }
                        },1000);
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) { }
        });
    }

    public static void getProduct(final boolean loadPicture){
        listVariants = new ArrayList<>();
        listSimilar = new ArrayList<>();
        String url = Constants.PRODUCT_FETCH_DETAILS + "?user_id=" + preferences.getString(Constants.USER_ID,null) +"&product_id="+productId;
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("product_variant");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        PojoVariantDetails pojoVariantsDetails = new PojoVariantDetails(
                                o.getString("variant_id"),
                                o.getString("variant_size"),
                                o.getString("variant_price"),
                                o.getString("variant_original"),
                                o.getString("variant_available"),
                                o.getString("variant_count")
                                );
                        listVariants.add(pojoVariantsDetails);
                    }
                    adapterProductVariant = new AdapterProductVariant(listVariants, ctx, productId, jsonObject.getString("product_available"));
                    recyclerViewVariant.setAdapter(adapterProductVariant);
                    adapterProductVariant.notifyDataSetChanged();
                    switch (jsonObject.getString("is_saved")){
                        case "0":
                            imgSave.setImageBitmap(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.heart_icon));
                            saved = "0";
                            break;
                        case "1":
                            imgSave.setImageBitmap(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.heart_black));
                            saved = "1";
                            break;
                    }
                    if (loadPicture) {
                        JSONArray jsonArray1 = jsonObject.getJSONArray("similar_products");
                        for (int i = 0; i < jsonArray1.length(); i++){
                            JSONObject o = jsonArray1.getJSONObject(i);
                            PojoSimilarProducts pojoSimilarProducts = new PojoSimilarProducts(
                                    o.getString("product_id"),
                                    o.getString("product_name"),
                                    o.getString("product_image")
                            );
                            listSimilar.add(pojoSimilarProducts);
                            adapterSimilarProducts = new AdapterSimilarProducts(listSimilar, ctx);
                            recyclerViewSimilar.setAdapter(adapterSimilarProducts);
                            adapterSimilarProducts.notifyDataSetChanged();
                        }
                        Picasso.with(ctx).load(jsonObject.getString("product_image")).into(productImage);
                        productName.setText(jsonObject.getString("product_name"));
                        productDescription.setText(jsonObject.getString("product_description"));
                        sectionName.setText(jsonObject.getString("section_name"));
                    }
                    if (preferences.getInt(Constants.LOGIN_FLAG,0) == 1){
                        getCartCount();
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


    @Override
    protected void onResume() {
        super.onResume();
        isOnline();
    }

    public static void getCartCount(){
        String cartUrl = Constants.CART_COUNTER_URL + preferences.getString(Constants.USER_ID,null);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, cartUrl, new Response.Listener<String>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(String response) {
                if (!response.equals("0")){
                    textCount.setVisibility(View.VISIBLE);
                    textCount.setText(response);
                    fabCart.setVisibility(View.VISIBLE);
                }
                else {
                    textCount.setVisibility(View.GONE);
                    fabCart.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
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
            getProduct(true);
        }
        else {
            dialogInternet.dismiss();
            dialogInternet.show();
        }
    }

}