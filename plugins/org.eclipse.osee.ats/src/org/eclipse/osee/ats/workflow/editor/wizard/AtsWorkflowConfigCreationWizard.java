/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow.editor.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ats.artifact.AtsArtifactToken;
import org.eclipse.osee.ats.config.AtsConfigManager;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workdef.WorkDefinition;
import org.eclipse.osee.ats.workdef.provider.AtsWorkDefinitionProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkflowConfigCreationWizard extends Wizard implements INewWizard {

   private NewWorkflowConfigPage1 page1;

   @Override
   public void addPages() {
      addPage(page1);
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      page1 = new NewWorkflowConfigPage1();
   }

   @Override
   public boolean performFinish() {

      try {
         final String workDefName = page1.getWorkDefName();
         try {
            if (WorkItemDefinitionFactory.getWorkItemDefinition(workDefName) != null) {
               AWorkbench.popup("ERROR", "Namespace already used, choose a unique namespace.");
               return false;
            }
         } catch (OseeCoreException ex) {
            // do nothing
         }
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Config Wizard");
         WorkDefinition workDef = null;
         XResultData resultData = new XResultData();
         workDef = AtsConfigManager.generateDefaultWorkflow(workDefName, resultData, transaction, null);

         Artifact workDefArt =
            AtsWorkDefinitionProvider.get().importWorkDefinitionToDb(workDef, workDefName, resultData, transaction);

         Artifact folder = ArtifactQuery.getArtifactFromToken(AtsArtifactToken.WorkDefinitionsFolder);
         folder.addChild(workDefArt);
         folder.persist(transaction);

         transaction.execute();

         Artifact artifact =
            ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.WorkDefinition, workDefName,
               AtsUtil.getAtsBranch());
         RendererManager.open(artifact, PresentationType.SPECIALIZED_EDIT);
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

}
