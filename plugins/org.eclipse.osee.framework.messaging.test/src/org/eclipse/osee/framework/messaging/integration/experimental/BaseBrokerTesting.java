/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.integration.experimental;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.apache.activemq.broker.BrokerService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.SystemTopic;
import org.eclipse.osee.framework.messaging.data.DefaultNodeInfos;
import org.eclipse.osee.framework.messaging.internal.MessageServiceProxy;

/**
 * @author Andrew M. Finkbeiner
 */
public class BaseBrokerTesting {

   private MessageServiceProxy messageServiceProviderImpl = null;
   private ConcurrentHashMap<String, BrokerService> brokers;

   @org.junit.Before
   public void beforeTest() {
      messageServiceProviderImpl = new MessageServiceProxy();
      brokers = new ConcurrentHashMap<>();
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

   protected ConnectionNode getConnectionNode() {
      return getMessaging().get(DefaultNodeInfos.OSEE_JMS_NODE_INFO);
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
         ProcessBuilder builder = new ProcessBuilder(url.getPath() + exe, "-console", "-nosplash", "-application",
            "jms.activemq.launch.RunActiveMq", DefaultNodeInfos.OSEE_JMS_DEFAULT_PORT);
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

      private final PrintStream printStream;
      private final InputStream input;

      public OutputReader(PrintStream printStream, InputStream input) {
         super();
         this.printStream = printStream;
         this.input = input;

      }

      @Override
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
         getMessaging().get(DefaultNodeInfos.OSEE_JMS_NODE_INFO).send(SystemTopic.KILL_TEST_JMS_BROKER, "kill",
            new MessageStatusTest(true));
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
      messaging = messageServiceProviderImpl.getProxiedService();
      assertTrue(messaging != null);
      return messaging;
   }

}
