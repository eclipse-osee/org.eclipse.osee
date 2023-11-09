/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab;

import java.util.logging.Level;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.ide.AtsArtifactImageProvider;
import org.eclipse.osee.ats.ide.actions.CopyActionDetailsAction;
import org.eclipse.osee.ats.ide.actions.EmailActionAction;
import org.eclipse.osee.ats.ide.actions.FavoriteAction;
import org.eclipse.osee.ats.ide.actions.OpenInArtifactEditorAction;
import org.eclipse.osee.ats.ide.actions.OpenInAtsWorldAction;
import org.eclipse.osee.ats.ide.actions.OpenInBrowserAction;
import org.eclipse.osee.ats.ide.actions.OpenParentAction;
import org.eclipse.osee.ats.ide.actions.OpenTeamDefinitionAction;
import org.eclipse.osee.ats.ide.actions.OpenVersionArtifactAction;
import org.eclipse.osee.ats.ide.actions.ResourceHistoryAction;
import org.eclipse.osee.ats.ide.actions.ShowChangeReportAction;
import org.eclipse.osee.ats.ide.actions.ShowContextChangeReportAction;
import org.eclipse.osee.ats.ide.actions.ShowMergeManagerAction;
import org.eclipse.osee.ats.ide.actions.ShowWordChangeReportAction;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.note.AddStateNoteAction;
import org.eclipse.osee.ats.ide.editor.tab.workflow.note.AddWorkflowNotesAction;
import org.eclipse.osee.ats.ide.editor.tab.workflow.util.WfeReloadAction;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.walker.action.OpenActionViewAction;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.duplicate.CloneWorkflowAction;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * @author Donald G. Dunne
 */
public abstract class WfeAbstractTab extends FormPage {
   protected final IAtsWorkItem workItem;
   protected final WorkflowEditor editor;
   protected final String tabName;
   protected LoadingComposite loadingComposite;
   protected Composite bodyComp;

   public WfeAbstractTab(WorkflowEditor editor, String id, IAtsWorkItem workItem, String tabName) {
      super(editor, id, tabName);
      this.editor = editor;
      this.workItem = workItem;
      this.tabName = tabName;
   }

   public void updateTitleBar(IManagedForm managedForm) {
      if (managedForm != null && Widgets.isAccessible(managedForm.getForm())) {
         // Form Title
         String titleString = AtsApiService.get().getWorkItemService().getWorkflowTitle(workItem, tabName);
         managedForm.getForm().setText(titleString);

         // Tooltip
         String tooltipTitle = AtsApiService.get().getWorkItemService().getTooltipTitle(workItem, titleString,
            workItem.getArtifactTypeName());
         tooltipTitle = Strings.escapeAmpersands(tooltipTitle);
         managedForm.getForm().setToolTipText(tooltipTitle);

         if (AtsApiService.get().getAgileService().isBacklog(workItem)) {
            managedForm.getForm().setImage(
               ImageManager.getImage(AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_BACKLOG)));
         } else {
            managedForm.getForm().setImage(ArtifactImageManager.getImage((Artifact) workItem.getStoreObject()));
         }

      }
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      IManagedForm managedForm = getManagedForm();
      if (managedForm != null && Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   public IToolBarManager createToolbar(IManagedForm managedForm) {
      IToolBarManager toolBarMgr = managedForm.getForm().getToolBarManager();
      AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) workItem;

      if (awa.isTeamWorkflow() && (AtsApiService.get().getBranchService().isCommittedBranchExists(
         (TeamWorkFlowArtifact) awa) || AtsApiService.get().getBranchService().isWorkingBranchInWork(
            (TeamWorkFlowArtifact) awa))) {
         toolBarMgr.add(new ShowMergeManagerAction((TeamWorkFlowArtifact) awa));
         toolBarMgr.add(new ShowChangeReportAction((TeamWorkFlowArtifact) awa));
         toolBarMgr.add(new ShowWordChangeReportAction((TeamWorkFlowArtifact) awa));
         toolBarMgr.add(new ShowContextChangeReportAction((TeamWorkFlowArtifact) awa));
      }
      toolBarMgr.add(new FavoriteAction(editor));
      if (awa.getParentAWA() != null) {
         toolBarMgr.add(new OpenParentAction(awa));
      }
      toolBarMgr.add(new EmailActionAction(editor));
      toolBarMgr.add(new AddWorkflowNotesAction(awa));
      toolBarMgr.add(new AddStateNoteAction(awa));
      toolBarMgr.add(new OpenInAtsWorldAction(awa));
      toolBarMgr.add(new OpenActionViewAction());
      if (AtsApiService.get().getUserService().isAtsAdmin()) {
         toolBarMgr.add(new OpenInArtifactEditorAction(editor));
      }
      toolBarMgr.add(new OpenVersionArtifactAction(awa));
      if (awa instanceof TeamWorkFlowArtifact) {
         toolBarMgr.add(new OpenTeamDefinitionAction((TeamWorkFlowArtifact) awa));
      }
      toolBarMgr.add(new CopyActionDetailsAction(awa, AtsApiService.get()));
      toolBarMgr.add(new OpenInBrowserAction(awa));
      toolBarMgr.add(new ResourceHistoryAction(awa));
      if (awa.isTeamWorkflow() && !awa.isChangeRequest()) {
         toolBarMgr.add(new CloneWorkflowAction((TeamWorkFlowArtifact) awa, null));
      }
      addRefreshAction(toolBarMgr);

      managedForm.getForm().updateToolBar();
      return toolBarMgr;
   }

   public void addRefreshAction(IToolBarManager toolBarMgr) {
      toolBarMgr.add(new WfeReloadAction((AbstractWorkflowArtifact) workItem, editor));
   }

   public void handleException(Exception ex) {
      setLoading(false);
      OseeLog.log(Activator.class, Level.SEVERE, ex);
      new ExceptionComposite(bodyComp, ex);
      bodyComp.layout();
   }

   public void setLoading(boolean set) {
      if (set) {
         loadingComposite = new LoadingComposite(bodyComp);
         bodyComp.layout();
      } else {
         if (Widgets.isAccessible(loadingComposite)) {
            loadingComposite.dispose();
         }
      }
      showBusy(set);
   }

   public void refresh() {
      // do nothing
   }

}