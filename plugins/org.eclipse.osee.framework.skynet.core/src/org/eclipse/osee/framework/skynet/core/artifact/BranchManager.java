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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationBuilder;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.operation.UpdateBranchOperation;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.update.ConflictResolverOperation;
import org.eclipse.osee.framework.skynet.core.commit.actions.CommitAction;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.httpRequests.CommitBranchHttpRequestOperation;
import org.eclipse.osee.framework.skynet.core.httpRequests.CreateBranchHttpRequestOperation;
import org.eclipse.osee.framework.skynet.core.httpRequests.PurgeBranchHttpRequestOperation;
import org.eclipse.osee.framework.skynet.core.httpRequests.UpdateBranchArchivedStateHttpRequestOperation;
import org.eclipse.osee.framework.skynet.core.httpRequests.UpdateBranchStateHttpRequestOperation;
import org.eclipse.osee.framework.skynet.core.httpRequests.UpdateBranchTypeHttpRequestOperation;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ArtifactJoinQuery;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * Provides access to all branches as well as support for creating branches of all types
 *
 * @author Ryan D. Brooks
 */
public class BranchManager {
   private static final BranchManager instance = new BranchManager();

   private static final String LAST_DEFAULT_BRANCH = "LastDefaultBranchUuid";
   public static final String COMMIT_COMMENT = "Commit Branch ";
   private static final BranchFactory branchFactory = new BranchFactory();
   private static final String SELECT_BRANCH = "select * from osee_branch where branch_id = ?";

   private IOseeBranch lastBranch;

   private BranchManager() {
      // this private empty constructor exists to prevent the default constructor from allowing public construction
   }

   /**
    * use static methods instead
    */
   @Deprecated
   public static BranchManager getInstance() {
      return instance;
   }

   private static BranchCache getCache() throws OseeCoreException {
      return ServiceUtil.getOseeCacheService().getBranchCache();
   }

   public static List<Branch> getBranches(BranchArchivedState archivedState, BranchType... branchTypes) throws OseeCoreException {
      return getBranches(new BranchFilter(archivedState, branchTypes));
   }

   public static List<Branch> getBranches(BranchFilter branchFilter) throws OseeCoreException {
      return getCache().getBranches(branchFilter);
   }

   public static void refreshBranches() throws OseeCoreException {
      String refreshWindow = OseeInfo.getValue("cache.reload.throttle.millis");
      boolean reload = true;
      if (Strings.isNumeric(refreshWindow)) {
         long timeInMillis = Long.parseLong(refreshWindow);
         long diff = System.currentTimeMillis() - getCache().getLastLoaded();
         if (diff < timeInMillis) {
            reload = false;
         }
      }
      if (reload) {
         getCache().reloadCache();
      }
   }

   public static IOseeBranch getBranch(String branchName) throws OseeCoreException {
      Collection<IOseeBranch> branches = getBranchesByName(branchName);
      if (branches.isEmpty()) {
         throw new BranchDoesNotExist("No branch exists with the name: [%s]", branchName);
      }
      if (branches.size() > 1) {
         throw new MultipleBranchesExist("More than 1 branch exists with the name: [%s]", branchName);
      }
      return branches.iterator().next();
   }

   public static Collection<IOseeBranch> getBranchesByName(String branchName) throws OseeCoreException {
      return Collections.castAll(getCache().getByName(branchName));
   }

   public static Branch getBranch(IOseeBranch branch) throws OseeCoreException {
      if (branch instanceof Branch) {
         return (Branch) branch;
      } else {
         return getBranch(branch.getUuid());
      }
   }

   /**
    * Do not call this method unless absolutely neccessary due to performance impacts.
    */
   public static synchronized void checkAndReload(Long uuid) throws OseeCoreException {
      if (!branchExists(uuid)) {
         loadBranchToCache(uuid);
      }
   }

   private static void loadBranchToCache(Object uuid) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(1, SELECT_BRANCH, uuid);
         if (chStmt.next()) {
            long branchUuid = chStmt.getLong("branch_id");
            String branchName = chStmt.getString("branch_name");
            BranchState branchState = BranchState.getBranchState(chStmt.getInt("branch_state"));
            BranchType branchType = BranchType.valueOf(chStmt.getInt("branch_type"));
            BranchArchivedState archiveState = BranchArchivedState.valueOf(chStmt.getInt("archived"));
            long parentBranchId = chStmt.getLong("parent_branch_id");
            int sourceTx = chStmt.getInt("parent_transaction_id");
            int baseTx = chStmt.getInt("baseline_transaction_id");
            int assocArtId = chStmt.getInt("associated_art_id");
            int inheritAccessControl = chStmt.getInt("inherit_access_control");

            Branch created = branchFactory.createOrUpdate(getCache(), branchUuid, branchName, branchType, branchState,
               archiveState.isArchived(), StorageState.LOADED, inheritAccessControl == 1);
            created.setBaseTransaction(TransactionManager.getTransactionId(baseTx));
            created.setSourceTransaction(TransactionManager.getTransactionId(sourceTx));
            created.setAssociatedArtifactId(assocArtId);
            created.setParentBranch(getBranch(parentBranchId));
         }
      } finally {
         chStmt.close();
      }

   }

   public static Branch getBranchByGuid(Long uuid) throws OseeCoreException {
      checkAndReload(uuid);
      Branch branch = getCache().getByGuid(uuid);
      if (branch == null) {
         throw new BranchDoesNotExist("Branch with uuid [%s] does not exist", uuid);
      }
      return branch;
   }

   public static boolean branchExists(IOseeBranch branchToken) throws OseeCoreException {
      return getCache().get(branchToken) != null;
   }

   public static boolean branchExists(long uuid) throws OseeCoreException {
      return getCache().getById(uuid) != null;
   }

   /**
    * returns the merge branch for this source destination pair from the cache or null if not found
    */
   public static MergeBranch getMergeBranch(IOseeBranch sourceBranch, IOseeBranch destinationBranch) throws OseeCoreException {
      MergeBranch mergeBranch = getCache().findMergeBranch(sourceBranch, destinationBranch);
      return mergeBranch;
   }

   /**
    * returns the first merge branch for this source destination pair from the cache or null if not found
    */
   public static MergeBranch getFirstMergeBranch(Branch sourceBranch) throws OseeCoreException {
      MergeBranch mergeBranch = getCache().findFirstMergeBranch(sourceBranch);
      return mergeBranch;
   }

   /**
    * returns a list tof all the merge branches for this source branch from the cache or null if not found
    */
   public static List<MergeBranch> getMergeBranches(Branch sourceBranch) throws OseeCoreException {
      List<MergeBranch> mergeBranches = getCache().findAllMergeBranches(sourceBranch);
      return mergeBranches;
   }

   /**
    * returns whether a source branch has existing merge branches
    */
   public static boolean hasMergeBranches(Branch sourceBranch) throws OseeCoreException {
      if (getMergeBranches(sourceBranch).isEmpty()) {
         return false;
      } else {
         return true;
      }
   }

   /**
    * returns whether a merge branch exists for a source and dest branch pair
    */
   public static boolean doesMergeBranchExist(IOseeBranch sourceBranch, IOseeBranch destBranch) throws OseeCoreException {
      return getMergeBranch(sourceBranch, destBranch) != null;
   }

   public static Branch getBranch(Long branchUuid) throws OseeCoreException {
      if (branchUuid == null) {
         throw new BranchDoesNotExist("Branch Uuid is null");
      }

      checkAndReload(branchUuid);
      Branch branch = getCache().getById(branchUuid);
      if (branch == null) {
         throw new BranchDoesNotExist("Branch could not be acquired for branch uuid %d", branchUuid);
      }
      return branch;
   }

   public static void reloadBranch(long uuid) {
      loadBranchToCache(uuid);
   }

   public static Collection<Branch> getAll() {
      return getCache().getAll();
   }

   /**
    * returns a list tof all the merge branches for this source branch from the cache or null if not found
    */
   public static boolean isUpdatable(Branch branchToUpdate) throws OseeCoreException {
      if (!hasMergeBranches(branchToUpdate) || branchToUpdate.getBranchState().isRebaselineInProgress()) {
         return true;
      }
      return false;
   }

   /**
    * Update branch
    */
   public static Job updateBranch(final Branch branch, final ConflictResolverOperation resolver) {
      IOperation operation = new UpdateBranchOperation(branch, resolver);
      return Operations.executeAsJob(operation, true);
   }

   public static void purgeBranch(final IOseeBranch branch) throws OseeCoreException {
      Operations.executeWorkAndCheckStatus(new PurgeBranchHttpRequestOperation(branch, false));
   }

   public static void updateBranchType(IProgressMonitor monitor, final long branchUuid, final BranchType type) throws OseeCoreException {
      IOperation operation = new UpdateBranchTypeHttpRequestOperation(branchUuid, type);
      Operations.executeWorkAndCheckStatus(operation, monitor);
   }

   public static void updateBranchState(IProgressMonitor monitor, final long branchUuid, final BranchState state) throws OseeCoreException {
      IOperation operation = new UpdateBranchStateHttpRequestOperation(branchUuid, state);
      Operations.executeWorkAndCheckStatus(operation, monitor);
   }

   public static void updateBranchArchivedState(IProgressMonitor monitor, final long branchUuid, final BranchArchivedState state) throws OseeCoreException {
      IOperation operation = new UpdateBranchArchivedStateHttpRequestOperation(branchUuid, state.isArchived());
      Operations.executeWorkAndCheckStatus(operation, monitor);
   }

   /**
    * Delete a branch from the system. (This operation will set the branch state to deleted. This operation is
    * undo-able)
    */
   public static Job deleteBranch(final IOseeBranch branch) {
      return Operations.executeAsJob(new DeleteBranchOperation(branch), true);
   }

   public static IStatus deleteBranchAndPend(final IOseeBranch branch) {
      return Operations.executeWork(new DeleteBranchOperation(branch));
   }

   /**
    * Delete branches from the system. (sets branch state to deleted. operation is undo-able)
    *
    * @throws OseeCoreException
    */
   public static Job deleteBranch(final List<? extends IOseeBranch> branches) {
      List<IOperation> ops = new ArrayList<>();
      for (IOseeBranch branch : branches) {
         ops.add(new DeleteBranchOperation(branch));
      }
      OperationBuilder builder = Operations.createBuilder("Deleting multiple branches...");
      builder.addAll(ops);
      return Operations.executeAsJob(builder.build(), false);
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
      if (!isEditable(conflictManager.getDestinationBranch())) {
         throw new OseeCoreException("Commit failed - unable to commit into a non-editable branch");
      }

      boolean skipCommitChecksAndEvents = OseeClientProperties.isSkipCommitChecksAndEvents();
      if (!skipCommitChecksAndEvents) {
         runCommitExtPointActions(conflictManager);
      }

      IOperation operation =
         new CommitBranchHttpRequestOperation(UserManager.getUser(), conflictManager.getSourceBranch(),
            conflictManager.getDestinationBranch(), archiveSourceBranch, skipCommitChecksAndEvents);
      Operations.executeWorkAndCheckStatus(operation, monitor);
   }

   private static void runCommitExtPointActions(ConflictManagerExternal conflictManager) throws OseeCoreException {
      ExtensionDefinedObjects<CommitAction> extensions = new ExtensionDefinedObjects<CommitAction>(
         "org.eclipse.osee.framework.skynet.core.CommitActions", "CommitActions", "className");
      for (CommitAction commitAction : extensions.getObjects()) {
         commitAction.runCommitAction(conflictManager.getSourceBranch(), conflictManager.getDestinationBranch());
      }
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
         UpdateMergeBranch op = new UpdateMergeBranch(ConnectionHandler.getJdbcClient(), mergeBranch, expectedArtIds,
            destBranch, sourceBranch);
         Operations.executeWorkAndCheckStatus(op);
      }
      return mergeBranch;
   }

   private static MergeBranch createMergeBranch(final Branch sourceBranch, final Branch destBranch, final ArrayList<Integer> expectedArtIds) throws OseeCoreException {
      ArtifactJoinQuery joinQuery = JoinUtility.createArtifactJoinQuery();
      for (int artId : expectedArtIds) {
         joinQuery.add(artId, sourceBranch.getUuid());
      }
      MergeBranch mergeBranch = null;
      try {
         joinQuery.store();

         int parentTxId = sourceBranch.getBaseTransaction().getId();
         String creationComment = String.format("New Merge Branch from %s(%s) and %s", sourceBranch.getName(),
            parentTxId, destBranch.getName());
         String branchName = "Merge " + sourceBranch.getShortName() + " <=> " + destBranch.getShortName();
         mergeBranch = (MergeBranch) createBranch(BranchType.MERGE, sourceBranch.getBaseTransaction(), branchName,
            Lib.generateUuid(), UserManager.getUser(), creationComment, joinQuery.getQueryId(), destBranch.getUuid());
         mergeBranch.setSourceBranch(sourceBranch);
         mergeBranch.setDestinationBranch(destBranch);
      } finally {
         joinQuery.delete();
      }
      return mergeBranch;
   }

   public static Branch createWorkingBranch(TransactionRecord parentTransactionId, String childBranchName, Artifact associatedArtifact) throws OseeCoreException {
      return createWorkingBranch(parentTransactionId, childBranchName, Lib.generateUuid(), associatedArtifact);
   }

   public static Branch createWorkingBranch(TransactionRecord parentTransactionId, String childBranchName, Long childBranchUuid, Artifact associatedArtifact) throws OseeCoreException {
      Conditions.notNull(childBranchUuid, "childBranchUuid");
      String creationComment = String.format("New Branch from %s (%s)", parentTransactionId.getBranch().getName(),
         parentTransactionId.getId());

      final String truncatedName = Strings.truncate(childBranchName, 195, true);
      return createBranch(BranchType.WORKING, parentTransactionId, truncatedName, childBranchUuid, associatedArtifact,
         creationComment, -1, -1);
   }

   /**
    * Creates a new Branch based on the most recent transaction on the parent branch.
    */
   public static Branch createWorkingBranchFromTx(TransactionRecord parentTransactionId, String childBranchName, Artifact associatedArtifact) throws OseeCoreException {
      String creationComment = String.format("New branch, copy of %s from transaction %s",
         parentTransactionId.getBranch().getName(), parentTransactionId.getId());

      final String truncatedName = Strings.truncate(childBranchName, 195, true);

      CreateBranchHttpRequestOperation operation = new CreateBranchHttpRequestOperation(BranchType.WORKING,
         parentTransactionId, truncatedName, Lib.generateUuid(), associatedArtifact, creationComment, -1, -1);
      operation.setTxCopyBranchType(true);
      Operations.executeWorkAndCheckStatus(operation);
      return operation.getNewBranch();
   }

   public static Branch createPortBranchFromTx(TransactionRecord parentTransactionId, String childBranchName, Artifact associatedArtifact) throws OseeCoreException {
      String creationComment = String.format("New port branch, copy of %s from transaction %s",
         parentTransactionId.getBranch().getName(), parentTransactionId.getId());

      final String truncatedName = Strings.truncate(childBranchName, 195, true);

      CreateBranchHttpRequestOperation operation = new CreateBranchHttpRequestOperation(BranchType.PORT,
         parentTransactionId, truncatedName, Lib.generateUuid(), associatedArtifact, creationComment, -1, -1);
      operation.setTxCopyBranchType(true);
      Operations.executeWorkAndCheckStatus(operation);
      return operation.getNewBranch();
   }

   public static Branch createWorkingBranch(IOseeBranch parentBranch, String childBranchName) throws OseeCoreException {
      return createWorkingBranch(parentBranch, childBranchName, UserManager.getUser(SystemUser.OseeSystem));
   }

   public static Branch createWorkingBranch(IOseeBranch parentBranch, String childBranchName, Artifact associatedArtifact) throws OseeCoreException {
      Conditions.checkNotNull(parentBranch, "Parent Branch");
      Conditions.checkNotNull(childBranchName, "Child Branch Name");
      Conditions.checkNotNull(associatedArtifact, "Associated Artifact");
      TransactionRecord parentTransactionId = TransactionManager.getHeadTransaction(parentBranch);
      return createWorkingBranch(parentTransactionId, childBranchName, Lib.generateUuid(), associatedArtifact);
   }

   public static IOseeBranch createWorkingBranch(IOseeBranch parentBranch, IOseeBranch childBranch) throws OseeCoreException {
      return createWorkingBranch(parentBranch, childBranch, UserManager.getUser(SystemUser.OseeSystem));
   }

   public static IOseeBranch createWorkingBranch(IOseeBranch parentBranch, IOseeBranch childBranch, Artifact associatedArtifact) throws OseeCoreException {
      TransactionRecord parentTransactionId = TransactionManager.getHeadTransaction(parentBranch);
      return createWorkingBranch(parentTransactionId, childBranch.getName(), childBranch.getUuid(), associatedArtifact);
   }

   /**
    * Creates a new Branch based on the most recent transaction on the parent branch.
    */
   public static IOseeBranch createBaselineBranch(IOseeBranch parentBranch, IOseeBranch childBranch) throws OseeCoreException {
      return createBaselineBranch(parentBranch, childBranch, UserManager.getUser(SystemUser.OseeSystem));
   }

   public static IOseeBranch createBaselineBranch(IOseeBranch parentBranch, IOseeBranch childBranch, Artifact associatedArtifact) throws OseeCoreException {
      TransactionRecord parentTransactionId = TransactionManager.getHeadTransaction(parentBranch);
      String creationComment = String.format("Branch Creation for %s", childBranch.getName());
      return createBranch(BranchType.BASELINE, parentTransactionId, childBranch.getName(), childBranch.getUuid(),
         associatedArtifact, creationComment, -1, -1);
   }

   private static Branch createBranch(BranchType branchType, TransactionRecord parentTransaction, String branchName, long branchUuid, Artifact associatedArtifact, String creationComment, int mergeAddressingQueryId, long destinationBranchId) throws OseeCoreException {
      CreateBranchHttpRequestOperation operation = new CreateBranchHttpRequestOperation(branchType, parentTransaction,
         branchName, branchUuid, associatedArtifact, creationComment, mergeAddressingQueryId, destinationBranchId);
      Operations.executeWorkAndCheckStatus(operation);
      return operation.getNewBranch();
   }

   /**
    * Creates a new root branch, imports skynet types and initializes.
    *
    * @param initializeArtifacts adds common artifacts needed by most normal root branches
    */
   public static IOseeBranch createTopLevelBranch(IOseeBranch branch) throws OseeCoreException {
      return createBaselineBranch(CoreBranches.SYSTEM_ROOT, branch, null);
   }

   public static IOseeBranch createTopLevelBranch(final String branchName) throws OseeCoreException {
      return createTopLevelBranch(TokenFactory.createBranch(branchName));
   }

   public static List<? extends IOseeBranch> getBaselineBranches() throws OseeCoreException {
      return getBranches(BranchArchivedState.UNARCHIVED, BranchType.BASELINE);
   }

   private void initializeLastBranchValue() {
      try {
         Long branchUuid = Long.valueOf(UserManager.getSetting(LAST_DEFAULT_BRANCH));
         lastBranch = getBranch(branchUuid);
      } catch (Exception ex) {
         try {
            lastBranch = getDefaultInitialBranch();
            UserManager.setSetting(LAST_DEFAULT_BRANCH, lastBranch.getUuid());
         } catch (OseeCoreException ex1) {
            OseeLog.log(Activator.class, Level.SEVERE, ex1);
         }
      }
   }

   private IOseeBranch getDefaultInitialBranch() throws OseeCoreException {
      ExtensionDefinedObjects<IDefaultInitialBranchesProvider> extensions =
         new ExtensionDefinedObjects<IDefaultInitialBranchesProvider>(
            "org.eclipse.osee.framework.skynet.core.DefaultInitialBranchProvider", "DefaultInitialBranchProvider",
            "class", true);
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
      return COMMON;
   }

   public static IOseeBranch getLastBranch() {
      if (instance.lastBranch == null) {
         instance.initializeLastBranchValue();
      }
      return instance.lastBranch;
   }

   public static void setLastBranch(IOseeBranch branch) {
      if (branch != null) {
         instance.lastBranch = branch;
      }
   }

   public static void persist(Branch... branches) throws OseeCoreException {
      getCache().storeItems(Arrays.asList(branches));
   }

   public static void persist(Collection<Branch> branches) throws OseeCoreException {
      getCache().storeItems(branches);
   }

   public static void decache(Branch branch) throws OseeCoreException {
      getCache().decache(branch);
   }

   public static boolean hasChanges(Branch branch) throws OseeCoreException {
      return branch.getBaseTransaction() != TransactionManager.getHeadTransaction(branch);
   }

   public static boolean isChangeManaged(IOseeBranch branch) throws OseeCoreException {
      Integer associatedArtifactId = getAssociatedArtifactId(branch);
      return associatedArtifactId > 0 && !associatedArtifactId.equals(SystemUser.OseeSystem);
   }

   public static Integer getAssociatedArtifactId(IOseeBranch branch) {
      return getBranch(branch).getAssociatedArtifactId();
   }

   public static Artifact getAssociatedArtifact(IOseeBranch branch) throws OseeCoreException {
      Integer associatedArtifactId = getAssociatedArtifactId(branch);
      if (associatedArtifactId == null || associatedArtifactId == -1) {
         return UserManager.getUser(SystemUser.OseeSystem);
      }
      return ArtifactQuery.getArtifactFromId(associatedArtifactId, COMMON);
   }

   public static Artifact getAssociatedArtifact(TransactionDelta txDelta) throws OseeCoreException {
      Artifact associatedArtifact = null;
      if (txDelta.areOnTheSameBranch()) {
         TransactionRecord txRecord = txDelta.getEndTx();
         int commitArtId = txRecord.getCommit();
         if (commitArtId != 0) {
            associatedArtifact = ArtifactQuery.getArtifactFromId(commitArtId, COMMON);
         }
      } else {
         IOseeBranch sourceBranch = txDelta.getStartTx().getBranch();
         associatedArtifact = BranchManager.getAssociatedArtifact(sourceBranch);
      }
      return associatedArtifact;
   }

   public static void invalidateBranches() throws OseeCoreException {
      getCache().invalidate();
   }

   public static Branch getParentBranch(IOseeBranch branch) {
      return getBranch(branch).getParentBranch();
   }

   public static boolean isParentSystemRoot(IOseeBranch branch) {
      return CoreBranches.SYSTEM_ROOT.equals(getParentBranch(branch));
   }

   public static TransactionRecord getBaseTransaction(IOseeBranch destinationBranch) {
      return ((Branch) destinationBranch).getBaseTransaction();
   }

   public static BranchState getState(IOseeBranch branch) {
      return ((Branch) branch).getBranchState();
   }

   public static boolean hasChanges(IOseeBranch branch) {
      return hasChanges((Branch) branch);
   }

   public static BranchType getBranchType(IOseeBranch branch) {
      return ((Branch) branch).getBranchType();
   }

   public static boolean isEditable(IOseeBranch branch) {
      return getBranch(branch).isEditable();
   }

   public static void resetWasLoaded() {
      getCache().invalidate();
   }

   public static boolean isLoaded() {
      return getCache().isLoaded();
   }
}
