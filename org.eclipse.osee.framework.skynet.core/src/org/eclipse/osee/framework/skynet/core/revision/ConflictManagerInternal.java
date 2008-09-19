package org.eclipse.osee.framework.skynet.core.revision;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflictBuilder;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflictBuilder;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictBuilder;
import org.eclipse.osee.framework.skynet.core.exception.BranchMergeException;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionDetailsType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Theron Virgin
 * @author Jeff C. Phillips
 */
public class ConflictManagerInternal {
   private static final String ARTIFACT_CONFLICTS =
         "SELECT art1.art_type_id, arv1.art_id, txs1.mod_type AS source_mod_type, txs1.gamma_id AS source_gamma, txs2.mod_type AS dest_mod_type, txs2.gamma_id AS dest_gamma, arv3.gamma_id AS begin_gamma FROM osee_define_txs txs1, osee_define_txs txs2, osee_define_txs txs3, osee_define_tx_details txd1, osee_define_tx_details txd2, osee_define_artifact_version arv1, osee_define_artifact_version arv2, osee_define_artifact_version arv3 , osee_define_artifact art1 WHERE txd1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND txs1.gamma_id = arv1.gamma_id and arv1.art_id = art1.art_id AND arv1.art_id = arv2.art_id AND arv2.gamma_id = txs2.gamma_id AND txs2.tx_current in (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND txs2.transaction_id = txd2.transaction_id AND txs2.transaction_id > ? AND txd2.branch_id = ? AND txs3.transaction_id = ? AND txs3.gamma_id = arv3.gamma_id and arv3.art_id = arv1.art_id";

   private static final String ATTRIBUTE_CONFLICTS_NEW =
         "SELECT atr1.art_id, txs1.mod_type, atr1.attr_type_id, atr1.attr_id, atr1.gamma_id AS source_gamma, atr1.value AS source_value, atr2.gamma_id AS dest_gamma, atr2.value as dest_value, txs2.mod_type AS dest_mod_type FROM osee_define_txs txs1, osee_define_txs txs2, osee_define_tx_details txd1, osee_define_tx_details txd2, osee_define_attribute atr1, osee_define_attribute atr2 WHERE txd1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.mod_type =  " + ModificationType.NEW.getValue() + " AND txs1.tx_current in (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND txs1.gamma_id = atr1.gamma_id AND atr1.attr_id = atr2.attr_id AND atr2.gamma_id = txs2.gamma_id AND txs2.tx_current in (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND txs2.transaction_id = txd2.transaction_id AND txs2.transaction_id > ? AND txd2.branch_id = ?";

   private static final String ATTRIBUTE_CONFLICTS =
         "SELECT atr1.art_id, txs1.mod_type, atr1.attr_type_id, atr1.attr_id, atr1.gamma_id AS source_gamma, atr1.value AS source_value, atr2.gamma_id AS dest_gamma, atr2.value as dest_value, txs2.mod_type AS dest_mod_type, atr3.gamma_id AS begin_gamma FROM osee_define_txs txs1, osee_define_txs txs2, osee_define_txs txs3, osee_define_tx_details txd1, osee_define_tx_details txd2, osee_define_attribute atr1, osee_define_attribute atr2, osee_define_attribute atr3 WHERE txd1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND txs1.gamma_id = atr1.gamma_id AND atr1.attr_id = atr2.attr_id AND atr2.gamma_id = txs2.gamma_id AND txs2.tx_current in (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND txs2.transaction_id = txd2.transaction_id AND txs2.transaction_id > ? AND txd2.branch_id = ? AND txs3.transaction_id = ?  AND txs3.gamma_id = atr3.gamma_id and atr3.attr_id = atr1.attr_id";

   private static final String HISTORICAL_ATTRIBUTE_CONFLICTS =
         "SELECT atr.attr_id, atr.art_id, source_gamma_id, dest_gamma_id, attr_type_id, mer.merge_branch_id, mer.dest_branch_id, value as source_value FROM osee_define_conflict con, osee_define_merge mer, osee_define_attribute atr Where mer.transaction_id = ? AND mer.merge_branch_id = con.branch_id And con.source_gamma_id = atr.gamma_id AND con.status = " + Conflict.Status.COMMITTED.getValue() + " order by attr_id";

   private static ConflictManagerInternal instance = new ConflictManagerInternal();

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Merge"));

   private ConflictManagerInternal() {
      super();
   }

   public static ConflictManagerInternal getInstance() {
      return instance;
   }

   public List<Conflict> getConflictsPerBranch(TransactionId commitTransaction) throws SQLException, OseeCoreException {
      long time = System.currentTimeMillis();
      long totalTime = time;
      if (DEBUG) {
         System.out.println(String.format("\nDiscovering Conflicts based on Transaction ID: %d", commitTransaction));
         totalTime = System.currentTimeMillis();
      }
      ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
      ConnectionHandlerStatement connectionHandlerStatement = null;
      if (DEBUG) {
         System.out.println("Running Query to find conflicts stored in the DataBase");
         time = System.currentTimeMillis();
      }
      try {
         connectionHandlerStatement =
               ConnectionHandler.runPreparedQuery(HISTORICAL_ATTRIBUTE_CONFLICTS,
                     commitTransaction.getTransactionNumber());
         if (DEBUG) {
            System.out.println(String.format("          Query finished in %s", Lib.getElapseString(time)));
         }
         ResultSet resultSet = connectionHandlerStatement.getRset();
         while (resultSet.next()) {
            AttributeConflict attributeConflict =
                  new AttributeConflict(resultSet.getInt("source_gamma_id"), resultSet.getInt("dest_gamma_id"),
                        resultSet.getInt("art_id"), commitTransaction, resultSet.getString("source_value"),
                        resultSet.getInt("attr_id"), resultSet.getInt("attr_type_id"),
                        BranchPersistenceManager.getBranch(resultSet.getInt("merge_branch_id")),
                        BranchPersistenceManager.getBranch(resultSet.getInt("dest_branch_id")));
            conflicts.add(attributeConflict);

            attributeConflict.computeStatus();

         }
      } finally {
         DbUtil.close(connectionHandlerStatement);
      }
      if (DEBUG) {
         debugDump(conflicts, totalTime);
      }
      return conflicts;
   }

   public List<Conflict> getConflictsPerBranch(Branch sourceBranch, Branch destinationBranch, TransactionId baselineTransaction) throws SQLException, OseeCoreException {
      long totalTime = 0;
      if (DEBUG) {
         System.out.println(String.format("\nDiscovering Conflicts based on Source Branch: %d Destination Branch: %d",
               sourceBranch.getBranchId(), destinationBranch.getBranchId()));
         totalTime = System.currentTimeMillis();
      }
      ArrayList<ConflictBuilder> conflictBuilders = new ArrayList<ConflictBuilder>();
      ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
      Set<Integer> artIdSet = new HashSet<Integer>();
      Set<Integer> artIdSetDontShow = new HashSet<Integer>();
      Set<Integer> artIdSetDontAdd = new HashSet<Integer>();
      if ((sourceBranch == null) || (destinationBranch == null)) {
         throw new IllegalArgumentException(String.format("Source Branch = %s Destination Branch = %s",
               sourceBranch == null ? "NULL" : sourceBranch.getBranchId(),
               destinationBranch == null ? "NULL" : destinationBranch.getBranchId()));
      }

      loadArtifactVersionConflicts(sourceBranch, destinationBranch, baselineTransaction, conflictBuilders, artIdSet,
            artIdSetDontShow, artIdSetDontAdd);
      loadAttributeConflicts(sourceBranch, destinationBranch, baselineTransaction, conflictBuilders, artIdSet);

      //Remove Art IDs for artifacts that should not be added to the branch because they were deleted etc. 
      for (Integer integer : artIdSetDontAdd) {
         artIdSet.remove(integer);
      }
      if (artIdSet.isEmpty()) return conflicts;

      Branch mergeBranch =
            BranchPersistenceManager.getOrCreateMergeBranch(sourceBranch, destinationBranch, new ArrayList<Integer>(
                  artIdSet));

      if (mergeBranch == null) throw new BranchMergeException("Could not create the Merge Branch.");

      preloadConflictArtifacts(sourceBranch, destinationBranch, mergeBranch, artIdSet);

      //Don't create the conflicts for attributes on an artifact that is deleted etc.
      for (ConflictBuilder conflictBuilder : conflictBuilders) {
         Conflict conflict = conflictBuilder.getConflict(mergeBranch, artIdSetDontShow);
         if (conflict != null) {
            conflicts.add(conflict);
            conflict.computeStatus();
         }
      }
      if (DEBUG) {
         debugDump(conflicts, totalTime);
      }
      return conflicts;
   }

   private void preloadConflictArtifacts(Branch sourceBranch, Branch destinationBranch, Branch mergeBranch, Collection<Integer> artIdSet) throws SQLException {
      long time = 0;
      if (DEBUG) {
         System.out.println("Prelodaing Conflict Artifacts");
         time = System.currentTimeMillis();
      }
      if (artIdSet != null && !artIdSet.isEmpty()) {
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

         List<Object[]> insertParameters = new LinkedList<Object[]>();
         for (int artId : artIdSet) {
            insertParameters.add(new Object[] {queryId, insertTime, artId, sourceBranch.getBranchId(),
                  SQL3DataType.INTEGER});
            insertParameters.add(new Object[] {queryId, insertTime, artId, destinationBranch.getBranchId(),
                  SQL3DataType.INTEGER});
            insertParameters.add(new Object[] {queryId, insertTime, artId, mergeBranch.getBranchId(),
                  SQL3DataType.INTEGER});
         }
         ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, insertParameters, true, false, true);
      }
      if (DEBUG) {
         System.out.println(String.format("    Preloading took %s", Lib.getElapseString(time)));
      }
   }

   /**
    * @param sourceBranch
    * @param destinationBranch
    * @param baselineTransaction
    * @param conflictBuilders
    * @param artIdSet
    */

   private void loadArtifactVersionConflicts(Branch sourceBranch, Branch destinationBranch, TransactionId baselineTransaction, ArrayList<ConflictBuilder> conflictBuilders, Set<Integer> artIdSet, Set<Integer> artIdSetDontShow, Set<Integer> artIdSetDontAdd) throws SQLException {
      long time = 0;
      if (DEBUG) {
         System.out.println("Finding Artifact Version Conflicts");
         System.out.println("    Running the Artifact Conflict Query");
         time = System.currentTimeMillis();
      }
      ConnectionHandlerStatement connectionHandlerStatement = null;

      try {
         connectionHandlerStatement =
               ConnectionHandler.runPreparedQuery(ARTIFACT_CONFLICTS, sourceBranch.getBranchId(),
                     baselineTransaction.getTransactionNumber(), destinationBranch.getBranchId(),
                     baselineTransaction.getTransactionNumber());

         if (DEBUG) {
            System.out.println(String.format("         Query completed in %s ", Lib.getElapseString(time)));
         }
         ResultSet resultSet = connectionHandlerStatement.getRset();

         if (!resultSet.next()) return;
         ArtifactConflictBuilder artifactConflictBuilder;
         int artId = 0;

         do {
            int nextArtId = resultSet.getInt("art_id");
            int sourceGamma = resultSet.getInt("source_gamma");
            int destGamma = resultSet.getInt("dest_gamma");
            int sourceModType = resultSet.getInt("source_mod_type");
            int destModType = resultSet.getInt("dest_mod_type");
            int artTypeId = resultSet.getInt("art_type_id");
            int beginGamma = resultSet.getInt("begin_gamma");

            if (artId != nextArtId && beginGamma != destGamma) {
               artId = nextArtId;

               if ((destModType == ModificationType.DELETED.getValue() && sourceModType == ModificationType.CHANGE.getValue()) || (destModType == ModificationType.CHANGE.getValue() && sourceModType == ModificationType.DELETED.getValue())) {

                  artifactConflictBuilder =
                        new ArtifactConflictBuilder(sourceGamma, destGamma, artId, baselineTransaction, sourceBranch,
                              destinationBranch, sourceModType, destModType, artTypeId);

                  conflictBuilders.add(artifactConflictBuilder);
                  artIdSet.add(artId);
               } else if (destModType == ModificationType.DELETED.getValue() && sourceModType == ModificationType.DELETED.getValue()) {
                  artIdSetDontShow.add(artId);
                  artIdSetDontAdd.add(artId);
               }
            }
         } while (resultSet.next());
      } finally {
         DbUtil.close(connectionHandlerStatement);
      }
      for (Integer integer : artIdSet) {
         artIdSetDontShow.add(integer);
      }
   }

   /**
    * @param sourceBranch
    * @param destinationBranch
    * @param baselineTransaction
    * @param conflicts
    * @throws SQLException
    */
   private void loadAttributeConflicts(Branch sourceBranch, Branch destinationBranch, TransactionId baselineTransaction, ArrayList<ConflictBuilder> conflictBuilders, Set<Integer> artIdSet) throws SQLException, OseeCoreException {
      long time = 0;
      if (DEBUG) {
         System.out.println("Finding Attribute Version Conflicts");
         System.out.println("    Running the First Attribute Conflict Query");
         time = System.currentTimeMillis();
      }
      ConnectionHandlerStatement connectionHandlerStatement = null;
      AttributeConflictBuilder attributeConflictBuilder;
      try {
         connectionHandlerStatement =
               ConnectionHandler.runPreparedQuery(ATTRIBUTE_CONFLICTS_NEW, sourceBranch.getBranchId(),
                     baselineTransaction.getTransactionNumber(), destinationBranch.getBranchId());

         if (DEBUG) {
            System.out.println(String.format("         Query completed in %s ", Lib.getElapseString(time)));
         }
         ResultSet resultSet = connectionHandlerStatement.getRset();

         int attrId = 0;

         if (resultSet.next()) {

            do {
               int nextAttrId = resultSet.getInt("attr_id");
               int artId = resultSet.getInt("art_id");
               int sourceGamma = resultSet.getInt("source_gamma");
               int destGamma = resultSet.getInt("dest_gamma");
               int modType = resultSet.getInt("mod_type");
               int attrTypeId = resultSet.getInt("attr_type_id");
               String sourceValue =
                     resultSet.getString("source_value") != null ? resultSet.getString("source_value") : resultSet.getString("dest_value");

               if (attrId != nextAttrId && modType == ModificationType.NEW.getValue()) {
                  attrId = nextAttrId;
                  attributeConflictBuilder =
                        new AttributeConflictBuilder(sourceGamma, destGamma, artId, baselineTransaction, sourceBranch,
                              destinationBranch, sourceValue, attrId, attrTypeId);

                  conflictBuilders.add(attributeConflictBuilder);
                  artIdSet.add(artId);
               }
            } while (resultSet.next());
         }

      } finally {
         DbUtil.close(connectionHandlerStatement);
      }

      if (DEBUG) {
         System.out.println("    Running the Second Attribute Conflict Query");
         time = System.currentTimeMillis();
      }
      try {
         connectionHandlerStatement =
               ConnectionHandler.runPreparedQuery(ATTRIBUTE_CONFLICTS, sourceBranch.getBranchId(),
                     baselineTransaction.getTransactionNumber(), destinationBranch.getBranchId(),
                     baselineTransaction.getTransactionNumber());

         if (DEBUG) {
            System.out.println(String.format("         Query completed in %s ", Lib.getElapseString(time)));
         }

         ResultSet resultSet = connectionHandlerStatement.getRset();

         if (!resultSet.next()) return;
         int attrId = 0;

         do {
            int nextAttrId = resultSet.getInt("attr_id");
            int artId = resultSet.getInt("art_id");
            int sourceGamma = resultSet.getInt("source_gamma");
            int destGamma = resultSet.getInt("dest_gamma");
            int attrTypeId = resultSet.getInt("attr_type_id");
            int beginGamma = resultSet.getInt("begin_gamma");
            String sourceValue =
                  resultSet.getString("source_value") != null ? resultSet.getString("source_value") : resultSet.getString("dest_value");

            if (attrId != nextAttrId && beginGamma != destGamma) {
               attrId = nextAttrId;
               attributeConflictBuilder =
                     new AttributeConflictBuilder(sourceGamma, destGamma, artId, baselineTransaction, sourceBranch,
                           destinationBranch, sourceValue, attrId, attrTypeId);

               conflictBuilders.add(attributeConflictBuilder);
               artIdSet.add(artId);
            }
         } while (resultSet.next());
      } finally {
         DbUtil.close(connectionHandlerStatement);
      }
   }

   private void debugDump(Collection<Conflict> conflicts, long time) throws SQLException, OseeCoreException {
      int displayCount = 1;
      System.out.println(String.format("Found %d conflicts in %s", conflicts.size(), Lib.getElapseString(time)));
      for (Conflict conflict : conflicts) {
         System.out.println(String.format(
               "    %d. ArtId = %d, ChangeItem = %s, SourceGamma = %d, DestGamma = %d, Status = %s", displayCount++,
               conflict.getArtId(), conflict.getChangeItem(), conflict.getSourceGamma(), conflict.getDestGamma(),
               conflict.getStatus()));
      }
   }

}
