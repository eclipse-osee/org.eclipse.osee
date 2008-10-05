package org.eclipse.osee.framework.skynet.core.revision;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChanged;
import org.eclipse.osee.framework.skynet.core.change.AttributeChanged;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.RelationChanged;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionDetailsType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * Acquires changes for either branches or transactions.
 * 
 * @author Jeff C. Phillips
 */
public class ChangeManager {
   private static final String BRANCH_ATTRIBUTE_WAS_CHANGE =
         "SELECT t3.attr_id, t3.value as was_value, t1.mod_type FROM osee_txs t1, osee_tx_details t2, osee_attribute t3, osee_artifact t8, osee_join_artifact t9 WHERE t2.branch_id = ? AND t2.transaction_id = t1.transaction_id AND t2.tx_type = 1 AND t8.art_id = t3.art_id AND t3.gamma_id = t1.gamma_id AND t3.art_id = t9.art_id AND t2.branch_id = t9.branch_id AND t9.query_id = ?";

   private static final String TRANSACTION_ATTRIBUTE_WAS_CHANGE =
         "SELECT att1.attr_id, att1.value as was_value, txs1.mod_type FROM osee_join_artifact al1, osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE  al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id < ? AND al1.query_id = ? AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by txd1.branch_id, att1.art_id, att1.attr_id, txd1.transaction_id desc";

   private static final String BRANCH_ATTRIBUTE_IS_CHANGES =
         "SELECT t8.art_type_id, t3.art_id, t3.attr_id, t3.gamma_id, t3.attr_type_id, t3.value as is_value, t1.mod_type FROM osee_txs t1, osee_tx_details t2, osee_attribute t3, osee_artifact t8 WHERE t2.branch_id = ? AND t2.transaction_id = t1.transaction_id AND t1.tx_current in (" + TxChange.DELETED.getValue() + ", " + TxChange.CURRENT.getValue() + ", " + TxChange.ARTIFACT_DELETED.getValue() + ") AND t2.tx_type = 0 AND t8.art_id = t3.art_id AND t3.gamma_id = t1.gamma_id";

   private static final String TRANSACTION_ATTRIBUTE_CHANGES =
         "SELECT t8.art_type_id, t3.art_id, t3.attr_id, t3.gamma_id, t3.attr_type_id, t3.value as is_value, t1.mod_type FROM osee_txs t1, osee_tx_details t2, osee_attribute t3, osee_artifact t8 WHERE t2.transaction_id = ? AND t2.transaction_id = t1.transaction_id AND t2.tx_type = 0 AND t8.art_id = t3.art_id AND t3.gamma_id = t1.gamma_id";

   private static final String BRANCH_REL_CHANGES =
         "SELECT tx1.mod_type, rl3.gamma_id, rl3.b_art_id, rl3.a_art_id, rl3.a_order, rl3.b_order, rl3.rationale, rl3.rel_link_id, rl3.rel_link_type_id from osee_txs tx1, osee_tx_details td2, osee_relation_link rl3 where tx1.tx_current in (" + TxChange.DELETED.getValue() + ", " + TxChange.CURRENT.getValue() + ", " + TxChange.ARTIFACT_DELETED.getValue() + ") AND td2.tx_type = 0 AND td2.branch_id = ? AND tx1.transaction_id = td2.transaction_id AND tx1.gamma_id = rl3.gamma_id";

   private static final String TRANSACTION_REL_CHANGES =
         "SELECT tx1.mod_type, rl3.gamma_id, rl3.b_art_id, rl3.a_art_id, rl3.a_order, rl3.b_order, rl3.rationale, rl3.rel_link_id, rl3.rel_link_type_id from osee_txs tx1, osee_tx_details td2, osee_relation_link rl3 where td2.tx_type = 0 AND td2.transaction_id = ? AND tx1.transaction_id = td2.transaction_id AND tx1.gamma_id = rl3.gamma_id";

   private static final String BRANCH_ARTIFACT_CHANGES =
         "select af4.art_id, af4.art_type_id, av3.gamma_id, tx1.mod_type FROM osee_txs tx1, osee_tx_details td2, osee_artifact_version av3, osee_artifact af4 WHERE td2.branch_id = ? AND td2.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND td2.transaction_id = tx1.transaction_id AND tx1.gamma_id = av3.gamma_id AND (tx1.tx_current = " + TxChange.DELETED.getValue() + " OR tx1.mod_type = " + ModificationType.NEW.getValue() + ")  AND av3.art_id = af4.art_id";

   private static final String TRANSACTION_ARTIFACT_CHANGES =
         "select af4.art_id, af4.art_type_id, av3.gamma_id, tx1.mod_type FROM osee_txs tx1, osee_tx_details td2, osee_artifact_version av3, osee_artifact af4 WHERE td2.transaction_id = ? AND td2.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND td2.transaction_id = tx1.transaction_id AND tx1.gamma_id = av3.gamma_id AND (tx1.mod_type = " + ModificationType.DELETED.getValue() + " OR tx1.mod_type = " + ModificationType.NEW.getValue() + ")  AND av3.art_id = af4.art_id";

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Change"));

   private static ChangeManager instance = new ChangeManager();

   private ChangeManager() {
      super();
   }

   public static ChangeManager getInstance() {
      return instance;
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return
    * @throws OseeCoreException
    */
   public Collection<Change> getChangesPerTransaction(TransactionId transactionId) throws OseeCoreException {
      return getChanges(null, transactionId);
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return
    * @throws OseeCoreException
    */
   public Collection<Change> getChangesPerBranch(Branch sourceBranch) throws OseeCoreException {
      return getChanges(sourceBranch, null);
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return
    * @throws OseeCoreException
    */
   private Collection<Change> getChanges(Branch sourceBranch, TransactionId transactionId) throws OseeCoreException {
      ArrayList<Change> changes = new ArrayList<Change>();
      Set<Integer> artIds = new HashSet<Integer>();
      Set<Integer> newAndDeletedArtifactIds = new HashSet<Integer>();
      boolean historical = sourceBranch == null;
      long totalTime = System.currentTimeMillis();

      if (DEBUG) {
         System.out.println(String.format("\nChange Manager: getChanges(%s, %s)", sourceBranch, transactionId));
      }

      loadNewOrDeletedArtifactChanges(sourceBranch, transactionId, artIds, changes, newAndDeletedArtifactIds);
      loadAttributeChanges(sourceBranch, transactionId, artIds, changes, newAndDeletedArtifactIds);
      loadRelationChanges(sourceBranch, transactionId, artIds, changes, newAndDeletedArtifactIds);

      Branch branch = historical ? transactionId.getBranch() : sourceBranch;

      if (historical) {
         for (Change change : changes) {
            change.setBranch(branch);
         }
      }

      long time = System.currentTimeMillis();
      if (!artIds.isEmpty()) {
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

         List<Object[]> insertParameters = new LinkedList<Object[]>();
         for (int artId : artIds) {
            insertParameters.add(new Object[] {queryId, insertTime, artId, branch.getBranchId(),
                  historical ? transactionId.getTransactionNumber() : SQL3DataType.INTEGER});
         }
         ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, insertParameters, false, historical, true);
      }

      if (DEBUG) {
         System.out.println(String.format("     Loaded %d Artifacts in %s", artIds.size(), Lib.getElapseString(time)));
         System.out.println(String.format("Change Manager: Found all of the Changes in %s\n",
               Lib.getElapseString(totalTime)));
      }
      return changes;
   }

   /**
    * @param sourceBranch
    * @param changes
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    * @throws OseeDataStoreException
    */
   private void loadNewOrDeletedArtifactChanges(Branch sourceBranch, TransactionId transactionId, Set<Integer> artIds, ArrayList<Change> changes, Set<Integer> newAndDeletedArtifactIds) throws BranchDoesNotExist, TransactionDoesNotExist, OseeDataStoreException {
      ConnectionHandlerStatement chStmt = null;
      Map<Integer, ArtifactChanged> artifactChanges = new HashMap<Integer, ArtifactChanged>();
      boolean hasBranch = sourceBranch != null;
      TransactionId fromTransactionId;
      TransactionId toTransactionId;
      long time = System.currentTimeMillis();
      if (DEBUG) {
         System.out.println(String.format("     Gathering New or Deleted Artifacts on %s",
               hasBranch ? "Branch: " + sourceBranch : "Transaction: " + transactionId));
      }

      try {
         //Changes per a branch
         if (hasBranch) {
            Pair<TransactionId, TransactionId> branchStartEndTransaction =
                  TransactionIdManager.getStartEndPoint(sourceBranch);

            fromTransactionId = branchStartEndTransaction.getKey();
            toTransactionId = branchStartEndTransaction.getValue();

            chStmt = ConnectionHandler.runPreparedQuery(BRANCH_ARTIFACT_CHANGES, sourceBranch.getBranchId());
         }
         //Changes per a transaction
         else {
            toTransactionId = transactionId;
            fromTransactionId = TransactionIdManager.getPriorTransaction(toTransactionId);

            chStmt =
                  ConnectionHandler.runPreparedQuery(TRANSACTION_ARTIFACT_CHANGES,
                        toTransactionId.getTransactionNumber());
         }
         int count = 0;
         while (chStmt.next()) {
            count++;
            int artId = chStmt.getInt("art_id");

            ArtifactChanged artifactChanged =
                  new ArtifactChanged(sourceBranch, chStmt.getInt("art_type_id"), chStmt.getInt("gamma_id"), artId,
                        toTransactionId, fromTransactionId, ModificationType.getMod(chStmt.getInt("mod_type")),
                        ChangeType.OUTGOING, !hasBranch);

            //We do not want to display artifacts that were new and then deleted
            //The only was this could happen is if the artifact was in here twice
            //since the sql only returns new or deleted artifacts
            if (!artifactChanges.containsKey(artId)) {
               artIds.add(artId);
               changes.add(artifactChanged);
               artifactChanges.put(artId, artifactChanged);
            } else {
               changes.remove(artifactChanges.get(artId));
               newAndDeletedArtifactIds.add(artId);
            }
         }
         if (DEBUG) {
            System.out.println(String.format("        Found %d Changes in %s", count, Lib.getElapseString(time)));
         }
      } finally {
         ConnectionHandler.close(chStmt);
      }
   }

   /**
    * @param sourceBranch
    * @param changes
    * @throws OseeCoreException
    */
   private void loadRelationChanges(Branch sourceBranch, TransactionId transactionId, Set<Integer> artIds, ArrayList<Change> changes, Set<Integer> newAndDeletedArtifactIds) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = null;
      TransactionId fromTransactionId;
      TransactionId toTransactionId;

      try {
         boolean hasBranch = sourceBranch != null;
         long time = System.currentTimeMillis();
         if (DEBUG) {
            System.out.println(String.format("     Gathering Relation Changes on %s",
                  hasBranch ? "Branch: " + sourceBranch : "Transaction: " + transactionId));
         }
         //Changes per a branch
         if (hasBranch) {
            chStmt = ConnectionHandler.runPreparedQuery(BRANCH_REL_CHANGES, sourceBranch.getBranchId());

            Pair<TransactionId, TransactionId> branchStartEndTransaction =
                  TransactionIdManager.getStartEndPoint(sourceBranch);

            fromTransactionId = branchStartEndTransaction.getKey();
            toTransactionId = branchStartEndTransaction.getValue();
         }//Changes per a transaction
         else {
            chStmt = ConnectionHandler.runPreparedQuery(TRANSACTION_REL_CHANGES, transactionId.getTransactionNumber());

            toTransactionId = transactionId;
            fromTransactionId = TransactionIdManager.getPriorTransaction(toTransactionId);
         }
         int count = 0;
         while (chStmt.next()) {
            count++;
            int aArtId = chStmt.getInt("a_art_id");
            int bArtId = chStmt.getInt("b_art_id");
            int relLinkId = chStmt.getInt("rel_link_id");

            if (!newAndDeletedArtifactIds.contains(aArtId) && !newAndDeletedArtifactIds.contains(bArtId)) {
               ModificationType modificationType = ModificationType.getMod(chStmt.getInt("mod_type"));
               String rationale = modificationType != ModificationType.DELETED ? chStmt.getString("rationale") : "";
               artIds.add(aArtId);
               artIds.add(bArtId);

               changes.add(new RelationChanged(sourceBranch, -1, chStmt.getInt("gamma_id"), aArtId, toTransactionId,
                     fromTransactionId, modificationType, ChangeType.OUTGOING, bArtId, relLinkId, rationale,
                     chStmt.getInt("a_order"), chStmt.getInt("b_order"),
                     RelationTypeManager.getType(chStmt.getInt("rel_link_type_id")), !hasBranch));
            }
         }
         if (DEBUG) {
            System.out.println(String.format("        Found %d Changes in %s", count, Lib.getElapseString(time)));
         }
      } finally {
         ConnectionHandler.close(chStmt);
      }
   }

   /**
    * @param sourceBranch
    * @param changes
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    * @throws OseeDataStoreException
    */
   private void loadAttributeChanges(Branch sourceBranch, TransactionId transactionId, Set<Integer> artIds, ArrayList<Change> changes, Set<Integer> newAndDeletedArtifactIds) throws BranchDoesNotExist, TransactionDoesNotExist, OseeDataStoreException {
      Map<Integer, Change> attributesWasValueCache = new HashMap<Integer, Change>();
      Map<Integer, ModificationType> artModTypes = new HashMap<Integer, ModificationType>();
      Set<Integer> modifiedArtifacts = new HashSet<Integer>();
      ConnectionHandlerStatement chStmt = null;
      ModificationType artModType;
      boolean hasBranch = sourceBranch != null;
      long time = System.currentTimeMillis();
      if (DEBUG) {
         System.out.println(String.format("     Gathering Attribute Changes on %s",
               hasBranch ? "Branch: " + sourceBranch : "Transaction: " + transactionId));
      }
      TransactionId fromTransactionId;
      TransactionId toTransactionId;
      int queryId;

      for (Change change : changes) {// cache in map for performance look ups
         artModTypes.put(change.getArtId(), change.getModificationType());
      }

      try {
         //Changes per a branch
         if (hasBranch) {
            chStmt = ConnectionHandler.runPreparedQuery(BRANCH_ATTRIBUTE_IS_CHANGES, sourceBranch.getBranchId());

            Pair<TransactionId, TransactionId> branchStartEndTransaction =
                  TransactionIdManager.getStartEndPoint(sourceBranch);

            fromTransactionId = branchStartEndTransaction.getKey();
            toTransactionId = branchStartEndTransaction.getValue();
         }//Changes per transaction number
         else {
            chStmt =
                  ConnectionHandler.runPreparedQuery(TRANSACTION_ATTRIBUTE_CHANGES,
                        transactionId.getTransactionNumber());

            toTransactionId = transactionId;
            fromTransactionId = TransactionIdManager.getPriorTransaction(toTransactionId);
         }
         AttributeChanged attributeChanged;

         int count = 0;
         while (chStmt.next()) {
            count++;
            int attrId = chStmt.getInt("attr_id");
            int artId = chStmt.getInt("art_id");
            int sourceGamma = chStmt.getInt("gamma_id");
            int attrTypeId = chStmt.getInt("attr_type_id");
            int artTypeId = chStmt.getInt("art_type_id");
            String isValue = chStmt.getString("is_value");
            ModificationType modificationType = ModificationType.getMod(chStmt.getInt("mod_type"));

            if (artModTypes.containsKey(artId)) {
               artModType = artModTypes.get(artId);
            } else {
               artModType = ModificationType.CHANGE;
            }

            if (!newAndDeletedArtifactIds.contains(artId)) {
               // Want to add an artifact changed item once if any attribute was modified && artifact was not
               // NEW or DELETED
               if (artModType == ModificationType.CHANGE && !modifiedArtifacts.contains(artId)) {
                  ArtifactChanged artifactChanged =
                        new ArtifactChanged(sourceBranch, artTypeId, sourceGamma, artId, toTransactionId,
                              fromTransactionId, ModificationType.CHANGE, ChangeType.OUTGOING, !hasBranch);

                  changes.add(artifactChanged);
                  modifiedArtifacts.add(artId);
               }

               if (modificationType != ModificationType.DELETED && modificationType != ModificationType.ARTIFACT_DELETED) {
                  modificationType = ModificationType.NEW;
               }

               attributeChanged =
                     new AttributeChanged(sourceBranch, artTypeId, sourceGamma, artId, toTransactionId,
                           fromTransactionId, modificationType, ChangeType.OUTGOING, isValue, "", attrId, attrTypeId,
                           artModType, !hasBranch);

               changes.add(attributeChanged);
               attributesWasValueCache.put(attrId, attributeChanged);
               artIds.add(artId);
            }
         }

         if (DEBUG) {
            System.out.println(String.format("        Found %d Changes in %s", count, Lib.getElapseString(time)));
         }
         //Load was values for branch change reports only
         if (!artIds.isEmpty()) {
            time = System.currentTimeMillis();
            int sqlParamter; // Will either be a branch id or transaction id
            Branch wasValueBranch;
            String sql;

            if (hasBranch) {
               wasValueBranch = sourceBranch;
               sql = BRANCH_ATTRIBUTE_WAS_CHANGE;
               sqlParamter = wasValueBranch.getBranchId();
            } else {
               wasValueBranch = transactionId.getBranch();
               sql = TRANSACTION_ATTRIBUTE_WAS_CHANGE;
               sqlParamter = transactionId.getTransactionNumber();
            }

            queryId = ArtifactLoader.getNewQueryId();
            Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
            List<Object[]> datas = new LinkedList<Object[]>();

            try {
               // insert into the artifact_join_table
               for (int artId : artIds) {
                  datas.add(new Object[] {queryId, insertTime, artId, wasValueBranch.getBranchId(),
                        SQL3DataType.INTEGER});
               }
               ArtifactLoader.selectArtifacts(datas);

               chStmt = ConnectionHandler.runPreparedQuery(sql, sqlParamter, queryId);
               int previousAttrId = -1;

               count = 0;
               while (chStmt.next()) {
                  count++;
                  int attrId = chStmt.getInt("attr_id");
                  if (previousAttrId != attrId) {
                     String wasValue = chStmt.getString("was_value");
                     if (attributesWasValueCache.containsKey(attrId) && attributesWasValueCache.get(attrId) instanceof AttributeChanged) {
                        AttributeChanged changed = (AttributeChanged) attributesWasValueCache.get(attrId);
                        if (changed.getModificationType() != ModificationType.DELETED && changed.getModificationType() != ModificationType.ARTIFACT_DELETED) {
                           changed.setModType(ModificationType.CHANGE);
                        }
                        changed.setWasValue(wasValue);
                     }
                     previousAttrId = attrId;
                  }
               }
            } finally {
               ArtifactLoader.clearQuery(queryId);
            }
            if (DEBUG) {
               System.out.println(String.format("        Loaded %d was values in %s", count, Lib.getElapseString(time)));
            }
         }
      } finally {
         ConnectionHandler.close(chStmt);
      }
   }

}
