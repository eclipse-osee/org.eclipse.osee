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
package org.eclipse.osee.framework.skynet.core.revision;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.BranchMergeException;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflictBuilder;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflictBuilder;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictBuilder;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Theron Virgin
 * @author Jeff C. Phillips
 */
public class ConflictManagerInternal {
   private static final String CONFLICT_CLEANUP =
         "DELETE FROM osee_conflict t1 WHERE merge_branch_id = ? and NOT EXISTS (SELECT 'X' FROM osee_join_artifact WHERE query_id = ? and t1.conflict_id = art_id)";

   private static final String GET_DESTINATION_BRANCHES =
         "SELECT dest_branch_id FROM osee_merge WHERE source_branch_id = ?";

   private static final String GET_MERGE_DATA =
         "SELECT commit_transaction_id, merge_branch_id FROM osee_merge WHERE source_branch_id = ? AND dest_branch_id = ?";

   private static final String GET_COMMIT_TRANSACTION_COMMENT =
         "SELECT transaction_id FROM osee_tx_details WHERE osee_comment = ? AND branch_id = ?";

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Merge"));

   private ConflictManagerInternal() {
   }

   public static List<Conflict> getConflictsPerBranch(TransactionRecord commitTransaction, IProgressMonitor monitor) throws OseeCoreException {
      long time = System.currentTimeMillis();
      long totalTime = time;

      monitor.beginTask(String.format("Loading Merge Manager for Transaction %d", commitTransaction.getId()), 100);
      monitor.subTask("Finding Database stored conflicts");
      if (DEBUG) {
         System.out.println(String.format("\nDiscovering Conflicts based on Transaction ID: %d",
               commitTransaction.getId()));
         totalTime = System.currentTimeMillis();
      }
      ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      if (DEBUG) {
         System.out.println("Running Query to find conflicts stored in the DataBase");
         time = System.currentTimeMillis();
      }
      try {
         chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CONFLICT_GET_HISTORICAL_ATTRIBUTES),
               commitTransaction.getId());
         if (DEBUG) {
            System.out.println(String.format("          Query finished in %s", Lib.getElapseString(time)));
         }
         while (chStmt.next()) {
            AttributeConflict attributeConflict =
                  new AttributeConflict(chStmt.getInt("source_gamma_id"), chStmt.getInt("dest_gamma_id"),
                        chStmt.getInt("art_id"), commitTransaction, chStmt.getString("source_value"),
                        chStmt.getInt("attr_id"), chStmt.getInt("attr_type_id"),
                        BranchManager.getBranch(chStmt.getInt("merge_branch_id")),
                        BranchManager.getBranch(chStmt.getInt("dest_branch_id")));
            conflicts.add(attributeConflict);

            attributeConflict.setStatus(ConflictStatus.getStatus(chStmt.getInt("status")));
         }
         monitor.done();
      } finally {
         chStmt.close();
      }
      if (DEBUG) {
         debugDump(conflicts, totalTime);
      }
      return conflicts;
   }

   public static List<Conflict> getConflictsPerBranch(Branch sourceBranch, Branch destinationBranch, TransactionRecord baselineTransaction, IProgressMonitor monitor) throws OseeCoreException {
      @SuppressWarnings("unused")
      // This is for bulk loading so we do not loose are references
      Collection<Artifact> bulkLoadedArtifacts;
      ArrayList<ConflictBuilder> conflictBuilders = new ArrayList<ConflictBuilder>();
      ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
      Set<Integer> artIdSet = new HashSet<Integer>();
      Set<Integer> artIdSetDontShow = new HashSet<Integer>();
      Set<Integer> artIdSetDontAdd = new HashSet<Integer>();

      //Check to see if the branch has already been committed than use the transaction version
      int commitTransactionId = getCommitTransaction(sourceBranch, destinationBranch);
      if (commitTransactionId != 0) {
         try {
            return getConflictsPerBranch(TransactionManager.getTransactionId(commitTransactionId), monitor);
         } catch (TransactionDoesNotExist ex) {
         }
      }
      long totalTime = 0;
      if (sourceBranch != null && destinationBranch != null) {
         monitor.beginTask(String.format("Loading Merge Manager for Branch %d into Branch %d", sourceBranch.getId(),
               destinationBranch.getId()), 100);
         monitor.subTask("Finding Database stored conflicts");

         if (DEBUG) {
            System.out.println(String.format(
                  "\nDiscovering Conflicts based on Source Branch: %d Destination Branch: %d", sourceBranch.getId(),
                  destinationBranch.getId()));
            totalTime = System.currentTimeMillis();
         }
      }
      if (sourceBranch == null || destinationBranch == null) {
         throw new OseeArgumentException(String.format("Source Branch = %s Destination Branch = %s",
               sourceBranch == null ? "NULL" : sourceBranch.getId(),
               destinationBranch == null ? "NULL" : destinationBranch.getId()));
      }

      //      BranchState sourceBranchState = sourceBranch.getBranchState();
      //      if (!sourceBranchState.isCreationInProgress() && !sourceBranchState.isCommitted() && !sourceBranchState.isRebaselined() && !sourceBranchState.isRebaselineInProgress()) {
      //         sourceBranch.setBranchState(BranchState.COMMIT_IN_PROGRESS);
      //         BranchManager.persist(sourceBranch);
      //      }

      int transactionId = findCommonTransaction(sourceBranch, destinationBranch);
      loadArtifactVersionConflictsNew(sourceBranch, destinationBranch, baselineTransaction, conflictBuilders, artIdSet,
            artIdSetDontShow, artIdSetDontAdd, monitor, transactionId);
      loadAttributeConflictsNew(sourceBranch, destinationBranch, baselineTransaction, conflictBuilders, artIdSet,
            monitor, transactionId);
      for (Integer integer : artIdSetDontAdd) {
         artIdSet.remove(integer);
      }
      if (artIdSet.isEmpty()) {
         return conflicts;
      }

      if (DEBUG) {
         System.out.println(String.format(" Conflicts found in %s", Lib.getElapseString(totalTime)));
      }
      monitor.subTask("Creating and/or maintaining the Merge Branch");
      Branch mergeBranch =
            BranchManager.getOrCreateMergeBranch(sourceBranch, destinationBranch, new ArrayList<Integer>(artIdSet));

      if (mergeBranch == null) {
         throw new BranchMergeException("Could not create the Merge Branch.");
      }
      monitor.worked(15);

      bulkLoadedArtifacts = preloadConflictArtifacts(sourceBranch, destinationBranch, mergeBranch, artIdSet, monitor);

      long time = System.currentTimeMillis();
      //Don't create the conflicts for attributes on an artifact that is deleted etc.
      for (ConflictBuilder conflictBuilder : conflictBuilders) {
         Conflict conflict = conflictBuilder.getConflict(mergeBranch, artIdSetDontShow);
         if (conflict != null) {
            conflicts.add(conflict);
            conflict.computeStatus();
         }
      }
      if (DEBUG) {
         System.out.println(String.format("    Creating conflict objects and setting theri status completed in %s",
               Lib.getElapseString(time)));
      }
      cleanUpConflictDB(conflicts, mergeBranch.getId(), monitor);
      if (DEBUG) {
         debugDump(conflicts, totalTime);
      }
      return conflicts;
   }

   private static Collection<Artifact> preloadConflictArtifacts(Branch sourceBranch, Branch destinationBranch, Branch mergeBranch, Collection<Integer> artIdSet, IProgressMonitor monitor) throws OseeCoreException {
      long time = 0;
      Collection<Artifact> artifacts = null;

      monitor.subTask("Preloading Artifacts Associated with the Conflicts");
      if (DEBUG) {
         System.out.println("Prelodaing Conflict Artifacts");
         time = System.currentTimeMillis();
      }
      if (artIdSet != null && !artIdSet.isEmpty()) {
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

         List<Object[]> insertParameters = new LinkedList<Object[]>();
         for (int artId : artIdSet) {
            insertParameters.add(new Object[] {queryId, insertTime, artId, sourceBranch.getId(), SQL3DataType.INTEGER});
            insertParameters.add(new Object[] {queryId, insertTime, artId, destinationBranch.getId(),
                  SQL3DataType.INTEGER});
            insertParameters.add(new Object[] {queryId, insertTime, artId, mergeBranch.getId(), SQL3DataType.INTEGER});
         }
         artifacts =
               ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, insertParameters, true, false, true);
      }
      if (DEBUG) {
         System.out.println(String.format("    Preloading took %s", Lib.getElapseString(time)));
      }
      monitor.worked(25);
      return artifacts;
   }

   /**
    * @param sourceBranch
    * @param destinationBranch
    * @param baselineTransaction
    * @param conflictBuilders
    * @param artIdSet
    * @throws OseeCoreException
    */

   private static void loadArtifactVersionConflictsNew(Branch sourceBranch, Branch destinationBranch, TransactionRecord baselineTransaction, ArrayList<ConflictBuilder> conflictBuilders, Set<Integer> artIdSet, Set<Integer> artIdSetDontShow, Set<Integer> artIdSetDontAdd, IProgressMonitor monitor, int transactionId) throws OseeCoreException {
      long time = 0;
      if (DEBUG) {
         System.out.println("Finding Artifact Version Conflicts");
         System.out.println("    Running the Artifact Conflict Query");
         time = System.currentTimeMillis();
      }
      monitor.subTask("Finding Artifact Version Conflicts");
      IOseeStatement chStmt = ConnectionHandler.getStatement();

      try {
         chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CONFLICT_GET_ARTIFACTS), sourceBranch.getId(),
               destinationBranch.getId(), transactionId);

         if (!chStmt.next()) {
            return;
         }
         ArtifactConflictBuilder artifactConflictBuilder;
         int artId = 0;

         do {
            int nextArtId = chStmt.getInt("art_id");
            int sourceGamma = chStmt.getInt("source_gamma");
            int destGamma = chStmt.getInt("dest_gamma");
            int sourceModType = chStmt.getInt("source_mod_type");
            int destModType = chStmt.getInt("dest_mod_type");
            int artTypeId = chStmt.getInt("art_type_id");

            if (artId != nextArtId) {
               artId = nextArtId;

               if (destModType == ModificationType.DELETED.getValue() && sourceModType == ModificationType.MODIFIED.getValue() || destModType == ModificationType.MODIFIED.getValue() && sourceModType == ModificationType.DELETED.getValue()) {

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
         } while (chStmt.next());
      } finally {
         chStmt.close();
      }
      if (DEBUG) {
         System.out.println(String.format("         Query completed in %s ", Lib.getElapseString(time)));
      }
      monitor.worked(20);
      for (Integer integer : artIdSet) {
         artIdSetDontShow.add(integer);
      }
   }

   /**
    * @param sourceBranch
    * @param destinationBranch
    * @param baselineTransaction
    * @param conflicts
    * @throws OseeCoreException
    */
   private static void loadAttributeConflictsNew(Branch sourceBranch, Branch destinationBranch, TransactionRecord baselineTransaction, ArrayList<ConflictBuilder> conflictBuilders, Set<Integer> artIdSet, IProgressMonitor monitor, int transactionId) throws OseeCoreException {
      long time = 0;
      if (DEBUG) {
         System.out.println("Finding Attribute Version Conflicts");
         System.out.println("    Running the Attribute Conflict Query");
         time = System.currentTimeMillis();
      }
      monitor.subTask("Finding the Attribute Conflicts");
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      AttributeConflictBuilder attributeConflictBuilder;
      try {
         chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.CONFLICT_GET_ATTRIBUTES), sourceBranch.getId(),
               destinationBranch.getId(), transactionId);

         int attrId = 0;

         if (chStmt.next()) {

            do {
               int nextAttrId = chStmt.getInt("attr_id");
               int artId = chStmt.getInt("art_id");
               int sourceGamma = chStmt.getInt("source_gamma");
               int destGamma = chStmt.getInt("dest_gamma");
               int attrTypeId = chStmt.getInt("attr_type_id");
               String sourceValue =
                     chStmt.getString("source_value") != null ? chStmt.getString("source_value") : chStmt.getString("dest_value");

               if (attrId != nextAttrId && isAttributeConflictValid(destGamma, sourceBranch)) {
                  attrId = nextAttrId;
                  attributeConflictBuilder =
                        new AttributeConflictBuilder(sourceGamma, destGamma, artId, baselineTransaction, sourceBranch,
                              destinationBranch, sourceValue, attrId, attrTypeId);

                  conflictBuilders.add(attributeConflictBuilder);
                  artIdSet.add(artId);
               }
            } while (chStmt.next());
         }
      } finally {
         chStmt.close();
      }
      if (DEBUG) {
         System.out.println(String.format("         Query completed in %s ", Lib.getElapseString(time)));
      }
      monitor.worked(30);
   }

   /**
    * Checks source branch hierarchy to see if the conflict gamma exists. If it does, its not a real conflict because
    * the source branch has already seen this change.
    * 
    * @return Returns True if the AttributeConflict candidate is really a conflict.
    * @throws OseeCoreException
    */
   private static boolean isAttributeConflictValid(int destinationGammaId, Branch sourceBranch) throws OseeCoreException {
      boolean isValidConflict = true;
      //We just need the largest value at first so the complete source branch will be searched
      int parentTransactionNumber = Integer.MAX_VALUE;

      for (Branch branch : sourceBranch.getAncestors()) {
         if (!branch.getBranchType().isSystemRootBranch() && !branch.getParentBranch().getBranchType().isSystemRootBranch()) {
            isValidConflict &= isAttributeConflictValidOnBranch(destinationGammaId, branch, parentTransactionNumber);
            parentTransactionNumber = branch.getSourceTransaction().getId();
         }

         if (!isValidConflict) {
            break;
         }
      }
      return isValidConflict;
   }

   /**
    * @return Returns True if the destination gamma does not exist on a branch else false if it does.
    * @throws OseeDataStoreException
    */
   private static boolean isAttributeConflictValidOnBranch(int destinationGammaId, Branch branch, int endTransactionNumber) throws OseeDataStoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      boolean isValidConflict;
      try {
         chStmt.runPreparedQuery(
               "select 'x' from osee_tx_details t1, osee_txs t2 where t2.transaction_id = t1.transaction_id and t2.gamma_id = ? and t1.branch_id =? and t1.transaction_id <=?",
               destinationGammaId, branch.getId(), endTransactionNumber);
         isValidConflict = !chStmt.next();
      } finally {
         chStmt.close();
      }
      return isValidConflict;
   }

   private static void debugDump(Collection<Conflict> conflicts, long time) throws OseeCoreException {
      int displayCount = 1;
      System.out.println(String.format("Found %d conflicts in %s", conflicts.size(), Lib.getElapseString(time)));
      for (Conflict conflict : conflicts) {
         System.out.println(String.format(
               "    %d. ArtId = %d, ChangeItem = %s, SourceGamma = %d, DestGamma = %d, Status = %s", displayCount++,
               conflict.getArtId(), conflict.getChangeItem(), conflict.getSourceGamma(), conflict.getDestGamma(),
               conflict.getStatus()));
      }
   }

   private static void cleanUpConflictDB(Collection<Conflict> conflicts, int branchId, IProgressMonitor monitor) throws OseeCoreException {
      int count = 0;
      long time = System.currentTimeMillis();
      monitor.subTask("Cleaning up old conflict data");
      int queryId = ArtifactLoader.getNewQueryId();

      try {
         if (conflicts != null && conflicts.size() != 0 && branchId != 0) {
            Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

            List<Object[]> insertParameters = new LinkedList<Object[]>();
            for (Conflict conflict : conflicts) {
               insertParameters.add(new Object[] {queryId, insertTime, conflict.getObjectId(), branchId,
                     SQL3DataType.INTEGER});
            }
            ArtifactLoader.insertIntoArtifactJoin(insertParameters);
            count = ConnectionHandler.runPreparedUpdate(CONFLICT_CLEANUP, branchId, queryId);
         }
      } finally {
         ArtifactLoader.clearQuery(queryId);
      }

      if (DEBUG) {
         System.out.println(String.format("    Cleaned up %d conflicts that are no longer conflicting in %s ", count,
               Lib.getElapseString(time)));
      }
      monitor.worked(10);
   }

   public static Collection<Integer> getDestinationBranchesMerged(int sourceBranchId) throws OseeCoreException {
      List<Integer> destinationBranches = new LinkedList<Integer>();
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(GET_DESTINATION_BRANCHES, sourceBranchId);
         while (chStmt.next()) {
            destinationBranches.add(chStmt.getInt("dest_branch_id"));
         }
      } finally {
         chStmt.close();
      }
      Collections.sort(destinationBranches);
      return destinationBranches;
   }

   private static int getCommitTransaction(Branch sourceBranch, Branch destBranch) throws OseeCoreException {
      int transactionId = 0;
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         if (sourceBranch != null && destBranch != null) {
            chStmt.runPreparedQuery(GET_MERGE_DATA, sourceBranch.getId(), destBranch.getId());
            if (chStmt.next()) {
               transactionId = chStmt.getInt("commit_transaction_id");
            }
            if (transactionId == 0) {
               chStmt.runPreparedQuery(GET_COMMIT_TRANSACTION_COMMENT,
                     BranchManager.COMMIT_COMMENT + sourceBranch.getName(), destBranch.getId());
               if (chStmt.next()) {
                  transactionId = chStmt.getInt("transaction_id");
               }
            }
         }
      } finally {
         chStmt.close();
      }
      return transactionId;
   }

   public static int getMergeBranchId(int sourceBranchId, int destBranchId) throws OseeCoreException {
      int mergeBranchId = 0;
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(GET_MERGE_DATA, sourceBranchId, destBranchId);
         if (chStmt.next()) {
            mergeBranchId = chStmt.getInt("merge_branch_id");
         }
      } finally {
         chStmt.close();
      }
      return mergeBranchId;
   }

   /**
    * The purpose of this function is find the transaction (Branch Create) that holds the last common values for two
    * branches that share a common history. If two branches share the same history than the point at which they diverged
    * should provide the reference for detecting conflicts based on the gamma at that point.
    */
   private static int findCommonTransaction(Branch sourceBranch, Branch destBranch) throws OseeCoreException {
      Collection<Branch> sourceBranches = sourceBranch.getAncestors();
      Collection<Branch> destBranches = destBranch.getAncestors();
      Branch commonBranch = null;
      for (Branch branch : sourceBranches) {
         if (destBranches.contains(branch)) {
            commonBranch = branch;
            break;
         }
      }
      if (commonBranch == null) {
         throw new OseeCoreException(String.format("Can not find a common ancestor for Branch %s and Branch %s",
               sourceBranch.getShortName(), destBranch.getShortName()));
      }
      int sourceTransaction = 0;
      int destTransaction = 0;
      if (commonBranch.equals(destBranch)) {
         destTransaction = Integer.MAX_VALUE;
      } else {
         for (Branch branch : destBranches) {
            if (branch.getParentBranch().equals(commonBranch)) {
               destTransaction = TransactionManager.getStartEndPoint(branch).getFirst().getId();
               break;
            }

         }
      }
      for (Branch branch : sourceBranches) {
         if (branch.getParentBranch().equals(commonBranch)) {
            sourceTransaction = TransactionManager.getStartEndPoint(branch).getFirst().getId();
            break;
         }
      }
      return sourceTransaction <= destTransaction ? sourceTransaction : destTransaction;
   }

}
