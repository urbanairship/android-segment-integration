package com.urbanairship.extension.segment;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.segment.analytics.ValueMap;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Autopilot;
import com.urbanairship.UAirship;

/**
 * Autopilot for segment integration.
 *
 * Autopilot allows segment integration call to be called outside of {@link Application#onCreate()}.
 * When a push is received and wakes the application up, Urban Airship will used the last config to
 * takeoff.
 */
public class IntegrationAutopilot extends Autopilot {

    public static final String PRODUCTION_APP_KEY = "productionAppKey";
    public static final String PRODUCTION_APP_SECRET = "productionAppSecret";
    public static final String DEVELOPMENT_APP_KEY = "developmentAppKey";
    public static final String DEVELOPMENT_APP_SECRET = "developmentAppSecret";
    public static final String IN_PRODUCTION = "inProduction";
    public static final String GCM_SENDER = "gcmSender";

    private static final String PREFERENCE_NAME = "com.urbanairship.extension.segment";

    @Override
    public void onAirshipReady(UAirship airship) {

    }

    @Override
    public AirshipConfigOptions createAirshipConfigOptions(@NonNull Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

        return new AirshipConfigOptions.Builder()
                .setProductionAppKey(preferences.getString(PRODUCTION_APP_KEY, null))
                .setProductionAppSecret(preferences.getString(PRODUCTION_APP_SECRET, null))
                .setDevelopmentAppKey(preferences.getString(DEVELOPMENT_APP_KEY, null))
                .setDevelopmentAppSecret(preferences.getString(DEVELOPMENT_APP_SECRET, null))
                .setInProduction(preferences.getBoolean(IN_PRODUCTION, false))
                .setGcmSender(preferences.getString(GCM_SENDER, null))
                .build();
    }

    /**
     * Updates the Urban Airship config.
     * @param context The application context.
     * @param settings Segment settings.
     */
    static void UpdateConfig(Context context, ValueMap settings) {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(PRODUCTION_APP_KEY, settings.getString(PRODUCTION_APP_KEY))
                .putString(PRODUCTION_APP_SECRET, settings.getString(PRODUCTION_APP_SECRET))
                .putString(DEVELOPMENT_APP_KEY, settings.getString(DEVELOPMENT_APP_KEY))
                .putString(DEVELOPMENT_APP_SECRET, settings.getString(DEVELOPMENT_APP_SECRET))
                .putString(GCM_SENDER, settings.getString(GCM_SENDER))
                .putBoolean(IN_PRODUCTION, settings.getBoolean(IN_PRODUCTION, false))
                .apply();
    }



}