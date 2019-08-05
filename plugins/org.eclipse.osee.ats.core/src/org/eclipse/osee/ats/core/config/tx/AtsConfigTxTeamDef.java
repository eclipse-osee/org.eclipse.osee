/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config.tx;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.RelatedPeerWorkDefinitionReference;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.RelatedTaskWorkDefinitionReference;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.TeamWorkflowArtifactType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.tx.AtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTx;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxTeamDef;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxVersion;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsVersionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.query.NextRelease;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigTxTeamDef extends AbstractAtsConfigTxObject<IAtsConfigTxTeamDef> implements IAtsConfigTxTeamDef {

   private final IAtsTeamDefinition teamDef;

   public AtsConfigTxTeamDef(IAtsObject atsObject, AtsApi atsApi, IAtsChangeSet changes, IAtsConfigTx cfgTx) {
      super(atsObject, atsApi, changes, cfgTx);
      Conditions.assertTrue(atsObject instanceof IAtsTeamDefinition, "AtsObject must be of type IAtsTeamDefinition");
      teamDef = (IAtsTeamDefinition) atsObject;
   }

   @Override
   public IAtsConfigTxTeamDef andWorkDef(NamedId workDefId) {
      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.WorkflowDefinitionReference, workDefId);
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andLeads(UserToken... leads) {
      for (UserToken lead : leads) {
         changes.relate(teamDef, AtsRelationTypes.TeamLead_Lead, lead);
      }
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andMembers(UserToken... members) {
      for (UserToken member : members) {
         changes.relate(teamDef, AtsRelationTypes.TeamMember_Member, member);
      }
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andVersion(String name, ReleasedOption released, IOseeBranch branch, NextRelease nextRelease, IOseeBranch... parallelVersions) {
      IAtsConfigTxVersion version = cfgTx.createVersion(name, released, branch, nextRelease, teamDef);
      handleParallelVersions(version, parallelVersions);
      return this;
   }

   private void handleParallelVersions(IAtsConfigTxVersion version, IOseeBranch... parallelVersions) {
      for (IOseeBranch parallelVer : parallelVersions) {
         boolean found = false;
         for (IAtsVersion teamDefVer : teamDef.getVersions()) {
            if (teamDefVer.getBaselineBranchId().isValid() && teamDefVer.getBaselineBranchId().equals(
               parallelVer.getId())) {
               changes.relate(version.getVersion(), AtsRelationTypes.ParallelVersion_Child, teamDefVer);
               found = true;
            }
         }
         if (!found) {
            throw new OseeArgumentException("No parallel version %s found for version %s", parallelVer.toStringWithId(),
               version.getVersion().toStringWithId());
         }
      }
   }

   @Override
   public IAtsConfigTxTeamDef andVersion(IAtsVersionArtifactToken versionTok, ReleasedOption released, IOseeBranch branch, NextRelease nextRelease, IOseeBranch... parallelVersions) {
      IAtsConfigTxVersion version = cfgTx.createVersion(versionTok, released, branch, nextRelease, teamDef);
      handleParallelVersions(version, parallelVersions);
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andVersion(IAtsVersionArtifactToken... versionToks) {
      for (IAtsVersionArtifactToken versionTok : versionToks) {
         cfgTx.createVersion(versionTok, ReleasedOption.UnReleased, null, NextRelease.None, teamDef);
      }
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef createChildTeamDef(IAtsTeamDefinition parent, IAtsTeamDefinitionArtifactToken childTok) {
      IAtsConfigTxTeamDef child = cfgTx.createTeamDef(parent, childTok);
      return child;
   }

   @Override
   public IAtsConfigTxTeamDef createChildTeamDef(String name) {
      return createChildTeamDef(teamDef, AtsTeamDefinitionArtifactToken.valueOf(Lib.generateArtifactIdAsInt(), name));
   }

   @Override
   public IAtsConfigTxTeamDef andTeamWorkflowArtifactType(ArtifactTypeToken artifactType) {
      changes.setSoleAttributeValue(teamDef, TeamWorkflowArtifactType, artifactType.getName());
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andRelatedTaskWorkflowDefinition(NamedId... taskWorkDefs) {
      for (NamedId id : taskWorkDefs) {
         changes.addAttribute(teamDef, RelatedTaskWorkDefinitionReference, id);
      }
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andRelatedPeerWorkflowDefinition(NamedId... peerWorkDefs) {
      for (NamedId id : peerWorkDefs) {
         changes.addAttribute(teamDef, RelatedPeerWorkDefinitionReference, id);
      }
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andAccessContextId(String contextId) {
      changes.setSoleAttributeValue(teamDef, CoreAttributeTypes.AccessContextId, contextId);
      return this;
   }

   @Override
   public IAtsTeamDefinition getTeamDef() {
      return teamDef;
   }

   @Override
   public IAtsConfigTxTeamDef andTaskSet(AtsTaskDefToken... taskSets) {
      for (AtsTaskDefToken taskSet : taskSets) {
         changes.addAttribute(teamDef, AtsAttributeTypes.TaskSetId, taskSet.getId());
      }
      return this;
   }

}
