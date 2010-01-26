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
import java.util.concurrent.TimeUnit;

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

	@org.junit.Test
	public void testServiceUpClientComesUp() throws Exception{
		startEmbeddedBroker("testBroker", "tcp://localhost:61106");
		ConnectionNode connection = getMessaging().get(new NodeInfo("osee-jms", new URI("tcp://localhost:61106")));
		Assert.assertNotNull(connection);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
		RemoteServiceRegistrar registrar = new RemoteServiceRegistrarImpl(connection, executor);
		RemoteServiceLookup lookup = new RemoteServiceLookupImpl(connection);
		
		registrar.registerService("some.service.id", "v1", new URI("tcp://localhost:666"), new TestPopulator(), 30, TimeUnit.SECONDS);
		
		TestNotification testNotification = new TestNotification();
		lookup.register("some.service.id", "v1", testNotification);
		
		testWait(1000);
		
		Assert.assertEquals(1, testNotification.getHealthUpdates());
		
		testWait(61000);
		
		Assert.assertEquals(3, testNotification.getHealthUpdates());
		Assert.assertTrue(registrar.unregisterService("some.service.id", "v1"));
		
		testWait(90000);
		
		Assert.assertEquals(3, testNotification.getHealthUpdates());
		Assert.assertTrue(lookup.unregister("some.service.id", "v1", testNotification));
		stopEmbeddedBroker("testBroker", "tcp://localhost:61106");
	}
	
	@org.junit.Test
	public void testClientUpServiceComesUp() throws Exception{
		startEmbeddedBroker("testBroker", "tcp://localhost:61106");
		ConnectionNode connection = getMessaging().get(new NodeInfo("osee-jms", new URI("tcp://localhost:61106")));
		Assert.assertNotNull(connection);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
		RemoteServiceRegistrar registrar = new RemoteServiceRegistrarImpl(connection, executor);
		RemoteServiceLookup lookup = new RemoteServiceLookupImpl(connection);
		
		
		TestNotification testNotification = new TestNotification();
		lookup.register("some.service.id", "v1", testNotification);
		
		registrar.registerService("some.service.id", "v1", new URI("tcp://localhost:666"), new TestPopulator(), 30, TimeUnit.SECONDS);
		testWait(1000);
		
		Assert.assertEquals(1, testNotification.getHealthUpdates());
		
		testWait(61000);
		
		Assert.assertEquals(3, testNotification.getHealthUpdates());
		Assert.assertTrue(registrar.unregisterService("some.service.id", "v1"));
		
		testWait(90000);
		
		Assert.assertEquals(3, testNotification.getHealthUpdates());
		Assert.assertTrue(lookup.unregister("some.service.id", "v1", testNotification));
		stopEmbeddedBroker("testBroker", "tcp://localhost:61106");
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

		private int healthUpdates = 0;
		@Override
		public void onHealthUpdate(ServiceHealth serviceHealth) {
			healthUpdates ++;
			System.out.println("healthUpdates " + healthUpdates);
		}

		public int getHealthUpdates() {
			return healthUpdates;
		}
		
	}
}
