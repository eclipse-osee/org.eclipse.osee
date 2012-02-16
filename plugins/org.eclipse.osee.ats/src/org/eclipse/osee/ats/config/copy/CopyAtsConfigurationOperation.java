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
import org.eclipse.osee.ats.core.client.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.client.config.AtsLoadConfigArtifactsOperation;
import org.eclipse.osee.ats.core.client.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.client.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.type.AtsRelationTypes;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

public class CopyAtsConfigurationOperation extends AbstractOperation {

   private final ConfigData data;
   protected XResultData resultData;
   Set<Artifact> newArtifacts;
   Set<Artifact> existingArtifacts;
   Set<Artifact> processedFromAis;

   private final Map<TeamDefinitionArtifact, TeamDefinitionArtifact> fromTeamDefToNewTeamDefMap =
      new HashMap<TeamDefinitionArtifact, TeamDefinitionArtifact>();

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

         getCopyAtsValidation().validate();
         if (resultData.isErrors()) {
            persistOrUndoChanges();
            return;
         }

         if (data.isPersistChanges()) {
            resultData.log("Persisting Changes");
         } else {
            resultData.log("Report-Only, Changes are not persisted");
         }

         newArtifacts = new HashSet<Artifact>(50);
         existingArtifacts = new HashSet<Artifact>(50);
         processedFromAis = new HashSet<Artifact>(10);

         createTeamDefinitions(data.getTeamDef(), data.getParentTeamDef());
         if (resultData.isErrors()) {
            persistOrUndoChanges();
            return;
         }

         createActionableItems(data.getActionableItem(), data.getParentActionableItem());

         if (resultData.isErrors()) {
            persistOrUndoChanges();
            return;
         }

         persistOrUndoChanges();
         XResultDataUI.report(resultData, getName());
      } finally {
         monitor.subTask("Done");
      }
   }

   /**
    * Has potential of returning null if this fromAi has already been processed.
    */
   protected ActionableItemArtifact createActionableItems(ActionableItemArtifact fromAi, ActionableItemArtifact parentAiArt) throws OseeCoreException {
      if (processedFromAis.contains(fromAi)) {
         resultData.log(String.format("Skipping already processed fromAi [%s]", fromAi));
         return null;
      } else {
         processedFromAis.add(fromAi);
      }
      // Get or create new team definition
      ActionableItemArtifact newAiArt = (ActionableItemArtifact) duplicateTeamDefinitionOrActionableItem(fromAi);
      parentAiArt.addChild(newAiArt);
      existingArtifacts.add(parentAiArt);
      newArtifacts.add(newAiArt);
      // Relate new Ais to their TeamDefs just like other config
      for (Artifact fromTeamDefArt : fromAi.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_Team,
         TeamDefinitionArtifact.class)) {
         TeamDefinitionArtifact newTeamDefArt = fromTeamDefToNewTeamDefMap.get(fromTeamDefArt);
         if (newTeamDefArt == null) {
            resultData.logWarningWithFormat(
               "No related Team Definition [%s] in scope for AI [%s].  Configure by hand.", fromTeamDefArt, newAiArt);
         } else {
            newAiArt.addRelation(AtsRelationTypes.TeamActionableItem_Team, newTeamDefArt);
         }
      }
      // Handle all children
      for (Artifact childFromAiArt : fromAi.getChildren()) {
         if (childFromAiArt instanceof ActionableItemArtifact) {
            createActionableItems((ActionableItemArtifact) childFromAiArt, newAiArt);
         }
      }
      return newAiArt;
   }

   protected TeamDefinitionArtifact createTeamDefinitions(TeamDefinitionArtifact fromTeamDef, TeamDefinitionArtifact parentTeamDef) throws OseeCoreException {
      // Get or create new team definition
      TeamDefinitionArtifact newTeamDef = (TeamDefinitionArtifact) duplicateTeamDefinitionOrActionableItem(fromTeamDef);
      parentTeamDef.addChild(newTeamDef);
      existingArtifacts.add(parentTeamDef);
      newArtifacts.add(newTeamDef);
      fromTeamDefToNewTeamDefMap.put(fromTeamDef, newTeamDef);
      if (data.isRetainTeamLeads()) {
         duplicateTeamLeadsAndMembers(fromTeamDef, newTeamDef);
      }
      // handle all children
      for (Artifact childFromTeamDef : fromTeamDef.getChildren()) {
         if (childFromTeamDef instanceof TeamDefinitionArtifact) {
            createTeamDefinitions((TeamDefinitionArtifact) childFromTeamDef, newTeamDef);
         }
      }
      return newTeamDef;
   }

   private void duplicateTeamLeadsAndMembers(TeamDefinitionArtifact fromTeamDef, TeamDefinitionArtifact newTeamDef) throws OseeCoreException {
      Collection<Artifact> leads = newTeamDef.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead);
      for (Artifact user : fromTeamDef.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead)) {
         if (!leads.contains(user)) {
            existingArtifacts.add(user);
            newTeamDef.addRelation(AtsRelationTypes.TeamLead_Lead, user);
            resultData.log("   - Relating team lead " + user);
         }
      }
      Collection<Artifact> members = newTeamDef.getRelatedArtifacts(AtsRelationTypes.TeamMember_Member);
      for (Artifact user : fromTeamDef.getRelatedArtifacts(AtsRelationTypes.TeamMember_Member)) {
         if (!members.contains(user)) {
            existingArtifacts.add(user);
            newTeamDef.addRelation(AtsRelationTypes.TeamMember_Member, user);
            resultData.log("   - Relating team member " + user);
         }
      }
      for (Artifact user : fromTeamDef.getRelatedArtifacts(AtsRelationTypes.PrivilegedMember_Member)) {
         if (!members.contains(user)) {
            existingArtifacts.add(user);
            newTeamDef.addRelation(AtsRelationTypes.PrivilegedMember_Member, user);
            resultData.log("   - Relating privileged member " + user);
         }
      }
   }

   private void persistOrUndoChanges() throws OseeCoreException {
      if (data.isPersistChanges()) {
         Artifacts.persistInTransaction("Copy ATS Configuration", newArtifacts);
         AtsLoadConfigArtifactsOperation operation = new AtsLoadConfigArtifactsOperation();
         operation.forceReload();
      } else {
         resultData.log("\n\nCleanup of created / modified artifacts\n\n");
         for (Artifact artifact : newArtifacts) {
            if (artifact.isInDb()) {
               resultData.logErrorWithFormat("Attempt to purge artifact in db [%s]", artifact);
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
               resultData.logErrorWithFormat("Attempt to reload artifact not in db [%s]", artifact);
            }
         }
      }
   }

   private Artifact duplicateTeamDefinitionOrActionableItem(Artifact fromArtifact) throws OseeCoreException {
      String newName = CopyAtsUtil.getConvertedName(data, fromArtifact.getName());
      if (newName.equals(fromArtifact.getName())) {
         throw new OseeArgumentException("Could not get new name from name conversion.");
      }
      // duplicate all but baseline branch guid
      Artifact newTeamDef =
         fromArtifact.duplicate(AtsUtil.getAtsBranch(), Arrays.asList(AtsAttributeTypes.BaselineBranchGuid));
      newTeamDef.setName(newName);
      resultData.log("Creating new " + newTeamDef.getArtifactTypeName() + ": " + newTeamDef);
      String fullName = newTeamDef.getSoleAttributeValue(AtsAttributeTypes.FullName, null);
      if (fullName != null) {
         String newFullName = CopyAtsUtil.getConvertedName(data, fullName);
         if (!newFullName.equals(fullName)) {
            newTeamDef.setSoleAttributeFromString(AtsAttributeTypes.FullName, newFullName);
            resultData.log("   - Converted \"ats.Full Name\" to " + newFullName);
         }
      }
      newArtifacts.add(newTeamDef);
      return newTeamDef;
   }

}
