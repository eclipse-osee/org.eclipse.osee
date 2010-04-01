/*
 * Created on Mar 31, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.res.test.cases;

import org.eclipse.osee.framework.messaging.event.res.event.NetworkSender;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class NetworkSenderTest {

   public static String Object = "NetworkSendTest.class";
   public static String MachineIp = "123.421.56.342";
   public static String MachineName = "A2340422";
   public static String SessionId = "N23422.32";
   public static String ClientVersion = "123.2";
   public static String Port = "485";
   public static String UserId = "b345344";

   public static NetworkSender networkSender =
         new NetworkSender(Object, SessionId, MachineName, UserId, MachineIp, Port, ClientVersion);

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#NetworkSender(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
    * .
    */
   @Test
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
    * Test method for
    * {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#NetworkSender(java.lang.String)}.
    */
   @Test
   public void testNetworkSenderString() {
      NetworkSender newNetworkSender = new NetworkSender(networkSender.toXml());
      Assert.assertEquals(Object, newNetworkSender.getSourceObject());
      Assert.assertEquals(SessionId, newNetworkSender.getSessionId());
      Assert.assertEquals(MachineName, newNetworkSender.getMachineName());
      Assert.assertEquals(UserId, newNetworkSender.getUserId());
      Assert.assertEquals(MachineIp, newNetworkSender.getMachineIp());
      Assert.assertEquals(ClientVersion, newNetworkSender.getClientVersion());
      Assert.assertEquals(Port, newNetworkSender.getPort());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getSourceObject()}.
    */
   @Test
   public void testGetSourceObject() {
      Assert.assertEquals(Object, networkSender.getSourceObject());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getSessionId()}.
    */
   @Test
   public void testGetSessionId() {
      Assert.assertEquals(SessionId, networkSender.getSessionId());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getMachineName()}.
    */
   @Test
   public void testGetMachineName() {
      Assert.assertEquals(MachineName, networkSender.getMachineName());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getUserId()}.
    */
   @Test
   public void testGetUserId() {
      Assert.assertEquals(UserId, networkSender.getUserId());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getMachineIp()}.
    */
   @Test
   public void testGetMachineIp() {
      Assert.assertEquals(MachineIp, networkSender.getMachineIp());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getClientVersion()}.
    */
   @Test
   public void testGetClientVersion() {
      Assert.assertEquals(ClientVersion, networkSender.getClientVersion());
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkSender#getPort()}.
    */
   @Test
   public void testGetPort() {
      Assert.assertEquals(Port, networkSender.getPort());
   }

}
