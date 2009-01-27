/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Donald G. Dunne - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config.wizard;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.AtsConfig;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.workflow.editor.AtsWorkflowConfigEditor;
import org.eclipse.osee.ats.workflow.editor.wizard.AtsWorkflowConfigCreationWizard;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Create new new .shape-file. Those files can be used with the ShapesEditor (see plugin.xml).
 * 
 * @author Donald G. Dunne
 */
public class AtsConfigWizard extends Wizard implements INewWizard {

   private AtsConfigWizardPage1 page1;

   /* (non-Javadoc)
    * @see org.eclipse.jface.wizard.IWizard#addPages()
    */
   @Override
   public void addPages() {
      // add pages to this wizard
      addPage(page1);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
    */
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      // create pages for this wizard
      page1 = new AtsConfigWizardPage1(this);
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.wizard.IWizard#performFinish()
    */
   @Override
   public boolean performFinish() {
      final String namespace = page1.getNamespace();
      try {
         try {
            if (WorkItemDefinitionFactory.getWorkItemDefinition(namespace) != null) {
               AWorkbench.popup("ERROR", "Configuration Namespace already used, choose a unique namespace.");
               return false;
            }
         } catch (OseeCoreException ex) {
            // do nothing
         }

         SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());

         // Create team def
         TeamDefinitionArtifact teamDef =
               (TeamDefinitionArtifact) ArtifactTypeManager.addArtifact(TeamDefinitionArtifact.ARTIFACT_NAME,
                     AtsPlugin.getAtsBranch(), page1.getTeamDefName());
         if (page1.getVersions().size() > 0) {
            teamDef.setSoleAttributeValue(ATSAttributes.TEAM_USES_VERSIONS_ATTRIBUTE.getStoreName(), true);
         }
         teamDef.addRelation(AtsRelation.TeamLead_Lead, UserManager.getUser());
         teamDef.addRelation(AtsRelation.TeamMember_Member, UserManager.getUser());
         AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact(transaction).addChild(teamDef);

         // Create actionable items
         List<ActionableItemArtifact> aias = new ArrayList<ActionableItemArtifact>();
         for (String name : page1.getActionableItems()) {
            ActionableItemArtifact aia =
                  (ActionableItemArtifact) ArtifactTypeManager.addArtifact(ActionableItemArtifact.ARTIFACT_NAME,
                        AtsPlugin.getAtsBranch(), name);
            aia.setSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), true);
            AtsConfig.getInstance().getOrCreateActionableItemsHeadingArtifact(transaction).addChild(aia);
            teamDef.addRelation(AtsRelation.TeamActionableItem_ActionableItem, aia);
            aias.add(aia);
         }

         // Create versions
         List<VersionArtifact> versions = new ArrayList<VersionArtifact>();
         for (String name : page1.getVersions()) {
            VersionArtifact aia =
                  (VersionArtifact) ArtifactTypeManager.addArtifact(VersionArtifact.ARTIFACT_NAME,
                        AtsPlugin.getAtsBranch(), name);
            teamDef.addRelation(AtsRelation.TeamDefinitionToVersion_Version, aia);
            versions.add(aia);
         }

         // create workflow
         WorkFlowDefinition workFlowDefinition =
               AtsWorkflowConfigCreationWizard.generateWorkflow(namespace, transaction, teamDef);

         // persist everything
         teamDef.persistAttributesAndRelations(transaction);
         for (Artifact artifact : aias) {
            artifact.persistAttributesAndRelations(transaction);
         }
         for (Artifact artifact : versions) {
            artifact.persistAttributesAndRelations(transaction);
         }
         transaction.execute();

         // open everything in editors
         AtsLib.openAtsAction(teamDef, AtsOpenOption.OpenAll);
         for (ActionableItemArtifact aia : aias) {
            AtsLib.openAtsAction(aia, AtsOpenOption.OpenAll);
         }
         AtsWorkflowConfigEditor.editWorkflow(workFlowDefinition);

      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return true;

   }
}
