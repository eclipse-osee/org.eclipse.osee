/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.orcs.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.Collection;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.server.ide.api.client.ClientEndpoint;
import org.eclipse.osee.framework.server.ide.api.client.model.Sessions;
import org.eclipse.osee.framework.server.ide.api.model.IdeVersion;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link ClientEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class ClientEndpointTest {

   @BeforeClass
   public static void setUp() throws Exception {
      DemoUtil.checkDbInitAndPopulateSuccess();
   }

   @Test
   public void testGetAll() {
      ClientEndpoint clientEp = AtsApiService.get().getOseeClient().getClientEndpoint();
      try (Response response = clientEp.getAll()) {
         Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
         Sessions sessions = response.readEntity(Sessions.class);
         if (Lib.isWindows()) {
            Assert.assertTrue(sessions.sessions.size() >= 1);
         }
      }
   }

   @Test
   @Ignore
   public void testGetClientsForUser() {
      ClientEndpoint clientEp = AtsApiService.get().getOseeClient().getClientEndpoint();
      Response response = clientEp.getClientsForUser(DemoUsers.Joe_Smith.getUserId());
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      Sessions sessions = response.readEntity(Sessions.class);
      System.out.println("2 - " + sessions);

      Assert.assertTrue(sessions.toString(),
         sessions.sessions.iterator().next().getUserId().equals(DemoUsers.Joe_Smith.getUserId()));

      response = clientEp.getClientsForUser("Joe_Smith");
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      sessions = response.readEntity(Sessions.class);
      System.out.println("3 - " + sessions);

      Assert.assertTrue(sessions.toString(),
         sessions.sessions.iterator().next().getUserId().equals(DemoUsers.Joe_Smith.getUserId()));
      response.close();
   }

   @Test
   public void supportedVersions() {
      ClientEndpoint clientEp = AtsApiService.get().getOseeClient().getClientEndpoint();
      IdeVersion versions = clientEp.getSupportedVersions();
      assertNotNull(versions);
      Collection<String> supportedVersions = versions.getVersions();
      assertEquals(true, !supportedVersions.isEmpty());
   }

}
