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
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
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
   private SkynetTransaction transaction;

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
         transaction = TransactionManager.createTransaction(AtsUtil.getAtsBranchToken(), getClass().getSimpleName());

         getCopyAtsValidation().validate();
         if (resultData.isErrors()) {
            persistOrUndoChanges(transaction);
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

         createTeamDefinitions(transaction, data.getTeamDef(), data.getParentTeamDef());
         if (resultData.isErrors()) {
            persistOrUndoChanges(transaction);
            return;
         }

         createActionableItems(transaction, data.getActionableItem(), data.getParentActionableItem());

         if (resultData.isErrors()) {
            persistOrUndoChanges(transaction);
            return;
         }

         AtsBulkLoad.reloadConfig(true);
         persistOrUndoChanges(transaction);
         XResultDataUI.report(resultData, getName());
      } finally {
         monitor.subTask("Done");
      }
   }

   /**
    * Has potential of returning null if this fromAi has already been processed.
    * 
    * @param transaction
    */
   protected IAtsActionableItem createActionableItems(SkynetTransaction transaction, IAtsActionableItem fromAi, IAtsActionableItem parentAi) throws OseeCoreException {
      Artifact fromAiArt = AtsClientService.get().getConfigArtifact(fromAi);

      if (processedFromAis.contains(fromAiArt)) {
         resultData.log(String.format("Skipping already processed fromAi [%s]", fromAiArt));
         return null;
      } else {
         processedFromAis.add(fromAiArt);
      }
      Artifact parentAiArt = AtsClientService.get().storeConfigObject(parentAi, transaction);

      // Get or create new team definition
      Artifact newAiArt = duplicateTeamDefinitionOrActionableItem(fromAiArt);
      IAtsActionableItem newAi = AtsClientService.get().getConfigObject(newAiArt);
      newAi.setParentActionableItem(parentAi);
      parentAi.getChildrenActionableItems().add(newAi);

      parentAiArt.addChild(newAiArt);
      existingArtifacts.add(parentAiArt);
      newArtifacts.add(newAiArt);
      // Relate new Ais to their TeamDefs just like other config
      for (Artifact fromTeamDefArt : fromAiArt.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_Team,
         Artifact.class)) {
         IAtsConfigObject fromTeamDef =
            AtsClientService.get().getAtsConfig().getSoleByGuid(fromTeamDefArt.getGuid(), IAtsTeamDefinition.class);
         IAtsTeamDefinition newTeamDef = fromTeamDefToNewTeamDefMap.get(fromTeamDef);

         if (newTeamDef == null) {
            resultData.logWarningWithFormat(
               "No related Team Definition [%s] in scope for AI [%s].  Configure by hand.", fromTeamDefArt, newAiArt);
         } else {
            Artifact newTeamDefArt = AtsClientService.get().getConfigArtifact(newTeamDef);
            newAiArt.addRelation(AtsRelationTypes.TeamActionableItem_Team, newTeamDefArt);
            if (data.isPersistChanges()) {
               newTeamDefArt.persist(transaction);
            }
         }
      }

      // Handle all children
      for (Artifact childFromAiArt : fromAiArt.getChildren()) {
         if (childFromAiArt.isOfType(AtsArtifactTypes.ActionableItem)) {
            IAtsActionableItem childAi = AtsClientService.get().getConfigObject(childFromAiArt);
            IAtsActionableItem newChildAi = AtsClientService.get().getConfigObject(newAiArt);
            createActionableItems(transaction, childAi, newChildAi);
         }
      }
      return newAi;
   }

   protected IAtsTeamDefinition createTeamDefinitions(SkynetTransaction transaction, IAtsTeamDefinition fromTeamDef, IAtsTeamDefinition parentTeamDef) throws OseeCoreException {
      // Get or create new team definition
      Artifact parentTeamDefArt = AtsClientService.get().getConfigArtifact(parentTeamDef);
      Artifact fromTeamDefArt = AtsClientService.get().getConfigArtifact(fromTeamDef);

      Artifact newTeamDefArt = duplicateTeamDefinitionOrActionableItem(fromTeamDefArt);
      IAtsTeamDefinition newTeamDef = AtsClientService.get().getConfigObject(newTeamDefArt);

      parentTeamDefArt.addChild(newTeamDefArt);
      existingArtifacts.add(parentTeamDefArt);
      newArtifacts.add(newTeamDefArt);
      fromTeamDefToNewTeamDefMap.put(fromTeamDef, newTeamDef);
      if (data.isRetainTeamLeads()) {
         duplicateTeamLeadsAndMembers(fromTeamDef, newTeamDef);
      }
      // handle all children
      for (Artifact childFromTeamDefArt : fromTeamDefArt.getChildren()) {
         if (childFromTeamDefArt.isOfType(AtsArtifactTypes.TeamDefinition)) {
            IAtsTeamDefinition childFromTeamDef = AtsClientService.get().getConfigObject(childFromTeamDefArt);
            AtsClientService.get().getAtsConfig().getSoleByGuid(childFromTeamDefArt.getGuid(), IAtsTeamDefinition.class);
            createTeamDefinitions(transaction, childFromTeamDef, newTeamDef);
         }
      }
      return newTeamDef;
   }

   private void duplicateTeamLeadsAndMembers(IAtsTeamDefinition fromTeamDef, IAtsTeamDefinition newTeamDef) throws OseeCoreException {
      Artifact fromTeamDefArt = AtsClientService.get().getConfigArtifact(fromTeamDef);
      Artifact newTeamDefArt = AtsClientService.get().getConfigArtifact(newTeamDef);

      Collection<Artifact> leads = newTeamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead);
      for (Artifact user : fromTeamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead)) {
         if (!leads.contains(user)) {
            existingArtifacts.add(user);
            newTeamDefArt.addRelation(AtsRelationTypes.TeamLead_Lead, user);
            resultData.log("   - Relating team lead " + user);
         }
      }
      Collection<Artifact> members = newTeamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamMember_Member);
      for (Artifact user : fromTeamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamMember_Member)) {
         if (!members.contains(user)) {
            existingArtifacts.add(user);
            newTeamDefArt.addRelation(AtsRelationTypes.TeamMember_Member, user);
            resultData.log("   - Relating team member " + user);
         }
      }
      for (Artifact user : fromTeamDefArt.getRelatedArtifacts(AtsRelationTypes.PrivilegedMember_Member)) {
         if (!members.contains(user)) {
            existingArtifacts.add(user);
            newTeamDefArt.addRelation(AtsRelationTypes.PrivilegedMember_Member, user);
            resultData.log("   - Relating privileged member " + user);
         }
      }
   }

   private void persistOrUndoChanges(SkynetTransaction transaction) throws OseeCoreException {
      if (data.isPersistChanges()) {
         for (Artifact art : newArtifacts) {
            art.persist(transaction);
         }
         transaction.execute();
         AtsClientService.get().invalidateConfigCache();
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
