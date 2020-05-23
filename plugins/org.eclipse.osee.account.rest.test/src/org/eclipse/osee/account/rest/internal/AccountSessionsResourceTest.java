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

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.account.rest.model.AccountSessionDetailsData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test Case for {@link AccountSessionsResource}
 *
 * @author Roberto E. Escobar
 */
public class AccountSessionsResourceTest {

   private static final ArtifactId ACCOUNT_ID = ArtifactId.valueOf(823952);

   //@formatter:off
   @Mock private AccountOps accountOps;
   @Mock private AccountSessionDetailsData details;
   //@formatter:on

   private AccountSessionsResource resource;

   @Before
   public void setUp() {
      initMocks(this);

      resource = new AccountSessionsResource(accountOps, ACCOUNT_ID);
   }

   @Test
   public void testGetAccountSessions() {
      List<AccountSessionDetailsData> accesses = new ArrayList<>();
      accesses.add(details);
      AccountSessionDetailsData[] expected = accesses.toArray(new AccountSessionDetailsData[] {});
      when(accountOps.getAccountSessionById(ACCOUNT_ID)).thenReturn(accesses);

      AccountSessionDetailsData[] actual = resource.getAccountSessions();

      assertArrayEquals(expected, actual);
      verify(accountOps).getAccountSessionById(ACCOUNT_ID);
   }
}
