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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.config.tx.AtsVersionArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsActionableItemArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTx;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxActionableItem;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxProgram;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxTeamDef;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxVersion;
import org.eclipse.osee.ats.api.config.tx.IAtsProgramArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsVersionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.query.NextRelease;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigTxImpl implements IAtsConfigTx {

   private final AtsApi atsApi;
   private final IAtsChangeSet changes;
   private final Map<String, IAtsTeamDefinition> newTeams = new HashMap<>();
   private final Map<String, IAtsActionableItem> newAis = new HashMap<>();
   private final Set<Long> usedIds = new HashSet<>();

   public AtsConfigTxImpl(String name, AtsApi atsApi) {
      this.atsApi = atsApi;
      changes = atsApi.createChangeSet(name);
   }

   @Override
   public IAtsConfigTxTeamDef createTeamDef(IAtsTeamDefinition parent, IAtsTeamDefinitionArtifactToken teamDef) {
      ArtifactToken newTeam = atsApi.getQueryService().getArtifact(teamDef);
      if (newTeam == null || newTeam.isInvalid()) {
         checkUsedIds(teamDef);
         newTeam = changes.createArtifact(teamDef);
      }
      IAtsTeamDefinition newTeamDef = atsApi.getTeamDefinitionService().createTeamDefinition(newTeam);
      if (parent != null) {
         changes.relate(parent, CoreRelationTypes.DefaultHierarchical_Child, newTeamDef);
      }
      newTeams.put(newTeam.getName(), newTeamDef);
      return new AtsConfigTxTeamDef(newTeamDef, atsApi, changes, this);
   }

   private void checkUsedIds(ArtifactToken art) {
      if (usedIds.contains(art.getId())) {
         throw new OseeArgumentException("Id %s already used.  Can't create token %s", art.getIdString(),
            art.toStringWithId());
      }
      usedIds.add(art.getId());
   }

   @Override
   public IAtsConfigTxActionableItem createActionableItem(IAtsActionableItem parent, IAtsActionableItemArtifactToken ai) {
      ArtifactToken newAiArt = atsApi.getQueryService().getArtifact(ai);
      if (newAiArt == null || newAiArt.isInvalid()) {
         checkUsedIds(ai);
         newAiArt = changes.createArtifact(ai);
      }
      IAtsActionableItem newAi = atsApi.getActionableItemService().getActionableItemById(newAiArt);
      if (parent != null) {
         changes.relate(parent, CoreRelationTypes.DefaultHierarchical_Child, newAi);
      }
      newAis.put(newAiArt.getName(), newAi);
      return new AtsConfigTxActionableItem(newAi, atsApi, changes, this);
   }

   @Override
   public IAtsConfigTxActionableItem createActionableItem(IAtsActionableItemArtifactToken actionableItem) {
      ArtifactToken newAiArt = atsApi.getQueryService().getArtifact(actionableItem);
      if (newAiArt == null || newAiArt.isInvalid()) {
         checkUsedIds(actionableItem);
         newAiArt = changes.createArtifact(actionableItem);
      }
      IAtsActionableItem newAi = atsApi.getActionableItemService().createActionableItem(newAiArt);
      newAis.put(newAiArt.getName(), newAi);
      return new AtsConfigTxActionableItem(newAi, atsApi, changes, this);
   }

   @Override
   public TransactionId execute() {
      return changes.executeIfNeeded();
   }

   @Override
   public IAtsConfigTxVersion createVersion(IAtsVersionArtifactToken versionTok, ReleasedOption released, BranchToken branch, NextRelease nextRelease, IAtsTeamDefinition teamDef) {
      checkUsedIds(versionTok);
      ArtifactToken verArt = changes.createArtifact(AtsArtifactTypes.Version, versionTok.getName(), versionTok.getId());
      changes.setSoleAttributeValue(verArt, AtsAttributeTypes.Released, released != ReleasedOption.UnReleased);
      changes.setSoleAttributeValue(verArt, AtsAttributeTypes.NextVersion, nextRelease == NextRelease.Next);
      if (branch != null && branch.isValid()) {
         changes.setSoleAttributeValue(verArt, AtsAttributeTypes.BaselineBranchId, branch.getIdString());
         changes.setSoleAttributeValue(verArt, AtsAttributeTypes.AllowCommitBranch, true);
         changes.setSoleAttributeValue(verArt, AtsAttributeTypes.AllowCreateBranch, true);
      }
      changes.relate(teamDef, AtsRelationTypes.TeamDefinitionToVersion_Version, verArt);
      Version newVersion = atsApi.getVersionService().createVersion(verArt);
      ((TeamDefinition) teamDef).addVersion(newVersion.getId());
      return new AtsConfigTxVersion(newVersion, atsApi, changes, this);
   }

   @Override
   public IAtsConfigTxVersion createVersion(String name, ReleasedOption released, BranchToken branch, NextRelease nextRelease, IAtsTeamDefinition teamDef) {
      return createVersion(AtsVersionArtifactToken.valueOf(Lib.generateArtifactIdAsInt(), name), released, branch,
         nextRelease, teamDef);
   }

   @Override
   public IAtsTeamDefinition getTeamDef(ArtifactId teamDef) {
      for (Entry<String, IAtsTeamDefinition> entry : newTeams.entrySet()) {
         if (entry.getValue().equals(teamDef)) {
            return entry.getValue();
         }
      }
      return null;
   }

   @Override
   public IAtsTeamDefinition getTeamDef(String name) {
      return newTeams.get(name);
   }

   @Override
   public IAtsConfigTxProgram createProgram(IAtsProgramArtifactToken programTok) {
      ArtifactToken newProgramArt = atsApi.getQueryService().getArtifact(programTok);
      if (newProgramArt == null || newProgramArt.isInvalid()) {
         checkUsedIds(programTok);
         newProgramArt = changes.createArtifact(programTok);
      }
      IAtsProgram program = atsApi.getProgramService().getProgramById(newProgramArt);
      return new AtsConfigTxProgram(program, atsApi, changes, this);
   }

   @Override
   public IAtsChangeSet getChanges() {
      return changes;
   }

   @Override
   public IAtsActionableItem getActionableItem(ArtifactId artifact) {
      for (Entry<String, IAtsActionableItem> entry : newAis.entrySet()) {
         if (entry.getValue().getId().equals(artifact.getId())) {
            return entry.getValue();
         }
      }
      return null;
   }

   @Override
   public CreateTasksDefinitionBuilder createTaskDefinitionBuilder(Long id, String name) {
      CreateTasksDefinitionBuilder txTasks = new CreateTasksDefinitionBuilder(id, name);
      return txTasks;
   }

   @Override
   public IAtsConfigTxProgram createProgram(ArtifactId parent, IAtsProgramArtifactToken program) {
      IAtsConfigTxProgram txProgram = createProgram(program);
      changes.relate(parent, CoreRelationTypes.DefaultHierarchical_Child, txProgram.getProgram());
      return txProgram;
   }

}
