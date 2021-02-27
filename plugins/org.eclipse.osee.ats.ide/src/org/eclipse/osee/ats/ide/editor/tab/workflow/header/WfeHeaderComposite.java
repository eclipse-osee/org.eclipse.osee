/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.note.NoteItem;
import org.eclipse.osee.ats.core.workflow.WorkflowManagerCore;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.DuplicateWidgetUpdateResolver;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.WorkflowManager;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkItemHookIde;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.AnnotationComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.AttributeAnnotationManager;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactStoredWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class WfeHeaderComposite extends Composite {

   private final WorkflowEditor editor;
   private final IAtsWorkItem workItem;
   private WfeRelationsHyperlinkComposite relationsComposite;
   private WfeActionableItemHeader actionableItemHeader;
   private WfeMetricsHeader metricsHeader;
   private final StateXWidgetPage currentStateXWidgetPage;
   private static Color LIGHT_GREY;
   private final IManagedForm managedForm;
   private WfeCustomHeader customHeader;
   private WfeTitleHeader titleHeader;
   private WfeTransitionHeader transitionHeader;
   private WfeStateCreatedOrigHeader stateHeader;
   private WfeTeamAndIdsHeader teamHeader;
   private WfeTargetedVersionHeader versionHeader;
   private WfeAssigneesHeader assigneeHeader;
   private WfeWorkPackage workPackageHeader;
   private WfeActionableItemReviewHeader aiReviewHeader;

   public WfeTransitionHeader getWfeTransitionComposite() {
      return transitionHeader;
   }

   public WfeHeaderComposite(Composite parent, int style, WorkflowEditor editor, StateXWidgetPage currentStateXWidgetPage, IManagedForm managedForm) {
      super(parent, style);
      this.editor = editor;
      this.currentStateXWidgetPage = currentStateXWidgetPage;
      this.managedForm = managedForm;
      this.workItem = editor.getWorkItem();
   }

   public void refresh() {
      if (titleHeader != null) {
         titleHeader.refresh();
      }
      if (stateHeader != null) {
         stateHeader.refresh();
      }
      if (teamHeader != null) {
         teamHeader.refresh();
      }
      if (versionHeader != null) {
         versionHeader.refresh();
      }
      if (assigneeHeader != null) {
         assigneeHeader.refresh();
      }
      if (actionableItemHeader != null) {
         actionableItemHeader.refresh();
      }
      if (metricsHeader != null) {
         metricsHeader.refresh();
      }
      if (workPackageHeader != null) {
         workPackageHeader.refresh();
      }
      if (relationsComposite != null) {
         relationsComposite.refresh();
      }
      if (transitionHeader != null) {
         transitionHeader.refresh();
      }
      if (blockedWfHeader != null) {
         blockedWfHeader.refresh();
      }
      if (holdWfHeader != null) {
         holdWfHeader.refresh();
      }
      if (aiReviewHeader != null) {
         aiReviewHeader.refresh();
      }
      if (customHeader != null) {
         customHeader.refresh();
      }
   }

   public void create() {
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 100;
      this.setLayoutData(gd);
      this.setLayout(ALayout.getZeroMarginLayout(1, false));

      try {
         titleHeader = new WfeTitleHeader(this, SWT.NONE, workItem, editor, xModListener);
         stateHeader = new WfeStateCreatedOrigHeader(this, SWT.NONE, workItem, editor);
         teamHeader = new WfeTeamAndIdsHeader(this, SWT.NONE, workItem, editor);
         createTargetVersionAndAssigneeHeader(this, currentStateXWidgetPage, editor.getToolkit());

         createLatestHeader(this, editor.getToolkit());
         if (workItem.isTeamWorkflow()) {
            actionableItemHeader = new WfeActionableItemHeader(this, editor.getToolkit(), workItem, editor);
         }

         if (workItem.getWorkDefinition().getHeaderDef().isShowMetricsHeader()) {
            metricsHeader = new WfeMetricsHeader(this, editor.getToolkit(), workItem, editor, managedForm);
         }

         int workPackageNumColumns = 2;
         if (workItem.getWorkDefinition().getHeaderDef().isShowWorkPackageHeader()) {
            createWorkPacakageHeader(this, editor.getToolkit(), workPackageNumColumns, editor);
         }
         int numColumns = 4;
         createWorkDefHeader(this, editor.getToolkit(), workItem, numColumns);
         blockedWfHeader = new WfeBlockedWorkflowHeader(this, SWT.NONE, workItem, editor);
         holdWfHeader = new WfeHoldWorkflowHeader(this, SWT.NONE, workItem, editor);

         customHeader = createCustomHeader(this, editor.getToolkit(), workItem, editor, managedForm);

         createSMANotesHeader(this, editor.getToolkit(), numColumns);
         createStateNotesHeader(this, workItem, editor.getToolkit(), numColumns, null);
         createAnnotationsHeader(this, editor.getToolkit());

         if (WfeRelationsHyperlinkComposite.relationExists((AbstractWorkflowArtifact) workItem)) {
            relationsComposite = new WfeRelationsHyperlinkComposite(this, SWT.NONE, editor);
            relationsComposite.create();
         }

         if (WfeActionableItemReviewHeader.isApplicable(workItem)) {
            aiReviewHeader =
               new WfeActionableItemReviewHeader(this, editor.getToolkit(), (AbstractReviewArtifact) workItem, editor);
         }

         boolean isEditable = WorkflowManagerCore.isEditable(AtsApiService.get().getUserService().getCurrentUser(),
            workItem, workItem.getStateDefinition(), AtsApiService.get().getUserService());
         transitionHeader = new WfeTransitionHeader(this, editor, isEditable);

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private WfeCustomHeader createCustomHeader(Composite comp, XFormToolkit toolkit, IAtsWorkItem workItem, WorkflowEditor editor, IManagedForm managedForm) {
      return new WfeCustomHeader(comp, SWT.NONE, managedForm, workItem, editor);
   }

   public static void createWorkDefHeader(Composite comp, XFormToolkit toolkit, IAtsWorkItem workItem, int horizontalSpan) {
      Composite headerComp = new Composite(comp, SWT.NONE);
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.horizontalSpan = horizontalSpan;
      headerComp.setLayoutData(gridData);
      int numColumns = 5;
      headerComp.setLayout(ALayout.getZeroMarginLayout(numColumns, false));
      toolkit.adapt(headerComp);

      IAtsWorkDefinition workDef = workItem.getWorkDefinition();
      Label label = FormsUtil.createLabelValue(toolkit, headerComp, "Work Definition: ", workDef.getName());
      label.addListener(SWT.MouseDoubleClick, new Listener() {

         @Override
         public void handleEvent(Event event) {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.showView("org.eclipse.ui.views.ContentOutline", null, IWorkbenchPage.VIEW_ACTIVATE);
            } catch (PartInitException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

      Label dragDropLabel = new Label(headerComp, SWT.NONE);
      dragDropLabel.setText("Drag/Drop Related Here: ");
      FormsUtil.setLabelFonts(dragDropLabel, FontManager.getDefaultLabelFont());
      dragDropLabel.setLayoutData(new GridData());
      toolkit.adapt(dragDropLabel, true, true);

      Label dragDropBox = new Label(headerComp, SWT.BORDER);
      new WfeDragAndDrop(dragDropBox, (AbstractWorkflowArtifact) workItem.getStoreObject(), WorkflowEditor.EDITOR_ID);
      dragDropBox.setText("                             ");
      dragDropBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      toolkit.adapt(dragDropBox, true, true);
      dragDropBox.setBackground(getLightGreyColor());
   }

   public void createSMANotesHeader(Composite comp, XFormToolkit toolkit, int horizontalSpan) {
      // Display Workflow Note
      String note = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem,
         AtsAttributeTypes.WorkflowNotes, "");
      if (!note.equals("")) {
         FormsUtil.createLabelOrHyperlink(comp, toolkit, horizontalSpan, "Note: " + note);
      }
   }

   public static void createStateNotesHeader(Composite comp, IAtsWorkItem workItem, XFormToolkit toolkit, int horizontalSpan, String forStateName) {
      // Display global Notes
      for (NoteItem noteItem : AtsApiService.get().getWorkItemService().getNotes(workItem).getNoteItems()) {
         if (forStateName == null || noteItem.getState().equals(forStateName)) {
            FormsUtil.createLabelOrHyperlink(comp, toolkit, horizontalSpan, noteItem.toString());
         }
      }
   }

   public void createWorkPacakageHeader(Composite parent, XFormToolkit toolkit, int horizontalSpan, WorkflowEditor editor) {
      boolean show = workItem.isTeamWorkflow() && ((IAtsTeamWorkflow) workItem).getTeamDefinition().hasWorkPackages();

      if (show) {
         Composite comp = toolkit.createContainer(parent, 6);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         comp.setLayout(ALayout.getZeroMarginLayout(6, false));

         workPackageHeader = new WfeWorkPackage(comp, SWT.NONE, (IAtsTeamWorkflow) workItem, editor);
      }
   }

   private void createLatestHeader(Composite comp, XFormToolkit toolkit) {
      if (AtsApiService.get().getStoreService().isHistorical(workItem)) {
         Label label = toolkit.createLabel(comp,
            "This is a historical version of this " + workItem.getArtifactTypeName() + " and can not be edited; Select \"Open Latest\" to view/edit latest version.");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      }
   }

   private void createAnnotationsHeader(Composite comp, XFormToolkit toolkit) {
      try {
         if (AttributeAnnotationManager.getAnnotations((Artifact) workItem.getStoreObject()).size() > 0) {
            new AnnotationComposite(toolkit, comp, SWT.None, (Artifact) workItem.getStoreObject());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Exception resolving annotations", ex);
      }
   }

   private void createTargetVersionAndAssigneeHeader(Composite parent, StateXWidgetPage page, XFormToolkit toolkit) {
      boolean isShowTargetedVersion = isShowTargetedVersion();
      boolean isCurrentNonCompleteCanceledState =
         page.isCurrentNonCompleteCancelledState((AbstractWorkflowArtifact) workItem.getStoreObject());
      if (!isShowTargetedVersion && !isCurrentNonCompleteCanceledState) {
         return;
      }

      Composite comp = toolkit.createContainer(parent, 6);
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      comp.setLayout(ALayout.getZeroMarginLayout(6, false));

      // Targeted Version
      if (isShowTargetedVersion) {
         versionHeader = new WfeTargetedVersionHeader(comp, SWT.NONE, (IAtsTeamWorkflow) workItem, editor);
         toolkit.createLabel(comp, "    ");
      }

      // Current Assignees
      if (isCurrentNonCompleteCanceledState) {
         boolean editable = WorkflowManager.isAssigneeEditable((AbstractWorkflowArtifact) workItem.getStoreObject());

         assigneeHeader = new WfeAssigneesHeader(comp, SWT.NONE, workItem, editable, editor);
      }
   }

   public boolean isShowTargetedVersion() {
      if (!workItem.isTeamWorkflow()) {
         return false;
      }
      return AtsApiService.get().getVersionService().isTeamUsesVersions(
         ((TeamWorkFlowArtifact) workItem).getTeamDefinition());
   }

   private static Color getLightGreyColor() {
      if (LIGHT_GREY == null) {
         LIGHT_GREY = Displays.getColor(240, 240, 240);
      }
      return LIGHT_GREY;
   }

   @Override
   public void dispose() {
      if (customHeader != null) {
         customHeader.dispose();
      }
      if (actionableItemHeader != null) {
         actionableItemHeader.dispose();
      }
      if (metricsHeader != null) {
         metricsHeader.dispose();
      }
   }

   public XResultData isXWidgetDirty(XResultData rd) {
      if (titleHeader != null) {
         titleHeader.isXWidgetDirty(rd);
      }
      if (customHeader != null) {
         customHeader.isXWidgetDirty(rd);
      }
      return rd;
   }

   public void getDirtyIArtifactWidgets(List<ArtifactStoredWidget> artWidgets) {
      if (titleHeader != null) {
         titleHeader.getDirtyIArtifactWidgets(artWidgets);
      }
      if (customHeader != null) {
         customHeader.getDirtyIArtifactWidgets(artWidgets);
      }
   }

   public Result isXWidgetSavable() {
      if (titleHeader != null) {
         Result result = titleHeader.isXWidgetSavable();
         if (result.isTrue()) {
            return result;
         }
      }
      if (customHeader != null) {
         return customHeader.isXWidgetSavable();
      }
      return Result.FalseResult;
   }

   final XModifiedListener xModListener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget xWidget) {
         try {
            if (AtsApiService.get().getStoreService().isDeleted(workItem)) {
               return;
            }
            // Notify extensions of widget modified
            for (IAtsWorkItemHookIde item : AtsApiService.get().getWorkItemServiceIde().getWorkItemHooksIde()) {
               try {
                  item.widgetModified(xWidget, editor.getToolkit(), workItem.getStateDefinition(), (Artifact) workItem,
                     WorkflowManagerCore.isEditable(AtsApiService.get().getUserService().getCurrentUser(), workItem,
                        workItem.getStateDefinition(), AtsApiService.get().getUserService()));
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
            DuplicateWidgetUpdateResolver.updateDuplicateWidgets(managedForm, (AbstractWorkflowArtifact) workItem,
               xWidget);
            editor.onDirtied();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   };
   private WfeBlockedWorkflowHeader blockedWfHeader;
   private WfeHoldWorkflowHeader holdWfHeader;

   public Collection<XWidget> getXWidgets(ArrayList<XWidget> widgets) {
      titleHeader.getXWidgets(widgets);
      customHeader.getXWidgets(widgets);
      return widgets;
   }

}
