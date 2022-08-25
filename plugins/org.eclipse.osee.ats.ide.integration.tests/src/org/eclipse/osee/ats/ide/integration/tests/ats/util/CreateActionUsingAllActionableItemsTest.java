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

package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.util.CreateActionUsingAllActionableItems;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test for {@link CreateActionUsingAllActionableItems}
 *
 * @author Donald G. Dunne
 */
public class CreateActionUsingAllActionableItemsTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() {
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(),
         CreateActionUsingAllActionableItemsTest.class.getSimpleName());
      for (Artifact art : ArtifactQuery.getArtifactListFromName("Big Action Test - Delete Me",
         AtsApiService.get().getAtsBranch())) {
         art.deleteAndPersist(transaction);
      }
      transaction.execute();
   }

   @org.junit.Test
   public void test() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      ActionResult action = CreateActionUsingAllActionableItems.createActionWithAllAis();
      if (TestUtil.isDemoDb()) {
         Assert.assertEquals("Should be 23 workflows created", 24, action.getTeamWfs().size());
      } else {
         Assert.assertEquals("Should be 34 workflows created", 34, action.getTeamWfs().size());
      }
      TestUtil.severeLoggingEnd(monitor);
   }

}
