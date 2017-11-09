/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config.copy;

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
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
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

   private final Map<IAtsTeamDefinition, IAtsTeamDefinition> fromTeamDefToNewTeamDefMap =
      new HashMap<IAtsTeamDefinition, IAtsTeamDefinition>();

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
         IAtsChangeSet changes = AtsClientService.get().createChangeSet(getName());

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

         AtsBulkLoad.reloadConfig(true);
         persistOrUndoChanges(changes);
         XResultDataUI.report(resultData, getName());
      } finally {
         monitor.subTask("Done");
      }
   }

   /**
    * Has potential of returning null if this fromAi has already been processed.
    */
   protected IAtsActionableItem createActionableItems(IAtsChangeSet changes, IAtsActionableItem fromAi, IAtsActionableItem parentAi) {
      Artifact fromAiArt = AtsClientService.get().getConfigArtifact(fromAi);

      if (processedFromAis.contains(fromAiArt)) {
         resultData.log(String.format("Skipping already processed fromAi [%s]", fromAiArt));
         return null;
      } else {
         processedFromAis.add(fromAiArt);
      }
      Artifact parentAiArt = AtsClientService.get().getArtifact(parentAi);

      // Get or create new team definition
      Artifact newAiArt = duplicateTeamDefinitionOrActionableItem(changes, fromAiArt);
      changes.add(newAiArt);
      IAtsActionableItem newAi = AtsClientService.get().getConfigItem(newAiArt);
      changes.relate(parentAiArt, CoreRelationTypes.Default_Hierarchical__Child, newAi);
      existingArtifacts.add(parentAiArt);
      newArtifacts.add(newAiArt);
      // Relate new Ais to their TeamDefs just like other config
      for (Artifact fromTeamDefArt : fromAiArt.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_Team,
         Artifact.class)) {
         IAtsConfigObject fromTeamDef = AtsClientService.get().getCache().getAtsObject(fromTeamDefArt.getId());
         IAtsTeamDefinition newTeamDef = fromTeamDefToNewTeamDefMap.get(fromTeamDef);

         if (newTeamDef == null) {
            resultData.warningf("No related Team Definition [%s] in scope for AI [%s].  Configure by hand.",
               fromTeamDefArt, newAiArt);
         } else {
            Artifact newTeamDefArt = AtsClientService.get().getConfigArtifact(newTeamDef);
            newAiArt.addRelation(AtsRelationTypes.TeamActionableItem_Team, newTeamDefArt);
            changes.add(newTeamDefArt);
         }
      }

      // Handle all children
      for (Artifact childFromAiArt : fromAiArt.getChildren()) {
         if (childFromAiArt.isOfType(AtsArtifactTypes.ActionableItem)) {
            IAtsActionableItem childAi = AtsClientService.get().getConfigItem(childFromAiArt);
            IAtsActionableItem newChildAi = AtsClientService.get().getConfigItem(newAiArt);
            createActionableItems(changes, childAi, newChildAi);
         }
      }
      return newAi;
   }

   protected IAtsTeamDefinition createTeamDefinitions(IAtsChangeSet changes, IAtsTeamDefinition fromTeamDef, IAtsTeamDefinition parentTeamDef) {
      // Get or create new team definition
      Artifact parentTeamDefArt = AtsClientService.get().getConfigArtifact(parentTeamDef);
      Artifact fromTeamDefArt = AtsClientService.get().getConfigArtifact(fromTeamDef);

      Artifact newTeamDefArt = duplicateTeamDefinitionOrActionableItem(changes, fromTeamDefArt);
      changes.add(newTeamDefArt);
      IAtsTeamDefinition newTeamDef = AtsClientService.get().getConfigItem(newTeamDefArt);

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
            IAtsTeamDefinition childFromTeamDef = AtsClientService.get().getConfigItem(childFromTeamDefArt);
            AtsClientService.get().getCache().getAtsObject(childFromTeamDefArt.getId());
            createTeamDefinitions(changes, childFromTeamDef, newTeamDef);
         }
      }
      return newTeamDef;
   }

   private void duplicateTeamLeadsAndMembers(IAtsChangeSet changes, IAtsTeamDefinition fromTeamDef, IAtsTeamDefinition newTeamDef) {
      Artifact fromTeamDefArt = AtsClientService.get().getConfigArtifact(fromTeamDef);
      Artifact newTeamDefArt = AtsClientService.get().getConfigArtifact(newTeamDef);

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
      for (Artifact user : fromTeamDefArt.getRelatedArtifacts(AtsRelationTypes.PrivilegedMember_Member)) {
         if (!members.contains(user)) {
            existingArtifacts.add(user);
            changes.add(user);
            newTeamDefArt.addRelation(AtsRelationTypes.PrivilegedMember_Member, user);
            resultData.log("   - Relating privileged member " + user);
         }
      }
   }

   private void persistOrUndoChanges(IAtsChangeSet changes) {
      if (data.isPersistChanges()) {
         changes.execute();
         AtsClientService.get().clearCaches();
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

   private Artifact duplicateTeamDefinitionOrActionableItem(IAtsChangeSet changes, Artifact fromArtifact) {
      String newName = CopyAtsUtil.getConvertedName(data, fromArtifact.getName());
      if (newName.equals(fromArtifact.getName())) {
         throw new OseeArgumentException("Could not get new name from name conversion.");
      }
      // duplicate all but baseline branch id
      Artifact newTeamDef = fromArtifact.duplicate(AtsClientService.get().getAtsBranch(),
         Arrays.asList(AtsAttributeTypes.BaselineBranchId));
      newTeamDef.setName(newName);
      changes.add(newTeamDef);
      resultData.log("Creating new " + newTeamDef.getArtifactTypeName() + ": " + newTeamDef);
      String fullName = newTeamDef.getSoleAttributeValue(AtsAttributeTypes.FullName, null);
      if (fullName != null) {
         String newFullName = CopyAtsUtil.getConvertedName(data, fullName);
         if (!newFullName.equals(fullName)) {
            newTeamDef.setSoleAttributeFromString(AtsAttributeTypes.FullName, newFullName);
            resultData.log("   - Converted \"ats.Full Name\" to " + newFullName);
         }
      }
      if (data.getNewProgramId() != null) {
         changes.setSoleAttributeFromString(newTeamDef, AtsAttributeTypes.ProgramId, data.getNewProgramId().toString());
      }
      newArtifacts.add(newTeamDef);
      return newTeamDef;
   }

}
