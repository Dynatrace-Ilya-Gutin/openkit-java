package com.dynatrace.openkit.protocol;

import com.dynatrace.openkit.api.Action;
import com.dynatrace.openkit.core.DeviceImpl;
import com.dynatrace.openkit.core.RootActionImpl;
import com.dynatrace.openkit.core.SynchronizedQueue;
import com.dynatrace.openkit.core.configuration.AbstractConfiguration;
import com.dynatrace.openkit.providers.LocalTimeProvider;
import com.dynatrace.openkit.providers.ThreadIDProvider;
import com.dynatrace.openkit.providers.TimeProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BeaconTest {

    private static final String APP_ID = "appID";
    private static final String APP_NAME = "appName";

    private AbstractConfiguration configuration;
    private ThreadIDProvider threadIDProvider;

    @Before
    public void setup() {
        configuration = mock(AbstractConfiguration.class);
        when(configuration.getApplicationID()).thenReturn(APP_ID);
        when(configuration.getApplicationName()).thenReturn(APP_NAME);
        when(configuration.getDevice()).thenReturn(new DeviceImpl());

        threadIDProvider = mock(ThreadIDProvider.class);
        TimeProvider.setTimeProvider(new NullTimeProvider());
    }

    @After
    public void tearDown() {
        TimeProvider.setTimeProvider(new LocalTimeProvider());
    }

    @Test
    public void canAddUserIdentifyEvent() {
        // given
        Beacon beacon = new Beacon(configuration, "127.0.0.1", threadIDProvider);
        String userID = "myTestUser";

        // when
        beacon.identifyUser(userID);
        String[] events = beacon.getEvents();

        // then
        assertThat(events, is(equalTo(new String[] { "et=60&na=" + userID + "&it=0&pa=0&s0=1&t0=0" })));
    }

    @Test
    public void canAddRootActionIfCaptureIsOn() {
        // given
        Beacon beacon = new Beacon(configuration, "127.0.0.1", threadIDProvider);
        when(configuration.isCapture()).thenReturn(true);
        String actionName = "rootAction";

        // when
        RootActionImpl rootAction = new RootActionImpl(beacon, actionName, new SynchronizedQueue<Action>());
        rootAction.leaveAction();
        String[] actions = beacon.getActions();

        // then
        assertThat(actions, is(equalTo(new String[] { "et=1&na=" + actionName + "&it=0&ca=1&pa=0&s0=1&t0=0&s1=2&t1=0" })));
    }

    @Test
    public void cannotAddRootActionIfCaptureIsOff() {
        // given
        Beacon beacon = new Beacon(configuration, "127.0.0.1", threadIDProvider);
        when(configuration.isCapture()).thenReturn(false);
        String actionName = "rootAction";

        // when
        beacon.addAction(new RootActionImpl(beacon, actionName, new SynchronizedQueue<Action>()));
        String[] actions = beacon.getActions();

        // then
        assertThat(actions, is(arrayWithSize(0)));
    }

    private class NullTimeProvider extends TimeProvider {

        @Override
        protected long provideTimestamp() {
            return 0;
        }
    }
}
