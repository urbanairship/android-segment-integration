package com.urbanairship.extension.segment;

import androidx.annotation.NonNull;

import com.segment.analytics.Properties;
import com.segment.analytics.Traits;
import com.segment.analytics.integrations.GroupPayload;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.ScreenPayload;
import com.segment.analytics.integrations.TrackPayload;
import com.segment.analytics.test.GroupPayloadBuilder;
import com.segment.analytics.test.ScreenPayloadBuilder;
import com.segment.analytics.test.TrackPayloadBuilder;
import com.urbanairship.UAirship;
import com.urbanairship.analytics.Analytics;
import com.urbanairship.analytics.CustomEvent;
import com.urbanairship.analytics.Event;
import com.urbanairship.json.JsonException;
import com.urbanairship.json.JsonList;
import com.urbanairship.json.JsonMap;
import com.urbanairship.json.JsonValue;
import com.urbanairship.push.NamedUser;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.TagEditor;
import com.urbanairship.push.TagGroupsEditor;
import com.urbanairship.push.TagGroupsMutation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class UrbanAirshipIntegrationTest {

    @Mock
    UAirship airship;
    @Mock
    PushManager pushManager;
    @Mock
    NamedUser namedUser;
    @Mock
    Analytics analytics;

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
        TestTagGroupEditor editor = new TestTagGroupEditor();
        when(airship.getNamedUser().editTagGroups()).thenReturn(editor);

        IdentifyPayload payload = new IdentifyPayload.Builder()
                .userId("userId")
                .build();

        integration.identify(payload);
        verify(namedUser).setId("userId");

        JsonMap expectedOperation = JsonMap.newBuilder()
                .put("set", JsonMap.newBuilder()
                        .put("segment-integration", JsonList.EMPTY_LIST)
                        .build())
                .build();

        assertEquals(editor.appliedMutations.get(0).toJsonValue(), expectedOperation.toJsonValue());
    }

    @Test
    public void testIdentifyWithTraits() throws JsonException {
        TestTagGroupEditor editor = new TestTagGroupEditor();
        when(airship.getNamedUser().editTagGroups()).thenReturn(editor);

        Traits traits = new Traits();
        traits.put("cool", true);
        traits.put("story", true);
        traits.put("foo", false);
        traits.putFirstName("Captain");

        IdentifyPayload payload = new IdentifyPayload.Builder()
                .userId("userId")
                .traits(traits)
                .build();

        integration.identify(payload);
        verify(namedUser).setId("userId");

        JsonMap expectedOperation = JsonMap.newBuilder()
                .put("set", JsonMap.newBuilder()
                        .put("segment-integration", JsonValue.wrap(new String[] {"cool", "story"}))
                        .build())
                .build();

        assertEquals(editor.appliedMutations.get(0).toJsonValue(), expectedOperation.toJsonValue());
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
        verify(analytics).addEvent(argThat(new ArgumentMatcher<Event>() {

            @Override
            public boolean matches(Event argument) {
                if (!(argument instanceof CustomEvent)) {
                    return false;
                }

                CustomEvent event = (CustomEvent) argument;
                if (!"event".equals(event.getEventName())) {
                    return false;
                }

                if (event.getEventValue().intValue() != 10) {
                    return false;
                }

                if (!event.getTransactionId().equals("cdp")) {
                    return false;
                }

                return true;
            }
        }));
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

    private class TestTagGroupEditor extends TagGroupsEditor {
        List<TagGroupsMutation> appliedMutations;

        @Override
        protected void onApply(@NonNull List<TagGroupsMutation> collapsedMutations) {
            appliedMutations = collapsedMutations;
        }
    }
}
