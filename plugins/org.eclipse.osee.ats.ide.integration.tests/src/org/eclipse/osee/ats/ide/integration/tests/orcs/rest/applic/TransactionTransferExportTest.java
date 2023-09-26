/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.orcs.rest.applic;

import java.util.Arrays;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.transaction.BranchLocation;
import org.eclipse.osee.orcs.rest.model.transaction.TransferDBType;
import org.eclipse.osee.orcs.rest.model.transaction.TransferInitData;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Carl Wilson, Torin Grenda
 */
public class TransactionTransferExportTest {

   private static TransactionEndpoint transactionEndpoint;
   private static TransferInitData testInitJson = new TransferInitData();
   /**
    * Simple Test To Get the last transaction in a branch
    */

   @BeforeClass
   public static void testSetup() {
      transactionEndpoint = ServiceUtil.getOseeClient().getTransactionEndpoint();

      //Setup initial initJson
      BranchLocation bl = new BranchLocation();
      BranchLocation blCommon = new BranchLocation();
      blCommon.setBaseTxId(getMaxTransaction(CoreBranches.COMMON));
      blCommon.setBranchId(CoreBranches.COMMON);
      bl.setBaseTxId(getMaxTransaction(DemoBranches.SAW_PL));
      bl.setBranchId(DemoBranches.SAW_PL); //
      testInitJson.setBranchLocations(Arrays.asList(blCommon, bl));
      testInitJson.setExportId(TransactionId.valueOf(124388928743L)); //Random Number
      testInitJson.setTransferDBType(TransferDBType.SOURCE);
      transactionEndpoint.initTransactionTransfer(testInitJson);
   }

   @Test
   public void testTransferExport() {
      //Create and commit a couple working branches with a few minor changes on them
      createTestWorkingBranch(DemoBranches.SAW_PL, "Test Working Branch #1");
      createTestWorkingBranch(DemoBranches.SAW_PL, "Test Working Branch #2");
      //Run export to export the changes made above
      transactionEndpoint.generateTransferFile(testInitJson.getExportId());
   }

   private static TransactionId getMaxTransaction(BranchId branchId) {
      return TransactionManager.getTransactionsForBranch(branchId).get(0);
   }

   private static void createTestWorkingBranch(BranchToken parentBranch, String branchName) {
      BranchToken workingBranch = BranchManager.createWorkingBranch(parentBranch, branchName);
      SkynetTransaction newTx = TransactionManager.createTransaction(workingBranch, "Sample transaction for exporting");
      Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement, workingBranch);
      artifact.internalSetApplicablityId(ApplicabilityId.valueOf(1234321L));
      artifact.addAttributeFromString(CoreAttributeTypes.MarkdownContent, "Test Markdown Content");
      artifact.addAttributeFromString(CoreAttributeTypes.Description, "Test Description Content");
      newTx.addArtifact(artifact);
      newTx.execute();
      boolean archiveSourceBranch = false;
      boolean overwriteUnresolvedConflicts = true;
      ConflictManagerExternal conflictManager = new ConflictManagerExternal(parentBranch, workingBranch);
      BranchManager.commitBranch(new NullProgressMonitor(), conflictManager, archiveSourceBranch,
         overwriteUnresolvedConflicts);
   }
}