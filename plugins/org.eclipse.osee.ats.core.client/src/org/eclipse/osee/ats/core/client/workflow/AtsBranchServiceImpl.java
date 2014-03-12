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
package org.eclipse.osee.ats.core.client.workflow;

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AbstractAtsBranchService;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchServiceImpl extends AbstractAtsBranchService {

   @Override
   public boolean isBranchInCommit(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return AtsBranchManagerCore.isBranchInCommit((TeamWorkFlowArtifact) teamWf);
   }

   @Override
   public boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return AtsBranchManagerCore.isWorkingBranchInWork(teamWf);
   }

   @Override
   public IOseeBranch getBranch(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return AtsBranchManagerCore.getWorkingBranch((TeamWorkFlowArtifact) teamWf);
   }

   @Override
   public Branch getBranch(IAtsConfigObject configObject) {
      Branch branch = null;
      if (configObject instanceof IAtsVersion) {
         IAtsVersion version = (IAtsVersion) configObject;
         if (version.getBaselineBranchUuid() > 0) {
            branch = BranchManager.getBranchByUuid(version.getBaselineBranchUuid());
         }
      }
      if (branch == null && (configObject instanceof IAtsTeamDefinition)) {
         IAtsTeamDefinition teamDef = (IAtsTeamDefinition) configObject;
         if (teamDef.getBaselineBranchUuid() > 0) {
            branch = BranchManager.getBranchByUuid(teamDef.getBaselineBranchUuid());
         }
      }
      if (branch == null) {
         Artifact artifact = AtsClientService.get().getArtifact(configObject);
         if (artifact != null) {
            String branchUuid = artifact.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchUuid, "");
            if (Strings.isValid(branchUuid)) {
               branch = BranchManager.getBranch(branchUuid);
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
         branch = BranchManager.getBranch(branchUuid);
      }
      return branch;
   }

}
