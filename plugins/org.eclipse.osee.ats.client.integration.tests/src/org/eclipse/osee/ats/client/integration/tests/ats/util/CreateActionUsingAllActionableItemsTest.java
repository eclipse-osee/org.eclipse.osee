/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.util;

import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.util.CreateActionUsingAllActionableItems;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
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
      AtsBulkLoad.reloadConfig(true);
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(),
         CreateActionUsingAllActionableItemsTest.class.getSimpleName());
      for (Artifact art : ArtifactQuery.getArtifactListFromName("Big Action Test - Delete Me",
         AtsClientService.get().getAtsBranch(), DeletionFlag.EXCLUDE_DELETED)) {
         art.deleteAndPersist(transaction);
      }
      transaction.execute();
   }

   @org.junit.Test
   public void test() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      ActionResult action = CreateActionUsingAllActionableItems.createActionWithAllAis();
      if (TestUtil.isDemoDb()) {
         Assert.assertEquals("Should be 15 workflows created", 15, action.getTeams().size());
      } else {
         Assert.assertEquals("Should be 34 workflows created", 34, action.getTeams().size());
      }
      TestUtil.severeLoggingEnd(monitor);
   }

}
