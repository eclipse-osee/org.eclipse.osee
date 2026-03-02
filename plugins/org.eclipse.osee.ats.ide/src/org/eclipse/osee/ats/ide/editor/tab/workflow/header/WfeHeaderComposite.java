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

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.CreatedDate;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.CurrentStateName;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Title;
import static org.eclipse.osee.framework.core.widget.WidgetId.XXStringsSelWidget;
import static org.eclipse.osee.framework.core.widget.WidgetId.XXTextWidget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workdef.WorkDefUtil;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.DuplicateWidgetUpdateResolver;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.ide.workdef.XWidgetBuilderAts;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkItemHookIde;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRendererListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsSelWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
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
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeHeaderComposite extends Composite implements XWidgetSwtRendererListener {

   private final long TEAM_WIDGET_ID = 23432;
   private final long PARENT_WIDGET_ID = 22378;
   private final long ATSID_WIDGET_ID = 88823;
   private final long PCRIDS_WIDGET_ID = 9923;
   private final long ACTION_WIDGET_ID = 2234;

   private final WorkflowEditor editor;
   private final IAtsWorkItem workItem;
   private WfeRelatedComposite relatedComposite;
   private WfeActionableItemHeader actionableItemHeader;
   private WfeMetricsHeader metricsHeader;
   private static Color LIGHT_GREY;
   private static WfeStateNotesHeader stateNotesHeader;
   private final IManagedForm managedForm;
   private WfeCustomHeader customHeader;
   private WfeTransitionHeader transitionHeader;
   private WfeActionableItemReviewHeader aiReviewHeader;
   private WfeBlockedWorkflowHeader blockedWfHeader;
   private WfeHoldWorkflowHeader holdWfHeader;
   private WfeAttachmentsComposite attachmentsComposite;
   private WfeWorkflowNotesHeader workflowNotesHeader;
   private Collection<XWidget> headerXWidgets;

   public WfeTransitionHeader getWfeTransitionHeader() {
      return transitionHeader;
   }

   public WfeHeaderComposite(Composite parent, int style, WorkflowEditor editor, StateXWidgetPage currentStateXWidgetPage, IManagedForm managedForm) {
      super(parent, style);
      this.editor = editor;
      this.managedForm = managedForm;
      this.workItem = editor.getWorkItem();
   }

   protected void createWidgets(Composite comp) {
      try {
         XWidgetBuilderAts wba = new XWidgetBuilderAts();
         // Title
         wba.andWidget("Title", Title, XXTextWidget).noClear().andFillHoriz();

         // State, Date, Originator
         wba.andWidget("Current State", CurrentStateName, XXStringsSelWidget).andNotEdit().andFillHoriz().andComposite(
            7);
         wba.andWidget(CreatedDate, XXStringsSelWidget).andNotEdit().andFillHoriz();
         wba.andOriginator().andFillHoriz().endComposite();

         // Team, IDs
         createIdsWidgets(wba);

         // Version and Assignees
         createVersionAndAssigneeWidgets(wba);

         // Create all Header defined widgets from above
         List<XWidgetData> widDatas = wba.getXWidgetDatas();
         XWidgetPage xWidgetPage = new XWidgetPage(widDatas, this);
         xWidgetPage.createBody(managedForm, comp, (Artifact) workItem.getStoreObject(), null, true);
         headerXWidgets = xWidgetPage.getSwtXWidgetRenderer().getXWidgets();

         // Load Ids in the background
         setIdWidgets();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void createVersionAndAssigneeWidgets(XWidgetBuilderAts wba) {
      boolean isShowTargetedVersion = WorkDefUtil.isShowTargetedVersion(workItem, AtsApiService.get());
      boolean isCurrentNonCompleteCanceledState = workItem.isInWork();
      if (!isShowTargetedVersion && !isCurrentNonCompleteCanceledState) {
         return;
      }
      if (isShowTargetedVersion) {
         wba.andTargetedVersionWidget().andTeamId(workItem.getTeamDef().getArtifactId()).andComposite(4);
      }
      if (isCurrentNonCompleteCanceledState) {
         wba.andAssignees();
      }
      wba.endWidget();
   }

   private void createIdsWidgets(XWidgetBuilderAts wba) {
      if (workItem.isTeamWorkflow()) {
         wba.andXXLabel(TEAM_WIDGET_ID, "Team", "").andFillHoriz().andComposite(6);
      } else if ((workItem.isTask() || workItem.isReview()) && workItem.getParentTeamWorkflow() != null) {
         wba.andXXLabel(PARENT_WIDGET_ID, "Parent Id", "").andFillHoriz().andComposite(6);
      }
      wba.andXXLabel(ATSID_WIDGET_ID, "ATS Id", "").andFillHoriz();

      if (!workItem.getPcrIdsAll().isEmpty()) {
         wba.andXXLabel(PCRIDS_WIDGET_ID, "PCR Id(s)", "").andFillHoriz();
      }
      IAtsAction action = workItem.getParentAction();
      if (action != null) {
         wba.andXXLabel(ACTION_WIDGET_ID, "Action Id", "").andFillHoriz();
      }
      wba.endComposite();
   }

   public void refresh() {
      if (actionableItemHeader != null) {
         actionableItemHeader.refresh();
      }
      if (metricsHeader != null) {
         metricsHeader.refresh();
      }
      if (stateNotesHeader != null) {
         stateNotesHeader.refresh();
      }
      if (workflowNotesHeader != null) {
         workflowNotesHeader.refresh();
      }
      if (relatedComposite != null) {
         relatedComposite.refresh();
      }
      if (attachmentsComposite != null) {
         attachmentsComposite.refresh();
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
         createWidgets(this);
      } catch (Exception ex) {
         System.err.println(Lib.exceptionToString(ex));
      }

      try {
         new WfeAnnotationsHeader(this, SWT.NONE, workItem, editor);

         createLatestHeader(this, editor.getToolkit());
         if (workItem.isTeamWorkflow()) {
            actionableItemHeader = new WfeActionableItemHeader(this, editor.getToolkit(), workItem, editor);
         }

         if (workItem.getWorkDefinition().getHeaderDef().isShowMetricsHeader()) {
            metricsHeader = new WfeMetricsHeader(this, editor.getToolkit(), workItem, editor, managedForm);
         }

         int numColumns = 4;
         createWorkDefHeader(this, editor.getToolkit(), workItem, numColumns);

         createBlockedHoldHeader(this, editor.getToolkit());

         customHeader = createCustomHeader(this, editor.getToolkit(), workItem, editor, managedForm);

         createWorkflowNotesHeader(this, editor.getToolkit(), numColumns);
         createStateNotesHeader(this, workItem, editor.getToolkit(), numColumns, null, editor);

         relatedComposite = new WfeRelatedComposite(this, SWT.NONE, editor);
         relatedComposite.create();

         attachmentsComposite = new WfeAttachmentsComposite(this, SWT.NONE, editor);
         attachmentsComposite.create();

         if (WfeActionableItemReviewHeader.isApplicable(workItem)) {
            aiReviewHeader =
               new WfeActionableItemReviewHeader(this, editor.getToolkit(), (AbstractReviewArtifact) workItem, editor);
         }

         boolean isEditable = WorkDefUtil.isEditable(workItem, AtsApiService.get());
         transitionHeader = new WfeTransitionHeader(this, editor, isEditable);

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createBlockedHoldHeader(WfeHeaderComposite parent, XFormToolkit toolkit) {
      Composite comp = new Composite(parent, SWT.NONE);
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      comp.setLayoutData(gridData);
      int numColumns = 4;
      comp.setLayout(ALayout.getZeroMarginLayout(numColumns, false));
      toolkit.adapt(comp);

      blockedWfHeader = new WfeBlockedWorkflowHeader(comp, SWT.NONE, workItem, editor);
      holdWfHeader = new WfeHoldWorkflowHeader(comp, SWT.NONE, workItem, editor);
   }

   private WfeCustomHeader createCustomHeader(Composite comp, XFormToolkit toolkit, IAtsWorkItem workItem,
      WorkflowEditor editor, IManagedForm managedForm) {
      return new WfeCustomHeader(comp, SWT.NONE, managedForm, workItem, editor);
   }

   public static void createWorkDefHeader(Composite comp, XFormToolkit toolkit, IAtsWorkItem workItem,
      int horizontalSpan) {
      Composite headerComp = new Composite(comp, SWT.NONE);
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.horizontalSpan = horizontalSpan;
      headerComp.setLayoutData(gridData);
      int numColumns = 5;
      headerComp.setLayout(ALayout.getZeroMarginLayout(numColumns, false));
      toolkit.adapt(headerComp);

      WorkDefinition workDef = workItem.getWorkDefinition();
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

   public void createWorkflowNotesHeader(Composite comp, XFormToolkit toolkit, int horizontalSpan) {
      workflowNotesHeader = new WfeWorkflowNotesHeader(comp, SWT.NONE, workItem, null, editor);
   }

   public void createStateNotesHeader(Composite comp, IAtsWorkItem workItem, XFormToolkit toolkit, int horizontalSpan,
      String forStateName, WorkflowEditor editor) {
      stateNotesHeader = new WfeStateNotesHeader(comp, SWT.NONE, workItem, null, editor);
   }

   private void createLatestHeader(Composite comp, XFormToolkit toolkit) {
      if (AtsApiService.get().getStoreService().isHistorical(workItem)) {
         Label label = toolkit.createLabel(comp,
            "This is a historical version of this " + workItem.getArtifactTypeName() + " and can not be edited; Select \"Open Latest\" to view/edit latest version.");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      }
   }

   public boolean isAssigneeEditable(AbstractWorkflowArtifact awa) {
      return !awa.isCompletedOrCancelled() && //
         !awa.isReadOnly() && awa.isAccessControlWrite();
   }

   private static Color getLightGreyColor() {
      if (LIGHT_GREY == null) {
         LIGHT_GREY = Displays.getColor(240, 240, 240);
      }
      return LIGHT_GREY;
   }

   @Override
   public void dispose() {
      super.dispose();
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
                  boolean isEditable =
                     !((Artifact) workItem.getStoreObject()).isReadOnly() && ((AbstractWorkflowArtifact) workItem.getStoreObject()).isAccessControlWrite();

                  item.widgetModified(xWidget, editor.getToolkit(), workItem.getStateDefinition(), (Artifact) workItem,
                     isEditable);
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

   public Collection<XWidget> getXWidgets(ArrayList<XWidget> widgets) {
      widgets.addAll(headerXWidgets);
      relatedComposite.getXWidgets(widgets);
      customHeader.getXWidgets(widgets);
      return widgets;
   }

   public static void createLabelOrHyperlink(Composite comp, XFormToolkit toolkit, final int horizontalSpan,
      final String str) {
      if (str.length() > 150) {
         Hyperlink label = toolkit.createHyperlink(comp, Strings.truncate(str, 150) + "...", SWT.NONE);
         label.setToolTipText("click to view all");
         label.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               new HtmlDialog("Note", null, str).open();
            }
         });
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalSpan = horizontalSpan;
         label.setLayoutData(gd);
      } else {
         Label label = toolkit.createLabel(comp, str);
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalSpan = horizontalSpan;
         label.setLayoutData(gd);
      }
   }

   private XWidget getHeaderWidget(long id) {
      for (XWidget widget : headerXWidgets) {
         if (widget.getId().equals(id)) {
            return widget;
         }
      }
      return null;
   }

   public void setIdWidgets() {
      XXStringsSelWidget atsIdWidget = (XXStringsSelWidget) getHeaderWidget(ATSID_WIDGET_ID);
      if (atsIdWidget != null && Widgets.isAccessible(atsIdWidget.getLabelWidget())) {
         Thread refreshThread = new Thread("Refresh Workflow Editor") {

            @Override
            public void run() {
               super.run();
               String pcrIdsValueStr = Collections.toString(", ", workItem.getPcrIdsAll());
               String teamWfIdValueStr = "";
               String parentIdValueStr = "";
               if (workItem.isTeamWorkflow()) {
                  teamWfIdValueStr = ((TeamWorkFlowArtifact) workItem).getTeamName();
               } else if ((workItem.isTask() || workItem.isReview()) && workItem.getParentTeamWorkflow() != null) {
                  IAtsTeamWorkflow parentTeamWorkflow = workItem.getParentTeamWorkflow();
                  parentIdValueStr = AtsApiService.get().getWorkItemService().getCombinedPcrId(parentTeamWorkflow);
               }
               IAtsAction action = workItem.getParentAction();
               String actionIdValueStr = "";
               if (action != null) {
                  actionIdValueStr = action.getAtsId();
               }

               final String fTeamWfIdValueStr = teamWfIdValueStr;
               final String fPcrIdsValueStr = pcrIdsValueStr;
               final String fParentIdValueStr = parentIdValueStr;
               final String fActionIdValueStrr = actionIdValueStr;
               Displays.ensureInDisplayThread(new Runnable() {

                  @Override
                  public void run() {
                     atsIdWidget.setSelected(workItem.getAtsId());

                     XXStringsSelWidget pcrIdsWidget = (XXStringsSelWidget) getHeaderWidget(PCRIDS_WIDGET_ID);
                     if (pcrIdsWidget != null && Widgets.isAccessible(pcrIdsWidget.getLabelWidget())) {
                        pcrIdsWidget.setSelected(fPcrIdsValueStr);
                     }
                     XXStringsSelWidget teamWfWidget = (XXStringsSelWidget) getHeaderWidget(TEAM_WIDGET_ID);
                     if (teamWfWidget != null && Widgets.isAccessible(teamWfWidget.getLabelWidget())) {
                        teamWfWidget.setSelected(fTeamWfIdValueStr);
                     }
                     XXStringsSelWidget parentIdWidget = (XXStringsSelWidget) getHeaderWidget(PARENT_WIDGET_ID);
                     if (parentIdWidget != null && Widgets.isAccessible(parentIdWidget.getLabelWidget())) {
                        parentIdWidget.setSelected(fParentIdValueStr);
                     }
                     XXStringsSelWidget actionIdWidget = (XXStringsSelWidget) getHeaderWidget(ACTION_WIDGET_ID);
                     if (actionIdWidget != null && Widgets.isAccessible(actionIdWidget.getLabelWidget())) {
                        actionIdWidget.setSelected(fActionIdValueStrr);
                     }
                  }
               });
            }

         };
         refreshThread.start();
      }
   }

}
