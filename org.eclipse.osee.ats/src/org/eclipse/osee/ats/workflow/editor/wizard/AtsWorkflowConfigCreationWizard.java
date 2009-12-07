/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
�* All rights reserved. This program and the accompanying materials
�* are made available under the terms of the Eclipse Public License v1.0
�* which accompanies this distribution, and is available at
�* http://www.eclipse.org/legal/epl-v10.html
�*
�* Contributors:
�*����Donald G. Dunne - initial API and implementation
�*******************************************************************************/
package org.eclipse.osee.ats.workflow.editor.wizard;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workflow.editor.AtsWorkflowConfigEditor;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsEndorseWorkPageDefinition;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemAttributes;
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

   @Override
   public void addPages() {
      // add pages to this wizard
      addPage(page1);
   }

   public void init(IWorkbench workbench, IStructuredSelection selection) {
      // create pages for this wizard
      page1 = new NewWorkflowConfigPage1(this);
   }

   @Override
   public boolean performFinish() {

      try {
         final String namespace = page1.getNamespace();
         final String startingWorkflow = page1.getStartingWorkflow();
         try {
            if (WorkItemDefinitionFactory.getWorkItemDefinition(namespace) != null) {
               AWorkbench.popup("ERROR", "Namespace already used, choose a unique namespace.");
               return false;
            }
         } catch (OseeCoreException ex) {
            // do nothing
         }
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Config Wizard");
         WorkFlowDefinition workflow = null;
         if (startingWorkflow.contains("Simple")) {
            workflow = generateSimpleWorkflow(namespace, transaction, null).getWorkDefinition();
         } else {
            workflow = generateDefaultWorkflow(namespace, transaction, null).getWorkDefinition();
         }
         transaction.execute();

         AtsWorkflowConfigEditor.editWorkflow(workflow);

      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return true;

   }
   public static class WorkflowData {
      private final WorkFlowDefinition workDefinition;
      private final Artifact workFlowArtifact;

      public WorkflowData(WorkFlowDefinition workDefinition, Artifact workFlowArtifact) {
         this.workDefinition = workDefinition;
         this.workFlowArtifact = workFlowArtifact;
      }

      /**
       * @return the workDefinition
       */
      public WorkFlowDefinition getWorkDefinition() {
         return workDefinition;
      }

      /**
       * @return the workFlowArtifact
       */
      public Artifact getWorkFlowArtifact() {
         return workFlowArtifact;
      }
   }

   public static WorkflowData generateDefaultWorkflow(String namespace, SkynetTransaction transaction, TeamDefinitionArtifact teamDef) throws OseeCoreException {
      // Duplicate default workflow artifact w/ namespace changes
      Artifact defaultWorkflow = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact("osee.ats.teamWorkflow");
      Artifact newWorkflowArt = defaultWorkflow.duplicate(AtsUtil.getAtsBranch());
      for (Attribute<?> attr : newWorkflowArt.getAttributes()) {
         if (attr instanceof StringAttribute) {
            attr.setFromString(attr.getDisplayableString().replaceAll("osee.ats.teamWorkflow", namespace));
         }
      }

      AtsWorkDefinitions.addUpdateWorkItemToDefaultHeirarchy(newWorkflowArt, transaction);
      newWorkflowArt.persist(transaction);

      // Duplicate work pages w/ namespace changes
      for (DefaultTeamState state : DefaultTeamState.values()) {
         Artifact defaultStateArt =
               WorkItemDefinitionFactory.getWorkItemDefinitionArtifact("osee.ats.teamWorkflow." + state.name());
         Artifact newStateArt = defaultStateArt.duplicate(AtsUtil.getAtsBranch());
         for (Attribute<?> attr : newStateArt.getAttributes()) {
            if (attr instanceof StringAttribute) {
               attr.setFromString(attr.getDisplayableString().replaceAll("osee.ats.teamWorkflow", namespace));
            }
         }
         if (state == DefaultTeamState.Completed || state == DefaultTeamState.Cancelled) {
            newStateArt.setSoleAttributeFromString(WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(),
                  "osee.ats.teamWorkflow." + state.name());
         }

         // Add same relations as default work pages to new work pages (widgets and rules)
         for (Artifact art : defaultStateArt.getRelatedArtifacts(CoreRelationTypes.WorkItem__Child)) {
            newStateArt.addRelation(CoreRelationTypes.WorkItem__Child, art);
         }

         AtsWorkDefinitions.addUpdateWorkItemToDefaultHeirarchy(newStateArt, transaction);
         newStateArt.persist(transaction);
      }
      return new WorkflowData(new WorkFlowDefinition(newWorkflowArt), newWorkflowArt);

   }

   public static WorkflowData generateSimpleWorkflow(String namespace, SkynetTransaction transaction, TeamDefinitionArtifact teamDef) throws OseeCoreException {
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
         teamDef.addRelation(CoreRelationTypes.WorkItem__Child, workflowArt);
         artifacts.add(teamDef);
      }
      artifacts.add(workflowArt);
      workflow.loadPageData(true);

      for (Artifact artifact : artifacts) {
         AtsWorkDefinitions.addUpdateWorkItemToDefaultHeirarchy(artifact, transaction);
         artifact.persist(transaction);
      }
      return new WorkflowData(workflow, workflowArt);
   }
}
