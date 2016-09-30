package com.urbanairship.extension.segment;

import com.segment.analytics.Properties;
import com.segment.analytics.Traits;
import com.segment.analytics.integrations.GroupPayload;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.ScreenPayload;
import com.segment.analytics.integrations.TrackPayload;
import com.segment.analytics.test.GroupPayloadBuilder;
import com.segment.analytics.test.IdentifyPayloadBuilder;
import com.segment.analytics.test.ScreenPayloadBuilder;
import com.segment.analytics.test.TrackPayloadBuilder;
import com.urbanairship.UAirship;
import com.urbanairship.analytics.Analytics;
import com.urbanairship.analytics.CustomEvent;
import com.urbanairship.push.NamedUser;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.TagEditor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashSet;

import static com.segment.analytics.Utils.createTraits;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class UrbanAirshipIntegrationTest {

    @Mock UAirship airship;
    @Mock PushManager pushManager;
    @Mock NamedUser namedUser;
    @Mock Analytics analytics;

    Integration integration;

    @Before
    public void setUp() {
        initMocks(this);

        when(airship.getPushManager()).thenReturn(pushManager);
        when(airship.getAnalytics()).thenReturn(analytics);
        when(airship.getNamedUser()).thenReturn(namedUser);

        integration = new UrbanAirshipIntegration(airship);
    }

    @Test
    public void testIdentify() {
        Traits traits = createTraits("userId");
        IdentifyPayload payload = new IdentifyPayloadBuilder().traits(traits).build();

        integration.identify(payload);
        verify(namedUser).setId("userId");
    }

    @Test
    public void testGroup() {
        GroupPayload payload = new GroupPayloadBuilder().groupId("groupId").build();

        TagEditor tagEditor = mock(TagEditor.class);
        when(pushManager.editTags()).thenReturn(tagEditor);
        when(tagEditor.addTag(anyString())).thenReturn(tagEditor);

        integration.group(payload);
        verify(tagEditor).addTag("groupId");
    }

    @Test
    public void testTrack() {
        Properties properties = new Properties()
                .putRevenue(10)
                .putName("name")
                .putDiscount(5)
                .putRepeatCustomer(false);

        TrackPayload payload = new TrackPayloadBuilder()
                .event("event")
                .properties(properties)
                .build();

        integration.track(payload);
        verify(analytics).addEvent(any(CustomEvent.class));
    }

    @Test
    public void testScreen() {
        String expected = "name";
        ScreenPayload payload = new ScreenPayloadBuilder()
                .category("category")
                .name("name")
                .build();

        integration.screen(payload);
        verify(analytics).trackScreen(expected);
    }

    @Test
    public void testReset() {
        integration.reset();
        verify(namedUser).setId(null);
        verify(pushManager).setTags(new HashSet<String>());
    }

    @Test
    public void testGetUnderlyingInstance() {
        assertEquals(airship, integration.getUnderlyingInstance());
    }
}
