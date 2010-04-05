/*
 * Created on Jan 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import static org.junit.Assert.assertTrue;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.internal.BaseBrokerTesting;
import org.eclipse.osee.framework.messaging.internal.TestMessages;
import org.eclipse.osee.framework.messaging.test.msg.TestMessage;

/**
 * @author b1528444
 *
 */
public class TestMessageServicesMultipleConsumers extends BaseBrokerTesting implements OseeMessagingStatusCallback {

	private static String BROKER_URI_SERVER = "tcp://localhost:61616";
	private static String BROKER_URI = "tcp://localhost:61616";

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
	public void testMultipleConsumers() throws Exception{
		ConnectionNode connection = getMessaging().get(new NodeInfo("osee-jms", new URI(BROKER_URI)));
		
		List<BasicListener> listeners = new ArrayList<BasicListener>();
		for(int i = 0; i < 20; i++){
		   BasicListener listener = new BasicListener(i);
		   listeners.add(listener);
		   connection.subscribe(TestMessages.JMS_TOPIC, listener, this);
		}
		
		TestMessage message = new TestMessage();
      message.setMessage("TestMessage 1");
      connection.send(TestMessages.JMS_TOPIC, message, this);
      
      testWait(500);

      for(BasicListener listener:listeners){
         assertTrue(listener.toString(), listener.isReceived());
      }
	}

	  @org.junit.Test
	   public void testMultipleConsumersWithSelector() throws Exception{
	      ConnectionNode connection = getMessaging().get(new NodeInfo("osee-jms", new URI(BROKER_URI)));
	      
	      List<BasicListener> listeners = new ArrayList<BasicListener>();
	      for(int i = 0; i < 20; i++){
	         BasicListener listener = new BasicListener(i);
	         listeners.add(listener);
	         connection.subscribe(TestMessages.JMS_TOPIC, listener, String.format("id = %d", i), this);
	      }
	      
	      TestMessage message = new TestMessage();
	      message.setMessage("TestMessage 1");
	      Properties properties = new Properties();
	      properties.put("id", 1);
	      connection.send(TestMessages.JMS_TOPIC, message, properties, this);
	      
	      testWait(500);

	      int receivedCount = 0;
	      for(BasicListener listener:listeners){
	         if(listener.isReceived()){
	            receivedCount++;
	         }
	      }
	      
	      assertTrue(String.format("received %d messages", receivedCount), receivedCount == 1);
	   }
	
   @Override
   public void fail(Throwable th) {
      assertTrue(th.getMessage(), false);
   }

   @Override
   public void success() {
   }
	
}
