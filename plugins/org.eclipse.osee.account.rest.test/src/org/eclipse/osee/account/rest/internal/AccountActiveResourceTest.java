/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.account.rest.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.account.rest.model.AccountActiveData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test Case for {@link AccountActiveResource}
 *
 * @author Roberto E. Escobar
 */
public class AccountActiveResourceTest {

   //@formatter:off
   @Mock private AccountOps ops;
   @Mock private AccountActiveData activeData;
   //@formatter:on

   private ArtifactId accountId;
   private AccountActiveResource resource;

   @Before
   public void setUp() {
      initMocks(this);

      resource = new AccountActiveResource(ops, accountId);
   }

   @Test
   public void testIsActive() {
      when(ops.isActive(accountId)).thenReturn(activeData);

      AccountActiveData actual = resource.isActive();
      assertEquals(activeData, actual);

      verify(ops).isActive(accountId);
   }

   @Test
   public void testSetActiveOk() {
      when(ops.setAccountActive(accountId, true)).thenReturn(true);

      try (Response response = resource.setActive()) {
         assertEquals(Status.OK.getStatusCode(), response.getStatus());
      }
      verify(ops).setAccountActive(accountId, true);
   }

   @Test
   public void testSetActiveNotModified() {
      when(ops.setAccountActive(accountId, true)).thenReturn(false);

      try (Response response = resource.setActive()) {
         assertEquals(Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
      }
      verify(ops).setAccountActive(accountId, true);
   }

   @Test
   public void testSetInactiveOk() {
      when(ops.setAccountActive(accountId, false)).thenReturn(true);

      try (Response response = resource.setInactive()) {
         assertEquals(Status.OK.getStatusCode(), response.getStatus());
      }
      verify(ops).setAccountActive(accountId, false);
   }

   @Test
   public void testSetInactiveMotModified() {
      when(ops.setAccountActive(accountId, false)).thenReturn(false);

      try (Response response = resource.setInactive()) {
         assertEquals(Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
      }
      verify(ops).setAccountActive(accountId, false);
   }

}
