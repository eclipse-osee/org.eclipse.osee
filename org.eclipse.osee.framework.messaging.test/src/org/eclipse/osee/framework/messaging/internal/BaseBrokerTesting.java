/*
 * Created on Jul 30, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.apache.activemq.broker.BrokerService;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.SystemTopic;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.future.MessageService;
import org.eclipse.osee.framework.messaging.test.msg.TestMessage;

/**
 * @author b1528444
 */
public class BaseBrokerTesting {

   private MessageServiceProviderImpl messageServiceProviderImpl = null;
   private ConcurrentHashMap<String, BrokerService> brokers;
   private Thread[] threads;

   @org.junit.Before
   public void beforeTest() {
      messageServiceProviderImpl = new MessageServiceProviderImpl(Thread.currentThread().getContextClassLoader());
      brokers = new ConcurrentHashMap<String, BrokerService>();
      try {
         messageServiceProviderImpl.start();
      } catch (Exception ex) {
         ex.printStackTrace();
      }

   }

   @org.junit.After
   public void afterTest() {
      try {
         messageServiceProviderImpl.stop();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   protected void startEmbeddedBroker(String brokerName, String brokerURI) throws Exception {
      BrokerService broker = new BrokerService();
      broker.setBrokerName(brokerName);
      broker.setPersistent(false);
      broker.setUseShutdownHook(true);
      broker.addConnector(brokerURI);
      broker.start();
      brokers.put(brokerURI, broker);
   }

   public void testWait(long timeMS) {
      try {
         Thread.sleep(timeMS);
      } catch (InterruptedException ex) {
         ex.printStackTrace();
      }
   }

   protected void stopEmbeddedBroker(String brokerName, String brokerURI) throws Exception {
      BrokerService broker = brokers.get(brokerURI);
      if (broker != null) {
         broker.stop();
      }
   }

   protected void startBroker() {
      try {
         String really = "file:C:\\Program Files\\OSEE_3.5.1\\0_9_0\\";// System.getProperty("eclipse.home.location");
         URL url = new URL(really);
         String exe = null;
         if (Lib.isWindows()) {
            exe = "eclipse.exe";
         } else {
            exe = "eclipse";
         }
         ProcessBuilder builder = new ProcessBuilder(url.getPath() + exe, "-console", "-nosplash", "-application", "jms.activemq.launch.RunActiveMq", DefaultNodeInfos.OSEE_JMS_DEFAULT_PORT);
         builder.directory(new File(url.getPath()));
         builder.redirectErrorStream(true);
         Process process = builder.start();
         Thread th = new Thread(new OutputReader(System.out, process.getInputStream()));
         th.start();
//         threads = Lib.handleProcessNoWait(process, new PrintWriter(System.out));
         Thread.sleep(30000);
      } catch (MalformedURLException ex) {
         OseeLog.log(BaseBrokerTesting.class, Level.SEVERE, ex);
         fail(ex.getMessage());
      } catch (IOException ex) {
         OseeLog.log(BaseBrokerTesting.class, Level.SEVERE, ex);
         fail(ex.getMessage());
      } catch (InterruptedException ex) {
         OseeLog.log(BaseBrokerTesting.class, Level.SEVERE, ex);
         fail(ex.getMessage());
      }
   }
   
   public class OutputReader implements Runnable {

      private PrintStream printStream;
      private InputStream input;

      public OutputReader(PrintStream printStream, InputStream input) {
         super();
         this.printStream = printStream;
         this.input = input;
         
      }

      public void run() {
         try {
           final byte[] buffer = new byte[4096];
           int size;
            while ((size = input.read(buffer)) != -1) {
               printStream.print(new String(buffer, 0, size, "UTF-8"));
               printStream.flush();
            }
         } catch (IOException ex) {
            ex.printStackTrace();
         } finally {
            try {
               input.close();
               printStream.flush();
            } catch (IOException ex) {
               ex.printStackTrace();
            }
         }
      }
   }

   

   protected void stopBroker() {
      try {
         getMessaging().get(DefaultNodeInfos.OSEE_JMS_DEFAULT).send(SystemTopic.KILL_TEST_JMS_BROKER, "kill", new MessageStatusTest(true));
         Thread.sleep(10000);
      } catch (InterruptedException ex) {
         OseeLog.log(BaseBrokerTesting.class, Level.SEVERE, ex);
         // fail(ex.getMessage());
      } catch (OseeCoreException ex) {
         OseeLog.log(BaseBrokerTesting.class, Level.SEVERE, ex);
         // fail(ex.getMessage());
      }
   }

   protected final MessageService getMessaging() {
      MessageService messaging = null;
      try {
         messaging = messageServiceProviderImpl.getMessageService();
      } catch (OseeCoreException ex) {
         fail("Failed to get messaging service. " + ex.getMessage());
      }
      assertTrue(messaging != null);
      return messaging;
   }

   protected void testJMSSendShouldFail(MessageService messaging) {
      MessageStatusTest status = new MessageStatusTest(false);
      try {
         messaging.get(DefaultNodeInfos.OSEE_JMS_DEFAULT).send(TestMessages.test, "test", status);
      } catch (OseeCoreException ex) {
         OseeLog.log(BaseBrokerTesting.class, Level.SEVERE, ex);
      }
      status.waitForStatus(5000);
   }

   protected void testJMSSendShouldPass(MessageService messaging) {
      MessageStatusTest status = new MessageStatusTest(true);
      try {
         messaging.get(DefaultNodeInfos.OSEE_JMS_DEFAULT).send(TestMessages.test, "test", status);
      } catch (OseeCoreException ex) {
         OseeLog.log(BaseBrokerTesting.class, Level.SEVERE, ex);
      }
      status.waitForStatus(5000);
   }

   protected void testJMSSubscribeShouldFail(MessageService messaging)
			throws OseeCoreException {
		MessageStatusTest status = new MessageStatusTest(false);
		OseeMessagingListener listener = new OseeMessagingListener(TestMessage.class) {
         @Override
         public void process(Object message,
               Map<String, Object> headers,
               ReplyConnection replyConnection) {
            TestMessage msg = (TestMessage) message;
            System.out.println(msg.getMessage());
         }
      };
      
		messaging.get(DefaultNodeInfos.OSEE_JMS_DEFAULT).subscribe(TestMessages.test2, listener, status);
		status.waitForStatus(5000);
		messaging.get(DefaultNodeInfos.OSEE_JMS_DEFAULT).unsubscribe(TestMessages.test2, listener, status);//we have to remove so we don't get a false fail later on
	}

   protected void testJMSSubscribeShouldPass(MessageService messaging) throws OseeCoreException {
      MessageStatusTest status = new MessageStatusTest(true);
      ConnectionNode node = messaging.get(DefaultNodeInfos.OSEE_JMS_DEFAULT);
      node.subscribe(TestMessages.test2, new OseeMessagingListener(TestMessage.class) {
         @Override
         public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
            TestMessage msg = (TestMessage) message;
            System.out.println(msg.getMessage());
         }
      }, status);
      status.waitForStatus(5000);
   }
}
