/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package jms.activemq.launch;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * @author Roberto E. Escobar
 */
public class RunActiveMq implements IApplication {
   private static String BROKER_URI;
   private BrokerService broker;

   private final boolean isKillable = true;
   private Session session;
   private MessageConsumer replyToConsumer;
   private Connection connection;

   @Override
   public Object start(IApplicationContext appContext) throws Exception {
      broker = new BrokerService();
      broker.setBrokerName("osee");
      broker.setUseShutdownHook(true);
      broker.setUseJmx(false);
      String[] myArgs = (String[]) appContext.getArguments().get(IApplicationContext.APPLICATION_ARGS);
      BROKER_URI = "tcp://localhost:" + myArgs[0];
      broker.addConnector(BROKER_URI);
      broker.start();

      if (isKillable) {
         ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
            ActiveMQConnection.DEFAULT_PASSWORD, BROKER_URI);
         connection = factory.createConnection();
         connection.start();
         session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
         Topic destination = session.createTopic("jms.kill.broker");
         replyToConsumer = session.createConsumer(destination);
         replyToConsumer.setMessageListener(new MessageListener() {

            @Override
            public void onMessage(Message arg0) {
               try {
                  System.err.println("got a kill message");
                  stopBrokerInNewThread();
                  System.err.println("did something with a kill message");
               } catch (Exception ex) {
                  ex.printStackTrace();
               }
            }
         });
         while (broker.isStarted()) {
            Thread.sleep(1000);
         }
      } else {
         while (broker.isStarted()) {
            Thread.sleep(60000);
         }
      }
      return null;
   }

   @Override
   public void stop() {
      try {
         broker.stop();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   void stopBrokerInNewThread() throws Exception {
      new Thread(new StopIt()).start();
   }

   /**
    * @author Roberto E. Escobar
    */
   private class StopIt implements Runnable {
      @Override
      public void run() {
         try {
            System.err.println("close the connection");
            connection.close();
            System.err.println("stop the broker");
            broker.stop();
            System.err.println("done");
         } catch (Throwable th) {
            th.printStackTrace();
         }
      }
   }

}
