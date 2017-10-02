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

   public SessionTest(Session session, String guid, String expectedClientAddress, String expectedClientMachine, String expectedClientVersion, int expectedClientPort, Date expectedCreationDate, String expectedUserId) {
      this.session = session;
      this.expectedGuid = guid;
      this.expectedClientAddress = expectedClientAddress;
      this.expectedClientMachine = expectedClientMachine;
      this.expectedClientVersion = expectedClientVersion;
      this.expectedClientPort = expectedClientPort;
      this.expectedCreationDate = expectedCreationDate;
      this.expectedUserId = expectedUserId;
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

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<>();
      for (int index = 1; index <= 3; index++) {
         String guid = "ABCD" + String.valueOf(index);
         String clientAddress = "addresss-" + index;
         String clientMachine = "machine-" + index;
         String clientVersion = "version-" + index;
         int clientPort = index * 345;
         Date creationDate = new Date();
         String userId = "userId-" + index;

         Session session =
            new Session(guid, userId, creationDate, clientVersion, clientMachine, clientAddress, clientPort);

         data.add(
            new Object[] {session, guid, clientAddress, clientMachine, clientVersion, clientPort, creationDate, userId

            });
      }
      return data;
   }
}
