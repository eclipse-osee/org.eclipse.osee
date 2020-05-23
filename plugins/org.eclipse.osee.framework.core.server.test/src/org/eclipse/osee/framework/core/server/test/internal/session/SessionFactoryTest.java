/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.server.test.internal.session;

import static org.mockito.MockitoAnnotations.initMocks;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.server.internal.session.Session;
import org.eclipse.osee.framework.core.server.internal.session.SessionFactory;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;

/**
 * Test Case for {@link Session}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class SessionFactoryTest {

   @Mock
   private Log logger;
   @Mock
   private JdbcService jdbcService;

   private final String guid;
   private final String userId;
   private final Date creationDate;
   private final String clientVersion;
   private final String clientMachineName;
   private final String clientAddress;
   private final int clientPort;
   private final SessionFactory factory;

   public SessionFactoryTest(String guid, String userId, Date creationDate, String clientVersion, String clientMachineName, String clientAddress, int clientPort) {
      super();
      this.guid = guid;
      this.userId = userId;
      this.creationDate = creationDate;
      this.clientVersion = clientVersion;
      this.clientMachineName = clientMachineName;
      this.clientAddress = clientAddress;
      this.clientPort = clientPort;
      this.factory = new SessionFactory(logger, jdbcService);
   }

   @Before
   public void setup() {
      initMocks(this);
   }

   @Test
   public void testCreate() {
      Session session = factory.createNewSession(guid, userId, creationDate, clientVersion, clientMachineName,
         clientAddress, clientPort);

      Assert.assertEquals(guid, session.getGuid());
      Assert.assertEquals(userId, session.getUserId());
      Assert.assertEquals(creationDate, session.getCreationDate());
      Assert.assertEquals(clientVersion, session.getClientVersion());
      Assert.assertEquals(clientMachineName, session.getClientMachineName());
      Assert.assertEquals(clientAddress, session.getClientAddress());
      Assert.assertEquals(clientPort, session.getClientPort());
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
         String userId = "hello.userId-" + index;

         data.add(new Object[] {guid, userId, creationDate, clientVersion, clientMachine, clientAddress, clientPort

         });
      }
      return data;
   }

}
