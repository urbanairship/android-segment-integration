# Airship Segment Integration

The Segment and Airship partnership is growing and we have recently launched a new bi-directional server-side integration. You can read about this [new integration here](https://docs.airship.com/partners/segment/).

The Segment platform has a limitation and only allows a single Destination endpoint per integration. This SDK integration and the new Server-side integration cannot be options on the Segment website under Airship.

For the immediate future, this integration will be moved to a Private-Beta within Segment. Customers will not see an interruption in service and Airship will continue to maintain this repo with each SDK release. Customers that have already been configured for the SDK integration will still be able to see the required elements for that integration within Segment.

The Segment and Airship teams are working together to identify a path forward for supporting both Destination integrations. In the meantime, we do not suggest that existing users configure both Destinations. This will result in duplicate events.

If you have any concerns or questions, please reach out to our Partner Integration Team <partner-integration-ua@airship.com>

## Setup

1) Add Segment to your app as defined in the following [Segment docs](https://segment.com/docs/sources/mobile/android/).

2) Include the Airship dependency in the project’s build.gradle file:
```
    repositories {
        ...

        maven {
            url  "https://urbanairship.bintray.com/android"
        }
    }


    dependencies {
        ...

        // Airship SDK
        compile 'com.urbanairship.android:segment-integration:+'
     }
```

3) Verify the applicationId is set in the project’s build.gradle file:
```
    android {
        ...

        defaultConfig {
            ...

            applicationId "com.example.application"
        }
    }
```

4) Add the Airship Integration factory:
```
    Analytics analytics = new Analytics.Builder(context, writeKey)
        .use(UrbanAirshipIntegration.FACTORY)
        ...
        .build();
```

5) Set up a Push Provider as described in the following [Airship docs](https://docs.airship.com/platform/android/getting-started/#push-provider-setup). Add your chosen push provider as a dependency in your app's build.gradle file. e.g. for FCM:
```
   dependencies {
       ...

       // Airship FCM Push Provider
       implementation 'com.urbanairship.android:urbanairship-fcm:11.0.1'
   }
```

6) Enable push notifications after the Airship Integration is ready, e.g.
```
   // Enable user notifications
   analytics.onIntegrationReady(UrbanAirshipIntegration.URBAN_AIRSHIP_KEY, new Analytics.Callback<Object>() {
       @Override
       public void onReady(Object instance) {
          UAirship airship = (UAirship) instance;
          airship.getPushManager().setUserNotificationsEnabled(true);
       }
   });
```

7) Configure the Airship Integration as described in the following [Segment docs](https://segment.com/docs/destinations/urban-airship/).