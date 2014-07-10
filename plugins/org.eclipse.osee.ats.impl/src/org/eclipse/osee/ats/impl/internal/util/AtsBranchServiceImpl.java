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

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AbstractAtsBranchService;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchServiceImpl extends AbstractAtsBranchService {

   private final OrcsApi orcsApi;

   public AtsBranchServiceImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public boolean isBranchInCommit(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      IOseeBranch branch = getBranch(teamWf);

      BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();
      BranchReadable branchReadable = query.andIds(branch).getResults().getExactlyOne();

      return branchReadable.getBranchState() == BranchState.COMMIT_IN_PROGRESS;
   }

   @Override
   public boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      boolean inWork = false;
      IOseeBranch branch = getBranch(teamWf);
      if (branch != null) {
         BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();
         BranchReadable branchReadable = query.andIds(branch).getResults().getExactlyOne();
         if (branchReadable != null) {
            BranchState state = branchReadable.getBranchState();
            inWork = state == BranchState.CREATED || state == BranchState.MODIFIED;
         }
      }
      return inWork;
   }

   @Override
   public IOseeBranch getBranch(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      IOseeBranch results = null;
      BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();
      ResultSet<BranchReadable> branches = query.excludeArchived().andIsOfType(BranchType.WORKING).getResults();

      if (!branches.isEmpty()) {
         ArtifactReadable artifact = null;
         if (teamWf.getStoreObject() != null) {
            artifact = (ArtifactReadable) teamWf.getStoreObject();
         } else {
            artifact =
               orcsApi.getQueryFactory(null).fromBranch(AtsUtilServer.getAtsBranch()).andGuid(teamWf.getGuid()).getResults().getExactlyOne();
         }
         for (BranchReadable branch : branches) {
            if (branch.getAssociatedArtifactId() == artifact.getLocalId()) {
               results = branch;
               break;
            }
         }
      }
      return results;
   }

   @Override
   public IOseeBranch getBranch(IAtsConfigObject configObject) {
      IOseeBranch branch = null;
      ArtifactReadable artifact =
         orcsApi.getQueryFactory(null).fromBranch(AtsUtilServer.getAtsBranch()).andGuid(configObject.getGuid()).getResults().getExactlyOne();
      if (artifact != null) {
         String branchUuid = artifact.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchUuid, "");
         if (Strings.isValid(branchUuid)) {
            BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();
            ResultSet<BranchReadable> branches = query.andUuids(Long.valueOf(branchUuid)).getResults();
            if (!branches.isEmpty()) {
               branch = branches.iterator().next();
            }
         }
      }
      return branch;
   }

   @Override
   public String getBranchShortName(ICommitConfigItem commitConfigArt) {
      return ((Branch) getBranch(commitConfigArt)).getShortName();
   }

   @Override
   public IOseeBranch getBranchInherited(IAtsVersion version) {
      IOseeBranch branch = null;
      long branchUuid = version.getBaselineBranchUuidInherited();
      if (branchUuid > 0) {
         BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();
         ResultSet<BranchReadable> branches = query.andUuids(branchUuid).getResults();
         if (!branches.isEmpty()) {
            branch = branches.iterator().next();
         }
      }
      return branch;
   }

}
