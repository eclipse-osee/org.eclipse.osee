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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.operation.FinishUpdateBranchOperation;
import org.eclipse.osee.framework.skynet.core.artifact.operation.UpdateBranchOperation;
import org.eclipse.osee.framework.skynet.core.artifact.requester.HttpPurgeBranchRequester;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.update.ConflictResolverOperation;
import org.eclipse.osee.framework.skynet.core.commit.actions.CommitAction;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * Provides access to all branches as well as support for creating branches of all types
 * 
 * @author Ryan D. Brooks
 */
public class BranchManager {
   private static final BranchManager instance = new BranchManager();

   private static final String LAST_DEFAULT_BRANCH = "LastDefaultBranch";
   public static final String COMMIT_COMMENT = "Commit Branch ";

   private Branch lastBranch;

   private BranchManager() {
      // this private empty constructor exists to prevent the default constructor from allowing public construction
   }

   @Deprecated
   // use static methods instead
   public static BranchManager getInstance() {
      return instance;
   }

   public static BranchCache getCache() {
      return Activator.getInstance().getOseeCacheService().getBranchCache();
   }

   private static Branch commonBranch = null;

   /**
    * Since BranchManager is static and not a service yet, cache common branch cause it currently takes too long to get
    * service every single time this method is called.
    */
   public synchronized static Branch getCommonBranch() throws OseeCoreException {
      if (commonBranch == null) {
         Branch branch = getCache().getCommonBranch();
         Conditions.checkNotNull(branch, "Common Branch");
         commonBranch = branch;
      }
      return commonBranch;
   }

   public static List<Branch> getBranches(BranchArchivedState archivedState, BranchType... branchTypes) throws OseeCoreException {
      return getBranches(new BranchFilter(archivedState, branchTypes));
   }

   public static List<Branch> getBranches(BranchFilter branchFilter) throws OseeCoreException {
      return getCache().getBranches(branchFilter);
   }

   public static void refreshBranches() throws OseeCoreException {
      getCache().reloadCache();
   }

   public static Branch getBranch(DefaultBasicGuidArtifact guidArt) throws OseeCoreException {
      return BranchManager.getBranchByGuid(guidArt.getBranchGuid());
   }

   public static Branch getBranch(String branchName) throws OseeCoreException {
      Collection<Branch> branches = getBranchesByName(branchName);
      if (branches.isEmpty()) {
         throw new BranchDoesNotExist(String.format("No branch exists with the name: [%s]", branchName));
      }
      if (branches.size() > 1) {
         throw new MultipleBranchesExist(String.format("More than 1 branch exists with the name: [%s]", branchName));
      }
      return branches.iterator().next();
   }

   public static Collection<Branch> getBranchesByName(String branchName) throws OseeCoreException {
      return getCache().getByName(branchName);
   }

   public static int getBranchId(IOseeBranch branch) throws OseeCoreException {
      return getBranch(branch).getId();
   }

   public static Branch getBranch(IOseeBranch branch) throws OseeCoreException {
      if (branch instanceof Branch) {
         return (Branch) branch;
      } else {
         return getBranchByGuid(branch.getGuid());
      }
   }

   public static Branch getBranchByGuid(String guid) throws OseeCoreException {
      Branch branch = getCache().getByGuid(guid);
      if (branch == null) {
         throw new BranchDoesNotExist(String.format("Branch with guid [%s] does not exist", guid));
      }
      return branch;
   }

   public static boolean branchExists(IOseeBranch branchToken) throws OseeCoreException {
      return getCache().get(branchToken) != null;
   }

   /**
    * returns the merge branch for this source destination pair from the cache or null if not found
    */
   public static MergeBranch getMergeBranch(Branch sourceBranch, Branch destinationBranch) throws OseeCoreException {
      return getMergeBranch(sourceBranch, destinationBranch, true);
   }

   public static MergeBranch getMergeBranch(Branch sourceBranch, Branch destinationBranch, boolean isReLoadAllowed) throws OseeCoreException {
      BranchCache cache = getCache();
      // If someone else made a branch on another machine, we may not know about it
      // so refresh the cache.
      MergeBranch mergeBranch = cache.findMergeBranch(sourceBranch, destinationBranch);
      if (mergeBranch == null && isReLoadAllowed) {
         if (cache.reloadCache()) {
            mergeBranch = cache.findMergeBranch(sourceBranch, destinationBranch);
         }
      }
      return mergeBranch;
   }

   public static boolean doesMergeBranchExist(Branch sourceBranch, Branch destBranch) throws OseeCoreException {
      return getMergeBranch(sourceBranch, destBranch, false) != null;
   }

   public static Branch getBranch(Integer branchId) throws OseeCoreException {
      if (branchId == null) {
         throw new BranchDoesNotExist("Branch Id is null");
      }

      BranchCache cache = getCache();
      // If someone else made a branch on another machine, we may not know about it
      // so refresh the cache.
      Branch branch = cache.getById(branchId);
      if (branch == null) {
         if (cache.reloadCache()) {
            branch = cache.getById(branchId);
         }
      }
      if (branch == null) {
         throw new BranchDoesNotExist("Branch could not be acquired for branch id: " + branchId);
      }
      return branch;
   }

   /**
    * Update branch
    */
   public static Job updateBranch(final Branch branch, final ConflictResolverOperation resolver) {
      IOperation operation = new UpdateBranchOperation(branch, resolver);
      return Operations.executeAsJob(operation, true);
   }

   /**
    * Completes the update branch operation by committing latest parent based branch with branch with changes. Then
    * swaps branches so we are left with the most current branch containing latest changes.
    */
   public static Job completeUpdateBranch(final ConflictManagerExternal conflictManager, final boolean archiveSourceBranch, final boolean overwriteUnresolvedConflicts) {
      IOperation operation =
         new FinishUpdateBranchOperation(conflictManager, archiveSourceBranch, overwriteUnresolvedConflicts);
      return Operations.executeAsJob(operation, true);
   }

   public static void purgeBranch(final Branch branch) throws OseeCoreException {
      try {
         HttpPurgeBranchRequester.purge(branch);
      } catch (RuntimeException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public static void updateBranchType(IProgressMonitor monitor, final int branchId, String branchGuid, final BranchType type) throws OseeCoreException {
      HttpUpdateBranchTypeRequester.updateBranchType(monitor, branchId, branchGuid, type);
   }

   public static void updateBranchState(IProgressMonitor monitor, final int branchId, String branchGuid, final BranchState state) throws OseeCoreException {
      HttpUpdateBranchStateRequester.updateBranchState(monitor, branchId, branchGuid, state);
   }

   public static void updateBranchArchivedState(IProgressMonitor monitor, final int branchId, String branchGuid, final BranchArchivedState state) throws OseeCoreException {
      HttpUpdateBranchArchivedStateRequester.updateBranchArchivedState(monitor, branchId, branchGuid, state);
   }

   /**
    * Delete a branch from the system. (This operation will set the branch state to deleted. This operation is
    * undo-able)
    */
   public static Job deleteBranch(final Branch branch) {
      return deleteBranch(branch, false);
   }

   /**
    * Delete a branch from the system. (This operation will set the branch state to deleted. This operation is
    * undo-able)
    */
   public static Job deleteBranch(final Branch branch, boolean pend) {
      if (pend) {
         return Operations.executeAndPend(new DeleteBranchOperation(branch), true);
      } else {
         return Operations.executeAsJob(new DeleteBranchOperation(branch), true);
      }
   }

   /**
    * Commit the net changes from the source branch into the destination branch. If there are conflicts between the two
    * branches, the source branch changes will override those on the destination branch.
    */
   public static void commitBranch(IProgressMonitor monitor, ConflictManagerExternal conflictManager, boolean archiveSourceBranch, boolean overwriteUnresolvedConflicts) throws OseeCoreException {
      if (monitor == null) {
         monitor = new NullProgressMonitor();
      }
      if (conflictManager.remainingConflictsExist() && !overwriteUnresolvedConflicts) {
         throw new OseeCoreException("Commit failed due to unresolved conflicts");
      }
      if (!conflictManager.getDestinationBranch().isEditable()) {
         throw new OseeCoreException("Commit failed - unable to commit into a non-editable branch");
      }
      runCommitExtPointActions(conflictManager);
      HttpCommitDataRequester.commitBranch(monitor, UserManager.getUser(), conflictManager.getSourceBranch(),
         conflictManager.getDestinationBranch(), archiveSourceBranch);
   }

   private static void runCommitExtPointActions(ConflictManagerExternal conflictManager) throws OseeCoreException {
      ExtensionDefinedObjects<CommitAction> extensions =
         new ExtensionDefinedObjects<CommitAction>("org.eclipse.osee.framework.skynet.core.CommitActions",
            "CommitActions", "className");
      for (CommitAction commitAction : extensions.getObjects()) {
         commitAction.runCommitAction(conflictManager.getSourceBranch(), conflictManager.getDestinationBranch());
      }
   }

   /**
    * Permanently removes transactions and any of their backing data that is not referenced by any other transactions.
    */
   public static void purgeTransactions(final int... transactionIdNumbers) {
      purgeTransactions(null, transactionIdNumbers);
   }

   /**
    * Permanently removes transactions and any of their backing data that is not referenced by any other transactions.
    */
   public static void purgeTransactions(IJobChangeListener jobChangeListener, final int... transactionIdNumbers) {
      purgeTransactions(jobChangeListener, false, transactionIdNumbers);
   }

   /**
    * Permanently removes transactions and any of their backing data that is not referenced by any other transactions.
    */
   public static Job purgeTransactions(IJobChangeListener jobChangeListener, boolean force, final int... transactionIdNumbers) {
      IOperation op = new PurgeTransactionOperation(Activator.getInstance(), force, transactionIdNumbers);
      return Operations.executeAsJob(op, true, Job.LONG, jobChangeListener);
   }

   /**
    * Calls the getMergeBranch method and if it returns null it will create a new merge branch based on the artIds from
    * the source branch.
    */
   public static Branch getOrCreateMergeBranch(Branch sourceBranch, Branch destBranch, ArrayList<Integer> expectedArtIds) throws OseeCoreException {
      MergeBranch mergeBranch = getMergeBranch(sourceBranch, destBranch);
      if (mergeBranch == null) {
         mergeBranch = createMergeBranch(sourceBranch, destBranch, expectedArtIds);
      } else {
         UpdateMergeBranch dbTransaction = new UpdateMergeBranch(mergeBranch, expectedArtIds, destBranch, sourceBranch);
         dbTransaction.execute();
      }
      return mergeBranch;
   }

   private static MergeBranch createMergeBranch(final Branch sourceBranch, final Branch destBranch, final ArrayList<Integer> expectedArtIds) throws OseeCoreException {
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
      int populateBaseTxFromAddressingQueryId = ArtifactLoader.getNewQueryId();
      List<Object[]> datas = new LinkedList<Object[]>();
      for (int artId : expectedArtIds) {
         datas.add(new Object[] {
            populateBaseTxFromAddressingQueryId,
            insertTime,
            artId,
            sourceBranch.getId(),
            SQL3DataType.INTEGER});
      }
      MergeBranch mergeBranch = null;
      try {
         ArtifactLoader.insertIntoArtifactJoin(datas);

         int parentTxId = sourceBranch.getBaseTransaction().getId();
         String creationComment =
            String.format("New Merge Branch from %s(%s) and %s", sourceBranch.getName(), parentTxId,
               destBranch.getName());
         String branchName = "Merge " + sourceBranch.getShortName() + " <=> " + destBranch.getShortName();
         mergeBranch =
            (MergeBranch) HttpBranchCreation.createBranch(BranchType.MERGE, parentTxId, sourceBranch.getId(),
               branchName, null, UserManager.getUser(), creationComment, populateBaseTxFromAddressingQueryId,
               destBranch.getId());
         mergeBranch.setSourceBranch(sourceBranch);
         mergeBranch.setDestinationBranch(destBranch);
      } finally {
         ArtifactLoader.clearQuery(populateBaseTxFromAddressingQueryId);
      }
      return mergeBranch;
   }

   /**
    * Creates a new Branch based on the transaction number selected and the parent branch.
    */
   public static Branch createWorkingBranch(TransactionRecord parentTransactionId, String childBranchName, String childBranchGuid, Artifact associatedArtifact) throws OseeCoreException {
      int parentBranchId = parentTransactionId.getBranchId();
      int parentTransactionNumber = parentTransactionId.getId();

      Branch parentBranch = BranchManager.getBranch(parentBranchId);
      String creationComment = "New Branch from " + parentBranch.getName() + "(" + parentTransactionNumber + ")";

      final String truncatedName = Strings.truncate(childBranchName, 195, true);
      return HttpBranchCreation.createBranch(BranchType.WORKING, parentTransactionNumber, parentBranchId,
         truncatedName, childBranchGuid, associatedArtifact, creationComment, -1, -1);
   }

   /**
    * Creates a new Branch based on the most recent transaction on the parent branch.
    */
   public static Branch createWorkingBranch(IOseeBranch parentBranch, String childBranchName, Artifact associatedArtifact) throws OseeCoreException {
      TransactionRecord parentTransactionId = TransactionManager.getHeadTransaction(parentBranch);
      return createWorkingBranch(parentTransactionId, childBranchName, null, associatedArtifact);
   }

   public static Branch createWorkingBranch(IOseeBranch parentBranch, IOseeBranch childBranch, Artifact associatedArtifact) throws OseeCoreException {
      TransactionRecord parentTransactionId = TransactionManager.getHeadTransaction(parentBranch);
      return createWorkingBranch(parentTransactionId, childBranch.getName(), childBranch.getGuid(), associatedArtifact);
   }

   /**
    * Creates a new Branch based on the most recent transaction on the parent branch.
    */
   public static Branch createBaselineBranch(IOseeBranch parentBranch, IOseeBranch childBranch, Artifact associatedArtifact) throws OseeCoreException {
      TransactionRecord parentTransactionId = TransactionManager.getHeadTransaction(parentBranch);
      String creationComment = String.format("Branch Creation for %s", childBranch.getName());
      return HttpBranchCreation.createBranch(BranchType.BASELINE, parentTransactionId.getId(),
         parentTransactionId.getBranch().getId(), childBranch.getName(), childBranch.getGuid(), associatedArtifact,
         creationComment, -1, -1);
   }

   /**
    * Creates a new root branch, imports skynet types and initializes.
    * 
    * @param initializeArtifacts adds common artifacts needed by most normal root branches
    */
   public static Branch createTopLevelBranch(IOseeBranch branch) throws OseeCoreException {
      return createBaselineBranch(CoreBranches.SYSTEM_ROOT, branch, null);
   }

   public static Branch createTopLevelBranch(final String branchName) throws OseeCoreException {
      return createTopLevelBranch(new CoreBranches(GUID.create(), branchName));
   }

   public static List<Branch> getBaselineBranches() throws OseeCoreException {
      return getBranches(BranchArchivedState.UNARCHIVED, BranchType.BASELINE);
   }

   private void initializeLastBranchValue() {
      try {
         String branchGuid = UserManager.getSetting(LAST_DEFAULT_BRANCH);
         lastBranch = getBranchByGuid(branchGuid);
      } catch (Exception ex) {
         try {
            lastBranch = getDefaultInitialBranch();
            UserManager.setSetting(LAST_DEFAULT_BRANCH, lastBranch.getGuid());
         } catch (OseeCoreException ex1) {
            OseeLog.log(Activator.class, Level.SEVERE, ex1);
         }
      }
   }

   private Branch getDefaultInitialBranch() throws OseeCoreException {
      ExtensionDefinedObjects<IDefaultInitialBranchesProvider> extensions =
         new ExtensionDefinedObjects<IDefaultInitialBranchesProvider>(
            "org.eclipse.osee.framework.skynet.core.DefaultInitialBranchProvider", "DefaultInitialBranchProvider",
            "class");
      for (IDefaultInitialBranchesProvider provider : extensions.getObjects()) {
         try {
            // Guard against problematic extensions
            for (Branch branch : provider.getDefaultInitialBranches()) {
               if (branch != null) {
                  return branch;
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING,
               "Exception occurred while trying to determine initial default branch", ex);
         }
      }
      return getCommonBranch();
   }

   public static Branch getLastBranch() {
      if (instance.lastBranch == null) {
         instance.initializeLastBranchValue();
      }
      return instance.lastBranch;
   }

   public static void setLastBranch(Branch branch) {
      if (branch != null) {
         instance.lastBranch = branch;
      }
   }

   public static Branch getSystemRootBranch() throws OseeCoreException {
      return getCache().getSystemRootBranch();
   }

   public static void persist(Branch... branches) throws OseeCoreException {
      getCache().storeItems(Arrays.asList(branches));
   }

   public static void persist(Collection<Branch> branches) throws OseeCoreException {
      getCache().storeItems(branches);
   }

   public static String toFileName(Branch branch) throws OseeCoreException {
      return BranchUtility.toFileName(branch);
   }

   public static Branch fromFileName(String fileName) throws OseeCoreException {
      return BranchUtility.fromFileName(getCache(), fileName);
   }

   public static void decache(Branch branch) throws OseeCoreException {
      getCache().decache(branch);
   }

   public static boolean hasChanges(Branch branch) throws OseeCoreException {
      return branch.getBaseTransaction() != TransactionManager.getHeadTransaction(branch);
   }

   public static boolean isChangeManaged(Branch branch) throws OseeCoreException {
      // TODO use Associated Artifacts
      int systemUserArtId = UserManager.getUser(SystemUser.OseeSystem).getArtId();

      int assocArtId = branch.getAssociatedArtifactId();
      return assocArtId > 0 && assocArtId != systemUserArtId;
   }

   public static Artifact getAssociatedArtifact(Branch branch) throws OseeCoreException {
      if (branch.getAssociatedArtifactId() == null || branch.getAssociatedArtifactId() == -1) {
         return UserManager.getUser(SystemUser.OseeSystem);
      }
      return ArtifactQuery.getArtifactFromId(branch.getAssociatedArtifactId(), BranchManager.getCommonBranch());
   }
}