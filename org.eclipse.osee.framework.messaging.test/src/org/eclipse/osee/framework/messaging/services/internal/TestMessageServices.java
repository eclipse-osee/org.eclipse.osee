/*
 * Created on Jan 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import junit.framework.Assert;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.future.NodeInfo;
import org.eclipse.osee.framework.messaging.internal.BaseBrokerTesting;
import org.eclipse.osee.framework.messaging.internal.TestMessages;
import org.eclipse.osee.framework.messaging.services.RemoteServiceLookup;
import org.eclipse.osee.framework.messaging.services.RemoteServiceRegistrar;
import org.eclipse.osee.framework.messaging.services.ServiceInfoPopulator;
import org.eclipse.osee.framework.messaging.services.ServiceNotification;
import org.eclipse.osee.framework.messaging.services.messages.ServiceDescriptionPair;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;

/**
 * @author b1528444
 *
 */
public class TestMessageServices extends BaseBrokerTesting{

	private static String BROKER_URI_SERVER = "tcp://localhost:61606";
	private static String BROKER_URI = "failover:tcp://localhost:61606";
	@org.junit.Before
	public void startBroker(){
		try {
			startEmbeddedBroker("testBroker", BROKER_URI_SERVER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
	}
	
	@org.junit.After
	public void stopBroker(){
		try {
			stopEmbeddedBroker("testBroker", BROKER_URI_SERVER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
	}
	
	@org.junit.Test
	public void testServiceUpClientComesUp() throws Exception{
		ConnectionNode connection = getMessaging().get(new NodeInfo("osee-jms", new URI(BROKER_URI)));
		Assert.assertNotNull(connection);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
		
		RemoteServiceRegistrar registrar = new RemoteServiceRegistrarImpl(connection, executor);
		RemoteServiceLookup lookup = new RemoteServiceLookupImpl(connection, executor);
		registrar.start();
		lookup.start();
		
		registrar.registerService("testService", "1002", "some.service.id", new URI("tcp://localhost:666"), new TestPopulator(), 30);
		
		TestNotification testNotification = new TestNotification();
		lookup.register("testService", "1002", testNotification);
		
		testWait(1000);
		Assert.assertTrue(testNotification.getServiceUpdates() >= 1);
		int currentNumberOfUpdates = testNotification.getServiceUpdates();
		testWait(61000);
		
		Assert.assertEquals(currentNumberOfUpdates + 2, testNotification.getServiceUpdates());
		Assert.assertTrue(registrar.unregisterService("testService", "1002", "some.service.id"));
		
		testWait(90000);
		
		Assert.assertEquals(1, testNotification.getServiceAway());
		Assert.assertEquals(currentNumberOfUpdates + 2, testNotification.getServiceUpdates());
		Assert.assertTrue(lookup.unregister("testService", "1002", testNotification));
		
		registrar.stop();
		lookup.stop();
		testWait(2000);
	}
	
	@org.junit.Test
	public void testClientUpServiceComesUp() throws Exception{
		ConnectionNode connection = getMessaging().get(new NodeInfo("osee-jms", new URI(BROKER_URI)));
		Assert.assertNotNull(connection);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
		RemoteServiceRegistrar registrar = new RemoteServiceRegistrarImpl(connection, executor);
		registrar.start();
		
		RemoteServiceLookup lookup = new RemoteServiceLookupImpl(connection, executor);
		lookup.start();
		
		TestNotification testNotification = new TestNotification();
		lookup.register("testService", "1002", testNotification);
		
		registrar.registerService("testService", "1002", "some.service.id", new URI("tcp://localhost:666"), new TestPopulator(), 30);
		testWait(1000);
		
		Assert.assertTrue(testNotification.getServiceUpdates() >= 1);
		int currentNumberOfUpdates = testNotification.getServiceUpdates();
		
		testWait(61000);
		
		Assert.assertEquals(currentNumberOfUpdates + 2, testNotification.getServiceUpdates());
		Assert.assertTrue(registrar.unregisterService("testService", "1002", "some.service.id"));
		
		testWait(90000);
		
		Assert.assertEquals(1, testNotification.getServiceAway());
		
		Assert.assertEquals(currentNumberOfUpdates + 2, testNotification.getServiceUpdates());
		Assert.assertTrue(lookup.unregister("testService", "1002", testNotification));
		
		lookup.stop();
		registrar.stop();
		testWait(2000);
	}
	
	@org.junit.Test
	public void testServiceComesUpClientGetsReply() throws Exception{
		ConnectionNode connection = getMessaging().get(new NodeInfo("osee-jms", new URI(BROKER_URI)));
		Assert.assertNotNull(connection);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
		
		RemoteServiceRegistrar registrar = new RemoteServiceRegistrarImpl(connection, executor);
		registrar.registerService("testService", "1002", "some.service.id", new URI("tcp://localhost:666"), new TestPopulator(), 50000);
		registrar.start();
		
		testWait(2000);
		RemoteServiceLookup lookup = new RemoteServiceLookupImpl(connection, executor);
		lookup.start();
		TestNotification testNotification = new TestNotification();
		lookup.register("testService", "1002", testNotification);
		testWait(1000);
		
		Assert.assertEquals(1, testNotification.getServiceUpdates());
		
		Assert.assertTrue(lookup.unregister("testService", "1002", testNotification));
		Assert.assertTrue(registrar.unregisterService("testService", "1002", "some.service.id"));
		
		registrar.stop();
		lookup.stop();
		testWait(2000);
	}
	
	@org.junit.Test
	public void testReply() throws Exception{
		ConnectionNode connection = getMessaging().get(new NodeInfo("osee-jms", new URI(BROKER_URI)));
		TestReplyListener service = new TestReplyListener();
		TestReplyListener replyReciever = new TestReplyListener();
		connection.subscribe(TestMessages.replyTopic, service, new OseeMessagingStatusImpl("failed to subscribe", TestMessageServices.class));
		connection.subscribeToReply(TestMessages.replyTopic, replyReciever);
		System.out.println(System.currentTimeMillis());
		connection.send(TestMessages.replyTopic, "test", new BasicOseeMessagingStatus());
		System.out.println(System.currentTimeMillis());
		testWait(2000);
		Assert.assertEquals(1, service.sentReply);
		Assert.assertEquals(1, replyReciever.receivedReply);
		connection.unsubscribe(TestMessages.replyTopic, service, new OseeMessagingStatusImpl("failed to subscribe", TestMessageServices.class));
	}
	
	private class TestReplyListener extends OseeMessagingListener {
		int sentReply;
		int receivedReply;
		String msg = "back at ya";
		@Override
		public void process(Object message, Map<String, Object> headers,
				ReplyConnection replyConnection) {
			if(replyConnection.isReplyRequested()){
				try {
					System.out.println(System.currentTimeMillis());
					System.out.println(msg);
					replyConnection.send(msg, null, new BasicOseeMessagingStatus());
					sentReply++;
				} catch (OseeCoreException ex) {
					ex.printStackTrace();
				}
			} else {
				System.out.println(System.currentTimeMillis());
				System.out.println(message);
				if(message.toString().equals(msg)){
					receivedReply++;
				}
			}
		}
	}
	
	private class BasicOseeMessagingStatus implements OseeMessagingStatusCallback	{
		@Override
		public void fail(Throwable th) {
			OseeLog.log(BasicOseeMessagingStatus.class, Level.SEVERE, th);
		}
		@Override
		public void success() {
		}
	}
	
	private class TestPopulator implements ServiceInfoPopulator {

		@Override
		public void updateServiceInfo(
				List<ServiceDescriptionPair> serviceDescription) {
			ServiceDescriptionPair pair1 = new ServiceDescriptionPair();
			pair1.setName("test");
			pair1.setValue("one");
			serviceDescription.add(pair1);
		}
		
	}
	
	private class TestNotification implements ServiceNotification {

		private int serviceUpdates = 0;
		private int serviceAway = 0;

		public int getServiceUpdates() {
			return serviceUpdates;
		}
		public int getServiceAway(){
			return serviceAway;
		}

		@Override
		public void onServiceGone(ServiceHealth serviceHealth) {
			serviceAway++;
			System.out.println("serviceAway " + serviceAway);
		}

		@Override
		public void onServiceUpdate(ServiceHealth serviceHealth) {
			serviceUpdates++;
			System.out.println("healthUpdates " + serviceUpdates);
		}
		
	}
}
