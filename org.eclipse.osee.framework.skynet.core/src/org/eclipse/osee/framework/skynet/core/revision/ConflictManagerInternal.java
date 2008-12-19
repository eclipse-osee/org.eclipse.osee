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
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.OseeSql;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.BranchMergeException;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflictBuilder;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflictBuilder;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictBuilder;
import org.eclipse.osee.framework.skynet.core.status.EmptyMonitor;
import org.eclipse.osee.framework.skynet.core.status.IStatusMonitor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Theron Virgin
 * @author Jeff C. Phillips
 */
public class ConflictManagerInternal {
   private static final String CONFLICT_CLEANUP =
         "DELETE FROM osee_conflict WHERE merge_branch_id = ? AND conflict_id NOT IN ";

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

   public static List<Conflict> getConflictsPerBranch(TransactionId commitTransaction, IStatusMonitor monitor) throws OseeCoreException {
      long time = System.currentTimeMillis();
      long totalTime = time;
      if (monitor == null) monitor = new EmptyMonitor();
      monitor.startJob(String.format("Loading Merge Manager for Transaction %d",
            commitTransaction.getTransactionNumber()), 100);
      monitor.setSubtaskName("Finding Database stored conflicts");
      if (DEBUG) {
         System.out.println(String.format("\nDiscovering Conflicts based on Transaction ID: %d",
               commitTransaction.getTransactionNumber()));
         totalTime = System.currentTimeMillis();
      }
      ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      if (DEBUG) {
         System.out.println("Running Query to find conflicts stored in the DataBase");
         time = System.currentTimeMillis();
      }
      try {
         chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Conflicts.SELECT_HISTORIC_ATTRIBUTE_CONFLICTS),
               commitTransaction.getTransactionNumber());
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

   public static List<Conflict> getConflictsPerBranch(Branch sourceBranch, Branch destinationBranch, TransactionId baselineTransaction, IStatusMonitor monitor) throws OseeCoreException {
      ArrayList<ConflictBuilder> conflictBuilders = new ArrayList<ConflictBuilder>();
      ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
      Set<Integer> artIdSet = new HashSet<Integer>();
      Set<Integer> artIdSetDontShow = new HashSet<Integer>();
      Set<Integer> artIdSetDontAdd = new HashSet<Integer>();

      //Check to see if the branch has already been committed than use the transaction version
      if (monitor == null) monitor = new EmptyMonitor();
      int commitTransactionId = getCommitTransaction(sourceBranch, destinationBranch);
      if (commitTransactionId != 0) {
         try {
            return getConflictsPerBranch(TransactionIdManager.getTransactionId(commitTransactionId), monitor);
         } catch (TransactionDoesNotExist ex) {
         }
      }
      long totalTime = 0;
      if (sourceBranch != null && destinationBranch != null) {
         monitor.startJob(String.format("Loading Merge Manager for Branch %d into Branch %d",
               sourceBranch.getBranchId(), destinationBranch.getBranchId()), 100);
         monitor.setSubtaskName("Finding Database stored conflicts");

         if (DEBUG) {
            System.out.println(String.format(
                  "\nDiscovering Conflicts based on Source Branch: %d Destination Branch: %d",
                  sourceBranch.getBranchId(), destinationBranch.getBranchId()));
            totalTime = System.currentTimeMillis();
         }
      }
      if ((sourceBranch == null) || (destinationBranch == null)) {
         throw new OseeArgumentException(String.format("Source Branch = %s Destination Branch = %s",
               sourceBranch == null ? "NULL" : sourceBranch.getBranchId(),
               destinationBranch == null ? "NULL" : destinationBranch.getBranchId()));
      }
      int transactionId = findCommonTransaction(sourceBranch, destinationBranch);
      loadArtifactVersionConflictsNew(sourceBranch, destinationBranch, baselineTransaction, conflictBuilders, artIdSet,
            artIdSetDontShow, artIdSetDontAdd, monitor, transactionId);
      loadAttributeConflictsNew(sourceBranch, destinationBranch, baselineTransaction, conflictBuilders, artIdSet,
            monitor, transactionId);
      for (Integer integer : artIdSetDontAdd) {
         artIdSet.remove(integer);
      }
      if (artIdSet.isEmpty()) return conflicts;

      if (DEBUG) {
         System.out.println(String.format(" Conflicts found in %s", Lib.getElapseString(totalTime)));
      }
      monitor.setSubtaskName("Creating and/or maintaining the Merge Branch");
      Branch mergeBranch =
            BranchManager.getOrCreateMergeBranch(sourceBranch, destinationBranch, new ArrayList<Integer>(artIdSet));

      if (mergeBranch == null) {
         throw new BranchMergeException("Could not create the Merge Branch.");
      }
      monitor.updateWork(15);

      preloadConflictArtifacts(sourceBranch, destinationBranch, mergeBranch, artIdSet, monitor);

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
      cleanUpConflictDB(conflicts, mergeBranch.getBranchId(), monitor);
      if (DEBUG) {
         debugDump(conflicts, totalTime);
      }
      return conflicts;
   }

   private static void preloadConflictArtifacts(Branch sourceBranch, Branch destinationBranch, Branch mergeBranch, Collection<Integer> artIdSet, IStatusMonitor monitor) throws OseeCoreException {
      long time = 0;

      monitor.setSubtaskName("Preloading Artifacts Associated with the Conflicts");
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
      monitor.updateWork(25);
   }

   /**
    * @param sourceBranch
    * @param destinationBranch
    * @param baselineTransaction
    * @param conflictBuilders
    * @param artIdSet
    * @throws OseeCoreException
    */

   private static void loadArtifactVersionConflictsNew(Branch sourceBranch, Branch destinationBranch, TransactionId baselineTransaction, ArrayList<ConflictBuilder> conflictBuilders, Set<Integer> artIdSet, Set<Integer> artIdSetDontShow, Set<Integer> artIdSetDontAdd, IStatusMonitor monitor, int transactionId) throws OseeCoreException {
      long time = 0;
      if (DEBUG) {
         System.out.println("Finding Artifact Version Conflicts");
         System.out.println("    Running the Artifact Conflict Query");
         time = System.currentTimeMillis();
      }
      monitor.setSubtaskName("Finding Artifact Version Conflicts");
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Conflicts.SELECT_ARTIFACT_CONFLICTS),
               sourceBranch.getBranchId(), destinationBranch.getBranchId(), transactionId);

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
         } while (chStmt.next());
      } finally {
         chStmt.close();
      }
      if (DEBUG) {
         System.out.println(String.format("         Query completed in %s ", Lib.getElapseString(time)));
      }
      monitor.updateWork(20);
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
   private static void loadAttributeConflictsNew(Branch sourceBranch, Branch destinationBranch, TransactionId baselineTransaction, ArrayList<ConflictBuilder> conflictBuilders, Set<Integer> artIdSet, IStatusMonitor monitor, int transactionId) throws OseeCoreException {
      long time = 0;
      if (DEBUG) {
         System.out.println("Finding Attribute Version Conflicts");
         System.out.println("    Running the Attribute Conflict Query");
         time = System.currentTimeMillis();
      }
      monitor.setSubtaskName("Finding the Attribute Conflicts");
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      AttributeConflictBuilder attributeConflictBuilder;
      try {
         chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Conflicts.SELECT_ATTRIBUTE_CONFLICTS),
               sourceBranch.getBranchId(), destinationBranch.getBranchId(), transactionId);

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

               if (attrId != nextAttrId) {
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
      monitor.updateWork(30);
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

   private static void cleanUpConflictDB(Collection<Conflict> conflicts, int branchId, IStatusMonitor monitor) throws OseeCoreException {
      int count = 0;
      long time = System.currentTimeMillis();
      monitor.setSubtaskName("Cleaning up old conflict data");
      if (conflicts != null && conflicts.size() != 0 && branchId != 0) {
         count = ConnectionHandler.runPreparedUpdate(CONFLICT_CLEANUP + createData(conflicts), branchId);
      }
      if (DEBUG) {
         System.out.println(String.format("    Cleaned up %d conflicts that are no longer conflicting in %s ", count,
               Lib.getElapseString(time)));
      }
      monitor.updateWork(10);
   }

   private static String createData(Collection<Conflict> conflicts) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      builder.append("(");
      boolean first = true;
      for (Conflict conflict : conflicts) {
         if (!first) {
            builder.append(" , ");
         } else {
            first = false;
         }
         builder.append(conflict.getObjectId());
      }
      builder.append(")");
      return builder.toString();
   }

   public static Collection<Integer> getDestinationBranchesMerged(int sourceBranchId) throws OseeCoreException {
      List<Integer> destinationBranches = new LinkedList<Integer>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
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

   public static int getCommitTransaction(Branch sourceBranch, Branch destBranch) throws OseeCoreException {
      int transactionId = 0;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         if (sourceBranch != null && destBranch != null) {
            chStmt.runPreparedQuery(GET_MERGE_DATA, sourceBranch.getBranchId(), destBranch.getBranchId());
            if (chStmt.next()) {
               transactionId = chStmt.getInt("commit_transaction_id");
            }
            if (transactionId == 0) {
               chStmt.runPreparedQuery(GET_COMMIT_TRANSACTION_COMMENT,
                     BranchManager.COMMIT_COMMENT + sourceBranch.getBranchName(), destBranch.getBranchId());
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
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
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
   public static int findCommonTransaction(Branch sourceBranch, Branch destBranch) throws OseeCoreException {
      List<Branch> sourceBranches = sourceBranch.getBranchHierarchy();
      List<Branch> destBranches = destBranch.getBranchHierarchy();
      Branch commonBranch = null;
      for (Branch branch : sourceBranches) {
         if (destBranches.contains(branch)) {
            commonBranch = branch;
            break;
         }
      }
      if (commonBranch == null) {
         throw new OseeCoreException(String.format("Can not find a common ancestor for Branch %s and Branch %s",
               sourceBranch.getBranchShortName(), destBranch.getBranchShortName()));
      }
      int sourceTransaction = 0;
      int destTransaction = 0;
      if (commonBranch.equals(destBranch)) {
         destTransaction = Integer.MAX_VALUE;
      } else {
         for (Branch branch : destBranches) {
            if (branch.getParentBranch().equals(commonBranch)) {
               destTransaction = TransactionIdManager.getStartEndPoint(branch).getKey().getTransactionNumber();
               break;
            }

         }
      }
      for (Branch branch : sourceBranches) {
         if (branch.getParentBranch().equals(commonBranch)) {
            sourceTransaction = TransactionIdManager.getStartEndPoint(branch).getKey().getTransactionNumber();
            break;
         }
      }
      return sourceTransaction <= destTransaction ? sourceTransaction : destTransaction;
   }

}
