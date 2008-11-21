/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.OseeSql;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Theron Virgin
 */
public class UpdateMergeBranch extends DbTransaction {
   private static final String UPDATE_ARTIFACTS =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT  ?, txs.gamma_id, txs.mod_type, CASE WHEN txs.mod_type = 3 THEN " + TxChange.DELETED.getValue() + " WHEN txs.mod_type = 5 THEN " + TxChange.ARTIFACT_DELETED.getValue() + " ELSE " + TxChange.CURRENT.getValue() + " END FROM osee_attribute attr, osee_txs txs, osee_tx_details det WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.tx_current != 0 AND txs.gamma_id = attr.gamma_id AND attr.art_id = ? AND not exists (SELECT 'x' FROM osee_txs txs1, osee_attribute attr1 WHERE txs1.transaction_id = ? AND txs1.gamma_id = attr1.gamma_id AND attr1.attr_id = attr.attr_id)";

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Merge"));

   private final Branch mergeBranch;
   private final ArrayList<Integer> expectedArtIds;
   private final Branch destBranch;
   private final Branch sourceBranch;

   /**
    * @param destBranch
    * @param expectedArtIds
    * @param mergeBranch
    * @param sourceBranch
    */
   public UpdateMergeBranch(Branch mergeBranch, ArrayList<Integer> expectedArtIds, Branch destBranch, Branch sourceBranch) throws OseeCoreException {
      this.destBranch = destBranch;
      this.expectedArtIds = expectedArtIds;
      this.mergeBranch = mergeBranch;
      this.sourceBranch = sourceBranch;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.DbTransaction#handleTxWork(java.sql.Connection)
    */
   @Override
   protected void handleTxWork(Connection connection) throws OseeCoreException {
      Collection<Integer> allMergeBranchArtifacts = getAllMergeArtifacts(mergeBranch);
      long time = System.currentTimeMillis();
      Collection<Integer> allMergeBranchArtifactsCopy = new HashSet<Integer>(allMergeBranchArtifacts);
      Collection<Artifact> goodMergeBranchArtifacts = ArtifactQuery.getArtifactsFromBranch(mergeBranch, true);

      if (DEBUG) {
         System.out.println(String.format("        Get artifacts on branch took %s", Lib.getElapseString(time)));
         time = System.currentTimeMillis();
         System.out.println("            Need the following Artifacts on the Merge Branch");
         System.out.print("            ");
         for (Integer integer : expectedArtIds) {
            System.out.print(integer + ", ");
         }
         System.out.print("\n");
      }
      int count = 0;
      //Delete any damaged artifacts (from a source revert) on the merge branch
      for (Artifact artifact : goodMergeBranchArtifacts) {
         allMergeBranchArtifactsCopy.remove(new Integer(artifact.getArtId()));
      }
      if (!allMergeBranchArtifactsCopy.isEmpty()) {
         for (Integer artifact : allMergeBranchArtifactsCopy) {
            ArtifactPersistenceManager.purgeArtifactFromBranch(connection, mergeBranch.getBranchId(),
                  artifact.intValue());
            count++;
         }
      }
      if (DEBUG) {
         System.out.println(String.format("          Deleting %d Damaged Artifacts took %s", count,
               Lib.getElapseString(time)));
         time = System.currentTimeMillis();
         count = 0;
      }

      //Delete any artifacts that shouldn't be on the merge branch but are
      for (Integer artid : expectedArtIds) {
         allMergeBranchArtifacts.remove(artid);
      }
      if (!allMergeBranchArtifacts.isEmpty()) {
         for (Integer artifact : allMergeBranchArtifacts) {
            count++;
            ArtifactPersistenceManager.purgeArtifactFromBranch(connection, mergeBranch.getBranchId(),
                  artifact.intValue());
         }
      }
      if (DEBUG) {
         System.out.println(String.format("          Deleting %d unused Artifacts took %s", count,
               Lib.getElapseString(time)));
         time = System.currentTimeMillis();
         count = 0;
      }
      int numberAttrUpdated = 0;
      //Copy over any missing attributes
      int baselineTransaction = TransactionIdManager.getStartEndPoint(mergeBranch).getKey().getTransactionNumber();
      for (Artifact artifact : goodMergeBranchArtifacts) {
         numberAttrUpdated +=
               ConnectionHandler.runPreparedUpdate(UPDATE_ARTIFACTS, baselineTransaction, sourceBranch.getBranchId(),
                     artifact.getArtId(), baselineTransaction);
      }
      if (DEBUG) {
         System.out.println(String.format("          Adding %d Attributes to Existing Artifacts took %s",
               numberAttrUpdated, Lib.getElapseString(time)));
         time = System.currentTimeMillis();
      }

      //Add any artifacts that should be on the merge branch but aren't
      for (Artifact artifact : goodMergeBranchArtifacts) {
         expectedArtIds.remove(new Integer(artifact.getArtId()));
      }
      if (!expectedArtIds.isEmpty()) {
         BranchCreator.getInstance().addArtifactsToBranch(connection, sourceBranch, destBranch, mergeBranch,
               expectedArtIds);
      }

      if (DEBUG) {
         System.out.println(String.format("          Adding %d new Artifacts took %s", expectedArtIds.size(),
               Lib.getElapseString(time)));
         time = System.currentTimeMillis();
      }
   }

   private static Collection<Integer> getAllMergeArtifacts(Branch branch) throws OseeCoreException {
      Collection<Integer> artSet = new HashSet<Integer>();
      long time = System.currentTimeMillis();

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Merge.SELECT_ARTIFACTS_ON_A_BRANCH),
               branch.getBranchId());
         while (chStmt.next()) {
            artSet.add(chStmt.getInt("art_id"));
         }
         if (DEBUG) {
            System.out.println(String.format(
                  "          Getting Artifacts that are on the Merge Branch Completed in %s", Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }

         chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Merge.SELECT_ATTRIBUTES_ON_A_BRANCH),
               branch.getBranchId());
         while (chStmt.next()) {
            artSet.add(chStmt.getInt("art_id"));
         }
         if (DEBUG) {
            System.out.println(String.format(
                  "          Getting Attributes that are on the Merge Branch Completed in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }

         chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Merge.SELECT_REL_LINKS_ON_A_BRANCH),
               branch.getBranchId());
         while (chStmt.next()) {
            artSet.add(chStmt.getInt("a_art_id"));
            artSet.add(chStmt.getInt("b_art_id"));
         }
      } finally {
         chStmt.close();
      }
      if (DEBUG) {
         System.out.println(String.format("          Getting Relations that are on the Merge Branch Completed in %s",
               Lib.getElapseString(time)));
         System.out.println("            Found the following Artifacts on the Merge Branch");
         System.out.print("            ");
         for (Integer integer : artSet) {
            System.out.print(integer + ", ");
         }
         System.out.print("\n");
      }
      return artSet;
   }
}