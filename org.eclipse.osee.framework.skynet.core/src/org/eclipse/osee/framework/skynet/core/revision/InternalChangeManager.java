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
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.OseeSql;
import org.eclipse.osee.framework.core.enums.ModificationType;
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
import org.eclipse.osee.framework.skynet.core.change.RelationChanged;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.status.IStatusMonitor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * Acquires changes for either branches or transactions.
 * 
 * @author Jeff C. Phillips
 */
public class InternalChangeManager {
   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Change"));

   private static InternalChangeManager instance = new InternalChangeManager();

   private InternalChangeManager() {
      super();
   }

   public static InternalChangeManager getInstance() {
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
   protected Collection<Change> getChanges(Branch sourceBranch, TransactionId transactionId, IStatusMonitor monitor) throws OseeCoreException {
      ArrayList<Change> changes = new ArrayList<Change>();
      Set<Integer> artIds = new HashSet<Integer>();
      Set<Integer> newAndDeletedArtifactIds = new HashSet<Integer>();
      boolean historical = sourceBranch == null;
      long totalTime = System.currentTimeMillis();

      monitor.startJob("Find Changes", 100);
      if (DEBUG) {
         System.out.println(String.format("\nChange Manager: getChanges(%s, %s)", sourceBranch, transactionId));
      }

      loadNewOrDeletedArtifactChanges(sourceBranch, transactionId, artIds, changes, newAndDeletedArtifactIds, monitor);
      loadAttributeChanges(sourceBranch, transactionId, artIds, changes, newAndDeletedArtifactIds, monitor);
      loadRelationChanges(sourceBranch, transactionId, artIds, changes, newAndDeletedArtifactIds, monitor);

      Branch branch = historical ? transactionId.getBranch() : sourceBranch;

      if (historical) {
         for (Change change : changes) {
            change.setBranch(branch);
         }
      }

      monitor.setSubtaskName("Loading Artifacts from the Database");
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
      monitor.done();
      return changes;
   }

   /**
    * @param sourceBranch
    * @param changes
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    * @throws OseeDataStoreException
    */
   private void loadNewOrDeletedArtifactChanges(Branch sourceBranch, TransactionId transactionId, Set<Integer> artIds, ArrayList<Change> changes, Set<Integer> newAndDeletedArtifactIds, IStatusMonitor monitor) throws OseeCoreException {

      Map<Integer, ArtifactChanged> artifactChanges = new HashMap<Integer, ArtifactChanged>();
      boolean hasBranch = sourceBranch != null;
      TransactionId fromTransactionId;
      TransactionId toTransactionId;
      long time = System.currentTimeMillis();
      if (DEBUG) {
         System.out.println(String.format("     Gathering New or Deleted Artifacts on %s",
               hasBranch ? "Branch: " + sourceBranch : "Transaction: " + transactionId));
      }

      monitor.setSubtaskName("Gathering New or Deleted Artifacts");
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {

         if (hasBranch) { //Changes per a branch
            Pair<TransactionId, TransactionId> branchStartEndTransaction =
                  TransactionIdManager.getStartEndPoint(sourceBranch);

            fromTransactionId = branchStartEndTransaction.getKey();
            toTransactionId = branchStartEndTransaction.getValue();

            chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Changes.SELECT_BRANCH_ARTIFACT_CHANGES),
                  sourceBranch.getBranchId());
         } else { //Changes per a transaction
            toTransactionId = transactionId;
            fromTransactionId = TransactionIdManager.getPriorTransaction(toTransactionId);

            chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Changes.SELECT_TRANSACTION_ARTIFACT_CHANGES),
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
         monitor.updateWork(25);
      } finally {
         chStmt.close();
      }
   }

   /**
    * @param sourceBranch
    * @param changes
    * @throws OseeCoreException
    */
   private void loadRelationChanges(Branch sourceBranch, TransactionId transactionId, Set<Integer> artIds, ArrayList<Change> changes, Set<Integer> newAndDeletedArtifactIds, IStatusMonitor monitor) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      TransactionId fromTransactionId;
      TransactionId toTransactionId;

      monitor.setSubtaskName("Gathering Relation Changes");
      try {
         boolean hasBranch = sourceBranch != null;
         long time = System.currentTimeMillis();
         if (DEBUG) {
            System.out.println(String.format("     Gathering Relation Changes on %s",
                  hasBranch ? "Branch: " + sourceBranch : "Transaction: " + transactionId));
         }
         //Changes per a branch
         if (hasBranch) {
            chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Changes.SELECT_BRANCH_REL_CHANGES),
                  sourceBranch.getBranchId());

            Pair<TransactionId, TransactionId> branchStartEndTransaction =
                  TransactionIdManager.getStartEndPoint(sourceBranch);

            fromTransactionId = branchStartEndTransaction.getKey();
            toTransactionId = branchStartEndTransaction.getValue();
         }//Changes per a transaction
         else {
            chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Changes.SELECT_TRANSACTION_REL_CHANGES),
                  transactionId.getTransactionNumber());

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
         monitor.updateWork(25);
      } finally {
         chStmt.close();
      }
   }

   /**
    * @param sourceBranch
    * @param changes
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    * @throws OseeDataStoreException
    */
   private void loadAttributeChanges(Branch sourceBranch, TransactionId transactionId, Set<Integer> artIds, ArrayList<Change> changes, Set<Integer> newAndDeletedArtifactIds, IStatusMonitor monitor) throws OseeCoreException {
      Map<Integer, Change> attributesWasValueCache = new HashMap<Integer, Change>();
      Map<Integer, ModificationType> artModTypes = new HashMap<Integer, ModificationType>();
      Set<Integer> modifiedArtifacts = new HashSet<Integer>();
      ConnectionHandlerStatement chStmt1 = new ConnectionHandlerStatement();
      ConnectionHandlerStatement chStmt2 = new ConnectionHandlerStatement();
      ModificationType artModType;
      boolean hasBranch = sourceBranch != null;
      long time = System.currentTimeMillis();
      monitor.setSubtaskName("Gathering Attribute Changes");
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
            chStmt1.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Changes.SELECT_BRANCH_ATTRIBUTE_IS_CHANGES),
                  sourceBranch.getBranchId());

            Pair<TransactionId, TransactionId> branchStartEndTransaction =
                  TransactionIdManager.getStartEndPoint(sourceBranch);

            fromTransactionId = branchStartEndTransaction.getKey();
            toTransactionId = branchStartEndTransaction.getValue();
         }//Changes per transaction number
         else {
            chStmt1.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Changes.SELECT_TRANSACTION_ATTRIBUTE_CHANGES),
                  transactionId.getTransactionNumber());

            toTransactionId = transactionId;
            fromTransactionId = TransactionIdManager.getPriorTransaction(toTransactionId);
         }
         AttributeChanged attributeChanged;

         int count = 0;
         while (chStmt1.next()) {
            count++;
            int attrId = chStmt1.getInt("attr_id");
            int artId = chStmt1.getInt("art_id");
            int sourceGamma = chStmt1.getInt("gamma_id");
            int attrTypeId = chStmt1.getInt("attr_type_id");
            int artTypeId = chStmt1.getInt("art_type_id");
            String isValue = chStmt1.getString("is_value");
            ModificationType modificationType = ModificationType.getMod(chStmt1.getInt("mod_type"));

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
                        new ArtifactChanged(sourceBranch, artTypeId, -1, artId, toTransactionId,
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
         monitor.updateWork(13);
         monitor.setSubtaskName("Gathering Was values");
         //Load was values for branch change reports only
         if (!artIds.isEmpty()) {
            time = System.currentTimeMillis();
            int sqlParamter; // Will either be a branch id or transaction id
            Branch wasValueBranch;
            String sql;

            if (hasBranch) {
               wasValueBranch = sourceBranch;
               sql = ClientSessionManager.getSQL(OseeSql.Changes.SELECT_BRANCH_ATTRIBUTE_WAS_CHANGE);
               sqlParamter = wasValueBranch.getBranchId();
            } else {
               wasValueBranch = transactionId.getBranch();
               sql = ClientSessionManager.getSQL(OseeSql.Changes.SELECT_TRANSACTION_ATTRIBUTE_WAS_CHANGE);
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

               chStmt2.runPreparedQuery(sql, sqlParamter, queryId);
               int previousAttrId = -1;

               count = 0;
               while (chStmt2.next()) {
                  count++;
                  int attrId = chStmt2.getInt("attr_id");
                  
                  if (previousAttrId != attrId) {
                     String wasValue = chStmt2.getString("was_value");
                     if (attributesWasValueCache.containsKey(attrId) && attributesWasValueCache.get(attrId) instanceof AttributeChanged) {
                        AttributeChanged changed = (AttributeChanged) attributesWasValueCache.get(attrId);
                        
                        if(changed.getArtModType() != ModificationType.NEW){
                        	if (changed.getModificationType() != ModificationType.DELETED && changed.getModificationType() != ModificationType.ARTIFACT_DELETED) {
                        	changed.setModType(ModificationType.CHANGE);
                        	}
                        	changed.setWasValue(wasValue);
                        }
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
            monitor.updateWork(12);
         }
      } finally {
         chStmt1.close();
         chStmt2.close();
      }
   }

   public boolean isChangesOnWorkingBranch(Branch workingBranch) throws OseeCoreException {
      Pair<TransactionId, TransactionId> transactionToFrom = TransactionIdManager.getStartEndPoint(workingBranch);
      if (transactionToFrom.getKey().equals(transactionToFrom.getValue())) {
         return false;
      }
      return true;
   }
}