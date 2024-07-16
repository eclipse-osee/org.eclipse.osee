/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.config.tx;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.RelatedTaskWorkflowDefinitionReference;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.TeamWorkflowArtifactType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.Csci;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.config.tx.AtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTx;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxTeamDef;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxVersion;
import org.eclipse.osee.ats.api.config.tx.IAtsProgramArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsVersionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.query.NextRelease;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigTxTeamDef extends AbstractAtsConfigTxObject<IAtsConfigTxTeamDef> implements IAtsConfigTxTeamDef {

   private final IAtsTeamDefinition teamDef;
   private final ArtifactToken workPackageEnumArt = ArtifactToken.SENTINEL;

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
   public IAtsConfigTxTeamDef andVersion(String name, ReleasedOption released, BranchToken branch,
      NextRelease nextRelease, IAtsVersionArtifactToken... parallelVersions) {
      IAtsConfigTxVersion version = cfgTx.createVersion(name, released, branch, nextRelease, teamDef);
      handleParallelVersions(version, parallelVersions);
      return this;
   }

   private void handleParallelVersions(IAtsConfigTxVersion version, IAtsVersionArtifactToken... parallelVersions) {
      for (IAtsVersionArtifactToken parallelVer : parallelVersions) {
         changes.relate(version.getVersion().getStoreObject(), AtsRelationTypes.ParallelVersion_Child, parallelVer);
      }
   }

   @Override
   public IAtsConfigTxTeamDef andParallelVersion(IAtsVersion ver1, IAtsVersion ver2) {
      changes.relate(ver1, AtsRelationTypes.ParallelVersion_Child, ver2);
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andVersion(IAtsVersionArtifactToken versionTok, ReleasedOption released,
      BranchToken branch, NextRelease nextRelease, IAtsVersionArtifactToken... parallelVersions) {
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
      return cfgTx.createTeamDef(parent, childTok);
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
         changes.addAttribute(teamDef, RelatedTaskWorkflowDefinitionReference, id);
      }
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andRelatedPeerWorkflowDefinition(NamedId... peerWorkDefs) {
      for (NamedId id : peerWorkDefs) {
         changes.addAttribute(teamDef, RelatedPeerWorkflowDefinitionReference, id);
      }
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

   @Override
   public IAtsConfigTxVersion andVersionTx(IAtsVersionArtifactToken versionTok, ReleasedOption released,
      BranchToken branch, NextRelease nextRelease, IAtsVersionArtifactToken... parallelVersions) {
      IAtsConfigTxVersion version = cfgTx.createVersion(versionTok, released, branch, nextRelease, teamDef);
      handleParallelVersions(version, parallelVersions);
      return version;
   }

   @Override
   public IAtsConfigTxTeamDef andWorkType(WorkType workType) {
      changes.addAttribute(teamDef, AtsAttributeTypes.WorkType, workType.name());
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andProgram(IAtsProgramArtifactToken program) {
      changes.addAttribute(teamDef, AtsAttributeTypes.ProgramId, program);
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andCsci(Csci... cscis) {
      for (Csci csci : cscis) {
         changes.addAttribute(teamDef, AtsAttributeTypes.CSCI, csci.name());
      }
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andAccessContexts(AccessContextToken... contextIds) {
      for (AccessContextToken id : contextIds) {
         and(CoreAttributeTypes.AccessContextId, String.format("%s, %s", id.getIdString(), id.getName()));
      }
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andAtsIdPrefix(String atsIdPrefix, String seqName, String seqStart) {
      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.AtsIdPrefix, atsIdPrefix);
      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.AtsIdSequenceName, seqName);
      changes.addAtsIdSequence(seqName, seqStart);
      return this;
   }

   @Override
   public IAtsConfigTxTeamDef andWorkPackages(String artName, String... workPackageNames) {
      if (workPackageEnumArt.isInvalid()) {
         Conditions.assertTrue(Strings.isValid(artName), "Invalid art name");
         Conditions.assertTrue(workPackageNames.length > 0, "Work Package Names can not be empty");
         ArtifactToken enumArt = changes.createArtifact(CoreArtifactTypes.OseeTypeEnum, artName);
         changes.relate(teamDef, CoreRelationTypes.DefaultHierarchical_Child, enumArt);
         changes.addTag(enumArt, AtsUtil.WORK_PKG_STATIC_ID);
         for (String workPackage : workPackageNames) {
            Conditions.assertTrue(Strings.isValid(artName), "Invalid package name");
            changes.addAttribute(enumArt, CoreAttributeTypes.IdValue, workPackage);
         }
      }
      return this;
   }

}
