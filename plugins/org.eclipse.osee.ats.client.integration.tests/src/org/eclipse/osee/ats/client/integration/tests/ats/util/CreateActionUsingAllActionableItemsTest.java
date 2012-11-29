/*
 * Created on Aug 29, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.util;

import junit.framework.Assert;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.CreateActionUsingAllActionableItems;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Test for {@link CreateActionUsingAllActionableItems}
 * 
 * @author Donald G. Dunne
 */
public class CreateActionUsingAllActionableItemsTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws OseeCoreException {
      AtsBulkLoad.reloadConfig(true);
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtil.getAtsBranch(),
            CreateActionUsingAllActionableItemsTest.class.getSimpleName());
      for (Artifact art : ArtifactQuery.getArtifactListFromName("Big Action Test - Delete Me",
         AtsUtil.getAtsBranchToken(), DeletionFlag.EXCLUDE_DELETED)) {
         art.deleteAndPersist(transaction);
      }
      transaction.execute();
   }

   @org.junit.Test
   public void test() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      ActionArtifact action = CreateActionUsingAllActionableItems.createActionWithAllAis();
      if (TestUtil.isDemoDb()) {
         Assert.assertEquals("Should be 14 workflows created", 14, action.getTeams().size());
      } else {
         Assert.assertEquals("Should be 33 workflows created", 33, action.getTeams().size());
      }
      TestUtil.severeLoggingEnd(monitor);
   }

}
