package com.codearlystudio.homedelivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codearlystudio.homedelivery.adapters.AdapterProducts;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.models.PojoProducts;
import com.codearlystudio.homedelivery.models.PojoSections;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint("StaticFieldLeak")
public class ExploreProducts extends AppCompatActivity {

    static Intent intent;
    static String sectionId = "", url = "", categoryId, intentSectionId = "";
    static int intentPosition = 0;
    public static Context ctx;
    static List<PojoProducts> listProducts;
    static AdapterProducts adapterProducts;
    public static RecyclerView recyclerView;
    public static Parcelable recylcerViewState;
    public static SharedPreferences preferences;
    SharedPreferences.Editor editor;
    List<PojoSections> listSections;
    public static TabLayout tabLayout;
    static int selectedTab = 0, navFlag = 0;
    public static RelativeLayout coordinatorLayout;
    static TextView sectionTitle, sectionCount, textCount;
    static FloatingActionButton fabCart;
    static LinearLayout linearBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_products);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        ctx = this;
        declareComponents();
        isOnline();
    }

    @SuppressLint("CommitPrefEdits")
    void declareComponents(){
        recylcerViewState = null;
        preferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        coordinatorLayout = findViewById(R.id.main_content);
        intent = getIntent();
        categoryId = intent.getStringExtra(Constants.CATEGORY_ID);
        intentSectionId = intent.getStringExtra(Constants.SECTION_ID);
        intentPosition = intent.getIntExtra(Constants.POSITION, 0);
        sectionTitle = findViewById(R.id.section_title);
        sectionCount = findViewById(R.id.section_count);
        linearBox = findViewById(R.id.section_box);
        editor = preferences.edit();
        fabCart = findViewById(R.id.fab_cart);
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, Cart.class);
                intent.putExtra(Constants.INTENT_SOURCE, "Explore Products");
                startActivity(intent); }});
        textCount = findViewById(R.id.text_count);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                linearBox.setVisibility(View.GONE);
                recyclerView.removeAllViews();
                recylcerViewState = null;
                navFlag = selectedTab;
                selectedTab = tab.getPosition();
                if (listProducts != null) {
                    listProducts.clear();
                }
                sectionId = listSections.get(tab.getPosition()).getSectionId();
                sectionTitle.setText( listSections.get(tab.getPosition()).getSectionName());
                getProducts();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        recylcerViewState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
    }

    public void fetchSections() {
        listSections = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constants.FETCH_SECTIONS + categoryId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("text");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        PojoSections pojoSections = new PojoSections(
                                o.getString("section_id"),
                                o.getString("section_name"),
                                o.getString("section_category"),
                                o.getString("section_image"),
                                o.getString("section_count")
                        );
                        listSections.add(pojoSections);
                        tabLayout.addTab(tabLayout.newTab().setText(pojoSections.getSectionName()));
                    }
                    if (listSections.size() != 0){
                        sectionId = listSections.get(0).getSectionId();
                    }
                }catch (JSONException ignored){ }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }
    public static void getProducts(){
        if (!intentSectionId.equals("")){
            sectionId = intentSectionId;
        }
        else {
            Objects.requireNonNull(tabLayout.getTabAt(selectedTab)).select();
        }
        listProducts = new ArrayList<>();
        url = Constants.PRODUCT_FETCH_URL + "?user_id=" + preferences.getString(Constants.USER_ID,null) +"&section_id="+sectionId;
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url.replaceAll(" ", "%20"), new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("text");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        PojoProducts pojoProducts = new PojoProducts(
                                o.getString("product_id"),
                                o.getString("product_name"),
                                o.getString("product_tags"),
                                o.getString("product_image"),
                                o.getString("product_description"),
                                o.getString("product_available"),
                                o.getString("product_cart_count"),
                                o.getString("product_saved"),
                                o.getJSONArray("product_variant")
                        );
                        listProducts.add(pojoProducts);
                    }
                    linearBox.setVisibility(View.VISIBLE);
                    sectionCount.setText("( " +listProducts.size() + " items )");
                    adapterProducts = new AdapterProducts(listProducts, ctx);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
                    recyclerView.setAdapter(adapterProducts);
                    if (recylcerViewState != null){
                        Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(recylcerViewState);
                    }
                    adapterProducts.notifyDataSetChanged();
                    if (preferences.getInt(Constants.LOGIN_FLAG,0) == 1){
                        getCartCount();
                    }
                    if (!intentSectionId.equals("")){
                        Objects.requireNonNull(tabLayout.getTabAt(intentPosition)).select();
                        intentSectionId = "";
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
            public void onErrorResponse(VolleyError error) { }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listSections != null && listSections.size() != 0){
            isOnlineProduct();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            fetchSections();
        }
        else {
            dialogInternet.dismiss();
            dialogInternet.show();
        }
    }
    void isOnlineProduct() {
        final BottomSheetDialog dialogInternet = new BottomSheetDialog(this);
        dialogInternet.setContentView(R.layout.no_internet_layout);
        dialogInternet.setCancelable(false);
        dialogInternet.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInternet.dismiss();
                isOnlineProduct();
            }
        });
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            dialogInternet.dismiss();
            getProducts();
        }
        else {
            dialogInternet.dismiss();
            dialogInternet.show();
        }
    }

}