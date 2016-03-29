package com.urbanairship.extension.segment;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.segment.analytics.ValueMap;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Autopilot;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.notifications.DefaultNotificationFactory;
import com.urbanairship.util.UAStringUtil;

/**
 * Autopilot for segment integration.
 * <p/>
 * Autopilot allows segment integration call to be called outside of {@link Application#onCreate()}.
 * When a push is received and wakes the application up, Urban Airship will used the last config to
 * takeoff.
 */
public class IntegrationAutopilot extends Autopilot {

    public static final String APP_KEY = "appKey";
    public static final String APP_SECRET = "appSecret";
    public static final String GCM_SENDER = "gcmSender";
    public static final String NOTIFICATION_ICON = "notificationIcon";
    public static final String NOTIFICATION_ACCENT_COLOR = "notificationAccentColor";
    private static final String PREFERENCE_NAME = "com.urbanairship.extension.segment";

    private SharedPreferences preferences;

    @Override
    public void onAirshipReady(UAirship airship) {
        Context context = UAirship.getApplicationContext();

        SharedPreferences preferences = getPreferences(context);

        DefaultNotificationFactory notificationFactory = new DefaultNotificationFactory(context);

        String accentColor = preferences.getString(NOTIFICATION_ACCENT_COLOR, null);
        if (!UAStringUtil.isEmpty(accentColor)) {
            try {
                notificationFactory.setColor(Color.parseColor(accentColor));
            } catch (IllegalArgumentException e) {
                Logger.error("Segment Integration: Invalid accent color " + accentColor + ": " + e.getMessage());
            }
        }

        String notificationIconName = preferences.getString(NOTIFICATION_ICON, null);
        if (!UAStringUtil.isEmpty(notificationIconName)) {
            int id  = context.getResources().getIdentifier(notificationIconName, "drawable", context.getPackageName());
            if (id > 0) {
                notificationFactory.setSmallIconId(id);
            } else {
                Logger.error("Segment Integration: Unable to find notification icon with name: " + notificationIconName);
            }
        }

        airship.getPushManager().setNotificationFactory(notificationFactory);
    }

    @Override
    public AirshipConfigOptions createAirshipConfigOptions(@NonNull Context context) {
        SharedPreferences preferences = getPreferences(context);

        AirshipConfigOptions.Builder builder = new AirshipConfigOptions.Builder();

        // Call before applyDefaultProperties so we can default inProduction to true, but allow
        // local airshipconfig.properties to override it.
        builder.setInProduction(true)
                .applyDefaultProperties(context);

        // Apply production App credentials from Segment
        if (preferences.contains(APP_KEY) && preferences.contains(APP_SECRET)) {
            builder.setProductionAppKey(preferences.getString(APP_KEY, null))
                    .setProductionAppSecret(preferences.getString(APP_SECRET, null));
        }

        // Apply GCM Sender from Segment
        if (preferences.contains(GCM_SENDER)) {
            builder.setGcmSender(preferences.getString(GCM_SENDER, null));
        }

        return builder.build();
    }

    public SharedPreferences getPreferences(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        }

        return preferences;
    }

    /**
     * Updates the Urban Airship config.
     *
     * @param context The application context.
     * @param settings Segment settings.
     */
    static void UpdateConfig(Context context, ValueMap settings) {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(APP_KEY, settings.getString(APP_KEY))
                .putString(APP_SECRET, settings.getString(APP_SECRET))
                .putString(GCM_SENDER, settings.getString(GCM_SENDER))
                .putString(GCM_SENDER, settings.getString(GCM_SENDER))
                .putString(NOTIFICATION_ICON, settings.getString(NOTIFICATION_ICON))
                .putString(NOTIFICATION_ACCENT_COLOR, settings.getString(NOTIFICATION_ACCENT_COLOR))
                .apply();
    }
}