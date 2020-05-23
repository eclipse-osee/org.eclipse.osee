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
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.account.rest.model.AccountPreferencesData;
import org.eclipse.osee.account.rest.model.AccountPreferencesInput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test Case for {@link AccountPreferencesResource}
 *
 * @author Roberto E. Escobar
 */
public class AccountPreferencesResourceTest {

   private static final ArtifactId ACCOUNT_ID = ArtifactId.valueOf(134522);

   //@formatter:off
   @Mock private AccountOps accountOps;
   @Mock private AccountPreferencesData preferences;
   @Mock private AccountPreferencesInput input;
   //@formatter:on

   private AccountPreferencesResource resource;

   @Before
   public void setUp() {
      initMocks(this);

      resource = new AccountPreferencesResource(accountOps, ACCOUNT_ID);
   }

   @Test
   public void testGetAccountPreferences() {
      when(accountOps.getAccountPreferencesDataById(ACCOUNT_ID)).thenReturn(preferences);

      AccountPreferencesData actual = resource.getAccountPreferences();

      assertEquals(preferences, actual);
      verify(accountOps).getAccountPreferencesDataById(ACCOUNT_ID);
   }

   @Test
   public void testSetAccountPreferences() {
      Map<String, String> map = new HashMap<>();
      map.put("a", "1");
      map.put("b", "2");
      map.put("c", "3");

      when(input.getMap()).thenReturn(map);
      when(accountOps.setAccountPreferences(ACCOUNT_ID, input)).thenReturn(true);

      Response actual = resource.setAccountPreferences(input);

      assertEquals(Status.OK.getStatusCode(), actual.getStatus());
      verify(accountOps).setAccountPreferences(ACCOUNT_ID, input);
   }

   @Test
   public void testSetAccountPreferencesNotModified() {
      Map<String, String> map = new HashMap<>();
      map.put("a", "1");
      map.put("b", "2");
      map.put("c", "3");

      when(input.getMap()).thenReturn(map);
      when(accountOps.setAccountPreferences(ACCOUNT_ID, input)).thenReturn(false);

      Response actual = resource.setAccountPreferences(input);

      assertEquals(Status.NOT_MODIFIED.getStatusCode(), actual.getStatus());
      verify(accountOps).setAccountPreferences(ACCOUNT_ID, input);
   }

}
