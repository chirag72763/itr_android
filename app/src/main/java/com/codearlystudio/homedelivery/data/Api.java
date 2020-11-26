package com.codearlystudio.homedelivery.data;

import com.codearlystudio.homedelivery.models.PojoSearch;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @retrofit2.http.POST("homedelivery/uploadUserPhoneAndName.php")
    Call<ServerResponse> uploadUserPhone(@Query("user_phone") String s1,
                                         @Query("user_name") String s2);

    @retrofit2.http.POST("homedelivery/checkIfUserExists.php")
    Call<ServerResponse> checkIfUserExists(@Query("user_phone") String s1,
                                         @Query("otp") String s2);

    @retrofit2.http.POST("homedelivery/addToCart.php")
    Call<ServerResponse> addToCart(@Query("user_id") String s1,
                                   @Query("product_id") String s2,
                                   @Query("variant_id") String s3,
                                   @Query("variant_size") String s4,
                                   @Query("variant_price") String s5);

    @retrofit2.http.POST("homedelivery/addRemoveFromSave.php")
    Call<ServerResponse> addRemoveFromSave(@Query("user_id") String s1,
                                            @Query("product_id") String s2,
                                            @Query("state") String s3);


    @retrofit2.http.POST("homedelivery/removeFromCart.php")
    Call<ServerResponse> removeFromCart(@Query("user_id") String s1,
                                        @Query("product_id") String s2);

    @retrofit2.http.POST("homedelivery/uploadFcmToken.php")
    Call<ServerResponse> uploadFcmToken(@Query("user_id") String s1,
                                        @Query("user_fcm_token") String s2);


    @retrofit2.http.POST("homedelivery/updateFcmTokenOnServer.php")
    Call<ServerResponse> updateFcmTokenOnServer(@Query("user_id") String s1);



    @retrofit2.http.POST("homedelivery/removeProductFromCart.php")
    Call<ServerResponse> removeProductFromCart(@Query("user_id") String s1,
                                        @Query("product_id") String s2,
                                        @Query("variant_id") String s3);

    @retrofit2.http.POST("homedelivery/removeAddress.php")
    Call<ServerResponse> removeAddress(@Query("address_id") String s1);

    @retrofit2.http.POST("homedelivery/updateDefaultAddress.php")
    Call<ServerResponse> updateDefaultAddress(@Query("address_id") String s1,
                                              @Query("user_id") String s2);


    @retrofit2.http.POST("homedelivery/addNewOrder.php")
    Call<ServerResponse> addNewOrder(@Query("user_id") String s1,
                                     @Query("date_time") String s2,
                                     @Query("delivery_fee") String s3);


    @retrofit2.http.POST("homedelivery/addNewAddress.php")
    Call<ServerResponse> addNewAddress(@Query("address_name") String s2,
                                       @Query("address_phone") String s3,
                                       @Query("address_pincode") String s4,
                                       @Query("address") String s5,
                                       @Query("address_landmark") String s6,
                                       @Query("address_city") String s7,
                                       @Query("address_state") String s8,
                                       @Query("address_email") String s9,
                                       @Query("user_id") String s10);


    @retrofit2.http.POST("homedelivery/removeAllFromCart.php")
    Call<ServerResponse> removeAllFromCart(@Query("user_id") String s1);

    @retrofit2.http.POST("homedelivery/returnOrder.php")
    Call<ServerResponse> returnOrder(@Query("ride_id") String s1);

    @retrofit2.http.POST("homedelivery/cancelOrder.php")
    Call<ServerResponse> cancelOrder(@Query("ride_id") String s1);

    @GET("getSearchResults.php")
    Call<List<PojoSearch>> getSearchResults(@Query("key") String s1);
}