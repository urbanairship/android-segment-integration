# Airship Segment Integration

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

5) Set up a Push Provider as described in the following [Airship docs](https://docs.airship.com/platform/android/getting-started/#push-provider-setup). Add the Airship core and your chosen push provider as a dependency in your app's build.gradle file. e.g. for FCM:
```
   dependencies {
       ...

       // Airship Core
       implementation 'com.urbanairship.android:urbanairship-core:10.0.2'

       // Airship FCM Push Provider
       implementation 'com.urbanairship.android:urbanairship-fcm:10.0.2'
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