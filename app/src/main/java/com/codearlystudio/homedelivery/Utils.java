package com.codearlystudio.homedelivery;

import android.content.Context;
import android.preference.PreferenceManager;

public class Utils {

    public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";

    public static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates).apply();
    }
}
