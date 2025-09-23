/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.orcs.health;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests to perform at the end of all integration tests to ensure that the state of the db is as it should be after all
 * tests.<br/>
 * <br/>
 * Example: It is invalid to have a transaction author be Invalid/Sentinel, so test that all transactions created during
 * tests do not violate this contract.<br/>
 * <br/>
 * Other tests should be added as db cleanup and invalid states are resolved/removed.
 *
 * @author Donald G. Dunne
 */
public class OrcsHealthTest {

   /**
    * Author should be the user that initiated the change. If it is a background operation or transaction not directly
    * initiated by a user, use OseeSystem, but this should only be last resort.
    */
   @Test
   public void testTransactionAuthors() {
      AtsApiIde atsApi = AtsApiService.get();
      List<Map<String, String>> query =
         atsApi.getQueryService().query("select * from osee_tx_details where author < 1");
      Assert.assertTrue("All transactions MUST have valid author.  Errors: " + query.toString(), query.isEmpty());
   }

}
