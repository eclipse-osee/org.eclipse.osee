/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactNameComparator;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSide;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class MigrateRelationOrder extends AbstractBlam {
   private static final String SELECT_CHILD_BASELINE_TXS =
         "select transaction_id from osee_branch br, osee_tx_details txd where br.parent_branch_id = ? and branch_type = " + BranchType.WORKING.ordinal() + " and br.branch_id = txd.branch_id and txd.tx_type = " + TransactionDetailsType.Baselined.ordinal();

   private static final String SELECT_GAMMA_FROM_TXS =
         "select gamma_id, mod_type from osee_txs where transaction_id = ?";
   private final ArtifactNameComparator nameComparator = new ArtifactNameComparator();
   private final ComputeLegacyOrder computeLegacyOrder = new ComputeLegacyOrder();
   private final boolean fix = false;;
   private final boolean singleFix = false;

   @Override
   public String getName() {
      return "Migrate Relation Order";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      if (fix) {
         fixPreviousBug();
         return;
      }

      Branch baselineBranch = variableMap.getBranch("Branch");
      SkynetTransaction transaction = new SkynetTransaction(baselineBranch, getName());

      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromBranch(baselineBranch, false);
      println("artifact count: " + artifacts.size());

      for (RelationType relationType : RelationTypeManager.getAllTypes()) {
         if (relationType.isOrdered()) {
            IRelationEnumeration relationEnum = new RelationTypeSide(relationType, RelationSide.SIDE_B);
            for (Artifact artifact : artifacts) {
               try {
                  writeNewOrder(transaction, relationEnum, artifact);
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         }
      }

      Integer transactionNumber = transaction.getTransactionNumber();
      transaction.execute();
      addToChildBaseilnes(baselineBranch.getId(), transactionNumber);
   }

   private void writeNewOrder(SkynetTransaction transaction, IRelationEnumeration relationEnum, Artifact artifact) throws OseeCoreException {
      List<Artifact> relatedArtiafcts = artifact.getRelatedArtifacts(relationEnum);

      if (relatedArtiafcts.size() <= 1) {
         return; // user defined ordering is not needed
      }

      List<Artifact> legacyOrder = computeLegacyOrder.getOrginalOrder(artifact.getRelations(relationEnum));

      if (legacyOrder.size() != relatedArtiafcts.size()) {
         throw new OseeStateException("sizes don't match");
      }

      Collections.sort(relatedArtiafcts, nameComparator);
      if (relatedArtiafcts.equals(legacyOrder)) {
         return; // these are already sorted lexicographically
      }

      artifact.setRelationOrder(relationEnum, legacyOrder);
      artifact.persist(transaction);
   }

   private void addToChildBaseilnes(Integer branchId, Integer transactionNumber) throws OseeCoreException {
      final List<Object[]> txGammaList = new ArrayList<Object[]>(3000);

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(3000, SELECT_GAMMA_FROM_TXS, transactionNumber);
         while (chStmt.next()) {
            txGammaList.add(new Object[] {-1, chStmt.getLong("gamma_id"), chStmt.getInt("mod_type")});
         }
      } finally {
         chStmt.close();
      }

      try {
         chStmt.runPreparedQuery(100, SELECT_CHILD_BASELINE_TXS, branchId);
         while (chStmt.next()) {
            Integer baselineTransactionId = chStmt.getInt("transaction_id");
            for (Object[] data : txGammaList) {
               data[0] = baselineTransactionId;
            }
            ConnectionHandler.runBatchUpdate(
                  "insert into osee_txs (transaction_id, gamma_id, mod_type, tx_current) VALUES (?,?,?,1)", txGammaList);
         }
      } finally {
         chStmt.close();
      }

   }

   private void testOneArtifact(SkynetTransaction transaction, String guid) throws OseeCoreException {
      writeNewOrder(transaction, CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, ArtifactQuery.getArtifactFromId(
            guid, transaction.getBranch()));
   }

   private void fixPreviousBug() throws OseeCoreException {
      String[] branchNames;

      if (singleFix) {
         branchNames = new String[] {"Block III - FTB4"};
      } else {
         branchNames =
               new String[] {"Block III - FTB0", "Common", "MYII V11", "AH-64 MSA PDSP", "MYII V13 - FTB2", "V11_REU",
                     "Block III - FTB0.1", "Block III - FTB2", "Saudi - SAN1", "Saudi - FTB1", "MYII V13 - SBVT",
                     "Saudi - V13 - FTB2", "Saudi - V13 - SAN2", "Saudi - SBVT1", "Link16 - Eng Bld 1", "Taiwan - EB0",
                     "MSA Documents", "UK", "Block III - FTB4", "UAE - EB2", "UAE - SAN 2", "AH-6I",
                     "Block III - FTB3", "Block III - FTB2.2", "MYII V13.1 - FTB1", "MYII V13 - FTB1A",
                     "MYII V13 - FTB1", "Saudi - V11"};

      }
      for (String branchName : branchNames) {
         Integer branchId = BranchManager.getBranch(branchName).getId();
         Integer transactionNumber =
               ConnectionHandler.runPreparedQueryFetchInt(-9999,
                     "select transaction_id from osee_tx_details where branch_id = ? and osee_comment = ?", branchId,
                     "Migrate Relation Order");
         fixPreviousBugForBranch(branchId, transactionNumber);
      }
   }

   private void fixPreviousBugForBranch(Integer branchId, Integer transactionNumber) throws OseeDataStoreException {
      final List<Object[]> txGammaList = new ArrayList<Object[]>(3000);

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(3000, SELECT_GAMMA_FROM_TXS, transactionNumber);
         while (chStmt.next()) {
            txGammaList.add(new Object[] {chStmt.getInt("mod_type"), -1, chStmt.getLong("gamma_id")});
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(100, SELECT_CHILD_BASELINE_TXS, branchId);
         while (chStmt.next()) {
            Integer baselineTransactionId = chStmt.getInt("transaction_id");
            if (singleFix) {
               baselineTransactionId = 850145;
            }

            for (Object[] data : txGammaList) {
               data[1] = baselineTransactionId;
            }
            ConnectionHandler.runBatchUpdate(
                  "update osee_txs set mod_type = ? where transaction_id = ? and gamma_id = ?", txGammaList);
            if (singleFix) {
               break;
            }
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public String getDescriptionUsage() {
      return "Migrate relation ordering information from OSEE 0.7.0 storage type to OSEE 0.8.2";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}