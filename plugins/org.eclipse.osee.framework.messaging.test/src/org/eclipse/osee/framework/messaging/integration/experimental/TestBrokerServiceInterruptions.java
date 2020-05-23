/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.messaging.integration.experimental;

import static org.eclipse.osee.framework.messaging.integration.experimental.MessageAssert.verifyJMSSendShouldFail;
import static org.eclipse.osee.framework.messaging.integration.experimental.MessageAssert.verifyJMSSendShouldPass;
import static org.eclipse.osee.framework.messaging.integration.experimental.MessageAssert.verifyJMSSubscribeShouldFail;
import static org.eclipse.osee.framework.messaging.integration.experimental.MessageAssert.verifyJMSSubscribeShouldPass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.junit.Test;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestBrokerServiceInterruptions extends BaseBrokerTesting {

   @Test
   public void testBrokerComesUpAfterAppsRunning() throws Exception {
      verifyJMSSendShouldFail(getConnectionNode());
      verifyJMSSubscribeShouldFail(getConnectionNode());

      startBroker();

      verifyJMSSubscribeShouldPass(getConnectionNode());
      verifyJMSSendShouldPass(getConnectionNode());

      stopBroker();
   }

   @Test
   public void testBrokerGoingDownTriggersConnectionEvent() throws Exception {
      startBroker();

      verifyJMSSendShouldPass(getConnectionNode());

      ConnectionNode connectionNode = getConnectionNode();
      TestConnectionListener connectionListener = new TestConnectionListener();
      connectionNode.addConnectionListener(connectionListener);

      assertTrue(connectionListener.isConnected());

      stopBroker();

      testWait(65000);//currently we ping the broker every minute to see if it still exists, so we've allowed enough time for a timeout

      assertFalse(connectionListener.isConnected());

   }

   @Test
   public void testBrokerGoingDownSendFails() throws Exception {
      startBroker();

      verifyJMSSendShouldPass(getConnectionNode());

      stopBroker();

      verifyJMSSendShouldFail(getConnectionNode());
   }

   @Test
   public void testBrokerGoingDownSubscribeFails() throws Exception {
      startBroker();

      verifyJMSSubscribeShouldPass(getConnectionNode());

      stopBroker();

      verifyJMSSubscribeShouldFail(getConnectionNode());
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
}
