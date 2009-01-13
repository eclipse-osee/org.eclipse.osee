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
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TXD_COMMENT;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.dbinit.MasterSkynetTypesImport;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.WindowLocal;

public class BranchManager {
   private static final BranchManager instance = new BranchManager();

   private static final String READ_BRANCH_TABLE =
         "SELECT * FROM osee_branch br1, osee_tx_details txd1 WHERE br1.branch_id = txd1.branch_id AND txd1.tx_type = " + TransactionDetailsType.Baselined.getId();
   private static final String READ_MERGE_BRANCHES =
         "SELECT m1.* FROM osee_merge m1, osee_tx_details txd1 WHERE m1.merge_branch_id = txd1.branch_id and txd1.tx_type = " + TransactionDetailsType.Baselined.getId();
   public static final String COMMIT_TRANSACTION =
         "INSERT INTO " + TRANSACTION_DETAIL_TABLE.columnsForInsert("tx_type", "branch_id", "transaction_id",
               TXD_COMMENT, "time", "author", "commit_art_id");
   private static final String SELECT_BRANCH_TRANSACTION =
         "SELECT transaction_id FROM osee_tx_details WHERE branch_id = ? AND time < ? ORDER BY time DESC";

   private static final String UPDATE_TRANSACTION_BRANCH =
         "UPDATE " + TRANSACTION_DETAIL_TABLE + " SET branch_id=? WHERE " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "=?";

   public static final String NEW_BRANCH_COMMENT = "New Branch from ";
   private static final String ARCHIVE_BRANCH = "UPDATE osee_branch set archived = 1 WHERE branch_id = ?";
   private static final String UPDATE_ASSOCIATED_ART_BRANCH =
         "UPDATE  osee_branch set associated_art_id = ? WHERE branch_id = ?";

   private final static String LAST_DEFAULT_BRANCH = "LastDefaultBranch";
   private static final IPreferenceStore preferenceStore = SkynetActivator.getInstance().getPreferenceStore();

   // This hash is keyed on the branchId
   private final TreeMap<Integer, Branch> branchCache = new TreeMap<Integer, Branch>();

   public static final String COMMIT_COMMENT = "Commit Branch ";

   private static final boolean MERGE_DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Merge"));

   private Branch systemRoot;

   private BranchManager() {
   }

   @Deprecated
   // use static methods instead
   public static BranchManager getInstance() {
      return instance;
   }

   public static Set<Branch> getAssociatedArtifactBranches(Artifact associatedArtifact) throws OseeCoreException {
      instance.ensurePopulatedCache(false);
      Set<Branch> branches = new HashSet<Branch>();
      for (Branch branch : getNormalBranches())
         if (branch.isAssociatedToArtifact(associatedArtifact)) {
            branches.add(branch);
         }
      return branches;
   }

   public static Branch getCommonBranch() throws OseeCoreException {
      return getKeyedBranch(Branch.COMMON_BRANCH_CONFIG_ID);
   }

   public static Branch getKeyedBranch(String keyname) throws OseeCoreException {
      return KeyedBranchCache.getKeyedBranch(keyname);
   }

   /**
    * Excludes branches of type MERGE and SYSTEM_ROOT
    * 
    * @return branches that are not archived and are of type STANDARD, TOP_LEVEL, or BASELINE
    * @throws OseeCoreException
    */
   public static List<Branch> getNormalBranches() throws OseeCoreException {
      List<Branch> branches =
            getBranches(BranchState.ACTIVE, BranchControlled.ALL, BranchType.WORKING, BranchType.TOP_LEVEL,
                  BranchType.BASELINE);
      Collections.sort(branches);
      return branches;
   }

   public static List<Branch> getBranches(BranchState branchState, BranchControlled branchControlled, BranchType... branchTypes) throws OseeCoreException {
      instance.ensurePopulatedCache(false);
      List<Branch> branches = new ArrayList<Branch>(500);
      for (Branch branch : instance.branchCache.values()) {
         if (branch.matchesState(branchState) && branch.matchesControlled(branchControlled) && branch.isOfType(branchTypes)) {
            branches.add(branch);
         }
      }

      return branches;
   }

   public static void refreshBranches() throws OseeCoreException {
      instance.ensurePopulatedCache(true);
   }

   @Deprecated
   // use getKeyedBranch() or get the branch by id or from getBranches(...)
   public static Branch getBranch(String branchName) throws OseeCoreException {
      instance.ensurePopulatedCache(false);
      for (Branch branch : instance.branchCache.values()) {
         if (branch.getBranchName().equals(branchName)) {
            return branch;
         }
      }
      throw new BranchDoesNotExist("No branch exists with the name: " + branchName);
   }

   private synchronized void ensurePopulatedCache(boolean forceRead) throws OseeDataStoreException {
      if (forceRead || branchCache.size() == 0) {
         // The branch cache can not be cleared here because applications may contain branch references.

         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(2000, READ_BRANCH_TABLE);
            while (chStmt.next()) {
               Branch cachedBranch = branchCache.get(chStmt.getInt("branch_id"));

               if (cachedBranch == null) {
                  cachedBranch = initializeBranchObject(chStmt);
                  branchCache.put(cachedBranch.getBranchId(), cachedBranch);
               } else {
                  cachedBranch.setBranchName(chStmt.getString("branch_name"));
                  cachedBranch.setArchived(chStmt.getInt("archived") == 1);
               }

               if (cachedBranch.isSystemRootBranch()) {
                  systemRoot = cachedBranch;
               }
            }
         } finally {
            chStmt.close();
         }

         try {
            chStmt.runPreparedQuery(1000, READ_MERGE_BRANCHES);
            while (chStmt.next()) {
               Branch sourceBranch = branchCache.get(chStmt.getInt("source_branch_id"));
               Branch destBranch = branchCache.get(chStmt.getInt("dest_branch_id"));
               Branch mergeBranch = branchCache.get(chStmt.getInt("merge_branch_id"));
               mergeBranch.setMergeBranchInfo(sourceBranch, destBranch);
            }
         } finally {
            chStmt.close();
         }
      }
   }

   /**
    * returns the merge branch for this source destination pair from the cache or null if not found
    */
   public static Branch getMergeBranch(Branch sourceBranch, Branch destBranch) throws OseeCoreException {
      instance.ensurePopulatedCache(false);
      for (Branch branch : instance.branchCache.values()) {
         if (branch.isMergeBranchFor(sourceBranch, destBranch)) {
            return branch;
         }
      }
      return null;
   }

   public static Collection<Branch> getWorkingBranches(Branch parentBranch) throws OseeCoreException {
      instance.ensurePopulatedCache(false);
      List<Branch> branches = new ArrayList<Branch>(500);
      for (Branch branch : instance.branchCache.values()) {
         if (branch.matchesState(BranchState.ACTIVE) && branch.isOfType(BranchType.WORKING) && parentBranch.equals(branch.getParentBranch())) {
            branches.add(branch);
         }
      }

      return branches;
   }

   public static Collection<Branch> getArchivedBranches() throws OseeCoreException {
      return getBranches(BranchState.ARCHIVED, BranchControlled.ALL, BranchType.WORKING, BranchType.TOP_LEVEL,
            BranchType.BASELINE);
   }

   /**
    * deletes (permanently removes from the datastore) each archived branch one at a time using sequential jobs
    * 
    * @throws InterruptedException
    */
   public static void deleteArchivedBranches() throws OseeCoreException, InterruptedException {
      for (Branch archivedBranch : getArchivedBranches()) {
         Job job = new DeleteBranchJob(archivedBranch);
         Jobs.startJob(job);
         job.join();
      }
   }

   public static Branch createBranchObject(String branchShortName, String branchName, int branchId, int parentBranchId, int parentTransactionId, boolean archived, int authorId, Timestamp creationDate, String creationComment, int associatedArtifactId, BranchType branchType) {
      Branch branch =
            new Branch(branchShortName, branchName, branchId, parentBranchId, parentTransactionId, archived, authorId,
                  creationDate, creationComment, associatedArtifactId, branchType);
      instance.branchCache.put(branchId, branch);
      return branch;
   }

   /**
    * Create a Branch object based on the result set from the READ_BRANCH_TABLE query
    * 
    * @param rSet
    * @return
    * @throws OseeDataStoreException
    */
   private static Branch initializeBranchObject(ConnectionHandlerStatement chStmt) throws OseeDataStoreException {
      return createBranchObject(chStmt.getString("short_name"), chStmt.getString("branch_name"),
            chStmt.getInt("branch_id"), chStmt.getInt("parent_branch_id"), chStmt.getInt("parent_transaction_id"),
            chStmt.getInt("archived") == 1, chStmt.getInt("author"), chStmt.getTimestamp("time"),
            chStmt.getString(TXD_COMMENT), chStmt.getInt("associated_art_id"),
            BranchType.getBranchType(chStmt.getInt("branch_type")));
   }

   /**
    * Calls the getMergeBranch method and if it returns null it will create a new merge branch based on the artIds from
    * the source branch.
    */
   public static Branch getOrCreateMergeBranch(Branch sourceBranch, Branch destBranch, ArrayList<Integer> expectedArtIds) throws OseeCoreException {
      long time = 0;
      Branch mergeBranch = getMergeBranch(sourceBranch, destBranch);

      if (mergeBranch == null) {
         if (MERGE_DEBUG) {
            System.out.println("Creating a new Merge Branch");
            time = System.currentTimeMillis();
         }
         mergeBranch = BranchCreator.getInstance().createMergeBranch(sourceBranch, destBranch, expectedArtIds);

         mergeBranch.setMergeBranchInfo(sourceBranch, destBranch);
         instance.branchCache.put(mergeBranch.getBranchId(), mergeBranch);

         if (MERGE_DEBUG) {
            System.out.println(String.format("     Branch created in %s", Lib.getElapseString(time)));
         }
      } else {
         if (MERGE_DEBUG) {
            System.out.println("Updating Existing Merge Branch");
            time = System.currentTimeMillis();
         }
         MergeBranchManager.updateMergeBranch(mergeBranch, expectedArtIds, destBranch, sourceBranch);
         if (MERGE_DEBUG) {
            System.out.println(String.format("     Branch updated in %s", Lib.getElapseString(time)));
         }
      }
      return mergeBranch;
   }

   public static Branch getBranch(Integer branchId) throws OseeDataStoreException, BranchDoesNotExist {
      // Always exception for invalid id's, they won't ever be found in the
      // database or cache.
      if (branchId == null) throw new BranchDoesNotExist("Branch Id is null");

      // If someone else made a branch on another machine, we may not know about it
      // so refresh the cache.
      if (!instance.branchCache.containsKey(branchId)) {
         instance.ensurePopulatedCache(true);
      }
      Branch branch = instance.branchCache.get(branchId);

      if (branch == null) {
         throw new BranchDoesNotExist("Branch could not be acquired for branch id: " + branchId);
      }

      return branch;
   }

   /**
    * Delete branch from the system.
    * 
    * @param branch
    */
   public static Job deleteBranch(final Branch branch) {
      return Jobs.startJob(new DeleteBranchJob(branch));
   }

   public static void handleBranchDeletion(int branchId) {
      Branch branch = instance.branchCache.remove(branchId);
      if (branch != null) {
         branch.setDeleted();
      }
   }

   /**
    * Commit the net changes from the source branch into the destination branch. If there are conflicts between the two
    * branches, the source branch changes will override those on the destination branch.
    * 
    * @param conflictManager
    * @param archiveSourceBranch
    * @throws OseeCoreException
    */
   public static void commitBranch(ConflictManagerExternal conflictManager, boolean archiveSourceBranch, boolean overwriteUnresolvedConflicts) throws OseeCoreException {
      if (conflictManager.remainingConflictsExist() && !overwriteUnresolvedConflicts) {
         throw new OseeCoreException("Commit failed due to unresolved conflicts");
      }
      new CommitDbTx(conflictManager, archiveSourceBranch).execute();
   }

   public static boolean isBranchInCommit(Branch branch) {
      return CommitDbTx.isBranchInCommit(branch);
   }

   /**
    * @throws OseeDataStoreException
    */
   int addCommitTransactionToDatabase(Branch toBranch, TransactionId fromTransactionID, User userToBlame) throws OseeDataStoreException {
      int newTransactionNumber = SequenceManager.getNextTransactionId();

      ConnectionHandler.runPreparedUpdate(COMMIT_TRANSACTION, TransactionDetailsType.NonBaselined.getId(),
            toBranch.getBranchId(), newTransactionNumber, "Commit Branch " + fromTransactionID.getTransactionNumber(),
            GlobalTime.GreenwichMeanTimestamp(), (userToBlame == null) ? -1 : userToBlame.getArtId(), -1);

      return newTransactionNumber;
   }

   /**
    * Archives a branch in the database by changing its archived value from 0 to 1.
    */
   public static void archive(Branch branch) throws OseeCoreException {
      ConnectionHandler.runPreparedUpdate(ARCHIVE_BRANCH, branch.getBranchId());
      branch.setArchived(true);
      validateDefaultBranch();
   }

   public static void validateDefaultBranch() throws OseeCoreException {
      Branch defaultBranch = getDefaultBranch();
      if (defaultBranch.isArchived() || defaultBranch.isDeleted()) {
         setDefaultBranch(defaultBranch.hasParentBranch() ? defaultBranch.getParentBranch() : getCommonBranch());
      }
   }

   /**
    * Permanently removes transactions and any of their backing data that is not referenced by any other transactions.
    * 
    * @param transactionIdNumber
    */
   public static void deleteTransactions(final int... transactionIdNumbers) {
      deleteTransactions(null, transactionIdNumbers);
   }

   /**
    * Permanently removes transactions and any of their backing data that is not referenced by any other transactions.
    * 
    * @param transactionIdNumber
    */
   public static void deleteTransactions(IJobChangeListener jobChangeListener, final int... transactionIdNumbers) {
      Jobs.startJob(new DeleteTransactionJob(transactionIdNumbers), jobChangeListener);

   }

   /**
    * Move a transaction to a particular branch. This is simply a database call and should only be used to fix user
    * errors. No internal cached data is updated, nor are any events fired from the modified data so any Skynet sessions
    * reading this data should be restarted to see the changes.
    * 
    * @throws OseeDataStoreException
    */
   public static void moveTransaction(TransactionId transactionId, Branch toBranch) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(UPDATE_TRANSACTION_BRANCH, toBranch.getBranchId(),
            transactionId.getTransactionNumber());
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

   public void updateAssociatedArtifact(Branch branch, Artifact artifact) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(UPDATE_ASSOCIATED_ART_BRANCH, artifact.getArtId(), branch.getBranchId());
   }

   /**
    * Creates a new Branch based on the transaction number selected and the parent branch.
    * 
    * @param parentTransactionId
    * @param childBranchName
    * @throws OseeCoreException
    */
   public static Branch createWorkingBranch(TransactionId parentTransactionId, String childBranchShortName, String childBranchName, Artifact associatedArtifact) throws OseeCoreException {
      return BranchCreator.getInstance().createChildBranch(parentTransactionId, childBranchShortName, childBranchName,
            associatedArtifact, false, null, null);
   }

   /**
    * Creates a new Branch based on the most recent transaction on the parent branch.
    * 
    * @param parentTransactionId
    * @param childBranchName
    * @throws OseeCoreException
    */
   public static Branch createWorkingBranch(Branch parentBranch, String childBranchShortName, String childBranchName, Artifact associatedArtifact) throws OseeCoreException {
      TransactionId parentTransactionId = TransactionIdManager.getlatestTransactionForBranch(parentBranch);
      return BranchCreator.getInstance().createChildBranch(parentTransactionId, childBranchShortName, childBranchName,
            associatedArtifact, false, null, null);
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
    * @param initializeArtifacts adds common artifacts needed by most normal root branches
    * @throws Exception
    * @see BranchManager#intializeBranch
    * @see MasterSkynetTypesImport#importSkynetDbTypes
    * @see BranchManager#getKeyedBranch(String)
    */
   public static Branch createRootBranch(String shortBranchName, String branchName, String staticBranchName, Collection<String> skynetTypesImportExtensionsIds, boolean initializeArtifacts) throws OseeCoreException {
      // Create branch with name and static name; short name will be computed from full name

      Branch systemRootBranch = BranchManager.getSystemRootBranch();

      Branch branch =
            HttpBranchCreation.createRootBranch(null, branchName, staticBranchName, systemRootBranch.getBranchId(),
                  systemRootBranch.getParentTransactionId(), false);
      // Add name to cached keyname if static branch name is desired
      if (staticBranchName != null) {
         KeyedBranchCache.createKeyedBranch(staticBranchName, branch);
      }
      // Re-init factory cache
      ArtifactFactoryManager.refreshCache();
      // Import skynet types if specified
      if (skynetTypesImportExtensionsIds != null && skynetTypesImportExtensionsIds.size() > 0) {
         MasterSkynetTypesImport.importSkynetDbTypes(skynetTypesImportExtensionsIds, branch);
      }
      // Initialize branch with common artifacts
      if (initializeArtifacts) {
         RootBranchInitializer rootInitializer = new RootBranchInitializer();
         rootInitializer.initialize(branch);
      }
      return branch;
   }

   public static Branch createSystemRootBranch() throws OseeCoreException {
      return HttpBranchCreation.createRootBranch(null, "System Root Branch", null, 1, 1, true);
   }

   public static List<Branch> getTopLevelBranches() throws OseeCoreException {
      return getBranches(BranchState.ACTIVE, BranchControlled.ALL, BranchType.TOP_LEVEL);
   }

   public static List<Branch> getChangeManagedBranches() throws OseeCoreException {
      return getBranches(BranchState.ACTIVE, BranchControlled.CHANGE_MANAGED, BranchType.WORKING, BranchType.TOP_LEVEL,
            BranchType.BASELINE);
   }

   public static void setDefaultBranch(Branch branch) throws OseeCoreException {
      if (branch == null) throw new OseeArgumentException("The branch argument can not be null");

      if (branch != instance.defaultBranch.get()) {
         instance.defaultBranch.set(branch);
         UserManager.getUser().setSetting(LAST_DEFAULT_BRANCH, String.valueOf(branch.getBranchId()));
         try {
            OseeEventManager.kickBranchEvent(instance, BranchEventType.DefaultBranchChanged, branch.getBranchId());
         } catch (Exception ex) {
            OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
         }
      }
   }

   private static final String SELECT_MAPPED_ID =
         "SELECT mapped_id FROM osee_import_map oim, osee_import_index_map oiim WHERE oim.sequence_id = oiim.sequence_id AND oiim.sequence_id = oiim.sequence_id AND oim.sequence_name = 'SKYNET_BRANCH_ID_SEQ' AND oiim.original_id = ?";
   private final WindowLocal<Branch> defaultBranch = new WindowLocal<Branch>() {
      @Override
      protected Branch initialValue() {
         Branch initialBranch = null;

         try {
            String branchIdStr = UserManager.getUser().getSetting(LAST_DEFAULT_BRANCH);
            if (branchIdStr == null) {
               int branchId = preferenceStore.getInt(LAST_DEFAULT_BRANCH);
               if (branchId > 0) {
                  branchId = ConnectionHandler.runPreparedQueryFetchInt(0, SELECT_MAPPED_ID, branchId);
                  if (branchId > 0) {
                     initialBranch = getBranch(branchId);
                     UserManager.getUser().setSetting(LAST_DEFAULT_BRANCH, String.valueOf(branchId));
                  }
               }
            } else {
               initialBranch = getBranch(Integer.parseInt(branchIdStr));
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
         }

         if (initialBranch == null) {
            try {
               initialBranch = getDefaultInitialBranch();
               UserManager.getUser().setSetting(LAST_DEFAULT_BRANCH, String.valueOf(initialBranch.getBranchId()));
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }

         return initialBranch;
      }
   };

   private Branch getDefaultInitialBranch() throws OseeCoreException {
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
                  OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
               }
            }
         }
      }

      for (IDefaultInitialBranchesProvider provider : defaultBranchProviders) {
         try {
            // Guard against problematic extensions
            for (Branch branch : provider.getDefaultInitialBranches()) {
               if (branch != null) {
                  return branch;
               }
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetActivator.class, Level.WARNING,
                  "Exception occurred while trying to determine initial default branch", ex);
         }
      }

      return getCommonBranch();
   }

   public static Branch getDefaultBranch() {
      return instance.defaultBranch.get();
   }

   /**
    * @return the rootBranch
    * @throws OseeCoreException
    */
   public static Branch getSystemRootBranch() throws OseeCoreException {
      instance.ensurePopulatedCache(false);
      return instance.systemRoot;
   }

   public static int getBranchTransaction(Date date, int branchId) throws OseeCoreException {
      int transactionId = -1;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      if (date == null) {
         throw new OseeCoreException("Must select a valid Date");
      }
      try {
         chStmt.runPreparedQuery(SELECT_BRANCH_TRANSACTION, branchId, new Timestamp(date.getTime()));

         if (chStmt.next()) {
            transactionId = chStmt.getInt("transaction_id");
         }
      } finally {
         chStmt.close();
      }
      return transactionId;
   }
}