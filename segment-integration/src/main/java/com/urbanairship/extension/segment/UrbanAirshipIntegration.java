/*
 Copyright Airship and Contributors
*/

package com.urbanairship.extension.segment;

import com.segment.analytics.Analytics;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.GroupPayload;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.ScreenPayload;
import com.segment.analytics.integrations.TrackPayload;
import com.urbanairship.Autopilot;
import com.urbanairship.UAirship;
import com.urbanairship.analytics.CustomEvent;
import com.urbanairship.util.UAStringUtil;

import java.util.HashSet;
import java.util.Set;


/**
 * Airship Segment integration.
 */
public class UrbanAirshipIntegration extends Integration<UAirship> {

    public static final String URBAN_AIRSHIP_KEY = "Urban Airship";
    private static final String TAGS_GROUP_KEY = "segment-integration";

    private static final long MAX_TAG_LENGTH = 128;

    private static final String EVENT_TRANSACTION_ID = "cdp";
    private static final String TRACK_REVENUE_KEY = "revenue";
    private static final String TRACK_VALUE_KEY = "value";


    public static final Factory FACTORY = new Factory() {

        @Override
        public Integration<?> create(ValueMap settings, Analytics analytics) {
            SegmentAutopilot.updateSegmentSettings(analytics.getApplication(), settings);
            Autopilot.automaticTakeOff(analytics.getApplication());

            // Make sure we actually are flying before returning the integration
            if (UAirship.isFlying() || UAirship.isTakingOff()) {
                return new UrbanAirshipIntegration(UAirship.shared());
            }

            return null;
        }

        @Override
        public String key() {
            return URBAN_AIRSHIP_KEY;
        }
    };

    private final UAirship airship;

    UrbanAirshipIntegration(UAirship airship) {
        this.airship = airship;
    }

    @Override
    public void identify(IdentifyPayload identify) {
        airship.getNamedUser().setId(identify.userId());


        Set<String> tags = new HashSet<>();
        if (identify.traits() != null) {
            for (String trait : identify.traits().keySet()) {
                if (UAStringUtil.isEmpty(trait) || trait.length() > MAX_TAG_LENGTH) {
                    continue;
                }

                Object value = identify.traits().get(trait);
                if (!(value instanceof Boolean)) {
                    continue;
                }

                Boolean boolValue = (Boolean) value;
                if (boolValue) {
                    tags.add(trait);
                }
            }
        }

        airship.getNamedUser()
                .editTagGroups()
                .setTags(TAGS_GROUP_KEY, tags)
                .apply();
    }

    @Override
    public void group(GroupPayload group) {
        String groupId = group.groupId();
        if (UAStringUtil.isEmpty(groupId)) {
            return;
        }

        airship.getPushManager()
                .editTags()
                .addTag(groupId)
                .apply();
    }

    @Override
    public void track(TrackPayload track) {
        CustomEvent.Builder eventBuilder = new CustomEvent.Builder(track.event())
                .setTransactionId(EVENT_TRANSACTION_ID);

        if (track.properties().containsKey(TRACK_REVENUE_KEY)) {
            eventBuilder.setEventValue(track.properties().revenue());
        } else if (track.properties().containsKey(TRACK_VALUE_KEY)) {
            eventBuilder.setEventValue(track.properties().value());
        }

        for (String key : track.properties().keySet()) {
            Object value = track.properties().get(key);

            if (value instanceof String) {
                eventBuilder.addProperty(key, (String) value);
            } else if (value instanceof Integer) {
                eventBuilder.addProperty(key, (Integer) value);
            } else if (value instanceof Long) {
                eventBuilder.addProperty(key, (Long) value);
            } else if (value instanceof Boolean) {
                eventBuilder.addProperty(key, (Boolean) value);
            } else if (value instanceof Number) {
                eventBuilder.addProperty(key, ((Number) value).doubleValue());
            }
        }

        airship.getAnalytics().addEvent(eventBuilder.build());
    }

    @Override
    public void screen(ScreenPayload screen) {
        airship.getAnalytics().trackScreen(screen.event());
    }

    @Override
    public void reset() {
        airship.getNamedUser().setId(null);
        airship.getPushManager().setTags(new HashSet<String>());
    }

    @Override
    public UAirship getUnderlyingInstance() {
        return airship;
    }

}
