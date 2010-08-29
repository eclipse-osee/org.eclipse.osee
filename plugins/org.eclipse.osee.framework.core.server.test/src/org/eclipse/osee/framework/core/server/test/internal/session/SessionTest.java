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
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.model.test.mocks.ModelAsserts;
import org.eclipse.osee.framework.core.model.test.type.AbstractOseeTypeTest;
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
public class SessionTest extends AbstractOseeTypeTest<Session> {

   private final int expectedId;
   private final String expectedClientAddress;
   private final String expectedClientMachine;
   private final String expectedClientVersion;
   private final int expectedClientPort;
   private final Date expectedCreationDate;
   private final String expectedUserId;
   private final Date expectedLastInteractionDate;
   private final String expectedLastInteraction;
   private final String exepectedManagedByServerId;

   public SessionTest(Session type, String guid, String name, int expectedId, String expectedClientAddress, String expectedClientMachine, String expectedClientVersion, int expectedClientPort, Date expectedCreationDate, String expectedUserId, Date expectedLastInteractionDate, String expectedLastInteraction, String exepectedManagedByServerId) {
      super(type, guid, name);
      this.expectedId = expectedId;
      this.expectedClientAddress = expectedClientAddress;
      this.expectedClientMachine = expectedClientMachine;
      this.expectedClientVersion = expectedClientVersion;
      this.expectedClientPort = expectedClientPort;
      this.expectedCreationDate = expectedCreationDate;
      this.expectedUserId = expectedUserId;
      this.expectedLastInteractionDate = expectedLastInteractionDate;
      this.expectedLastInteraction = expectedLastInteraction;
      this.exepectedManagedByServerId = exepectedManagedByServerId;
   }

   @Test
   public void testGuidAsInteger() {
      int actualId = Session.guidAsInteger(getType().getGuid());
      Assert.assertEquals(expectedId, actualId);
   }

   @Test
   public void testGetClientVersion() {
      Assert.assertEquals(expectedClientVersion, getType().getClientVersion());
   }

   @Test
   public void testGetCreationDate() {
      Assert.assertEquals(expectedCreationDate, getType().getCreationDate());
   }

   @Test
   public void testGetUserId() {
      Assert.assertEquals(expectedUserId, getType().getUserId());
   }

   @Test
   public void testSetGetClientAddress() throws Exception {
      String newValue = GUID.create();
      ModelAsserts.assertTypeSetGet(getType(), Session.SESSION_CLIENT_ADDRESS, "getClientAddress", "setClientAddress",
         expectedClientAddress, newValue);
   }

   @Test
   public void testSetGetClientMachineName() throws Exception {
      String newValue = GUID.create();
      ModelAsserts.assertTypeSetGet(getType(), Session.SESSION_CLIENT_MACHINE_NAME, "getClientMachineName",
         "setClientMachineName", expectedClientMachine, newValue);
   }

   @Test
   public void testSetGetClientPort() throws Exception {
      int newValue = Integer.MAX_VALUE;
      ModelAsserts.assertTypeSetGet(getType(), Session.SESSION_CLIENT_PORT, "getClientPort", "setClientPort",
         expectedClientPort, newValue);
   }

   @Test
   public void testSetGetLastInteractionDate() throws Exception {
      Date newValue = new Date();
      ModelAsserts.assertTypeSetGet(getType(), Session.SESSION_LAST_INTERACTION_DATE, "getLastInteractionDate",
         "setLastInteractionDate", expectedLastInteractionDate, newValue);
   }

   @Test
   public void testSetGetLastInteractionDetails() throws Exception {
      String newValue = GUID.create();
      ModelAsserts.assertTypeSetGet(getType(), Session.SESSION_LAST_INTERACTION_DETAILS, "getLastInteractionDetails",
         "setLastInteractionDetails", expectedLastInteraction, newValue);
   }

   @Test
   public void testSetManagedByServerId() throws Exception {
      String newValue = GUID.create();
      ModelAsserts.assertTypeSetGet(getType(), Session.SESSION_MANAGED_BY_SERVER_ID, "getManagedByServerId",
         "setManagedByServerId", exepectedManagedByServerId, newValue);
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 3; index++) {
         String guid = "ABCD" + String.valueOf(index);
         int actualIndex = index - 1;
         int expectedId = 1946 + actualIndex * 20 + 4 * actualIndex;

         String name = "index-" + index;

         String clientAddress = "addresss-" + index;
         String clientMachine = "machine-" + index;
         String clientVersion = "version-" + index;
         int clientPort = index * 345;
         Date creationDate = new Date();
         String userId = "userId-" + index;
         Date lastInteractionDate = new Date();
         String lastInteraction = "lastInteraction-" + index;
         String managedByServerId = "serverId-" + index;

         Session session =
            new Session(guid, name, userId, creationDate, managedByServerId, clientVersion, clientMachine,
               clientAddress, clientPort, lastInteractionDate, lastInteraction);
         session.setStorageState(StorageState.LOADED);
         session.clearDirty();

         data.add(new Object[] {
            session,
            guid,
            name,
            expectedId,
            clientAddress,
            clientMachine,
            clientVersion,
            clientPort,
            creationDate,
            userId,
            lastInteractionDate,
            lastInteraction,
            managedByServerId});
      }
      return data;
   }
}
