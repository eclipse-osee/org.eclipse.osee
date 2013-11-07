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
import org.eclipse.osee.framework.core.server.internal.session.SessionFactory;
import org.eclipse.osee.framework.core.server.test.mocks.MockBuildTypeIdentifier;
import org.eclipse.osee.framework.core.server.test.mocks.MockLog;
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
public class SessionFactoryTest {

   private final String guid;
   private final String userId;
   private final Date creationDate;
   private final String managedByServerId;
   private final String clientVersion;
   private final String clientMachineName;
   private final String clientAddress;
   private final int clientPort;
   private final Date lastInteractionDate;
   private final String lastInteractionDetails;
   private final MockBuildTypeIdentifier typeIdentifier = new MockBuildTypeIdentifier();
   private final SessionFactory factory = new SessionFactory(new MockLog(), null, null, typeIdentifier);

   public SessionFactoryTest(String guid, String userId, Date creationDate, String managedByServerId, String clientVersion, String clientMachineName, String clientAddress, int clientPort, Date lastInteractionDate, String lastInteractionDetails) {
      super();
      this.guid = guid;
      this.userId = userId;
      this.creationDate = creationDate;
      this.managedByServerId = managedByServerId;
      this.clientVersion = clientVersion;
      this.clientMachineName = clientMachineName;
      this.clientAddress = clientAddress;
      this.clientPort = clientPort;
      this.lastInteractionDate = lastInteractionDate;
      this.lastInteractionDetails = lastInteractionDetails;
   }

   @Test
   public void testCreate() {
      Session session =
         factory.createNewSession(guid, userId, creationDate, clientVersion, clientMachineName, clientAddress,
            clientPort, lastInteractionDate, lastInteractionDetails);

      Assert.assertEquals(guid, session.getGuid());
      Assert.assertEquals(userId, session.getUserId());
      Assert.assertEquals(creationDate, session.getCreationDate());
      Assert.assertEquals(clientVersion, session.getClientVersion());
      Assert.assertEquals(clientMachineName, session.getClientMachineName());
      Assert.assertEquals(clientAddress, session.getClientAddress());
      Assert.assertEquals(clientPort, session.getClientPort());
      Assert.assertEquals(lastInteractionDate, session.getLastInteractionDate());
      Assert.assertEquals(lastInteractionDetails, session.getLastInteractionDetails());
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
         String userId = "hello.userId-" + index;
         Date lastInteractionDate = new Date();
         String lastInteraction = "lastInteraction-" + index;
         String managedByServerId = "serverId-" + index;

         data.add(new Object[] {
            guid,
            userId,
            creationDate,
            managedByServerId,
            clientVersion,
            clientMachine,
            clientAddress,
            clientPort,
            lastInteractionDate,
            lastInteraction,});
      }
      return data;
   }

}
