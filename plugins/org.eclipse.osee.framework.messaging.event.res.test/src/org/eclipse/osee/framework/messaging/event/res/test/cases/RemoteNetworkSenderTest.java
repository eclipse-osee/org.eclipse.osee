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
package org.eclipse.osee.framework.messaging.event.res.test.cases;

import org.eclipse.osee.framework.messaging.event.res.RemoteEventUtil;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class RemoteNetworkSenderTest {

   public static String Object = "NetworkSendTest.class";
   public static String MachineIp = "123.421.56.342";
   public static String MachineName = "A2340422";
   public static String SessionId = "N23422.32";
   public static String ClientVersion = "123.2";
   public static int Port = 485;
   public static String UserId = "b345344";

   public static RemoteNetworkSender1 networkSender = RemoteEventUtil.getNetworkSender(Object, SessionId, MachineName,
      UserId, MachineIp, Port, ClientVersion);

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
