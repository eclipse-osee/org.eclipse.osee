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
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.core.BranchType;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.dbinit.MasterSkynetTypesImport;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionDetailsType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.WindowLocal;

public class BranchPersistenceManager {
   private static final String READ_BRANCH_TABLE =
         "SELECT * FROM osee_branch br1, osee_tx_details txd1 WHERE br1.branch_id = txd1.branch_id AND txd1.tx_type=" + TransactionDetailsType.Baselined.getId();
   private static final String READ_MERGE_BRANCHES =
         "select * from osee_branch b1, osee_merge m2, osee_tx_details t2 where b1.branch_id = m2.merge_branch_id and t2.branch_id = b1.branch_id and t2.tx_type = 1";
   private static final String COMMIT_TRANSACTION =
         "INSERT INTO " + TRANSACTION_DETAIL_TABLE.columnsForInsert("tx_type", "branch_id", "transaction_id",
               TXD_COMMENT, "time", "author", "commit_art_id");

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
   //This hash is keyed in the source branch id and destination branch id
   private final DoubleKeyHashMap<Integer, Integer, Branch> mergeBranchCache =
         new DoubleKeyHashMap<Integer, Integer, Branch>();

   public static final String COMMIT_COMMENT = "Commit Branch ";

   private static final boolean MERGE_DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Merge"));
   private static final boolean PERSISTENCE_DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Persistence"));

   private static final BranchPersistenceManager instance = new BranchPersistenceManager();

   private BranchPersistenceManager() {
   }

   public static BranchPersistenceManager getInstance() {
      return instance;
   }

   public static Set<Branch> getAssociatedArtifactBranches(Artifact associatedArtifact) throws OseeDataStoreException {
      instance.ensurePopulatedCache(false);
      Set<Branch> branches = new HashSet<Branch>();
      for (Branch branch : getBranches())
         if (branch.isAssociatedToArtifact(associatedArtifact)) {
            branches.add(branch);
         }
      return branches;
   }

   public static Branch getCommonBranch() throws OseeDataStoreException {
      try {
         return getKeyedBranch(Branch.COMMON_BRANCH_CONFIG_ID);
      } catch (BranchDoesNotExist ex) {
         throw new OseeDataStoreException("The common branch is required to exist but: " + ex);
      }
   }

   public static Branch getKeyedBranch(String keyname) throws OseeDataStoreException, BranchDoesNotExist {
      return KeyedBranchCache.getKeyedBranch(keyname);
   }

   public static Branch getAtsBranch() throws OseeDataStoreException {
      return getCommonBranch();
   }

   public static List<Branch> getBranches() throws OseeDataStoreException {
      instance.ensurePopulatedCache(false);
      List<Branch> branches = new ArrayList<Branch>(instance.branchCache.values());
      Collections.sort(branches);
      return branches;
   }

   public static Collection<Branch> refreshBranches() throws OseeDataStoreException {
      instance.ensurePopulatedCache(true);
      return getBranches();
   }

   public static Branch getBranch(String branchName) throws OseeDataStoreException, BranchDoesNotExist {
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

         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt = ConnectionHandler.runPreparedQuery(500, READ_BRANCH_TABLE);
            while (chStmt.next()) {
               int branchId = chStmt.getInt("branch_id");
               boolean isArchived = chStmt.getInt("archived") == 1;

               Branch branch = branchCache.get(branchId);

               if (isArchived) {
                  if (branch != null) {
                     branchCache.remove(branch.getBranchId());
                  }
               } else {
                  if (branch == null) {
                     branch = initializeBranchObject(chStmt);
                  } else {
                     branch.setBranchName(chStmt.getString("branch_name"));
                  }
               }
            }
         } finally {
            ConnectionHandler.close(chStmt);
         }
      }
   }

   public static Collection<Branch> getArchivedBranches() throws OseeDataStoreException {
      Collection<Branch> archivedBranches = new ArrayList<Branch>(100);
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(500, READ_BRANCH_TABLE);
         while (chStmt.next()) {
            if (chStmt.getInt("archived") == 1) {
               archivedBranches.add(initializeBranchObject(chStmt));
            }
         }
      } finally {
         ConnectionHandler.close(chStmt);
      }
      return archivedBranches;
   }

   /**
    * deletes (permanently removes from the datastore) each archived branch one at a time using sequential jobs
    * 
    * @throws InterruptedException
    */
   public static void deleteArchivedBranches() throws OseeDataStoreException, InterruptedException {
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
    * @throws OseeDataStoreException
    */
   private static Branch initializeBranchObject(ConnectionHandlerStatement chStmt) throws OseeDataStoreException {
      int branchId = chStmt.getInt("branch_id");
      int associatedArtifactId = chStmt.getInt("associated_art_id");

      return new Branch(chStmt.getString("short_name"), chStmt.getString("branch_name"), branchId,
            chStmt.getInt("parent_branch_id"), false, chStmt.getInt("author"), chStmt.getTimestamp("time"),
            chStmt.getString(TXD_COMMENT), associatedArtifactId, BranchType.getBranchType(new Integer(
                  chStmt.getInt("branch_type"))));
   }

   /**
    * Calls the getMergeBranch method and if it returns null it will create a new merge branch based on the artIds from
    * the source branch.
    */
   public static Branch getOrCreateMergeBranch(Branch sourceBranch, Branch destBranch, ArrayList<Integer> expectedArtIds) throws OseeCoreException {
      long time = 0;
      Branch mergeBranch = getMergeBranch(sourceBranch.getBranchId(), destBranch.getBranchId());

      if (mergeBranch == null) {
         if (MERGE_DEBUG) {
            System.out.println("Creating a new Merge Branch");
            time = System.currentTimeMillis();
         }
         mergeBranch = BranchCreator.getInstance().createMergeBranch(sourceBranch, destBranch, expectedArtIds);
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

   /**
    * Checks the merge branch cache for the branch if it does not find it then it will query the database for the
    * branch.
    */
   public static Branch getMergeBranch(Integer sourceBranchId, Integer destBranchId) throws OseeCoreException {
      if (sourceBranchId < 1 || destBranchId < 1) {
         throw new IllegalArgumentException(
               "Branch ids are invalid source branch id:" + sourceBranchId + " destination branch id:" + destBranchId);
      }

      if (!instance.mergeBranchCache.containsKey(sourceBranchId, destBranchId)) {
         instance.ensureMergePopulatedCache(true);
      }

      Branch mergeBranch = instance.mergeBranchCache.get(sourceBranchId, destBranchId);
      return mergeBranch;
   }

   private synchronized void ensureMergePopulatedCache(boolean forceRead) throws OseeDataStoreException {
      if (forceRead || mergeBranchCache.isEmpty()) {
         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt = ConnectionHandler.runPreparedQuery(500, READ_MERGE_BRANCHES);
            while (chStmt.next()) {
               int sourceBranchId = chStmt.getInt("source_branch_id");
               int destBranchId = chStmt.getInt("dest_branch_id");
               boolean isArchived = chStmt.getInt("archived") == 1;

               Branch branch = mergeBranchCache.get(sourceBranchId, destBranchId);

               if (isArchived) {
                  if (branch != null) {
                     mergeBranchCache.remove(sourceBranchId, destBranchId);
                  }
               } else {
                  if (branch == null) {
                     branch = initializeBranchObject(chStmt);
                     mergeBranchCache.put(sourceBranchId, destBranchId, branch);
                  }
               }
            }
         } finally {
            ConnectionHandler.close(chStmt);
         }
      }
   }

   public static Branch getBranch(Integer branchId) throws BranchDoesNotExist, OseeDataStoreException {
      // Always exception for invalid id's, they won't ever be found in the
      // database or cache.
      if (branchId == null) throw new BranchDoesNotExist("Branch Id is null");
      if (branchId < 1) throw new BranchDoesNotExist("Branch Id " + branchId + " is invalid");

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

   public static void removeBranchFromCache(int branchId) {
      instance.branchCache.remove(branchId);
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
    * @throws IllegalArgumentException
    */
   public static Job commitBranch(final Branch childBranch, final boolean archiveChildBranch, final boolean forceCommit) throws OseeCoreException {
      Branch parentBranch = childBranch.getParentBranch();

      return commitBranch(childBranch, parentBranch, archiveChildBranch, forceCommit);
   }

   /**
    * Commit the net changes from the fromBranch into the toBranch. If there are conflicts between the two branches, the
    * fromBranch's changes override those on the toBranch if overrideConflicts is true otherwise a
    * CommitConflictException is thrown.
    * 
    * @throws CommitConflictException
    * @throws IllegalArgumentException
    */
   public static Job commitBranch(final Branch fromBranch, final Branch toBranch, boolean archiveFromBranch, boolean forceCommit) throws OseeCoreException {
      CommitJob commitJob = new CommitJob(toBranch, fromBranch, archiveFromBranch, forceCommit);
      Jobs.startJob(commitJob);
      return commitJob;
   }

   /**
    * Creates a working branch from the net changes of the fromBranch onto the toBranch
    * 
    * @throws OseeCoreException
    */
   public Branch createWorkingBranchFromBranchChanges(final Branch fromBranch, final Branch toBranch, Artifact associatedArtifact) throws OseeCoreException {
      return createWorkingBranchFromBranchData(fromBranch, null, toBranch, associatedArtifact);
   }

   /**
    * Creates a working branch from the net changes of the fromTransaction onto the toBranch
    * 
    * @throws OseeCoreException
    */
   public Branch createWorkingBranchFromBranchChanges(TransactionId fromTransactionId, final Branch toBranch, Artifact associatedArtifact) throws OseeCoreException {
      return createWorkingBranchFromBranchData(null, fromTransactionId, toBranch, associatedArtifact);
   }

   private Branch createWorkingBranchFromBranchData(final Branch fromBranch, TransactionId fromTransactionId, final Branch toBranch, Artifact associatedArtifact) throws OseeCoreException {
      String toBranchName;

      if (fromTransactionId == null) {
         fromTransactionId =
               TransactionIdManager.getTransactionId(TransactionIdManager.getParentBaseTransactionNumber(fromBranch.getCreationComment()));
         toBranchName = fromBranch.getBranchName() + " Copy " + GlobalTime.GreenwichMeanTimestamp();
      } else {
         toBranchName = fromTransactionId.getBranch().getBranchName() + " Copy " + GlobalTime.GreenwichMeanTimestamp();
      }

      createWorkingBranch(fromTransactionId, toBranchName, toBranchName, associatedArtifact);
      return getBranch(toBranchName);
   }

   /**
    * @throws OseeDataStoreException
    */
   static int addCommitTransactionToDatabase(OseeConnection connection, Branch parentBranch, Branch childBranch, User userToBlame) throws OseeDataStoreException {
      int newTransactionNumber = SequenceManager.getNextTransactionId();

      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      String comment = COMMIT_COMMENT + childBranch.getBranchName();
      int authorId = (userToBlame == null) ? -1 : userToBlame.getArtId();
      ConnectionHandler.runPreparedUpdate(connection, COMMIT_TRANSACTION, TransactionDetailsType.NonBaselined.getId(),
            parentBranch.getBranchId(), newTransactionNumber, comment, timestamp, authorId,
            childBranch.getAssociatedArtifactId());
      // Update commit artifact cache with new information
      if (childBranch.getAssociatedArtifactId() > 0) {
         RevisionManager.getInstance().cacheTransactionDataPerCommitArtifact(childBranch.getAssociatedArtifactId(),
               newTransactionNumber);
      }

      return newTransactionNumber;
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
   public static void archive(Branch branch) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(ARCHIVE_BRANCH, branch.getBranchId());

      branch.setArchived();
      instance.branchCache.remove(branch.getBranchId());

      setDefaultBranch(branch.getParentBranchOrAlternative(getCommonBranch()));
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
   public static Branch createWorkingBranch(final TransactionId parentTransactionId, final String childBranchShortName, final String childBranchName, final Artifact associatedArtifact) throws OseeCoreException {
      return BranchCreator.getInstance().createChildBranch(parentTransactionId, childBranchShortName, childBranchName,
            associatedArtifact, false, null, null);
   }

   private Set<Integer> getSubtypeDescriptors(String[] artTypeNames) throws OseeCoreException {
      Set<Integer> artifactTypeIds;
      if (artTypeNames == null) {
         artifactTypeIds = new HashSet<Integer>(0);
      } else {
         artifactTypeIds = new HashSet<Integer>(artTypeNames.length);
         for (String typeName : artTypeNames) {
            artifactTypeIds.add(ArtifactTypeManager.getType(typeName) != null ? ArtifactTypeManager.getType(typeName).getArtTypeId() : -1);
         }
      }
      return artifactTypeIds;
   }

   /**
    * Creates a new Branch with a mix of compressed and uncompressed data.
    * 
    * @return The created Branch
    */

   public static Branch createBranchWithFiltering(TransactionId parentTransactionId, String childBranchShortName, String childBranchName, Artifact associatedArtifact, String[] compressArtTypeNames, String[] preserveArtTypeNames) throws Exception {
      Set<Integer> compressArtTypeIds = instance.getSubtypeDescriptors(compressArtTypeNames);
      Set<Integer> preserveArtTypeIds = instance.getSubtypeDescriptors(preserveArtTypeNames);

      return BranchCreator.getInstance().createChildBranch(parentTransactionId, childBranchShortName, childBranchName,
            associatedArtifact, true, compressArtTypeIds, preserveArtTypeIds);
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
    * @see BranchPersistenceManager#intializeBranch
    * @see MasterSkynetTypesImport#importSkynetDbTypes
    * @see BranchPersistenceManager#getKeyedBranch(String)
    */
   public static Branch createRootBranch(String shortBranchName, String branchName, String staticBranchName, Collection<String> skynetTypesImportExtensionsIds, boolean initializeArtifacts) throws OseeCoreException {
      // Create branch with name and static name; short name will be computed from full name
      Branch branch = BranchCreator.getInstance().createRootBranch(null, branchName, staticBranchName);
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

   public static List<Branch> getRootBranches() throws OseeDataStoreException {
      List<Branch> branches = new ArrayList<Branch>();
      for (Branch branch : getBranches()) {
         if (!branch.hasParentBranch()) {
            branches.add(branch);
         }
      }
      return branches;
   }

   public static List<Branch> getChangeManagedBranches() throws OseeDataStoreException {
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
   protected static void cache(Branch branch) {
      instance.branchCache.put(branch.getBranchId(), branch);
   }

   public static void setDefaultBranch(Branch branch) {
      if (branch == null) throw new IllegalArgumentException("The branch argument can not be null");

      if (branch != instance.defaultBranch.get()) {
         instance.defaultBranch.set(branch);
         preferenceStore.setValue(LAST_DEFAULT_BRANCH, getDefaultBranch().getBranchId());
         try {
            OseeEventManager.kickBranchEvent(instance, BranchEventType.DefaultBranchChanged, branch.getBranchId());
         } catch (Exception ex) {
            SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
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
                  OseeLog.log(SkynetActivator.class, Level.WARNING,
                        "Could not use default branch id from the preference store: " + ex);
                  initialBranch = getCommonBranch();
                  preferenceStore.setValue(LAST_DEFAULT_BRANCH, initialBranch.getBranchId());
               } catch (OseeDataStoreException ex1) {
                  OseeLog.log(SkynetActivator.class, Level.SEVERE, ex1);
               }
            } catch (OseeDataStoreException ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }

         if (initialBranch == null) {
            try {
               initialBranch = getDefaultInitialBranch();
            } catch (OseeDataStoreException ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }

         return initialBranch;
      }
   };

   private Branch getDefaultInitialBranch() throws OseeDataStoreException {
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
            OseeLog.log(SkynetActivator.class, Level.WARNING,
                  "Exception occurred while trying to determine initial default branch", ex);
         }
      }

      return getCommonBranch();
   }

   public static Branch getDefaultBranch() {
      return instance.defaultBranch.get();
   }
}