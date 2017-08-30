/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.commit.CommitStatus;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.core.config.Versions;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G Dunne
 */
public abstract class AbstractAtsBranchService implements IAtsBranchService {

   protected static Map<String, IOseeBranch> idToWorkingBranchCache = new HashMap<>();
   protected static Map<String, Long> idToWorkingBranchCacheUpdated = new HashMap<>(50);
   protected IAtsServices services;
   private static final int SHORT_NAME_LIMIT = 35;
   private static Set<BranchId> branchesInCommit = new HashSet<>();
   private ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy;

   public AbstractAtsBranchService() {
   }

   public AbstractAtsBranchService(IAtsServices services, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      this.services = services;
      this.teamWorkflowProvidersLazy = teamWorkflowProvidersLazy;
   }

   /**
    * Returns true if there was ever a commit of a working branch regardless of whether the working branch is archived
    * or not.
    */
   @Override
   public boolean isWorkingBranchEverCommitted(IAtsTeamWorkflow teamWf) {
      return getBranchesCommittedTo(teamWf).size() > 0;
   }

   @Override
   public Collection<BranchId> getBranchesToCommitTo(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      Set<BranchId> branches = new HashSet<>();
      for (ICommitConfigItem obj : getConfigArtifactsConfiguredToCommitTo(teamWf)) {
         if (isBranchValid(obj)) {
            branches.add(getBranch(obj));
         }
      }
      return branches;
   }

   @Override
   public IOseeBranch getWorkingBranch(IAtsTeamWorkflow teamWf, boolean force) {
      long now = new Date().getTime();
      boolean notSet = idToWorkingBranchCacheUpdated.get(teamWf.getAtsId()) == null;
      if (AtsUtilCore.isInTest() || notSet || force || now - idToWorkingBranchCacheUpdated.get(
         teamWf.getAtsId()) > 1000) {
         IOseeBranch branch = IOseeBranch.SENTINEL;
         try {
            IOseeBranch workingBranch = getWorkingBranchExcludeStates(teamWf, BranchState.REBASELINED,
               BranchState.DELETED, BranchState.PURGED, BranchState.COMMIT_IN_PROGRESS,
               BranchState.CREATION_IN_PROGRESS, BranchState.DELETE_IN_PROGRESS, BranchState.PURGE_IN_PROGRESS);
            branch = workingBranch == null ? IOseeBranch.SENTINEL : workingBranch;
         } catch (ItemDoesNotExist ex) {
            // do nothing
         }
         idToWorkingBranchCache.put(teamWf.getAtsId(), branch);
         idToWorkingBranchCacheUpdated.put(teamWf.getAtsId(), now);
      }
      return idToWorkingBranchCache.get(teamWf.getAtsId());
   }

   @Override
   public BranchId getConfiguredBranchForWorkflow(IAtsTeamWorkflow teamWf) {
      BranchId parentBranch = BranchId.SENTINEL;

      // Check for parent branch id in Version artifact
      if (teamWf.getTeamDefinition().isTeamUsesVersions()) {
         IAtsVersion verArt = services.getVersionService().getTargetedVersion(teamWf);
         if (verArt != null) {
            parentBranch = getBranch((IAtsConfigObject) verArt);
         }
      }

      // If not defined in version, check for parent branch from team definition
      if (parentBranch.isInvalid() && teamWf.isTeamWorkflow() && isBranchValid(teamWf.getTeamDefinition())) {
         parentBranch = getBranch((IAtsConfigObject) teamWf.getTeamDefinition());
      }

      // If not defined, return SENTINEL
      return parentBranch;
   }

   @Override
   public CommitStatus getCommitStatus(IAtsTeamWorkflow teamWf, BranchId destinationBranch) {
      CommitStatus commitStatus = getCommitStatus(teamWf, destinationBranch, null);
      return commitStatus;
   }

   @Override
   public TransactionRecord getCommitTransactionRecord(IAtsTeamWorkflow teamWf, BranchId branch) {
      if (branch.isInvalid()) {
         return TransactionRecord.SENTINEL;
      }

      Collection<TransactionRecord> transactions = getCommittedArtifactTransactionIds(teamWf);
      for (TransactionRecord transId : transactions) {
         if (transId.isOnBranch(branch)) {
            return transId;
         }
      }
      return TransactionRecord.SENTINEL;
   }

   @Override
   public ICommitConfigItem getParentBranchConfigArtifactConfiguredToCommitTo(IAtsTeamWorkflow teamWf) {
      if (teamWf.getTeamDefinition().isTeamUsesVersions()) {
         if (services.getVersionService().hasTargetedVersion(teamWf)) {
            return services.getVersionService().getTargetedVersion(teamWf);
         }
      } else {
         if (teamWf.isTeamWorkflow() && isBranchValid(teamWf.getTeamDefinition())) {
            return teamWf.getTeamDefinition();
         }
      }
      return null;
   }

   @Override
   public TransactionToken getEarliestTransactionId(IAtsTeamWorkflow teamWf) {
      Collection<? extends TransactionToken> transactionIds = getTransactionIds(teamWf, false);
      TransactionToken earliestTransactionId;
      if (transactionIds.isEmpty()) {
         earliestTransactionId = TransactionToken.SENTINEL;
      } else {
         earliestTransactionId = transactionIds.iterator().next();
         for (TransactionToken transactionId : transactionIds) {
            if (transactionId.isOlderThan(earliestTransactionId)) {
               earliestTransactionId = transactionId;
            }
         }
      }
      return earliestTransactionId;
   }

   @Override
   public boolean isBranchesAllCommittedExcept(IAtsTeamWorkflow teamWf, BranchId branchToExclude) {
      Collection<BranchId> committedTo = getBranchesCommittedTo(teamWf);
      for (BranchId destBranch : getBranchesToCommitTo(teamWf)) {
         if (destBranch.notEqual(
            branchToExclude) && !committedTo.contains(destBranch) && !isNoCommitNeeded(teamWf, destBranch)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public Collection<BranchId> getBranchesCommittedTo(IAtsTeamWorkflow teamWf) {
      Set<BranchId> branches = new HashSet<>();
      for (TransactionToken transId : getTransactionIds(teamWf, false)) {
         branches.add(transId.getBranch());
      }
      return branches;
   }

   @Override
   public Collection<BranchId> getBranchesLeftToCommit(IAtsTeamWorkflow teamWf) {
      Set<BranchId> branchesLeft = new HashSet<>();
      Collection<BranchId> committedTo = getBranchesCommittedTo(teamWf);
      for (BranchId branchToCommit : getBranchesToCommitTo(teamWf)) {
         if (!committedTo.contains(branchToCommit) && !isNoCommitNeeded(teamWf, branchToCommit)) {
            branchesLeft.add(branchToCommit);
         }
      }
      return branchesLeft;
   }

   @Override
   public CommitStatus getCommitStatus(IAtsTeamWorkflow teamWf, ICommitConfigItem configArt) {
      return getCommitStatus((IAtsTeamWorkflow) teamWf.getStoreObject(), getBranch(configArt), null);
   }

   @Override
   public boolean isBranchInCommit(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      if (!isWorkingBranchInWork(teamWf)) {
         return false;
      }
      return branchesInCommit.contains(getWorkingBranch(teamWf));
   }

   @Override
   public Collection<ICommitConfigItem> getConfigArtifactsConfiguredToCommitTo(IAtsTeamWorkflow teamWf) {
      Set<ICommitConfigItem> configObjects = new HashSet<>();
      if (teamWf.getTeamDefinition().isTeamUsesVersions()) {
         if (services.getVersionService().hasTargetedVersion(teamWf)) {
            Versions.getParallelVersions(services.getVersionService().getTargetedVersion(teamWf), configObjects,
               services);
         }
      } else {
         if (teamWf.isTeamWorkflow() && isBranchValid(teamWf.getTeamDefinition())) {
            configObjects.add(teamWf.getTeamDefinition());
         }
      }
      return configObjects;
   }

   @Override
   public Collection<TransactionRecord> getCommitTransactionsToUnarchivedBaselineBranchs(IAtsTeamWorkflow teamWf) {
      Collection<TransactionRecord> transactionIds = new ArrayList<>();
      for (TransactionRecord transactionId : getCommittedArtifactTransactionIds(teamWf)) {
         // exclude working branches including branch states that are re-baselined
         BranchId branch = transactionId.getBranch();
         if (getBranchType(branch).isBaselineBranch() && !isArchived(branch)) {
            transactionIds.add(transactionId);
         }
      }
      return transactionIds;
   }

   public boolean isNoCommitNeeded(IAtsTeamWorkflow teamWf, BranchId destinationBranch) throws OseeCoreException {
      return getCommitStatus(teamWf, destinationBranch) == CommitStatus.No_Commit_Needed;
   }

   /**
    * Return true if all commit destination branches are configured and have been committed to
    */
   @Override
   public boolean isBranchesAllCommitted(IAtsTeamWorkflow teamWf) {
      Collection<BranchId> committedTo = getBranchesCommittedTo(teamWf);
      for (BranchId destBranch : getBranchesToCommitTo(teamWf)) {
         if (!committedTo.contains(destBranch) && !isNoCommitNeeded(teamWf, destBranch)) {
            return false;
         }
      }
      return true;
   }

   /**
    * Return working branch associated with SMA whether it is committed or not; This data is cached across all workflows
    * with the cache being updated by local and remote events.
    */
   @Override
   public IOseeBranch getWorkingBranch(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return getWorkingBranch(teamWf, false);
   }

   /**
    * @return true if at least one destination branch committed to
    */
   @Override
   public boolean isCommittedBranchExists(IAtsTeamWorkflow teamWf) {
      return !getBranchesCommittedTo(teamWf).isEmpty();
   }

   /**
    * @return false if any object in parallel configuration is not configured with a valid branch
    */
   @Override
   public boolean isAllObjectsToCommitToConfigured(IAtsTeamWorkflow teamWf) {
      Collection<ICommitConfigItem> configs = getConfigArtifactsConfiguredToCommitTo(teamWf);
      for (ICommitConfigItem config : configs) {
         if (!isBranchValid(config)) {
            return false;
         }
         if (!branchExists(config.getBaselineBranchId())) {
            return false;
         }
      }
      return true;
   }

   @Override
   public String getBranchShortName(ICommitConfigItem commitConfigItem) {
      return Strings.truncate(commitConfigItem.getName(), SHORT_NAME_LIMIT);
   }

   @Override
   public boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      BranchId branch = getWorkingBranch(teamWf);
      return branch.isValid() && !getBranchState(branch).isCommitted();
   }

   @Override
   public BranchId getBranch(IAtsConfigObject configObject) {
      BranchId branch = BranchId.SENTINEL;
      if (configObject instanceof IAtsVersion) {
         IAtsVersion version = (IAtsVersion) configObject;
         if (version.getBaselineBranchId().isValid()) {
            branch = version.getBaselineBranchId();
         }
      }
      if (branch.isInvalid() && configObject instanceof IAtsTeamDefinition) {
         IAtsTeamDefinition teamDef = (IAtsTeamDefinition) configObject;
         if (teamDef.getBaselineBranchId().isValid()) {
            branch = teamDef.getBaselineBranchId();
         }
      }
      if (branch.isInvalid()) {
         branch = BranchId.valueOf(services.getAttributeResolver().getSoleAttributeValue(configObject,
            AtsAttributeTypes.BaselineBranchUuid, BranchId.SENTINEL.getId()));
      }
      return branch;
   }

   /**
    * Return working branch associated with SMA whether it is committed or not; This data is cached across all workflows
    * with the cache being updated by local and remote events.
    */
   @Override
   public IOseeBranch getBranch(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return getWorkingBranch(teamWf, false);
   }

   @Override
   public BranchId getBranch(ICommitConfigItem configObject) {
      return getBranch((IAtsConfigObject) configObject);
   }

   @Override
   public boolean isBranchValid(ICommitConfigItem configObject) {
      boolean validBranch = false;
      if (configObject.getBaselineBranchId().isValid()) {
         validBranch = true;
      }
      return validBranch;
   }

   /**
    * This method was refactored from above so it could be tested independently
    */
   @Override
   public Collection<Object> combineCommitTransactionsAndConfigItems(Collection<ICommitConfigItem> configArtSet, Collection<TransactionRecord> commitTxs) throws OseeCoreException {
      // commitMgrInputObjs will hold a union of all commits from configArtSet and commitTxs.
      // - first, we addAll configArtSet
      // - next, we loop through commitTxs and for any tx that has the same branch as ANY pre-existing commit
      //    in configArtSet we do NOT add it to commitMgrInputObjs.
      Collection<Object> commitMgrInputObjs = new HashSet<>();
      commitMgrInputObjs.addAll(configArtSet);
      //for each tx commit...
      for (TransactionToken txRecord : commitTxs) {
         boolean isCommitAlreadyPresent = false;
         // ... compare the branch of the tx commit to all the parent branches in configArtSet and do NOT add the tx
         // commit if it is already represented.
         for (ICommitConfigItem configArt : configArtSet) {
            BranchId configArtBranch = getBranch(configArt);
            if (txRecord.isOnBranch(configArtBranch)) {
               isCommitAlreadyPresent = true;
               break;
            }
         }
         if (!isCommitAlreadyPresent) {
            commitMgrInputObjs.add(txRecord);
         }
      }
      return commitMgrInputObjs;
   }

   /**
    * @return Logically combines the results from getConfigArtifactsConfiguredToCommitTo() and
    * getCommitTransactionsToUnarchivedBaselineBranchs() into a single Collection of Objects. Objects are selected from
    * getConfigArtifactsConfiguredToCommitTo() first. Then compared to the branches in the Collection of TxRecords from
    * getCommitTransactionsToUnarchivedBaselineBranchs(). The TxRecords take LESS priority than the ICommitConfigArts
    * from getConfigArtifactsConfiguredToCommitTo()
    */
   @Override
   public Collection<Object> getCommitTransactionsAndConfigItemsForTeamWf(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      Collection<ICommitConfigItem> configArtSet = getConfigArtifactsConfiguredToCommitTo(teamWf);
      Collection<TransactionRecord> commitTxs = getCommitTransactionsToUnarchivedBaselineBranchs(teamWf);
      Collection<Object> commitMgrInputObjs = combineCommitTransactionsAndConfigItems(configArtSet, commitTxs);
      return commitMgrInputObjs;
   }

   @Override
   public Result isCommitBranchAllowed(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      if (!teamWf.isTeamWorkflow()) {
         return Result.FalseResult;
      }
      if (teamWf.getTeamDefinition().isTeamUsesVersions()) {
         IAtsVersionService versionService = services.getVersionService();
         if (!versionService.hasTargetedVersion(teamWf)) {
            return new Result(false, "Workflow not targeted for Version");
         }
         IAtsVersion targetedVersion = versionService.getTargetedVersion(teamWf);
         Result result = targetedVersion.isAllowCommitBranchInherited();
         if (result.isFalse()) {
            return result;
         }

         if (!isBranchValid(targetedVersion)) {
            return new Result(false, "Parent Branch not configured for Version [" + targetedVersion + "]");
         }
         return Result.TrueResult;

      } else {
         Result result = teamWf.getTeamDefinition().isAllowCommitBranchInherited();
         if (result.isFalse()) {
            return result;
         }

         if (!isBranchValid(teamWf.getTeamDefinition())) {
            return new Result(false,
               "Parent Branch not configured for Team Definition [" + teamWf.getTeamDefinition() + "]");
         }
         return Result.TrueResult;
      }
   }

   @Override
   public Result isCreateBranchAllowed(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      if (!teamWf.isTeamWorkflow()) {
         return Result.FalseResult;
      }

      if (teamWf.getTeamDefinition().isTeamUsesVersions()) {
         IAtsVersionService versionService = services.getVersionService();
         if (!versionService.hasTargetedVersion(teamWf)) {
            return new Result(false, "Workflow not targeted for Version");
         }
         IAtsVersion targetedVersion = versionService.getTargetedVersion(teamWf);
         Result result = targetedVersion.isAllowCreateBranchInherited();
         if (result.isFalse()) {
            return result;
         }

         if (!isBranchValid(targetedVersion)) {
            return new Result(false, "Parent Branch not configured for Version [" + targetedVersion + "]");
         }
         BranchId baselineBranch = getBranch((IAtsConfigObject) targetedVersion);
         if (!getBranchType(baselineBranch).isBaselineBranch()) {
            return new Result(false, "Parent Branch must be of Baseline branch type.  See Admin for configuration.");
         }
         return Result.TrueResult;

      } else {
         Result result = teamWf.getTeamDefinition().isAllowCreateBranchInherited();
         if (result.isFalse()) {
            return result;
         }

         if (!isBranchValid(teamWf.getTeamDefinition())) {
            return new Result(false,
               "Parent Branch not configured for Team Definition [" + teamWf.getTeamDefinition() + "]");
         }
         BranchId baselineBranch = getBranch((IAtsConfigObject) teamWf.getTeamDefinition());
         if (!getBranchType(baselineBranch).isBaselineBranch()) {
            return new Result(false, "Parent Branch must be of Baseline branch type.  See Admin for configuration.");
         }
         return Result.TrueResult;
      }
   }

   @Override
   public CommitStatus getCommitStatus(IAtsTeamWorkflow teamWf, BranchId destinationBranch, ICommitConfigItem configArt) {
      BranchId workingBranch = getWorkingBranch(teamWf);
      if (workingBranch.isValid()) {
         if (getBranchState(workingBranch).isRebaselineInProgress()) {
            return CommitStatus.Rebaseline_In_Progress;
         }
      }

      if (destinationBranch.isInvalid()) {
         return CommitStatus.Branch_Not_Configured;
      }

      Collection<? extends TransactionToken> transactions = getCommittedArtifactTransactionIds(teamWf);
      boolean mergeBranchExists = isMergeBranchExists(teamWf, destinationBranch);

      for (TransactionToken transId : transactions) {
         if (transId.isOnBranch(destinationBranch)) {
            if (mergeBranchExists) {
               return CommitStatus.Committed_With_Merge;
            } else {
               return CommitStatus.Committed;
            }
         }
      }

      if (workingBranchCommittedToDestinationBranchParentPriorToDestinationBranchCreation(teamWf, destinationBranch,
         transactions)) {
         return CommitStatus.No_Commit_Needed;
      }

      Result result = new Result(false);
      if (configArt == null) {
         result = isCommitBranchAllowed(teamWf);
      } else {
         result = configArt.isAllowCommitBranchInherited();
      }
      if (result.isFalse()) {
         return CommitStatus.Branch_Commit_Disabled;
      }
      if (getWorkingBranch(teamWf).isInvalid()) {
         return CommitStatus.Working_Branch_Not_Created;
      }
      if (mergeBranchExists) {
         return CommitStatus.Merge_In_Progress;
      }

      return CommitStatus.Commit_Needed;
   }

   /**
    * @return TransactionId associated with this state machine artifact
    */
   @Override
   public Collection<TransactionRecord> getTransactionIds(IAtsTeamWorkflow teamWf, boolean forMergeBranches) {
      if (forMergeBranches) {
         BranchId workingBranch = getWorkingBranch(teamWf);
         // grab only the transaction that had merge conflicts
         Collection<TransactionRecord> transactionIds = new ArrayList<>();
         for (TransactionRecord transactionId : getCommitTransactionsToUnarchivedBaselineBranchs(teamWf)) {
            if (isMergeBranchExists(teamWf, workingBranch, transactionId.getBranch())) {
               transactionIds.add(transactionId);
            }
         }
         return transactionIds;
      } else {
         return getCommitTransactionsToUnarchivedBaselineBranchs(teamWf);
      }
   }

   /**
    * Return true if merge branch exists in DB (whether archived or not)
    */
   @Override
   public boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, BranchId destinationBranch) throws OseeCoreException {
      return isMergeBranchExists(teamWf, getWorkingBranch(teamWf), destinationBranch);
   }

   @Override
   public Set<BranchId> getBranchesInCommit() {
      return branchesInCommit;
   }

   @Override
   public boolean workingBranchCommittedToDestinationBranchParentPriorToDestinationBranchCreation(IAtsTeamWorkflow teamWf, BranchId destinationBranch, Collection<? extends TransactionToken> commitTransactionIds) throws OseeCoreException {
      BranchId destinationBranchParent = getParentBranch(destinationBranch);
      if (getBranchType(destinationBranchParent) == BranchType.SYSTEM_ROOT) {
         return false;
      }

      TransactionId committedToParentTransRecord = null;
      for (TransactionToken transId : commitTransactionIds) {
         if (transId.isOnBranch(destinationBranchParent)) {
            committedToParentTransRecord = transId;
            break;
         }
      }
      if (committedToParentTransRecord != null) {
         if (committedToParentTransRecord.isOlderThan(getBaseTransaction(destinationBranch))) {
            return true;
         }
      }
      return workingBranchCommittedToDestinationBranchParentPriorToDestinationBranchCreation(teamWf,
         destinationBranchParent, commitTransactionIds);
   }

   @Override
   public String getBranchName(IAtsTeamWorkflow teamWf) {
      String defaultBranchName = null;
      String smaTitle = teamWf.getName();
      if (smaTitle.length() > 40) {
         smaTitle = smaTitle.substring(0, 39) + "...";
      }
      String typeName = services.getWorkItemService().getArtifactTypeShortName(teamWf);
      if (Strings.isValid(typeName)) {
         defaultBranchName = String.format("%s - %s - %s", teamWf.getAtsId(), typeName, smaTitle);
      } else {
         defaultBranchName = String.format("%s - %s", teamWf.getAtsId(), smaTitle);
      }
      for (ITeamWorkflowProvider teamExtension : teamWorkflowProvidersLazy.getProviders()) {
         String name = teamExtension.getBranchName(teamWf, defaultBranchName);
         if (Strings.isValid(name)) {
            defaultBranchName = name;
            break;
         }
      }
      defaultBranchName = Strings.truncate(defaultBranchName, 195, true);
      return defaultBranchName;
   }

   @Override
   public BranchId getWorkingBranchInWork(IAtsTeamWorkflow teamWf) {
      IOseeBranch branch = getWorkingBranch(teamWf);
      if (branch != null && (getBranchState(branch).isCreated() || getBranchState(branch).isModified())) {
         return branch;
      }
      return BranchId.SENTINEL;
   }

}
