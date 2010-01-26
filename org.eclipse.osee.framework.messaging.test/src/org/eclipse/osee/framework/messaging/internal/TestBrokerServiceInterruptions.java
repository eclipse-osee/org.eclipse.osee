/*
 * Created on Jul 30, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.SystemTopic;
import org.eclipse.osee.framework.messaging.test.msg.TestMessage;


/**
 * @author b1528444
 */
public class TestBrokerServiceInterruptions extends BaseBrokerTesting {

	@org.junit.Test
	public void testBrokerComesUpAfterAppsRunning() throws Exception {
		testJMSSendShouldFail(getMessaging());
		testJMSSubscribeShouldFail(getMessaging());

		startBroker();

		testJMSSubscribeShouldPass(getMessaging());
		testJMSSendShouldPass(getMessaging());

		stopBroker();
	}

	//@org.junit.Test
	public void testBrokerGoingDownTriggersComponentEvent() throws Exception {
		startBroker();

		testJMSSendShouldPass(getMessaging());

		// have to add this so that we have an existing JMS connection,
		// otherwise no interrupted event
		getMessaging().get(DefaultNodeInfos.OSEE_JMS_DEFAULT).subscribe(TestMessages.TestTopic,
				new OseeMessagingListener() {
					@Override
					public void process(Object message,
							Map<String, Object> headers,
							ReplyConnection replyConnection) {
					}
				}, new MessageStatusTest(true));

		MessageStatusTest status1 = new MessageStatusTest(true);
		ComponentListenerTest testListener = new ComponentListenerTest(false);
		getMessaging().get(DefaultNodeInfos.OSEE_VM).subscribe( SystemTopic.JMS_HEALTH_STATUS,
				testListener, status1);
		status1.waitForStatus(500);

		assertTrue("Jms Component Listener should be notified to on.",
				testListener.isJmsAvailable());

		MessageStatusTest status2 = new MessageStatusTest(true);
		ComponentListenerTest testListener2 = new ComponentListenerTest(true);
		getMessaging().get(DefaultNodeInfos.OSEE_VM).subscribe(SystemTopic.JMS_HEALTH_STATUS,
				testListener2, status2);
		status2.waitForStatus(500);

		stopBroker();

		assertFalse("Jms Component Listener was not notified to be off.",
				testListener2.isJmsAvailable());
	}

	private class ComponentListenerTest extends OseeMessagingListener {

		private boolean jmsAvailable;

		public ComponentListenerTest(boolean initialState) {
			jmsAvailable = initialState;
		}

		boolean isJmsAvailable() {
			return jmsAvailable;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.osee.framework.messaging.OseeMessagingListener#process(java.lang.Object, java.util.Map, org.eclipse.osee.framework.messaging.ReplyConnection)
		 */
		@Override
		public void process(Object message, Map<String, Object> headers,
				ReplyConnection replyConnection) {
			TestMessage msg = (TestMessage)message;
		}

	}

	@org.junit.Test
	public void testBrokerGoingDownSendFails() throws Exception {
		startBroker();

		testJMSSendShouldPass(getMessaging());

		stopBroker();

		testJMSSendShouldFail(getMessaging());
	}

	@org.junit.Test
	public void testBrokerGoingDownSubscribeFails() throws Exception {
		startBroker();

		testJMSSubscribeShouldPass(getMessaging());

		stopBroker();

		testJMSSubscribeShouldFail(getMessaging());
	}

}
