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

package org.eclipse.osee.framework.messaging.event.res.test.internal;

import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test Case for {@link RemoteNetworkSender1}
 * 
 * @author Donald G. Dunne
 */
public class RemoteNetworkSenderTest {

   private static final String Object = "NetworkSendTest.class";
   private static final String MachineIp = "123.421.56.342";
   private static final String MachineName = "A2340422";
   private static final String SessionId = "N23422.32";
   private static final String ClientVersion = "123.2";
   private static final int Port = 485;
   private static final String UserId = "b345344";
   private static RemoteNetworkSender1 networkSender;

   @BeforeClass
   public static void setup() {
      networkSender = new RemoteNetworkSender1();
      networkSender.setSourceObject(Object);
      networkSender.setSessionId(SessionId);
      networkSender.setMachineName(MachineName);
      networkSender.setUserId(UserId);
      networkSender.setMachineIp(MachineIp);
      networkSender.setPort(Port);
      networkSender.setClientVersion(ClientVersion);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#NetworkSender(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
    * .
    */
   @org.junit.Test
   public void testNetworkSenderStringString() {
      Assert.assertEquals(Object, networkSender.getSourceObject());
      Assert.assertEquals(SessionId, networkSender.getSessionId());
      Assert.assertEquals(MachineName, networkSender.getMachineName());
      Assert.assertEquals(UserId, networkSender.getUserId());
      Assert.assertEquals(MachineIp, networkSender.getMachineIp());
      Assert.assertEquals(ClientVersion, networkSender.getClientVersion());
      Assert.assertEquals(Port, networkSender.getPort());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getSourceObject()}.
    */
   @org.junit.Test
   public void testGetSourceObject() {
      Assert.assertEquals(Object, networkSender.getSourceObject());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getSessionId()}.
    */
   @org.junit.Test
   public void testGetSessionId() {
      Assert.assertEquals(SessionId, networkSender.getSessionId());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getMachineName()}.
    */
   @org.junit.Test
   public void testGetMachineName() {
      Assert.assertEquals(MachineName, networkSender.getMachineName());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getUserId()}.
    */
   @org.junit.Test
   public void testGetUserId() {
      Assert.assertEquals(UserId, networkSender.getUserId());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getMachineIp()}.
    */
   @org.junit.Test
   public void testGetMachineIp() {
      Assert.assertEquals(MachineIp, networkSender.getMachineIp());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getClientVersion()}.
    */
   @org.junit.Test
   public void testGetClientVersion() {
      Assert.assertEquals(ClientVersion, networkSender.getClientVersion());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getPort()}.
    */
   @org.junit.Test
   public void testGetPort() {
      Assert.assertEquals(Port, networkSender.getPort());
   }

}
