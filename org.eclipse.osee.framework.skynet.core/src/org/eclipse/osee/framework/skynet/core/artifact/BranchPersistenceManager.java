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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_ID_SEQ;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TXD_COMMENT;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetArtifactEventBase;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetEventBase;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetRelationLinkEventBase;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Branch.BranchType;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.dbinit.MasterSkynetTypesImport;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionDetailsType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.utility.RemoteArtifactEventFactory;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.WindowLocal;

public class BranchPersistenceManager {
   static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchPersistenceManager.class);

   private static final String READ_BRANCH_TABLE =
         "SELECT * FROM osee_define_branch br1, osee_define_tx_details txd1 WHERE br1.branch_id = txd1.branch_id AND txd1.tx_type=" + TransactionDetailsType.Baselined.ordinal();
   private static final String READ_MERGE_BRANCHES =
         "select * from osee_define_branch b1, osee_define_merge m2, osee_define_tx_details t2 where b1.branch_id = m2.merge_branch_id and t2.branch_id = b1.branch_id and t2.tx_type = 1";
   private static final String CHANGED_RELATIONS =
         "SELECT t1.gamma_id, t2.rel_link_id, t2.a_art_id, t2.b_art_id, t2.modification_id, t2.rel_link_type_id, t2.a_order, t2.b_order, t2.rationale FROM (SELECT tx1.gamma_id FROM " + SkynetDatabase.TRANSACTIONS_TABLE + " tx1, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " td1 WHERE tx1.transaction_id = td1.transaction_id AND td1.branch_id = ? AND tx1.gamma_id NOT IN (SELECT tx2.gamma_id FROM " + SkynetDatabase.TRANSACTIONS_TABLE + " tx2, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " td2 WHERE tx2.transaction_id = td2.transaction_id AND td2.branch_id = ?)) t1 INNER JOIN " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t2 ON (t1.gamma_id=t2.gamma_id)";
   private static final String CHANGED_ARTIFACTS =
         "SELECT t1.gamma_id, t2.art_id, t2.modification_id FROM (SELECT tx1.gamma_id FROM " + SkynetDatabase.TRANSACTIONS_TABLE + " tx1, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " td1 WHERE tx1.transaction_id = td1.transaction_id AND td1.branch_id = ? AND tx1.gamma_id NOT IN (SELECT tx2.gamma_id FROM " + SkynetDatabase.TRANSACTIONS_TABLE + " tx2, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " td2 WHERE tx2.transaction_id = td2.transaction_id AND td2.branch_id = ?)) t1 INNER JOIN " + SkynetDatabase.ARTIFACT_VERSION_TABLE + " t2 ON (t1.gamma_id=t2.gamma_id)";
   private static final String COMMIT_TRANSACTION =
         "INSERT INTO " + TRANSACTION_DETAIL_TABLE.columnsForInsert("tx_type", "branch_id", "transaction_id",
               TXD_COMMENT, "time", "author", "commit_art_id");

   private static final String UPDATE_TRANSACTION_BRANCH =
         "UPDATE " + TRANSACTION_DETAIL_TABLE + " SET branch_id=? WHERE " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "=?";

   private static final String SELECT_BRANCH_FOR_TRANSACTION =
         "SELECT branch_id FROM osee_define_tx_details WHERE transaction_id = ?";
   public static final String NEW_BRANCH_COMMENT = "New Branch from ";
   private static final String ARCHIVE_BRANCH = "UPDATE osee_define_branch set archived = 1 WHERE branch_id = ?";
   private static final String UPDATE_ASSOCIATED_ART_BRANCH =
         "UPDATE  osee_define_branch set associated_art_id = ? WHERE branch_id = ?";

   private final static String LAST_DEFAULT_BRANCH = "LastDefaultBranch";
   private static final IPreferenceStore preferenceStore = SkynetActivator.getInstance().getPreferenceStore();

   // This hash is keyed on the branchId
   private TreeMap<Integer, Branch> branchCache;
   //This hash is keyed in the source branch id and destination branch id
   private DoubleKeyHashMap<Integer, Integer, Branch> mergeBranchCache;
   private Map<Integer, Branch> transactionIdBranchCache;

   private static final BranchPersistenceManager instance = new BranchPersistenceManager();

   private BranchPersistenceManager() {
      this.branchCache = new TreeMap<Integer, Branch>();
      this.transactionIdBranchCache = new HashMap<Integer, Branch>();
      this.mergeBranchCache = new DoubleKeyHashMap<Integer, Integer, Branch>();
   }

   public static BranchPersistenceManager getInstance() {
      return instance;
   }

   public Set<Branch> getAssociatedArtifactBranches(Artifact associatedArtifact) throws SQLException {
      ensurePopulatedCache(false);
      Set<Branch> branches = new HashSet<Branch>();
      for (Branch branch : getBranches())
         if (branch.isAssociatedToArtifact(associatedArtifact)) {
            branches.add(branch);
         }
      return branches;
   }

   public static Branch getCommonBranch() throws SQLException {
      return getKeyedBranch(Branch.COMMON_BRANCH_CONFIG_ID);
   }

   public static Branch getKeyedBranch(String keyname) throws SQLException {
      return KeyedBranchCache.getInstance().getKeyedBranch(keyname);
   }

   public static Branch getAtsBranch() throws OseeCoreException {
      try {
         return getCommonBranch();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public static List<Branch> getBranches() throws SQLException {
      instance.ensurePopulatedCache(false);
      List<Branch> branches = new ArrayList<Branch>(instance.branchCache.values());
      Collections.sort(branches);
      return branches;
   }

   public static Collection<Branch> refreshBranches() throws SQLException {
      instance.ensurePopulatedCache(true);
      return getBranches();
   }

   public Branch getBranch(String branchName) throws IllegalArgumentException, SQLException {
      ensurePopulatedCache(false);
      for (Branch branch : branchCache.values()) {
         if (branch.getBranchName().equals(branchName)) {
            return branch;
         }
      }
      throw new IllegalArgumentException("No branch exists with the name: " + branchName);
   }

   private synchronized void ensurePopulatedCache(boolean forceRead) throws SQLException {
      if (forceRead || branchCache.size() == 0) {
         // The branch cache can not be cleared here because applications may contain branch
         // references.

         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt = ConnectionHandler.runPreparedQuery(500, READ_BRANCH_TABLE);
            ResultSet rSet = chStmt.getRset();
            while (chStmt.next()) {
               int branchId = rSet.getInt("branch_id");
               boolean isArchived = rSet.getInt("archived") == 1;

               Branch branch = branchCache.get(branchId);

               if (isArchived) {
                  if (branch != null) {
                     branchCache.remove(branch.getBranchId());
                  }
               } else {
                  if (branch == null) {
                     branch = initializeBranchObject(rSet);
                  } else {
                     branch.setBranchName(rSet.getString("branch_name"));
                  }
               }
            }
         } finally {
            DbUtil.close(chStmt);
         }
      }
   }

   public Collection<Branch> getArchivedBranches() throws SQLException {
      Collection<Branch> archivedBranches = new ArrayList<Branch>(100);
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(500, READ_BRANCH_TABLE);
         ResultSet rSet = chStmt.getRset();
         while (chStmt.next()) {
            if (rSet.getInt("archived") == 1) {
               archivedBranches.add(initializeBranchObject(rSet));
            }
         }
      } finally {
         DbUtil.close(chStmt);
      }
      return archivedBranches;
   }

   /**
    * deletes (permanently removes from the datastore) each archived branch one at a time using sequential jobs
    * 
    * @throws SQLException
    * @throws InterruptedException
    */
   public void deleteArchivedBranches() throws SQLException, InterruptedException {
      for (Branch archivedBranch : getArchivedBranches()) {
         Job job = new DeleteBranchJob(archivedBranch);
         Jobs.startJob(job);
         job.join();
      }
   }

   /**
    * Create a Branch object based on the result set from the READ_BRANCH_TABLE query
    * 
    * @param rSet
    * @return
    * @throws SQLException
    */
   private Branch initializeBranchObject(ResultSet rSet) throws SQLException {
      int branchId = rSet.getInt("branch_id");
      int associatedArtifactId = rSet.getInt("associated_art_id");

      return new Branch(rSet.getString("short_name"), rSet.getString("branch_name"), branchId,
            rSet.getInt("parent_branch_id"), false, rSet.getInt("author"), rSet.getTimestamp("time"),
            rSet.getString(TXD_COMMENT), associatedArtifactId, BranchType.getBranchType(new Integer(
                  rSet.getInt("branch_type"))));
   }

   /**
    * Calls the getMergeBranch method and if it returns null it will create a new merge branch based on the artIds from
    * the source branch.
    */
   public Branch getOrCreateMergeBranch(Branch sourceBranch, Branch destBranch, ArrayList<Integer> expectedArtIds) throws OseeCoreException, SQLException {
      Branch mergeBranch = getMergeBranch(sourceBranch.getBranchId(), destBranch.getBranchId());

      if (mergeBranch == null) {
         mergeBranch = BranchCreator.getInstance().createMergeBranch(sourceBranch, destBranch, expectedArtIds);
      } else {
         MergeBranchManager.updateMergeBranch(mergeBranch, expectedArtIds, destBranch, sourceBranch);
      }
      return mergeBranch;
   }

   /**
    * Checks the merge branch cache for the branch if it does not find it then it will query the database for the
    * branch.
    */
   public Branch getMergeBranch(Integer sourceBranchId, Integer destBranchId) throws OseeCoreException, SQLException {
      if (sourceBranchId < 1 || destBranchId < 1) {
         throw new IllegalArgumentException(
               "Branch ids are invalid source branch id:" + sourceBranchId + " destination branch id:" + destBranchId);
      }

      if (!mergeBranchCache.containsKey(sourceBranchId, destBranchId)) {
         ensureMergePopulatedCache(true);
      }

      Branch mergeBranch = mergeBranchCache.get(sourceBranchId, destBranchId);
      return mergeBranch;
   }

   private synchronized void ensureMergePopulatedCache(boolean forceRead) throws SQLException {
      if (forceRead || mergeBranchCache.isEmpty()) {
         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt = ConnectionHandler.runPreparedQuery(500, READ_MERGE_BRANCHES);
            ResultSet rSet = chStmt.getRset();
            while (chStmt.next()) {
               int sourceBranchId = rSet.getInt("source_branch_id");
               int destBranchId = rSet.getInt("dest_branch_id");
               boolean isArchived = rSet.getInt("archived") == 1;

               Branch branch = mergeBranchCache.get(sourceBranchId, destBranchId);

               if (isArchived) {
                  if (branch != null) {
                     mergeBranchCache.remove(sourceBranchId, destBranchId);
                  }
               } else {
                  if (branch == null) {
                     branch = initializeBranchObject(rSet);
                     mergeBranchCache.put(sourceBranchId, destBranchId, branch);
                  }
               }
            }
         } finally {
            DbUtil.close(chStmt);
         }
      }
   }

   public Branch getBranch(Integer branchId) throws SQLException, BranchDoesNotExist {
      // Always exception for invalid id's, they won't ever be found in the
      // database or cache.
      if (branchId < 1) throw new BranchDoesNotExist("Branch Id " + branchId + " is invalid");

      // If someone else made a branch on another machine, we may not know about it
      // so rehit the database for ids we don't have in cache.
      if (!branchCache.containsKey(branchId)) {
         ensurePopulatedCache(true);
      }
      Branch branch = branchCache.get(branchId);

      if (branch == null) {
         throw new BranchDoesNotExist("Branch could not be acquired for branch id: " + branchId);
      }

      return branch;
   }

   public Branch getBranchForTransactionNumber(Integer transactionNumber) throws SQLException, BranchDoesNotExist {
      Branch branch;
      if (transactionIdBranchCache.containsKey(transactionNumber)) {
         branch = transactionIdBranchCache.get(transactionNumber);
      } else {
         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt =
                  ConnectionHandler.runPreparedQuery(SELECT_BRANCH_FOR_TRANSACTION, SQL3DataType.INTEGER,
                        transactionNumber);

            if (chStmt.next()) {
               branch = getBranch(chStmt.getRset().getInt("branch_id"));
            } else {
               throw new BranchDoesNotExist(
                     "There is no branch in the database associated with transaction: " + transactionNumber);
            }
         } finally {
            DbUtil.close(chStmt);
         }
         transactionIdBranchCache.put(transactionNumber, branch);
      }

      return branch;
   }

   /**
    * Delete branch from the system.
    * 
    * @param branch
    */
   public Job deleteBranch(final Branch branch) {
      return Jobs.startJob(new DeleteBranchJob(branch));
   }

   public void removeBranchFromCache(int branchId) {
      branchCache.remove(branchId);
   }

   public static class CommitConflictException extends RuntimeException {
      private static final long serialVersionUID = 1L;

      public CommitConflictException() {
         super();
      }

      public CommitConflictException(String s) {
         super(s);
      }

      public CommitConflictException(String message, Throwable cause) {
         super(message, cause);
      }

      public CommitConflictException(Throwable cause) {
         super(cause);
      }
   }

   public static class NoChangesToCommitException extends RuntimeException {
      private static final long serialVersionUID = 1L;

      public NoChangesToCommitException() {
         super();
      }

      public NoChangesToCommitException(String s) {
         super(s);
      }

      public NoChangesToCommitException(String message, Throwable cause) {
         super(message, cause);
      }

      public NoChangesToCommitException(Throwable cause) {
         super(cause);
      }
   }

   /**
    * Commit the net changes from the childBranch into its parent branch.
    * 
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public Job commitBranch(final Branch childBranch, final boolean archiveChildBranch, final boolean forceCommit) throws SQLException, OseeCoreException {
      Branch parentBranch = childBranch.getParentBranch();

      if (parentBranch == null) {
         throw new IllegalArgumentException("This branch does not have a parent branch.");
      }
      return commitBranch(childBranch, parentBranch, archiveChildBranch, forceCommit);
   }

   /**
    * Commit the net changes from the fromBranch into the toBranch. If there are conflicts between the two branches, the
    * fromBranch's changes override those on the toBranch if overrideConflicts is true otherwise a
    * CommitConflictException is thrown.
    * 
    * @throws SQLException
    * @throws CommitConflictException
    * @throws IllegalArgumentException
    */
   public Job commitBranch(final Branch fromBranch, final Branch toBranch, boolean archiveFromBranch, boolean forceCommit) throws SQLException, OseeCoreException {
      CommitJob commitJob = new CommitJob(toBranch, fromBranch, archiveFromBranch, forceCommit);
      Jobs.startJob(commitJob);
      return commitJob;
   }

   /**
    * Creates a working branch from the net changes of the fromBranch onto the toBranch
    * 
    * @throws SQLException
    */
   public Branch createWorkingBranchFromBranchChanges(final Branch fromBranch, final Branch toBranch, Artifact associatedArtifact) throws Exception {
      return createWorkingBranchFromBranchData(fromBranch, null, toBranch, associatedArtifact);
   }

   /**
    * Creates a working branch from the net changes of the fromTransaction onto the toBranch
    * 
    * @throws SQLException
    */
   public Branch createWorkingBranchFromBranchChanges(TransactionId fromTransactionId, final Branch toBranch, Artifact associatedArtifact) throws Exception {
      return createWorkingBranchFromBranchData(null, fromTransactionId, toBranch, associatedArtifact);
   }

   private Branch createWorkingBranchFromBranchData(final Branch fromBranch, TransactionId fromTransactionId, final Branch toBranch, Artifact associatedArtifact) throws Exception {
      String toBranchName;

      if (fromTransactionId == null) {
         fromTransactionId =
               TransactionIdManager.getInstance().getNonEditableTransactionId(
                     TransactionIdManager.getParentBaseTransactionNumber(fromBranch.getCreationComment()));
         toBranchName = fromBranch.getBranchName() + " Copy " + GlobalTime.GreenwichMeanTimestamp();
      } else {
         toBranchName = fromTransactionId.getBranch().getBranchName() + " Copy " + GlobalTime.GreenwichMeanTimestamp();
      }

      createWorkingBranch(fromTransactionId, toBranchName, toBranchName, associatedArtifact);
      return getBranch(toBranchName);
   }

   /**
    * @throws SQLException
    */
   int addCommitTransactionToDatabase(Branch parentBranch, Branch childBranch, User userToBlame) throws SQLException {
      int newTransactionNumber = Query.getNextSeqVal(TRANSACTION_ID_SEQ);

      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      String comment = "Commit Branch " + childBranch.getBranchName();
      int authorId = (userToBlame == null) ? -1 : userToBlame.getArtId();
      ConnectionHandler.runPreparedUpdate(COMMIT_TRANSACTION, SQL3DataType.INTEGER,
            TransactionDetailsType.NonBaselined.getId(), SQL3DataType.INTEGER, parentBranch.getBranchId(),
            SQL3DataType.INTEGER, newTransactionNumber, SQL3DataType.VARCHAR, comment, SQL3DataType.TIMESTAMP,
            timestamp, SQL3DataType.INTEGER, authorId, SQL3DataType.INTEGER, childBranch.getAssociatedArtifactId());
      // Update commit artifact cache with new information
      if (childBranch.getAssociatedArtifactId() > 0) {
         RevisionManager.getInstance().cacheTransactionDataPerCommitArtifact(childBranch.getAssociatedArtifactId(),
               newTransactionNumber);
      }

      return newTransactionNumber;
   }

   /**
    * @throws SQLException
    */
   int addCommitTransactionToDatabase(Branch toBranch, TransactionId fromTransactionID, User userToBlame) throws SQLException {
      int newTransactionNumber = Query.getNextSeqVal(TRANSACTION_ID_SEQ);

      ConnectionHandler.runPreparedUpdate(COMMIT_TRANSACTION, SQL3DataType.INTEGER,
            TransactionDetailsType.NonBaselined.getId(), SQL3DataType.INTEGER, toBranch.getBranchId(),
            SQL3DataType.INTEGER, newTransactionNumber, SQL3DataType.VARCHAR,
            "Commit Branch " + fromTransactionID.getTransactionNumber(), SQL3DataType.TIMESTAMP,
            GlobalTime.GreenwichMeanTimestamp(), SQL3DataType.INTEGER,
            (userToBlame == null) ? -1 : userToBlame.getArtId(), SQL3DataType.INTEGER, -1);

      return newTransactionNumber;
   }

   /**
    * @param relId
    * @param modType
    * @param aArtId
    * @param bArtId
    * @param relTypeId
    * @param aOrderValue
    * @param bOrderValue
    * @param rationale
    * @param parentBranch
    * @param newTransactionNumber
    * @return SkynetRelationLinkEventBase
    */
   private SkynetRelationLinkEventBase createRemoteRelationEvent(int gammaId, int relId, int modType, int aArtId, int bArtId, int relTypeId, int aOrderValue, int bOrderValue, String rationale, Branch parentBranch, Branch childBranch, int newTransactionNumber) {

      SkynetRelationLinkEventBase remoteRelationEvent = null;

      try {
         if (modType == ModificationType.DELETED.getValue()) {
            remoteRelationEvent =
                  new NetworkRelationLinkDeletedEvent(relTypeId, gammaId, parentBranch.getBranchId(),
                        newTransactionNumber, relId, aArtId, bArtId, SkynetAuthentication.getUser().getArtId());
         } else if (modType == ModificationType.CHANGE.getValue()) {
            remoteRelationEvent =
                  new NetworkRelationLinkModifiedEvent(gammaId, parentBranch.getBranchId(), newTransactionNumber,
                        relId, aArtId, bArtId, rationale, aOrderValue, bOrderValue,
                        SkynetAuthentication.getUser().getArtId(), relTypeId);
         } else if (modType == ModificationType.NEW.getValue()) {
            // remoteRelationEvent = new RemoteNewRelationLinkEvent(parentBranch.getBranchId(),
            // newTransactionNumber,
            // relId, aArtifact.getArtId(), aArtifact.getArtTypeId(), bArtifact.getArtId(),
            // bArtifact.getArtTypeId(), rationale, aOrderValue, bOrderValue, relTypeId,
            // aArtifact.getFactory().getClass().getCanonicalName(),
            // bArtifact.getFactory().getClass().getCanonicalName(), aArtifact.getGuid(),
            // bArtifact.getGuid(),
            // aArtifact.getHumanReadableId(), bArtifact.getHumanReadableId());
         }
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return remoteRelationEvent;
   }

   void getRelationRemoteEvent(Branch parentBranch, Branch childBranch, int newTransactionNumber, List<SkynetEventBase> remoteEvents) throws SQLException {
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(CHANGED_RELATIONS, SQL3DataType.INTEGER, childBranch.getBranchId(),
                     SQL3DataType.INTEGER, parentBranch.getBranchId());

         int relId;
         int relTypeId;
         int modType;
         int aArtId;
         int bArtId;
         int aOrderValue;
         int bOrderValue;
         int gammaId;
         String rationale = "";
         SkynetRelationLinkEventBase remoteRelationEvent = null;
         ResultSet rSet;

         while (chStmt.next()) {
            rSet = chStmt.getRset();
            relId = rSet.getInt("rel_link_id");
            modType = rSet.getInt("modification_id");
            aArtId = rSet.getInt("a_art_id");
            bArtId = rSet.getInt("b_art_id");
            relTypeId = rSet.getInt("rel_link_type_id");
            aOrderValue = rSet.getInt("a_order");
            bOrderValue = rSet.getInt("b_order");
            rationale = rSet.getString("rationale");
            gammaId = rSet.getInt("gamma_id");

            remoteRelationEvent =
                  createRemoteRelationEvent(gammaId, relId, modType, aArtId, bArtId, relTypeId, aOrderValue,
                        bOrderValue, rationale, parentBranch, childBranch, newTransactionNumber);

            if (!remoteEvents.contains(remoteRelationEvent) && remoteRelationEvent != null) {
               remoteEvents.add(remoteRelationEvent);
            }
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   void getArtifactRemoteEvents(Branch parentBranch, Branch childBranch, User userToBlame, List<SkynetEventBase> remoteEvents, int newTransactionNumber) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {

         chStmt =
               ConnectionHandler.runPreparedQuery(CHANGED_ARTIFACTS, SQL3DataType.INTEGER, childBranch.getBranchId(),
                     SQL3DataType.INTEGER, parentBranch.getBranchId());

         if (!chStmt.next()) throw new NoChangesToCommitException("No Changes To Commit");

         ResultSet rSet;
         int artId;
         int modificationId;
         SkynetArtifactEventBase remoteEvent = null;

         do {
            rSet = chStmt.getRset();
            artId = rSet.getInt("art_id");
            modificationId = rSet.getInt("modification_id");
            Artifact artifact;

            try {
               artifact = ArtifactQuery.getArtifactFromId(artId, parentBranch);
            } catch (OseeCoreException ex) {
               continue;
            }

            if (modificationId == ModificationType.DELETED.getValue()) {
               remoteEvent =
                     new NetworkArtifactDeletedEvent(parentBranch.getBranchId(), newTransactionNumber,
                           artifact.getArtId(), artifact.getArtTypeId(),
                           artifact.getFactory().getClass().getCanonicalName(),
                           SkynetAuthentication.getUser().getArtId());
            } else {
               try {
                  remoteEvent = RemoteArtifactEventFactory.makeEvent(artifact, newTransactionNumber);
               } catch (Exception ex) {
                  continue;
               }
            }

            if (!remoteEvents.contains(remoteEvent)) {
               remoteEvents.add(remoteEvent);
            }
         } while (chStmt.next());
      } finally {
         DbUtil.close(chStmt);
      }
   }

   /**
    * Archives a branch in the database by changing its archived value from 0 to 1.
    */
   public void archive(Branch branch) throws SQLException {
      ConnectionHandler.runPreparedUpdate(ARCHIVE_BRANCH, SQL3DataType.INTEGER, branch.getBranchId());

      branch.setArchived();
      branchCache.remove(branch.getBranchId());

      Branch defaultBranch = branch.getParentBranch();

      if (defaultBranch == null) {
         defaultBranch = getCommonBranch();
      }

      setDefaultBranch(defaultBranch);
   }

   /**
    * Permanently removes transactions and any of their backing data that is not referenced by any other transactions.
    * 
    * @param transactionIdNumber
    */
   public void deleteTransactions(final int... transactionIdNumbers) {
      deleteTransactions(null, transactionIdNumbers);
   }

   /**
    * Permanently removes transactions and any of their backing data that is not referenced by any other transactions.
    * 
    * @param transactionIdNumber
    */
   public void deleteTransactions(IJobChangeListener jobChangeListener, final int... transactionIdNumbers) {
      Jobs.startJob(new DeleteTransactionJob(transactionIdNumbers), jobChangeListener);

   }

   /**
    * Move a transaction to a particular branch. This is simply a database call and should only be used to fix user
    * errors. No internal cached data is updated, nor are any events fired from the modified data so any Skynet sessions
    * reading this data should be restarted to see the changes.
    */
   public void moveTransaction(TransactionId transactionId, Branch toBranch) {
      try {
         ConnectionHandler.runPreparedUpdate(UPDATE_TRANSACTION_BRANCH, SQL3DataType.INTEGER, toBranch.getBranchId(),
               SQL3DataType.INTEGER, transactionId.getTransactionNumber());
      } catch (SQLException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

   public void updateAssociatedArtifact(Branch branch, Artifact artifact) throws SQLException {
      ConnectionHandler.runPreparedUpdate(UPDATE_ASSOCIATED_ART_BRANCH, SQL3DataType.INTEGER, artifact.getArtId(),
            SQL3DataType.INTEGER, branch.getBranchId());
   }

   /**
    * Creates a new Branch based on the transaction number selected and the parent branch.
    * 
    * @param parentTransactionId
    * @param childBranchName
    * @throws SQLException
    */
   public Branch createWorkingBranch(final TransactionId parentTransactionId, final String childBranchShortName, final String childBranchName, final Artifact associatedArtifact) throws OseeCoreException, SQLException {
      Collection<ArtifactType> compressArtTypes =
            ConfigurationPersistenceManager.getValidArtifactTypes(parentTransactionId.getBranch());

      return BranchCreator.getInstance().createChildBranch(parentTransactionId, childBranchShortName, childBranchName,
            associatedArtifact, false, compressArtTypes, null);
   }

   /**
    * Creates a new Branch based on the transaction number selected and the parent branch.
    * 
    * @param parentTransactionId
    * @param childBranchName
    * @throws SQLException
    */
   public Branch createTestBranch(TransactionId parentTransactionId, final String childBranchShortName, final String childBranchName, final Artifact associatedArtifact) throws OseeCoreException, SQLException {
      Collection<ArtifactType> preserveArtTypes =
            ConfigurationPersistenceManager.getValidArtifactTypes(parentTransactionId.getBranch());

      return BranchCreator.getInstance().createChildBranch(parentTransactionId, childBranchShortName, childBranchName,
            associatedArtifact, false, null, preserveArtTypes);
   }

   private Set<ArtifactType> getSubtypeDescriptors(String[] artTypeNames) throws SQLException {
      Set<ArtifactType> artifactTypes;
      if (artTypeNames == null) {
         artifactTypes = new HashSet<ArtifactType>(0);
      } else {
         artifactTypes = new HashSet<ArtifactType>(artTypeNames.length);
         for (String typeName : artTypeNames) {
            artifactTypes.add(ArtifactTypeManager.getType(typeName));
         }
      }
      return artifactTypes;
   }

   /**
    * Creates a new Branch with a mix of compressed and uncompressed data.
    * 
    * @return The created Branch
    */
   public Branch createBranchWithFiltering(TransactionId parentTransactionId, String childBranchShortName, String childBranchName, Artifact associatedArtifact, String[] compressArtTypeNames, String[] preserveArtTypeNames) throws Exception {
      Set<ArtifactType> compressArtTypes = getSubtypeDescriptors(compressArtTypeNames);
      Set<ArtifactType> preserveArtTypes = getSubtypeDescriptors(preserveArtTypeNames);

      return BranchCreator.getInstance().createChildBranch(parentTransactionId, childBranchShortName, childBranchName,
            associatedArtifact, true, compressArtTypes, preserveArtTypes);
   }

   /**
    * Creates a new root branch, imports skynet types and initializes. If programatic access is necessary, setting the
    * staticBranchName will add a key for this branch and allow access to the branch through
    * getKeyedBranch(staticBranchName).
    * 
    * @param shortBranchName short name to use; null will auto-compute short name from first 25 chars of branchName
    * @param branchName
    * @param staticBranchName will allow programatic access to branch from getKeyedBranch
    * @param skynetTypesImportExtensionsIds skynetDbTypes extensionIds to import onto new branch
    * @param initialize adds common artifacts needed by most normal root branches
    * @return
    * @throws Exception
    * @see BranchPersistenceManager#intializeBranch
    * @see MasterSkynetTypesImport#importSkynetDbTypes
    * @see BranchPersistenceManager#getKeyedBranch(String)
    */
   public Branch createRootBranch(String shortBranchName, String branchName, String staticBranchName, Collection<String> skynetTypesImportExtensionsIds, boolean initialize) throws Exception {
      // Create branch with name and static name; short name will be computed from full name
      Branch branch = BranchCreator.getInstance().createRootBranch(null, branchName, staticBranchName);
      // Add name to cached keyname if static branch name is desired
      if (staticBranchName != null) {
         KeyedBranchCache.getInstance().createKeyedBranch(staticBranchName, branch);
      }
      // Re-init factory cache
      ArtifactFactoryManager.refreshCache();
      // Import skynet types if specified
      if (skynetTypesImportExtensionsIds != null && skynetTypesImportExtensionsIds.size() > 0) {
         MasterSkynetTypesImport.getInstance().importSkynetDbTypes(ConnectionHandler.getConnection(),
               skynetTypesImportExtensionsIds, branch);
      }
      // Initialize branch with common artifacts
      if (initialize) {
         RootBranchInitializer rootInitializer = new RootBranchInitializer();
         rootInitializer.initialize(branch);
      }
      return branch;
   }

   public List<Branch> getRootBranches() throws SQLException {
      List<Branch> branches = new ArrayList<Branch>();
      for (Branch branch : getBranches()) {
         if (!branch.hasParentBranch()) {
            branches.add(branch);
         }
      }
      return branches;
   }

   public List<Branch> getChangeManagedBranches() throws SQLException {
      List<Branch> branches = new ArrayList<Branch>();
      for (Branch branch : getBranches()) {
         if (branch.isChangeManaged()) {
            branches.add(branch);
         }
      }
      return branches;
   }

   /**
    * @param branch
    */
   protected void cache(Branch branch) {
      branchCache.put(branch.getBranchId(), branch);
   }

   public void setDefaultBranch(Branch branch) {
      if (branch == null) throw new IllegalArgumentException("The branch argument can not be null");

      if (branch != defaultBranch.get()) {
         defaultBranch.set(branch);
         preferenceStore.setValue(LAST_DEFAULT_BRANCH, getDefaultBranch().getBranchId());
         SkynetEventManager.getInstance().kick(new DefaultBranchChangedEvent(this));
      }
   }

   private final WindowLocal<Branch> defaultBranch = new WindowLocal<Branch>() {
      @Override
      protected Branch initialValue() {
         Branch initialBranch = null;
         int branchId = preferenceStore.getInt(LAST_DEFAULT_BRANCH);

         if (branchId > 0) {
            try {
               initialBranch = getBranch(branchId);
            }
            // if the branch id could not be acquired from the preferenceStore set the default
            // branch to the common branch.
            catch (BranchDoesNotExist ex) {
               try {
                  logger.log(Level.WARNING,
                        "Could not use default branch id from the preference store: " + ex.toString());
                  initialBranch = getCommonBranch();
                  preferenceStore.setValue(LAST_DEFAULT_BRANCH, initialBranch.getBranchId());
               } catch (SQLException ex1) {
                  logger.log(Level.SEVERE, ex1.toString(), ex1);
               }
            } catch (SQLException ex) {
               logger.log(Level.SEVERE, ex.toString(), ex);
            }
         }

         if (initialBranch == null) {
            try {
               initialBranch = getDefaultInitialBranch();
            } catch (SQLException ex) {
               logger.log(Level.SEVERE, ex.toString(), ex);
            }
         }

         return initialBranch;
      }
   };

   private Branch getDefaultInitialBranch() throws SQLException {
      List<IDefaultInitialBranchesProvider> defaultBranchProviders = new LinkedList<IDefaultInitialBranchesProvider>();

      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint(
                  "org.eclipse.osee.framework.skynet.core.DefaultInitialBranchProvider");
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         for (IConfigurationElement element : elements) {
            if (element.getName().equals("Provider")) {
               try {
                  defaultBranchProviders.add((IDefaultInitialBranchesProvider) element.createExecutableExtension("class"));
               } catch (Exception ex) {
                  logger.log(Level.SEVERE, ex.toString(), ex);
               }
            }
         }
      }

      Collection<Branch> branches;
      for (IDefaultInitialBranchesProvider provider : defaultBranchProviders) {
         try {
            branches = provider.getDefaultInitialBranches();

            // Guard against problematic extensions
            if (branches != null) {
               for (Branch branch : branches) {
                  if (branch != null) {
                     return branch;
                  }
               }
            }
         } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception occurred while trying to determine initial default branch", ex);
         }
      }

      return getCommonBranch();
   }

   public Branch getDefaultBranch() {
      return defaultBranch.get();
   }
}