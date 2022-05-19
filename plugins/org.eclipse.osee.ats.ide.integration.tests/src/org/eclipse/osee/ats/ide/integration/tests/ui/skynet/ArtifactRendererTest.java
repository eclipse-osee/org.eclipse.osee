/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ui.skynet;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Arrays;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Megumi Telles
 */
public class ArtifactRendererTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private static final String NAME1 = "Name with \"quote\"";
   private static final String NAME2 = "Name with 'quote'";
   private static final String EXPECTED_NAME = "Name+with+_quot";
   private static Artifact artifact1;
   private static Artifact artifact2;
   private static TransactionToken otherBranchTx;
   private static TransactionToken startTx1;
   private static TransactionToken startTx2;

   @Before
   public void setUp() {

      otherBranchTx = TransactionManager.getHeadTransaction(COMMON);
      artifact1 = new Artifact(CoreBranches.COMMON, NAME1);
      String comment1 = getClass().getSimpleName() + "_1";
      artifact1.persist(comment1);
      startTx1 = TransactionManager.getHeadTransaction(DemoBranches.SAW_Bld_1);
      BranchManager.setAssociatedArtifactId(DemoBranches.SAW_Bld_1, artifact1);

      artifact2 = new Artifact(CoreBranches.COMMON, NAME2);
      String comment2 = getClass().getSimpleName() + "_2";
      artifact2.persist(comment2);
      startTx2 = TransactionManager.getHeadTransaction(DemoBranches.SAW_Bld_2);
      BranchManager.setAssociatedArtifactId(DemoBranches.SAW_Bld_2, artifact2);
   }

   @Test
   public void testAssociatedArtifact_notAllowedDoubleQuotes() throws Exception {

      TransactionDelta deltaTx = new TransactionDelta(startTx1, otherBranchTx);
      ArtifactDelta delta = new ArtifactDelta(null, artifact2, artifact1);
      Change change = new ArtifactChange(COMMON, artifact1.getGammaId(), artifact1, deltaTx, ModificationType.MODIFIED,
         "", "", false, artifact1, delta);

      String name = RenderingUtil.getAssociatedArtifactName(Arrays.asList(change));
      Assert.assertEquals(EXPECTED_NAME, name);
   }

   @Test
   public void testAssociatedArtifact_notAllowedSingleQuotes() throws Exception {
      TransactionDelta deltaTx = new TransactionDelta(startTx2, otherBranchTx);
      ArtifactDelta delta = new ArtifactDelta(null, artifact1, artifact2);
      Change change = new ArtifactChange(COMMON, artifact2.getGammaId(), artifact2, deltaTx, ModificationType.MODIFIED,
         "", "", false, artifact2, delta);

      String name = RenderingUtil.getAssociatedArtifactName(Arrays.asList(change));
      Assert.assertEquals(EXPECTED_NAME, name);
   }
}
