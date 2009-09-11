/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.AtsFolderUtil;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.AtsFolderUtil.AtsFolder;
import org.eclipse.osee.ats.workflow.editor.AtsWorkflowConfigEditor;
import org.eclipse.osee.ats.workflow.editor.wizard.AtsWorkflowConfigCreationWizard;
import org.eclipse.osee.ats.workflow.editor.wizard.AtsWorkflowConfigCreationWizard.WorkflowData;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigManager {

   /**
    * This method creates a simple configuration of ATS given teamdef name, version names (if desired), actionable items
    * and workflow id.
    * 
    * @param namespace
    * @param teamDefName - name of team definition to use
    * @param versionNames - list of version names (if team is using versions)
    * @param actionableItems - list of actionable items
    * @param workflowId - workflowId to use (if null, workflow will be created)
    * @return Result of creation
    * @throws OseeCoreException
    */
   public static Result configureAtsForDefaultTeam(String namespace, String teamDefName, Collection<String> versionNames, Collection<String> actionableItems, String workflowId) throws OseeCoreException {
      try {
         if (WorkItemDefinitionFactory.getWorkItemDefinition(namespace) != null) {
            return new Result("Configuration Namespace already used, choose a unique namespace.");
         }
      } catch (OseeCoreException ex) {
         // do nothing
      }

      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());

      // Create team def
      TeamDefinitionArtifact teamDef =
            (TeamDefinitionArtifact) ArtifactTypeManager.addArtifact(TeamDefinitionArtifact.ARTIFACT_NAME,
                  AtsUtil.getAtsBranch(), teamDefName);
      if (versionNames == null || versionNames.size() > 0) {
         teamDef.setSoleAttributeValue(ATSAttributes.TEAM_USES_VERSIONS_ATTRIBUTE.getStoreName(), true);
      }
      teamDef.addRelation(AtsRelation.TeamLead_Lead, UserManager.getUser());
      teamDef.addRelation(AtsRelation.TeamMember_Member, UserManager.getUser());
      AtsFolderUtil.getFolder(AtsFolder.Teams).addChild(teamDef);
      teamDef.persist(transaction);

      // Create actionable items
      List<ActionableItemArtifact> aias = new ArrayList<ActionableItemArtifact>();
      // Create top actionable item
      ActionableItemArtifact topAia =
            (ActionableItemArtifact) ArtifactTypeManager.addArtifact(ActionableItemArtifact.ARTIFACT_NAME,
                  AtsUtil.getAtsBranch(), teamDefName);
      topAia.setSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), false);
      topAia.persist(transaction);

      AtsFolderUtil.getFolder(AtsFolder.ActionableItem).addChild(topAia);
      teamDef.addRelation(AtsRelation.TeamActionableItem_ActionableItem, topAia);
      teamDef.persist(transaction);

      aias.add(topAia);
      // Create childrent actionable item
      for (String name : actionableItems) {
         ActionableItemArtifact aia =
               (ActionableItemArtifact) ArtifactTypeManager.addArtifact(ActionableItemArtifact.ARTIFACT_NAME,
                     AtsUtil.getAtsBranch(), name);
         aia.setSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), true);
         topAia.addChild(aia);
         aia.persist(transaction);
         aias.add(aia);
      }

      // Create versions
      List<VersionArtifact> versions = new ArrayList<VersionArtifact>();
      if (versionNames != null) {
         for (String name : versionNames) {
            VersionArtifact version =
                  (VersionArtifact) ArtifactTypeManager.addArtifact(VersionArtifact.ARTIFACT_NAME,
                        AtsUtil.getAtsBranch(), name);
            teamDef.addRelation(AtsRelation.TeamDefinitionToVersion_Version, version);
            versions.add(version);
            version.persist(transaction);
         }
      }

      // create workflow
      Artifact workflowArt =
            ArtifactQuery.checkArtifactFromTypeAndName(WorkFlowDefinition.ARTIFACT_NAME, workflowId,
                  AtsUtil.getAtsBranch());
      WorkFlowDefinition workFlowDefinition;
      // If can't be found, create a new one
      if (workflowArt == null) {
         WorkflowData workflowData =
               AtsWorkflowConfigCreationWizard.generateDefaultWorkflow(namespace, transaction, teamDef);
         workFlowDefinition = workflowData.getWorkDefinition();
         workflowArt = workflowData.getWorkFlowArtifact();
         workflowArt.persist(transaction);
      }
      // Else, use existing one
      else {
         workFlowDefinition = (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(workflowId);
      }
      // Relate new team def to workflow artifact
      teamDef.addRelation(AtsRelation.WorkItem__Child, workflowArt);
      teamDef.persist(transaction);

      transaction.execute();

      // open everything in editors
      AtsUtil.openAtsAction(teamDef, AtsOpenOption.OpenAll);
      for (ActionableItemArtifact aia : aias) {
         AtsUtil.openAtsAction(aia, AtsOpenOption.OpenAll);
      }
      AtsWorkflowConfigEditor.editWorkflow(workFlowDefinition);

      return Result.TrueResult;

   }
}
