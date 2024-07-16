/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.config.copy;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class CopyAtsConfigurationOperation extends AbstractOperation {

   private final ConfigData data;
   protected XResultData resultData;
   Set<Artifact> newArtifacts;
   Set<Artifact> existingArtifacts;
   Set<Artifact> processedFromAis;

   private final Map<IAtsTeamDefinition, IAtsTeamDefinition> fromTeamDefToNewTeamDefMap = new HashMap<>();
   private final Map<Artifact, IAtsActionableItem> newAiArtToNewAi = new HashMap<>();

   public CopyAtsConfigurationOperation(ConfigData data, XResultData resultData) {
      super("Copy ATS Configuration", Activator.PLUGIN_ID);
      this.data = data;
      this.resultData = resultData;
   }

   protected CopyAtsValidation getCopyAtsValidation() {
      return new CopyAtsValidation(data, resultData);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      try {
         data.validateData(resultData);
         if (resultData.isErrors()) {
            return;
         }
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(getName());

         getCopyAtsValidation().validate();
         if (resultData.isErrors()) {
            persistOrUndoChanges(changes);
            return;
         }

         if (data.isPersistChanges()) {
            resultData.log("Persisting Changes ");
         } else {
            resultData.log("Report-Only, Changes are not persisted");
         }

         newArtifacts = new HashSet<>(50);
         existingArtifacts = new HashSet<>(50);
         processedFromAis = new HashSet<>(10);

         createTeamDefinitions(changes, data.getTeamDef(), data.getParentTeamDef());
         if (resultData.isErrors()) {
            persistOrUndoChanges(changes);
            return;
         }

         createActionableItems(changes, data.getActionableItem(), data.getParentActionableItem());

         if (resultData.isErrors()) {
            persistOrUndoChanges(changes);
            return;
         }

         if (AtsApiService.get().isSingleServerDeployment()) {
            AtsApiService.get().reloadServerAndClientCaches();
         }
         persistOrUndoChanges(changes);
         XResultDataUI.report(resultData, getName());
      } finally {
         monitor.subTask("Done");
      }
   }

   /**
    * Has potential of returning null if this fromAi has already been processed.
    */
   protected IAtsActionableItem createActionableItems(IAtsChangeSet changes, IAtsActionableItem fromAi,
      IAtsActionableItem parentAi) {
      Artifact fromAiArt = AtsApiService.get().getQueryServiceIde().getArtifact(fromAi);

      if (processedFromAis.contains(fromAiArt)) {
         resultData.log(String.format("Skipping already processed fromAi [%s]", fromAiArt));
         return null;
      } else {
         processedFromAis.add(fromAiArt);
      }
      Artifact parentAiArt = AtsApiService.get().getQueryServiceIde().getArtifact(parentAi);

      // Get or create new actionable item
      Pair<Artifact, IAtsConfigObject> newAiArtResult = duplicateTeamDefinitionOrActionableItem(changes, fromAiArt);
      Artifact newAiArt = newAiArtResult.getFirst();
      IAtsActionableItem newAi = (IAtsActionableItem) newAiArtResult.getSecond();
      changes.add(newAiArt);
      changes.relate(parentAiArt, CoreRelationTypes.DefaultHierarchical_Child, newAi);
      existingArtifacts.add(parentAiArt);
      newArtifacts.add(newAiArt);
      // Relate new Ais to their TeamDefs just like other config
      for (Artifact fromTeamDefArt : fromAiArt.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_TeamDefinition,
         Artifact.class)) {
         IAtsConfigObject fromTeamDef =
            AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(fromTeamDefArt);
         IAtsTeamDefinition newTeamDef = fromTeamDefToNewTeamDefMap.get(fromTeamDef);

         if (newTeamDef == null) {
            resultData.warningf("No related Team Definition [%s] in scope for AI [%s].  Configure by hand.",
               fromTeamDefArt, newAiArt);
         } else {
            Artifact newTeamDefArt = AtsApiService.get().getQueryServiceIde().getArtifact(newTeamDef);
            newAiArt.addRelation(AtsRelationTypes.TeamActionableItem_TeamDefinition, newTeamDefArt);
            changes.add(newTeamDefArt);
         }
      }

      // Handle all children
      for (Artifact childFromAiArt : fromAiArt.getChildren()) {
         if (childFromAiArt.isOfType(AtsArtifactTypes.ActionableItem)) {
            IAtsActionableItem childAi =
               AtsApiService.get().getActionableItemService().getActionableItemById(childFromAiArt);
            IAtsActionableItem newChildAi = newAiArtToNewAi.get(newAiArt);
            createActionableItems(changes, childAi, newChildAi);
         }
      }
      return newAi;
   }

   protected IAtsTeamDefinition createTeamDefinitions(IAtsChangeSet changes, IAtsTeamDefinition fromTeamDef,
      IAtsTeamDefinition parentTeamDef) {
      // Get or create new team definition
      Artifact parentTeamDefArt = AtsApiService.get().getQueryServiceIde().getArtifact(parentTeamDef);
      Artifact fromTeamDefArt = AtsApiService.get().getQueryServiceIde().getArtifact(fromTeamDef);

      Pair<Artifact, IAtsConfigObject> teamDefResult = duplicateTeamDefinitionOrActionableItem(changes, fromTeamDefArt);
      Artifact newTeamDefArt = teamDefResult.getFirst();
      IAtsTeamDefinition newTeamDef = (IAtsTeamDefinition) teamDefResult.getSecond();
      changes.add(newTeamDefArt);

      parentTeamDefArt.addChild(newTeamDefArt);
      changes.add(parentTeamDefArt);
      existingArtifacts.add(parentTeamDefArt);
      newArtifacts.add(newTeamDefArt);
      fromTeamDefToNewTeamDefMap.put(fromTeamDef, newTeamDef);
      if (data.isRetainTeamLeads()) {
         duplicateTeamLeadsAndMembers(changes, fromTeamDef, newTeamDef);
      }
      // handle all children
      for (Artifact childFromTeamDefArt : fromTeamDefArt.getChildren()) {
         if (childFromTeamDefArt.isOfType(AtsArtifactTypes.TeamDefinition)) {
            IAtsTeamDefinition childFromTeamDef =
               AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(childFromTeamDefArt);
            AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(childFromTeamDefArt);
            createTeamDefinitions(changes, childFromTeamDef, newTeamDef);
         }
      }
      return newTeamDef;
   }

   private void duplicateTeamLeadsAndMembers(IAtsChangeSet changes, IAtsTeamDefinition fromTeamDef,
      IAtsTeamDefinition newTeamDef) {
      Artifact fromTeamDefArt = AtsApiService.get().getQueryServiceIde().getArtifact(fromTeamDef);
      Artifact newTeamDefArt = AtsApiService.get().getQueryServiceIde().getArtifact(newTeamDef);

      Collection<Artifact> leads = newTeamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead);
      for (Artifact user : fromTeamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead)) {
         if (!leads.contains(user)) {
            existingArtifacts.add(user);
            changes.add(user);
            newTeamDefArt.addRelation(AtsRelationTypes.TeamLead_Lead, user);
            resultData.log("   - Relating team lead " + user);
         }
      }
      Collection<Artifact> members = newTeamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamMember_Member);
      for (Artifact user : fromTeamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamMember_Member)) {
         if (!members.contains(user)) {
            existingArtifacts.add(user);
            changes.add(user);
            newTeamDefArt.addRelation(AtsRelationTypes.TeamMember_Member, user);
            resultData.log("   - Relating team member " + user);
         }
      }
   }

   private void persistOrUndoChanges(IAtsChangeSet changes) {
      if (data.isPersistChanges()) {
         changes.execute();

         /**
          * Clear server and client caches so these new ATS Config objects are seen. This will only work in a singleton
          * server environment.
          */
         AtsApiService.get().reloadServerAndClientCaches();
         AtsApiService.get().clearCaches();
      } else {
         resultData.log("\n\nCleanup of created / modified artifacts\n\n");
         for (Artifact artifact : newArtifacts) {
            if (artifact.isInDb()) {
               resultData.errorf("Attempt to purge artifact in db [%s]", artifact);
            } else {
               resultData.log("purging " + artifact.toStringWithId());
               artifact.purgeFromBranch();
            }
         }
         for (Artifact artifact : existingArtifacts) {
            if (artifact.isInDb()) {
               resultData.log("undoing changes " + artifact.toStringWithId());
               artifact.reloadAttributesAndRelations();
            } else {
               resultData.errorf("Attempt to reload artifact not in db [%s]", artifact);
            }
         }
      }
   }

   private Pair<Artifact, IAtsConfigObject> duplicateTeamDefinitionOrActionableItem(IAtsChangeSet changes,
      Artifact fromArt) {
      String newName = CopyAtsUtil.getConvertedName(data, fromArt.getName());
      if (newName.equals(fromArt.getName())) {
         throw new OseeArgumentException("Could not get new name from name conversion.");
      }
      // duplicate all but baseline branch id
      Artifact newArt =
         fromArt.duplicate(AtsApiService.get().getAtsBranch(), Arrays.asList(AtsAttributeTypes.BaselineBranchId));
      IAtsConfigObject newConfigObj = null;
      if (fromArt.isOfType(AtsArtifactTypes.TeamDefinition)) {
         newConfigObj = AtsApiService.get().getTeamDefinitionService().createTeamDefinition(newArt);
      } else if (fromArt.isOfType(AtsArtifactTypes.ActionableItem)) {
         newConfigObj = AtsApiService.get().getActionableItemService().createActionableItem(newArt);
         newAiArtToNewAi.put(newArt, (IAtsActionableItem) newConfigObj);
      } else {
         throw new OseeArgumentException("Unexpected artifact type %s", fromArt.getArtifactTypeName());
      }
      newArt.setName(newName);
      changes.add(newArt);
      resultData.log("Creating new " + newArt.getArtifactTypeName() + ": " + newArt);
      String fullName = newArt.getSoleAttributeValue(AtsAttributeTypes.FullName, null);
      if (fullName != null) {
         String newFullName = CopyAtsUtil.getConvertedName(data, fullName);
         if (!newFullName.equals(fullName)) {
            newArt.setSoleAttributeFromString(AtsAttributeTypes.FullName, newFullName);
            resultData.log("   - Converted \"ats.Full Name\" to " + newFullName);
         }
      }
      if (data.getNewProgramId() != null) {
         changes.setSoleAttributeFromString(newArt, AtsAttributeTypes.ProgramId, data.getNewProgramId().toString());
      }
      newArtifacts.add(newArt);
      return new Pair<Artifact, IAtsConfigObject>(newArt, newConfigObj);
   }

}
