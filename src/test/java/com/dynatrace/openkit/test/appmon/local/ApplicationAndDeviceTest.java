/***************************************************
 * (c) 2016-2017 Dynatrace LLC
 *
 * @author: Christian Schwarzbauer
 */
package com.dynatrace.openkit.test.appmon.local;

import java.util.ArrayList;

import org.junit.Test;

import com.dynatrace.openkit.test.TestHTTPClient.Request;
import com.dynatrace.openkit.test.shared.ApplicationAndDeviceTestShared;

public class ApplicationAndDeviceTest extends AbstractLocalAppMonTest {

	@Test
	public void test() {
		ApplicationAndDeviceTestShared.test(openKit, TEST_IP);

		ArrayList<Request> sentRequests = openKitTestImpl.getSentRequests();
		String expectedBeacon = "vv=3&va=7.0.0000&ap=" + TEST_APPLICATION_ID + "&an=" + TEST_APPLICATION_NAME + "&vn=2017.42.3141&pt=1&vi=" + testConfiguration.getVisitorID() + "&sn=1&ip=" + TEST_IP + "&os=Windows+10&mf=Dynatrace&md=OpenKitTester&tv=1006000&ts=1004000&tx=1010000&et=19&it=1&pa=0&s0=3&t0=5000&et=1&na=ApplicationAndDeviceTestAction&it=1&ca=1&pa=0&s0=1&t0=3000&s1=2&t1=1000";

		validateDefaultRequests(sentRequests, expectedBeacon);
	}

}
