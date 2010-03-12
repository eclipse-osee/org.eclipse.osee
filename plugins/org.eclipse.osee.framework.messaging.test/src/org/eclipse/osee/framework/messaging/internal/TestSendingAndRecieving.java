/*
 * Created on Jul 30, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.test.msg.TestMessage;

/**
 * @author b1528444
 */
public class TestSendingAndRecieving extends BaseBrokerTesting {

	
	private final int messagesToSend = 10;
	private int messagesReceived = 0;

	@org.junit.Test
	public void testSendingAndRecievingUsingJMS() {
//		stopBroker();
		startBroker();
		try {
			messagesReceived = 0;
			MessageStatusTest status1 = new MessageStatusTest(true);
			getMessaging().get(DefaultNodeInfos.OSEE_JMS_DEFAULT).subscribe(
					TestMessages.JMS_TOPIC, new OseeMessagingListener(TestMessage.class) {
						@Override
						public void process(Object message,
								Map<String, Object> headers,
								ReplyConnection replyConnection) {
							TestMessage msg = (TestMessage) message;
							System.out.println(msg.getMessage());
							messagesReceived++;
						}
					}, status1);
			status1.waitForStatus(500);
			Thread sending = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < messagesToSend; i++) {
						MessageStatusTest status2 = new MessageStatusTest(true);
						try {
							TestMessage message = new TestMessage();
							message.setMessage("TestMessage " + i);
							getMessaging().get(
									DefaultNodeInfos.OSEE_JMS_DEFAULT).send(
											TestMessages.JMS_TOPIC, message, status2);
							System.out.println("sending " + i);
						} catch (OseeCoreException ex) {
							fail(ex.getMessage());
						}
						status2.waitForStatus(500);

					}
				}
			});
			sending.start();
			Thread.sleep(10000);

			assertTrue(String.format("sent[%d] != recieved[%d]",
					messagesToSend, messagesReceived),
					messagesToSend == messagesReceived);

		} catch (Exception ex) {
		   ex.printStackTrace();
			fail(ex.getMessage());
		}
		stopBroker();
	}

	@org.junit.Test
	public void testSendingAndRecievingUsingVM() {
		try {
			messagesReceived = 0;
			MessageStatusTest status1 = new MessageStatusTest(true);
			getMessaging().get(DefaultNodeInfos.OSEE_VM).subscribe(TestMessages.VM_TOPIC,
					new OseeMessagingListener() {
						@Override
						public void process(Object message,
								Map<String, Object> headers,
								ReplyConnection replyConnection) {
							messagesReceived++;
						}
					}, status1);
			Thread sending = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < messagesToSend; i++) {
						TestMessage message = new TestMessage();
						message.setMessage("TestMessage " + i);
						MessageStatusTest status2 = new MessageStatusTest(true);
						try {
							getMessaging().get(DefaultNodeInfos.OSEE_VM).send(
									TestMessages.VM_TOPIC, message, status2);
						} catch (OseeCoreException ex) {
							fail(ex.getMessage());
						}
						status2.waitForStatus(500);
					}
				}
			});
			sending.start();
			Thread.sleep(10000);

			assertTrue(String.format("sent[%d] != recieved[%d]",
					messagesToSend, messagesReceived),
					messagesToSend == messagesReceived);

		} catch (Exception ex) {
			assertFalse(ex.getMessage(), true);
		}
	}
}
