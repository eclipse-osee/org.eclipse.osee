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
package org.eclipse.osee.ats.impl.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AbstractAtsBranchService;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.TransactionQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchServiceImpl extends AbstractAtsBranchService {

   private final OrcsApi orcsApi;
   private static final HashMap<Integer, List<ITransaction>> commitArtifactIdMap =
      new HashMap<Integer, List<ITransaction>>();

   public AtsBranchServiceImpl(IAtsServices atsServices, OrcsApi orcsApi) {
      super(atsServices);
      this.orcsApi = orcsApi;
   }

   @Override
   public IOseeBranch getCommittedWorkingBranch(IAtsTeamWorkflow teamWf) {
      int assocArtId = ((ArtifactReadable) teamWf.getStoreObject()).getLocalId();
      BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();
      query =
         query.andIsOfType(BranchType.WORKING).andStateIs(BranchState.COMMITTED).excludeArchived().andAssociatedArtId(
            assocArtId);
      return query.getResults().getOneOrNull();
   }

   @Override
   public IOseeBranch getWorkingBranchExcludeStates(IAtsTeamWorkflow teamWf, BranchState... negatedBranchStates) {
      BranchQuery branchQuery = orcsApi.getQueryFactory(null).branchQuery();
      if (negatedBranchStates.length > 0) {
         Collection<BranchState> statesToSearch = new LinkedList<BranchState>(Arrays.asList(BranchState.values()));
         statesToSearch.removeAll(Arrays.asList(negatedBranchStates));
         branchQuery.andStateIs(statesToSearch.toArray(new BranchState[statesToSearch.size()]));
      }
      branchQuery.andIsOfType(BranchType.WORKING);
      branchQuery.andAssociatedArtId(((ArtifactReadable) teamWf.getStoreObject()).getLocalId());
      return branchQuery.getResultsAsId().getOneOrNull();
   }

   @Override
   public BranchType getBranchType(IOseeBranch branch) {
      BranchReadable fullBranch = getBranchByUuid(branch.getUuid());
      return fullBranch.getBranchType();
   }

   @Override
   public BranchState getBranchState(IOseeBranch branch) {
      BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();
      BranchReadable fullBranch = query.andUuids(branch.getUuid()).getResults().getExactlyOne();
      return fullBranch.getBranchState();
   }

   /**
    * Return true if merge branch exists in DB (whether archived or not)
    */
   @Override
   public boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, IOseeBranch destinationBranch) throws OseeCoreException {
      return isMergeBranchExists(teamWf, getWorkingBranch(teamWf), destinationBranch);
   }

   /**
    * Method available for optimized checking of merge branches so don't have to re-acquire working branch if already
    * have
    */
   @Override
   public boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, IOseeBranch workingBranch, IOseeBranch destinationBranch) throws OseeCoreException {
      if (workingBranch == null) {
         return false;
      }
      BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();
      query = query.andIsMergeFor(workingBranch.getUuid(), destinationBranch.getUuid());
      return query.getCount() > 0;
   }

   @Override
   public BranchReadable getBranchByUuid(long branchUuid) {
      return orcsApi.getQueryFactory(null).branchQuery().andUuids(branchUuid).getResults().getExactlyOne();
   }

   @Override
   public boolean branchExists(long branchUuid) {
      BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();
      return query.andUuids(branchUuid).getCount() > 0;
   }

   @Override
   public BranchArchivedState getArchiveState(IOseeBranch branch) {
      BranchReadable fullBranch = getBranchByUuid(branch.getUuid());
      return fullBranch.getArchiveState();
   }

   @Override
   public IOseeBranch getBranch(ITransaction transaction) {
      return getBranchByUuid(((TransactionRecord) transaction).getBranchId());
   }

   @Override
   public Date getTimeStamp(ITransaction transaction) {
      return ((TransactionRecord) transaction).getTimeStamp();
   }

   @Override
   public Collection<ITransaction> getCommittedArtifactTransactionIds(IAtsTeamWorkflow teamWf) {
      ArtifactReadable artifactReadable = (ArtifactReadable) teamWf;
      List<ITransaction> transactionIds = commitArtifactIdMap.get(artifactReadable.getLocalId());
      // Cache the transactionIds first time through.  Other commits will be added to cache as they
      // happen in this client or as remote commit events come through
      if (transactionIds == null) {
         transactionIds = new ArrayList<ITransaction>(5);
         TransactionQuery txQuery = orcsApi.getQueryFactory(null).transactionQuery();
         txQuery.andCommitIds(artifactReadable.getLocalId());
         for (TransactionReadable tx : txQuery.getResults()) {
            transactionIds.add(tx);
         }
         commitArtifactIdMap.put(artifactReadable.getLocalId(), transactionIds);
      }
      return transactionIds;
   }

   @Override
   public IOseeBranch getParentBranch(IOseeBranch branch) {
      BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();
      BranchReadable fullBranch = query.andUuids(branch.getUuid()).getResults().getExactlyOne();
      return getBranchByUuid(fullBranch.getParentBranch());
   }

   @Override
   public ITransaction getBaseTransaction(IOseeBranch branch) {
      TransactionQuery txQuery = orcsApi.getQueryFactory(null).transactionQuery();
      return txQuery.andBranch(branch).andIs(TransactionDetailsType.Baselined).getResults().getExactlyOne();
   }
}
