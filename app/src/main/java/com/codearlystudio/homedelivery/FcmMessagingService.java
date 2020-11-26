package com.codearlystudio.homedelivery;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.codearlystudio.homedelivery.data.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class FcmMessagingService extends FirebaseMessagingService {

    NotificationChannel mChannel;
    Intent intent;
    SharedPreferences preferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        preferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        try {
            JSONObject json = new JSONObject(remoteMessage.getData().toString());
            if (remoteMessage.getData().size() > 0) {
                displayNotificationIntent(json);
            }
        } catch (JSONException ignored) { }
    }

    private void displayNotificationIntent(JSONObject jsonObject) {
        try {
            String CHANNEL_ID = "fcm_notifications";// The id of the channel.
            CharSequence name = getString(R.string.default_notification_channel_id);// The user-visible name of the channel.
            @SuppressLint("InlinedApi") int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            }
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_default));
            final JSONObject data = jsonObject.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            String intent_where = data.getString("intent_where");
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            switch (intent_where){
                case "order_details":
                    intent = new Intent(this, OrderDetails.class);
                    intent.putExtra(Constants.INTENT_SOURCE, "FCM");
                    intent.putExtra(Constants.RIDE_ID, data.getString("ride_id"));
                    break;

                case "cart":
                    intent = new Intent(this, Cart.class);
                    intent.putExtra(Constants.INTENT_SOURCE, "FCM");
                    break;

                case "promotions":
                    intent = new Intent(this, MainActivity.class);
                    String picture = data.getString("banner_url");
                    Bitmap bmp = Picasso.with(getApplicationContext()).load(picture).get();
                    builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bmp));
                    break;
                case "specific_promotion":
                    intent = new Intent(this, ExploreProducts.class);
                    intent.putExtra(Constants.INTENT_SOURCE, "FCM");
                    intent.putExtra(Constants.CATEGORY_ID, data.getString("category_id"));
                    intent.putExtra(Constants.SECTION_ID, data.getString("section_id"));
                    intent.putExtra(Constants.SECTION_NAME, data.getString("section_name"));
                    intent.putExtra(Constants.POSITION, Integer.parseInt(data.getString("position")));
                    String picture1 = data.getString("banner_url");
                    Bitmap bmp1 = Picasso.with(getApplicationContext()).load(picture1).get();
                    builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bmp1));
                    builder.setLargeIcon(bmp1);
                    break;
                default:
                    intent = new Intent(this, MainActivity.class);
                    break;
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
            builder.setContentTitle(title);
            builder.setContentText(message);
            builder.setSmallIcon(R.drawable.logo_notification);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            builder.setPriority(Notification.PRIORITY_MAX);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            builder.setChannelId(CHANNEL_ID);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
                notificationManager.createNotificationChannel(mChannel);
            }
            int unique_id = (int) System.currentTimeMillis();
            notificationManager.notify(unique_id, builder.build());
        } catch (Exception ignored) {}
    }
}
