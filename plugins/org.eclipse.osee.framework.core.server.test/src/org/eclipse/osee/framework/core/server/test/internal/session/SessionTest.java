/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.server.test.internal.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.server.internal.session.Session;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link Session}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class SessionTest {

   private final Session session;
   private final String expectedGuid;
   private final String expectedClientAddress;
   private final String expectedClientMachine;
   private final String expectedClientVersion;
   private final int expectedClientPort;
   private final Date expectedCreationDate;
   private final String expectedUserId;
   private final Date expectedLastInteractionDate;
   private final String expectedLastInteraction;

   public SessionTest(Session session, String guid, String expectedClientAddress, String expectedClientMachine, String expectedClientVersion, int expectedClientPort, Date expectedCreationDate, String expectedUserId, Date expectedLastInteractionDate, String expectedLastInteraction) {
      this.session = session;
      this.expectedGuid = guid;
      this.expectedClientAddress = expectedClientAddress;
      this.expectedClientMachine = expectedClientMachine;
      this.expectedClientVersion = expectedClientVersion;
      this.expectedClientPort = expectedClientPort;
      this.expectedCreationDate = expectedCreationDate;
      this.expectedUserId = expectedUserId;
      this.expectedLastInteractionDate = expectedLastInteractionDate;
      this.expectedLastInteraction = expectedLastInteraction;
   }

   @Test
   public void testGetGuid() {
      Assert.assertEquals(expectedGuid, session.getGuid());
   }

   @Test
   public void testGetClientVersion() {
      Assert.assertEquals(expectedClientVersion, session.getClientVersion());
   }

   @Test
   public void testGetCreationDate() {
      Assert.assertEquals(expectedCreationDate, session.getCreationDate());
   }

   @Test
   public void testGetUserId() {
      Assert.assertEquals(expectedUserId, session.getUserId());
   }

   @Test
   public void testSetGetClientAddress() throws Exception {
      String newValue = GUID.create();

      Assert.assertEquals(expectedClientAddress, session.getClientAddress());

      session.setClientAddress(newValue);
      Assert.assertEquals(newValue, session.getClientAddress());
   }

   @Test
   public void testSetGetClientMachineName() throws Exception {
      String newValue = GUID.create();

      Assert.assertEquals(expectedClientMachine, session.getClientMachineName());

      session.setClientMachineName(newValue);
      Assert.assertEquals(newValue, session.getClientMachineName());
   }

   @Test
   public void testSetGetClientPort() throws Exception {
      int newValue = Integer.MAX_VALUE;

      Assert.assertEquals(expectedClientPort, session.getClientPort());

      session.setClientPort(newValue);
      Assert.assertEquals(newValue, session.getClientPort());
   }

   @Test
   public void testSetGetLastInteractionDate() throws Exception {
      Date newValue = new Date();

      Assert.assertEquals(expectedLastInteractionDate, session.getLastInteractionDate());

      session.setLastInteractionDate(newValue);
      Assert.assertEquals(newValue, session.getLastInteractionDate());
   }

   @Test
   public void testSetGetLastInteractionDetails() throws Exception {
      String newValue = GUID.create();

      Assert.assertEquals(expectedLastInteraction, session.getLastInteractionDetails());

      session.setLastInteractionDetails(newValue);
      Assert.assertEquals(newValue, session.getLastInteractionDetails());
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 3; index++) {
         String guid = "ABCD" + String.valueOf(index);
         String clientAddress = "addresss-" + index;
         String clientMachine = "machine-" + index;
         String clientVersion = "version-" + index;
         int clientPort = index * 345;
         Date creationDate = new Date();
         String userId = "userId-" + index;
         Date lastInteractionDate = new Date();
         String lastInteraction = "lastInteraction-" + index;

         Session session =
            new Session(guid, userId, creationDate, clientVersion, clientMachine, clientAddress, clientPort,
               lastInteractionDate, lastInteraction);

         data.add(new Object[] {
            session,
            guid,
            clientAddress,
            clientMachine,
            clientVersion,
            clientPort,
            creationDate,
            userId,
            lastInteractionDate,
            lastInteraction});
      }
      return data;
   }
}
