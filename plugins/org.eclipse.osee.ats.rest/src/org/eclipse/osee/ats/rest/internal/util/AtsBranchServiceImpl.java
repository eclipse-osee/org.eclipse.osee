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
package org.eclipse.osee.ats.rest.internal.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.core.util.AbstractAtsBranchService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.TransactionQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchServiceImpl extends AbstractAtsBranchService {

   private final OrcsApi orcsApi;
   private final TransactionQuery txQuery;
   private final HashCollection<ArtifactId, TransactionRecord> commitArtifactIdMap =
      new HashCollection<>(true, HashSet.class);

   public AtsBranchServiceImpl(IAtsServices atsServices, OrcsApi orcsApi, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      super(atsServices, teamWorkflowProvidersLazy);
      this.orcsApi = orcsApi;
      txQuery = orcsApi.getQueryFactory().transactionQuery();
   }

   @Override
   public IOseeBranch getCommittedWorkingBranch(IAtsTeamWorkflow teamWf) {
      int assocArtId = ((ArtifactReadable) teamWf.getStoreObject()).getLocalId();
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      query =
         query.andIsOfType(BranchType.WORKING).andStateIs(BranchState.COMMITTED).excludeArchived().andAssociatedArtId(
            assocArtId);
      return query.getResults().getOneOrNull();
   }

   @Override
   public IOseeBranch getWorkingBranchExcludeStates(IAtsTeamWorkflow teamWf, BranchState... negatedBranchStates) {
      BranchQuery branchQuery = orcsApi.getQueryFactory().branchQuery();
      if (negatedBranchStates.length > 0) {
         Collection<BranchState> statesToSearch = new LinkedList<>(Arrays.asList(BranchState.values()));
         statesToSearch.removeAll(Arrays.asList(negatedBranchStates));
         branchQuery.andStateIs(statesToSearch.toArray(new BranchState[statesToSearch.size()]));
      }
      branchQuery.andIsOfType(BranchType.WORKING);
      branchQuery.andAssociatedArtId(((ArtifactReadable) teamWf.getStoreObject()).getLocalId());
      return branchQuery.getResultsAsId().getOneOrNull();
   }

   @Override
   public BranchType getBranchType(BranchId branch) {
      return getBranch(branch).getBranchType();
   }

   @Override
   public BranchState getBranchState(BranchId branch) {
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      BranchReadable fullBranch = query.andUuids(branch.getUuid()).getResults().getExactlyOne();
      return fullBranch.getBranchState();
   }

   /**
    * Return true if merge branch exists in DB (whether archived or not)
    */
   @Override
   public boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, BranchId destinationBranch) throws OseeCoreException {
      return isMergeBranchExists(teamWf, getWorkingBranch(teamWf), destinationBranch);
   }

   /**
    * Method available for optimized checking of merge branches so don't have to re-acquire working branch if already
    * have
    */
   @Override
   public boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, BranchId workingBranch, BranchId destinationBranch) throws OseeCoreException {
      if (workingBranch == null) {
         return false;
      }
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      query = query.andIsMergeFor(workingBranch, destinationBranch);
      return query.getCount() > 0;
   }

   @Override
   public BranchReadable getBranchByUuid(long branchUuid) {
      return orcsApi.getQueryFactory().branchQuery().andUuids(branchUuid).getResults().getExactlyOne();
   }

   private BranchReadable getBranch(BranchId branch) {
      return orcsApi.getQueryFactory().branchQuery().andIds(branch).getResults().getExactlyOne();
   }

   @Override
   public boolean branchExists(long branchUuid) {
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      return query.andUuids(branchUuid).getCount() > 0;
   }

   @Override
   public boolean isArchived(BranchId branch) {
      return getBranch(branch).getArchiveState().isArchived();
   }

   @Override
   public Collection<TransactionRecord> getCommittedArtifactTransactionIds(IAtsTeamWorkflow teamWf) {
      ArtifactId artifact = teamWf.getStoreObject();
      if (!commitArtifactIdMap.containsKey(artifact)) {
         txQuery.andCommitIds(artifact.getId().intValue());
         txQuery.getResults().forEach(
            tx -> commitArtifactIdMap.put(artifact, new TransactionRecord(tx.getId(), tx.getBranch(), tx.getComment(),
               tx.getDate(), tx.getAuthor().getId().intValue(), tx.getCommitArt().getId().intValue(), tx.getTxType())));
      }
      Collection<TransactionRecord> transactions = commitArtifactIdMap.getValues(artifact);
      return transactions == null ? Collections.emptyList() : transactions;
   }

   @Override
   public BranchId getParentBranch(BranchId branch) {
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      BranchReadable fullBranch = query.andIds(branch).getResults().getExactlyOne();
      return fullBranch.getParentBranch();
   }

   @Override
   public TransactionToken getBaseTransaction(BranchId branch) {
      TransactionQuery txQuery = orcsApi.getQueryFactory().transactionQuery();
      return txQuery.andBranch(branch).andIs(TransactionDetailsType.Baselined).getResults().getExactlyOne();
   }

   @Override
   public void setBranchName(IOseeBranch branch, String name) {
      orcsApi.getBranchOps().changeBranchName(branch, name);
   }

   @Override
   public String getBranchName(BranchId branchId) {
      return getBranch(branchId).getName();
   }
}
