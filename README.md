# Urban Airship Segment Integration

## Setup

1) Include the Urban Airship dependency in the project’s build.gradle file:

    repositories {
       ...

       maven {
          url  "https://urbanairship.bintray.com/android"
       }
    }


    dependencies {
       ...

       // Urban Airship SDK
       compile 'com.urbanairship.android:segment-integration:1.0.+'
    }


2) Verify the applicationId is set in the project’s build.gradle file:


    android {
       ...

       defaultConfig {
          ...

          applicationId "com.example.application"
       }
    }


3) Add the Urban Airship Integration factory:

    Analytics analytics = new Analytics.Builder(context, writeKey)
       .use(UrbanAirshipIntegration.FACTORY)
       ...
       .build();
