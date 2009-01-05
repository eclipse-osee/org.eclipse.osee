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
package org.eclipse.osee.ats.workflow.editor.wizard;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.workflow.editor.AtsWorkflowConfigEditor;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsEndorseWorkPageDefinition;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition.TransitionType;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Create new new .shape-file. Those files can be used with the ShapesEditor (see plugin.xml).
 * 
 * @author Donald G. Dunne
 */
public class AtsWorkflowConfigCreationWizard extends Wizard implements INewWizard {

   private NewWorkflowConfigPage1 page1;

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
      page1 = new NewWorkflowConfigPage1(this);
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
               AWorkbench.popup("ERROR", "Namespace already used, choose a unique namespace.");
               return false;
            }
         } catch (OseeCoreException ex) {
            // do nothing
         }
         SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         WorkFlowDefinition workflow = generateWorkflow(namespace, transaction, null);
         transaction.execute();

         AtsWorkflowConfigEditor.editWorkflow(workflow);

      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return true;

   }

   public static WorkFlowDefinition generateWorkflow(String namespace, SkynetTransaction transaction, TeamDefinitionArtifact teamDef) throws OseeCoreException {
      WorkFlowDefinition workflow = new WorkFlowDefinition(namespace, namespace, null);
      WorkPageDefinition endorsePage =
            new WorkPageDefinition("Endorse", namespace + ".Endorse", AtsEndorseWorkPageDefinition.ID);

      workflow.setStartPageId(endorsePage.getPageName());

      WorkPageDefinition implementPage = new WorkPageDefinition("Implement", namespace + ".Implement", null);
      implementPage.addWorkItem(RuleWorkItemId.atsRequireStateHourSpentPrompt.name());
      implementPage.addWorkItem(ATSAttributes.WORK_PACKAGE_ATTRIBUTE.getStoreName());
      implementPage.addWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());

      WorkPageDefinition completedPage =
            new WorkPageDefinition("Completed", namespace + ".Completed", AtsCompletedWorkPageDefinition.ID);

      WorkPageDefinition cancelledPage =
            new WorkPageDefinition("Cancelled", namespace + ".Cancelled", AtsCancelledWorkPageDefinition.ID);

      workflow.addPageTransition(endorsePage.getPageName(), implementPage.getPageName(), TransitionType.ToPageAsDefault);
      workflow.addPageTransition(implementPage.getPageName(), endorsePage.getPageName(), TransitionType.ToPageAsReturn);
      workflow.addPageTransition(cancelledPage.getPageName(), endorsePage.getPageName(), TransitionType.ToPageAsReturn);
      workflow.addPageTransition(implementPage.getPageName(), completedPage.getPageName(),
            TransitionType.ToPageAsDefault);
      workflow.addPageTransition(endorsePage.getPageName(), cancelledPage.getPageName(), TransitionType.ToPage);

      List<Artifact> artifacts = new ArrayList<Artifact>();
      artifacts.add(endorsePage.toArtifact(WriteType.New));
      artifacts.add(implementPage.toArtifact(WriteType.New));
      artifacts.add(completedPage.toArtifact(WriteType.New));
      artifacts.add(cancelledPage.toArtifact(WriteType.New));
      Artifact workflowArt = workflow.toArtifact(WriteType.New);
      if (teamDef != null) {
         teamDef.addRelation(AtsRelation.WorkItem__Child, workflowArt);
         artifacts.add(teamDef);
      }
      artifacts.add(workflowArt);
      workflow.loadPageData(true);

      for (Artifact artifact : artifacts) {
         AtsWorkDefinitions.addUpdateWorkItemToDefaultHeirarchy(artifact, transaction);
         artifact.persistAttributesAndRelations(transaction);
      }
      return workflow;
   }
}
