/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.test.AtsTestUtilCore;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that the in-memory artifact transaction ID is properly updated after both attribute-only and relation-only
 * changes. This is critical for cache invalidation in XXWorkItemData.
 *
 * @author Donald G. Dunne
 */
public class TransactionLoadingTest {

   @Before
   public void setup() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
   }

   @After
   public void cleanup() {
      AtsTestUtil.cleanupSimpleTest(getClass().getSimpleName());
   }

   @Test
   public void testTransactionUpdatesOnRelationChange() {
      Artifact teamWfArt = (Artifact) AtsTestUtil.getTeamWf().getStoreObject();
      TransactionToken initialTx = teamWfArt.getTransaction();
      Assert.assertTrue("Initial transaction should be valid", initialTx.isValid());

      // --- Attribute change: confirm tx updates ---
      SkynetTransaction attrTx = TransactionManager.createTransaction(teamWfArt.getBranch(), "Attr change test");
      teamWfArt.setSoleAttributeValue(AtsAttributeTypes.Description, "Test description for tx test");
      attrTx.addArtifact(teamWfArt);
      TransactionToken attrTxResult = attrTx.execute();
      Assert.assertTrue("Attribute transaction should be valid", attrTxResult.isValid());

      // Check in-memory tx updated
      TransactionToken afterAttrTx = teamWfArt.getTransaction();
      Assert.assertNotEquals("Transaction should change after attribute persist", initialTx, afterAttrTx);

      // --- Relation-only change: set targeted version ---
      IAtsVersion version = AtsTestUtilCore.getVerArt1();
      Assert.assertNotNull("Version artifact should exist", version);
      Artifact versionArt = ArtifactQuery.getArtifactFromId(version.getArtifactToken(), teamWfArt.getBranch());
      Assert.assertNotNull("Version artifact should be loadable", versionArt);

      SkynetTransaction relTx = TransactionManager.createTransaction(teamWfArt.getBranch(), "Relation change test");
      teamWfArt.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
         java.util.Collections.singletonList(versionArt));
      relTx.addArtifact(teamWfArt);
      TransactionToken relTxResult = relTx.execute();
      Assert.assertTrue("Relation transaction should be valid", relTxResult.isValid());

      // Check in-memory tx updated after relation-only change
      TransactionToken afterRelTx = teamWfArt.getTransaction();
      Assert.assertNotEquals("Expect different transaction returned from execute", afterRelTx, afterAttrTx);

      // Reload and confirm the DB has the new tx
      teamWfArt.reloadAttributesAndRelations();
      TransactionToken afterReloadTx = teamWfArt.getTransaction();
      System.err.println("After Rel TX: " + afterRelTx);
      System.err.println("After Reload TX: " + afterReloadTx);
      Assert.assertEquals("After reload, transaction should be last reltx", afterRelTx, afterReloadTx);

      // Decache so we do a full reload
      ArtifactCache.deCache(teamWfArt);
      Assert.assertNull(ArtifactCache.getActive(teamWfArt.getArtifactId(), CoreBranches.COMMON));

      Artifact teamWfArtLoad = ArtifactQuery.getArtifactFromId(teamWfArt.getArtifactId(), CoreBranches.COMMON);
      TransactionToken afterFullQueryAndReload = teamWfArtLoad.getTransaction();
      Assert.assertEquals("After reload, transaction should be last reltx", afterRelTx, afterFullQueryAndReload);

   }
}
