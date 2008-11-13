/*
 * Created on Nov 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Theron Virgin
 */
public class UpdateMergeBranch extends DbTransaction {
   private static final String GET_ART_IDS_FOR_ART_VER_TABLE =
         "SELECT t1.art_id FROM " + ARTIFACT_VERSION_TABLE + " t1, " + TRANSACTION_DETAIL_TABLE + " t2, " + TRANSACTIONS_TABLE + " t3 where t2.transaction_id = t3.transaction_id and t3.gamma_id = t1.gamma_id and t2.branch_id = ?";
   private static final String GET_ART_IDS_FOR_ATR_VER_TABLE =
         "SELECT t1.art_id, t1.attr_id FROM " + ATTRIBUTE_VERSION_TABLE + " t1, " + TRANSACTION_DETAIL_TABLE + " t2, " + TRANSACTIONS_TABLE + " t3 where t2.transaction_id = t3.transaction_id and t3.gamma_id = t1.gamma_id and t2.branch_id = ?";
   private static final String GET_ART_IDS_FOR_REL_VER_TABLE =
         "SELECT t1.a_art_id, t1.b_art_id FROM " + RELATION_LINK_VERSION_TABLE + " t1, " + TRANSACTION_DETAIL_TABLE + " t2, " + TRANSACTIONS_TABLE + " t3 where t2.transaction_id = t3.transaction_id and t3.gamma_id = t1.gamma_id and t2.branch_id = ?";
   private static final String UPDATE_ARTIFACTS =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, txs.gamma_id, txs.mod_type, CASE WHEN txs.mod_type = 3 THEN " + TxChange.DELETED.getValue() + " WHEN txs.mod_type = 5 THEN " + TxChange.ARTIFACT_DELETED.getValue() + " ELSE " + TxChange.CURRENT.getValue() + " END FROM osee_tx_details det, osee_txs txs, osee_attribute attr WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.tx_current != 0 AND txs.gamma_id = attr.gamma_id AND attr.art_id = ? AND not exists (SELECT 'x' FROM osee_txs txs1, osee_attribute attr1 WHERE txs1.transaction_id = ? AND txs1.gamma_id = attr1.gamma_id AND attr1.attr_id = attr.attr_id)";

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
         System.out.println("            Need the following Artifacts on the Merge Branch");
         System.out.print("            ");
         for (Integer integer : expectedArtIds) {
            System.out.print(integer + ", ");
         }
         System.out.print("\n");
      }
      time = System.currentTimeMillis();
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
         BranchCreator.getInstance().addArtifactsToBranch(sourceBranch, destBranch, mergeBranch, expectedArtIds);
      }

      if (DEBUG) {
         System.out.println(String.format("          Adding %d new Artifacts took %s", expectedArtIds.size(),
               Lib.getElapseString(time)));
         time = System.currentTimeMillis();
      }
   }

   private static Collection<Integer> getAllMergeArtifacts(Branch branch) throws OseeDataStoreException {
      Collection<Integer> artSet = new HashSet<Integer>();
      long time = System.currentTimeMillis();

      ConnectionHandlerStatement chStmt1 = new ConnectionHandlerStatement();
      ConnectionHandlerStatement chStmt2 = new ConnectionHandlerStatement();
      ConnectionHandlerStatement chStmt3 = new ConnectionHandlerStatement();
      try {
         chStmt1.runPreparedQuery(GET_ART_IDS_FOR_ART_VER_TABLE, branch.getBranchId());
         while (chStmt1.next()) {
            artSet.add(chStmt1.getInt("art_id"));
         }
         if (DEBUG) {
            System.out.println(String.format(
                  "          Getting Artifacts that are on the Merge Branch Completed in %s", Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }

         chStmt2.runPreparedQuery(GET_ART_IDS_FOR_ATR_VER_TABLE, branch.getBranchId());
         while (chStmt2.next()) {
            artSet.add(chStmt2.getInt("art_id"));
         }
         if (DEBUG) {
            System.out.println(String.format(
                  "          Getting Attributes that are on the Merge Branch Completed in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }

         chStmt3.runPreparedQuery(GET_ART_IDS_FOR_REL_VER_TABLE, branch.getBranchId());
         while (chStmt3.next()) {
            artSet.add(chStmt3.getInt("a_art_id"));
            artSet.add(chStmt3.getInt("b_art_id"));
         }
      } finally {
         chStmt1.close();
         chStmt2.close();
         chStmt3.close();
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