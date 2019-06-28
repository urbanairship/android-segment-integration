/* Copyright Airship and Contributors */

package com.urbanairship.sample;

import android.app.Application;
import com.segment.analytics.Analytics;

import com.urbanairship.extension.segment.UrbanAirshipIntegration;
import com.urbanairship.UAirship;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create an analytics client with the given context and Segment write key.
        Analytics analytics = new Analytics.Builder(this, "3bzatGNsExyMlDyEQUjk1rOhBUVPB1tt")
                // Enable this to record certain application events automatically!
                .trackApplicationLifecycleEvents()
                // Enable this to record screen views automatically!
                .recordScreenViews()
                // Use the Airship Integration factory
                .use(UrbanAirshipIntegration.FACTORY)
                .logLevel(Analytics.LogLevel.VERBOSE)
                .build();

        // Set the initialized instance as a globally accessible instance.
        Analytics.setSingletonInstance(analytics);

        // Log some events
        analytics.track("Android Sample Started");
        analytics.group("segment");
        analytics.screen("home");
        analytics.identify("test-user");
        analytics.flush();

        // Enable user notifications
        analytics.onIntegrationReady(UrbanAirshipIntegration.URBAN_AIRSHIP_KEY, new Analytics.Callback<Object>() {
            @Override
            public void onReady(Object instance) {
                UAirship airship = (UAirship) instance;
                airship.getPushManager().setUserNotificationsEnabled(true);
            }
        });
    }

}
