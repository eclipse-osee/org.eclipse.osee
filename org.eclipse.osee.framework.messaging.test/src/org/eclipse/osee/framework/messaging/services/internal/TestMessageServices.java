/*
 * Created on Jan 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import junit.framework.Assert;

import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.future.NodeInfo;
import org.eclipse.osee.framework.messaging.internal.BaseBrokerTesting;
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

	
	@org.junit.Before
	public void startBroker(){
		try {
			startEmbeddedBroker("testBroker", "tcp://localhost:61106");
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
	}
	
	@org.junit.After
	public void stopBroker(){
		try {
			stopEmbeddedBroker("testBroker", "tcp://localhost:61106");
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
	}
	
	
	@org.junit.Test
	public void testServiceUpClientComesUp() throws Exception{
		ConnectionNode connection = getMessaging().get(new NodeInfo("osee-jms", new URI("tcp://localhost:61106")));
		Assert.assertNotNull(connection);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
		RemoteServiceRegistrar registrar = new RemoteServiceRegistrarImpl(connection, executor);
		RemoteServiceLookup lookup = new RemoteServiceLookupImpl(connection, executor);
		
		registrar.registerService("testService", "1002", "some.service.id", new URI("tcp://localhost:666"), new TestPopulator(), 30);
		
		TestNotification testNotification = new TestNotification();
		lookup.register("testService", "1002", testNotification);
		
		testWait(1000);
		
		Assert.assertEquals(1, testNotification.getServiceUpdates());
		
		testWait(61000);
		
		Assert.assertEquals(3, testNotification.getServiceUpdates());
		Assert.assertTrue(registrar.unregisterService("testService", "1002", "some.service.id"));
		
		testWait(90000);
		
		Assert.assertEquals(1, testNotification.getServiceAway());
		
		Assert.assertEquals(3, testNotification.getServiceUpdates());
		Assert.assertTrue(lookup.unregister("testService", "1002", testNotification));
	}
	
	@org.junit.Test
	public void testClientUpServiceComesUp() throws Exception{
		ConnectionNode connection = getMessaging().get(new NodeInfo("osee-jms", new URI("tcp://localhost:61106")));
		Assert.assertNotNull(connection);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
		RemoteServiceRegistrar registrar = new RemoteServiceRegistrarImpl(connection, executor);
		RemoteServiceLookup lookup = new RemoteServiceLookupImpl(connection, executor);
		
		
		TestNotification testNotification = new TestNotification();
		lookup.register("testService", "1002", testNotification);
		
		registrar.registerService("testService", "1002", "some.service.id", new URI("tcp://localhost:666"), new TestPopulator(), 30);
		testWait(1000);
		
		Assert.assertEquals(1, testNotification.getServiceUpdates());
		
		testWait(61000);
		
		Assert.assertEquals(3, testNotification.getServiceUpdates());
		Assert.assertTrue(registrar.unregisterService("testService", "1002", "some.service.id"));
		
		testWait(90000);
		
		Assert.assertEquals(1, testNotification.getServiceAway());
		
		Assert.assertEquals(3, testNotification.getServiceUpdates());
		Assert.assertTrue(lookup.unregister("testService", "1002", testNotification));
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
