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
package org.eclipse.osee.framework.messaging.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNode;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestBrokerServiceInterruptions extends BaseBrokerTesting {

   //   @Ignore
   @org.junit.Test
   public void testBrokerComesUpAfterAppsRunning() throws Exception {
      testJMSSendShouldFail(getMessaging());
      testJMSSubscribeShouldFail(getMessaging());

      startBroker();

      testJMSSubscribeShouldPass(getMessaging());
      testJMSSendShouldPass(getMessaging());

      stopBroker();
   }

   //	@Ignore
   @org.junit.Test
   public void testBrokerGoingDownTriggersConnectionEvent() throws Exception {
      startBroker();

      testJMSSendShouldPass(getMessaging());

      ConnectionNode connectionNode = getMessaging().get(DefaultNodeInfos.OSEE_JMS_DEFAULT);
      TestConnectionListener connectionListener = new TestConnectionListener();
      connectionNode.addConnectionListener(connectionListener);

      assertTrue(connectionListener.isConnected());

      stopBroker();

      testWait(65000);//currently we ping the broker every minute to see if it still exists, so we've allowed enough time for a timeout

      assertFalse(connectionListener.isConnected());

   }

   private class TestConnectionListener implements ConnectionListener {

      private boolean isConnected = false;

      @Override
      public void connected(ConnectionNode node) {
         System.out.println("connected from test listner");
         isConnected = true;
      }

      public boolean isConnected() {
         return isConnected;
      }

      @Override
      public void notConnected(ConnectionNode node) {
         System.out.println("not connected from test listener");
         isConnected = false;
      }
   }

   //	@Ignore
   @org.junit.Test
   public void testBrokerGoingDownSendFails() throws Exception {
      startBroker();

      testJMSSendShouldPass(getMessaging());

      stopBroker();

      testJMSSendShouldFail(getMessaging());
   }

   //	@Ignore
   @org.junit.Test
   public void testBrokerGoingDownSubscribeFails() throws Exception {
      startBroker();

      testJMSSubscribeShouldPass(getMessaging());

      stopBroker();

      testJMSSubscribeShouldFail(getMessaging());
   }

}
