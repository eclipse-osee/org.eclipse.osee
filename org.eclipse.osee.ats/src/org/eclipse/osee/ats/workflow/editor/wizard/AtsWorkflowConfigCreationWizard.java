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
import org.eclipse.osee.ats.workflow.editor.AtsWorkflowConfigEditor;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsEndorseWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsImplementWorkPageDefinition;
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
         if (WorkItemDefinitionFactory.getWorkItemDefinition(namespace) != null) {
            AWorkbench.popup("ERROR", "Namespace already used, choose a unique namespace.");
            return false;
         }

         WorkFlowDefinition workflow = new WorkFlowDefinition(namespace, namespace, null);
         WorkPageDefinition endorsePage =
               new WorkPageDefinition("Endorse", namespace + ".Endorse", AtsEndorseWorkPageDefinition.ID);

         workflow.setStartPageId(endorsePage.getId());

         WorkPageDefinition implementPage =
               new WorkPageDefinition("Implement", namespace + ".Implement", AtsImplementWorkPageDefinition.ID);

         WorkPageDefinition completedPage =
               new WorkPageDefinition("Complete", namespace + ".Completed", AtsCompletedWorkPageDefinition.ID);

         WorkPageDefinition cancelledPage =
               new WorkPageDefinition("Cancelled", namespace + ".Cancelled", AtsCancelledWorkPageDefinition.ID);

         workflow.addPageTransition(endorsePage.getId(), implementPage.getId(), TransitionType.ToPageAsDefault);
         workflow.addPageTransition(implementPage.getId(), endorsePage.getId(), TransitionType.ToPageAsReturn);
         workflow.addPageTransition(implementPage.getId(), completedPage.getId(), TransitionType.ToPageAsDefault);
         workflow.addPageTransition(endorsePage.getId(), cancelledPage.getId(), TransitionType.ToPage);

         List<Artifact> artifacts = new ArrayList<Artifact>();
         artifacts.add(endorsePage.toArtifact(WriteType.New));
         artifacts.add(implementPage.toArtifact(WriteType.New));
         artifacts.add(completedPage.toArtifact(WriteType.New));
         artifacts.add(cancelledPage.toArtifact(WriteType.New));
         artifacts.add(workflow.toArtifact(WriteType.New));

         SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         for (Artifact artifact : artifacts) {
            artifact.persistAttributesAndRelations(transaction);
            AtsWorkDefinitions.addUpdateWorkItemToDefaultHeirarchy(artifact, transaction);
         }
         transaction.execute();

         AtsWorkflowConfigEditor.editWorkflow(workflow);
         workflow.loadPageData(true);

      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return true;

   }
}
