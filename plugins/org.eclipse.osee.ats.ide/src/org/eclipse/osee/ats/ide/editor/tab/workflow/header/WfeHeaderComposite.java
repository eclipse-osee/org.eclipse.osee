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
import static org.eclipse.osee.framework.core.widget.WidgetId.XXStringSTextHEditWidget;
import static org.eclipse.osee.framework.core.widget.WidgetId.XXStringsWidget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workdef.WorkDefUtil;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.ats.ide.workdef.XWidgetBuilderAts;
import org.eclipse.osee.ats.ide.workdef.XWidgetWorkDefRenderer;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRendererListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXLabelValueWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.WidgetDebugProvider;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class WfeHeaderComposite extends Composite implements XWidgetSwtRendererListener {

   // Random ids so that widgets can be quickly retrieved
   private final long TEAM_WIDGET_ID = 23432;
   private final long PARENT_WIDGET_ID = 22378;
   private final long ATSID_WIDGET_ID = 88823;
   private final long PCRIDS_WIDGET_ID = 9923;
   private final long ACTION_WIDGET_ID = 2234;
   private final long OTHER_AIS_WIDGET_ID = 23421;

   private final WorkflowEditor editor;
   private final IAtsWorkItem workItem;
   private final IManagedForm managedForm;
   private WfeCustomHeader customHeader;
   private WfeTransitionHeader transitionHeader;
   private WfeActionableItemReviewHeader aiReviewHeader;
   private WfeAttachmentsComposite attachmentsComposite;
   private final List<XWidget> widgets = new ArrayList<>();
   private XXWorkItemData xxWid;
   private XWidgetPage xWidgetPage;
   private WfeRelatedWidgets relatedWidgets;

   public WfeTransitionHeader getWfeTransitionHeader() {
      return transitionHeader;
   }

   public WfeHeaderComposite(Composite parent, int style, WorkflowEditor editor, IManagedForm managedForm) {
      super(parent, style);
      this.editor = editor;
      this.managedForm = managedForm;
      this.workItem = editor.getWorkItem();
   }

   public void create() {
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 100;
      this.setLayoutData(gd);
      this.setLayout(ALayout.getZeroMarginLayout(1, false));
      editor.getToolkit().adapt(this);

      try {

         new WfeAnnotationsHeader(this, SWT.NONE, workItem, editor);

         XWidgetBuilderAts wba = new XWidgetBuilderAts();

         createIsHistoricalHeader(wba);

         // Title
         wba.andWidget("Title", Title, XXStringSTextHEditWidget).sTextBorder().noClear().fillHoriz().startComp(3);
         wba.andCopyIdHyperlinkWidget();

         // State, Date, Originator
         wba.andWidget(CurrentStateName, XXStringsWidget).noEdit().fillHoriz().startComp(7);
         wba.andWidget(CreatedDate, XXStringsWidget).noEdit().fillHoriz();
         wba.andOriginatorWidget().fillHoriz().noClear().endComp();

         // Team, IDs
         createIdsWidgets(wba);

         // Work Definition and Drag/Drop box
         createWorkDefAndDragDtropHeader(wba);

         // Version and Assignees
         createVersionAndAssigneeWidgets(wba);

         createAisAndOther(wba);

         if (workItem.getWorkDefinition().getHeaderDef().isShowMetricsHeader()) {
            createMetricsWidgets(wba);
         }

         createBlockHoldHeader(wba);

         wba.andWorkflowNotesWidget();

         // Create Action, Tasks, Reviews, Siblings, etc...
         relatedWidgets = new WfeRelatedWidgets(editor);
         relatedWidgets.create(wba);

         // Create all Header defined widgets from above
         List<XWidgetData> widDatas = wba.getXWidgetDatas();
         xWidgetPage = new XWidgetPage(widDatas, this);
         xWidgetPage.createBody(managedForm, this, (Artifact) workItem.getStoreObject(), null, true);
         widgets.addAll(xWidgetPage.getSwtXWidgetRenderer().getXWidgets());

         relatedWidgets.refresh();

         // Change Type, Points, Priority, Need By and other custom widgets
         customHeader = createCustomHeader(this, editor.getToolkit(), workItem, editor, managedForm);
         widgets.addAll(customHeader.getXWidgets());

         attachmentsComposite = new WfeAttachmentsComposite(this, SWT.NONE, editor);
         attachmentsComposite.create();

         if (WfeActionableItemReviewHeader.isApplicable(workItem)) {
            aiReviewHeader =
               new WfeActionableItemReviewHeader(this, editor.getToolkit(), (AbstractReviewArtifact) workItem, editor);
         }

         transitionHeader = new WfeTransitionHeader(this, editor, true);

         // Load values in the background
         setWidgetValues();
         // Set all to allow Clear unless already defined as NO_CLEAR
         for (XWidget widget : widgets) {
            if (widget.isNot(XOption.NO_CLEAR)) {
               widget.set(XOption.CLEAR);
            }
            // Header widgets render through the base XWidgetSwtRenderer (no work def); wrap the debug
            // provider so Ctrl+Click also shows the Work Def, matching state section widgets.
            addWorkDefToWidgetDebug(widget);
         }

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void createAisAndOther(XWidgetBuilderAts wba) {
      if (getXXWid().isTeamWf()) {
         // Still want to be able to edit to add new AIs, but don't allow clear here
         wba.andActionableItemWidget().noClear().startComp(4);
         wba.andLabelValueWidget(OTHER_AIS_WIDGET_ID, "Other AI(s)", "");
      }
   }

   private void createBlockHoldHeader(XWidgetBuilderAts wba) {
      wba.andBlockWidget().startComp(5);
      wba.andSpaceWidget(20);
      wba.andHoldWidget();
      wba.endComp();
   }

   private void createIsHistoricalHeader(XWidgetBuilder wb) {
      if (AtsApiService.get().getStoreService().isHistorical(workItem)) {
         wb.andLabelWidget("This is a historical version of this workflow and can not be edited; " //
            + "Select \"Open Latest\" to view/edit latest version.").fgLabelColor(SWT.COLOR_RED).noColon();
      }
   }

   private void createMetricsWidgets(XWidgetBuilderAts wba) {
      wba.andPercentCompleteWfeWidget().startComp(15);
      wba.andSpaceWidget().fillHoriz();
      wba.andDoubleWidget(AtsAttributeTypes.EstimatedHours);
      wba.andSpaceWidget().fillHoriz();
      wba.andHoursSpentWfeWidget();
      wba.andSpaceWidget().fillHoriz();
      wba.andTotalHoursSpentWidget().noEdit();
      wba.andSpaceWidget().fillHoriz();
      wba.andRemainingHoursWfeWidget().noEdit();
      wba.endComp();
   }

   private void createVersionAndAssigneeWidgets(XWidgetBuilderAts wba) {
      boolean isShowTargetedVersion = WorkDefUtil.isShowTargetedVersion(workItem, AtsApiService.get());
      if (isShowTargetedVersion) {
         wba.andTargetedVersionWidget().andTeamId(workItem.getTeamDef().getArtifactId()).startComp(4);
      }
      boolean isCurrentNonCompleteCanceledState = workItem.isInWork();
      if (isCurrentNonCompleteCanceledState) {
         wba.andAssigneesWidget().noClear();
      }
   }

   private void createIdsWidgets(XWidgetBuilderAts wba) {
      if (workItem.isTeamWorkflow()) {
         wba.andLabelValueWidget(TEAM_WIDGET_ID, "Team", "").fillHoriz().startComp(6);
      } else if ((workItem.isTask() || workItem.isReview()) && workItem.getParentTeamWorkflow() != null) {
         wba.andLabelValueWidget(PARENT_WIDGET_ID, "Parent Id", "").fillHoriz().startComp(6);
      }
      wba.andLabelValueWidget(ATSID_WIDGET_ID, "ATS Id", "").fillHoriz();

      if (!workItem.getPcrIdsAll().isEmpty()) {
         wba.andLabelValueWidget(PCRIDS_WIDGET_ID, "PCR Id(s)", "").fillHoriz();
      }
      IAtsAction action = workItem.getParentAction();
      if (action != null) {
         wba.andLabelValueWidget(ACTION_WIDGET_ID, "Action Id", "").fillHoriz();
      }
      wba.endComp();
   }

   private void createWorkDefAndDragDtropHeader(XWidgetBuilderAts wba) {

      Listener doubleClickWorkDefListener = new Listener() {

         @Override
         public void handleEvent(Event event) {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.showView("org.eclipse.ui.views.ContentOutline", null, IWorkbenchPage.VIEW_ACTIVATE);
            } catch (PartInitException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      wba.andLabelValueWidget("Work Definition", workItem.getWorkDefinition().getName()) //
         .valueListener(SWT.MouseDoubleClick, doubleClickWorkDefListener) //
         .noEdit().andToolTip("Double-click to view").startComp(3);
      wba.andSpaceWidget(15);
      wba.andDropAttachmentWidget();
      wba.endComp();
   }

   public void refresh() {
      for (XWidget widget : widgets) {
         widget.refresh();
      }
      if (relatedWidgets != null) {
         relatedWidgets.refresh();
      }
      if (attachmentsComposite != null) {
         attachmentsComposite.refresh();
      }
      if (transitionHeader != null) {
         transitionHeader.refresh();
      }
      if (aiReviewHeader != null) {
         aiReviewHeader.refresh();
      }
      if (customHeader != null) {
         customHeader.refresh();
      }
   }

   private WfeCustomHeader createCustomHeader(Composite comp, XFormToolkit toolkit, IAtsWorkItem workItem,
      WorkflowEditor editor, IManagedForm managedForm) {
      return new WfeCustomHeader(comp, SWT.NONE, managedForm, workItem, editor);
   }

   public Collection<XWidget> getXWidgets() {
      return widgets;
   }

   /**
    * Wrap the widget's existing debug provider so the Ctrl+Click debug output includes the Work Def, matching state
    * section widgets. The header renders through the base XWidgetSwtRenderer which has no work def reference.
    */
   private void addWorkDefToWidgetDebug(XWidget widget) {
      if (!(widget instanceof XXAbstractBaseXWidget<?>)) {
         return;
      }
      XXAbstractBaseXWidget<?> baseWidget = (XXAbstractBaseXWidget<?>) widget;
      WidgetDebugProvider delegate = baseWidget.getDebugProvider();
      // XWidgetWorkDefRenderer already logs the Work Def (custom-header widgets); only add it for widgets
      // rendered through the base XWidgetSwtRenderer, which has no work def reference.
      if (delegate instanceof XWidgetWorkDefRenderer) {
         return;
      }
      baseWidget.setDebugProvider(new WidgetDebugProvider() {

         @Override
         public XResultData getWidgetDebug(XXAbstractBaseXWidget<?> abstractBaseXWidget, XResultData rd) {
            rd.logf("Work Def: %s\n", workItem.getWorkDefinition().toStringWithId());
            if (delegate != null) {
               delegate.getWidgetDebug(abstractBaseXWidget, rd);
            }
            return rd;
         }
      });
   }

   private XWidget getHeaderWidget(long id) {
      for (XWidget widget : widgets) {
         if (widget.getId().equals(id)) {
            return widget;
         }
      }
      return null;
   }

   private XXWorkItemData getXXWid() {
      xxWid = XXWorkItemData.get(workItem);
      return xxWid;
   }

   public void setWidgetValues() {
      XXStringsWidget atsIdWidget = (XXStringsWidget) getHeaderWidget(ATSID_WIDGET_ID);
      if (atsIdWidget != null && Widgets.isAccessible(atsIdWidget.getLabelWidget())) {
         Thread refreshThread = new Thread("Refresh Workflow Editor") {

            @Override
            public void run() {
               super.run();
               setOtherAisWidget();
               setIdsWidgets(atsIdWidget);
            }

            private void setOtherAisWidget() {
               IAtsTeamWorkflow teamWf = getXXWid().getTeamWf();
               if (teamWf == null) {
                  return;
               }
               XXLabelValueWidget otherAisWidget = (XXLabelValueWidget) getHeaderWidget(OTHER_AIS_WIDGET_ID);
               if (otherAisWidget == null) {
                  return;
               }
               IAtsAction action = xxWid.getAction();
               final Collection<IAtsActionableItem> ais = new ArrayList<>();
               if (action != null) {
                  ais.addAll(AtsApiService.get().getActionableItemService().getActionableItems(action));
                  ais.removeAll(xxWid.getTeamWf().getActionableItems());
               }
               Displays.ensureInDisplayThread(new Runnable() {

                  @Override
                  public void run() {
                     if (action == null) {
                        otherAisWidget.setValue("Error: No Parent Action", SWT.COLOR_RED);
                     } else {
                        if (ais.isEmpty()) {
                           otherAisWidget.setValue("No sibling Actionable Item(s) / Workflows", SWT.NONE);
                        } else {
                           String aisStr = AtsObjects.toString(",", ais);
                           otherAisWidget.setValue(aisStr, SWT.NONE);
                        }
                     }
                  }
               });
            }

            private void setIdsWidgets(XXStringsWidget atsIdWidget) {
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

                     XXStringsWidget pcrIdsWidget = (XXStringsWidget) getHeaderWidget(PCRIDS_WIDGET_ID);
                     if (pcrIdsWidget != null && Widgets.isAccessible(pcrIdsWidget.getLabelWidget())) {
                        pcrIdsWidget.setSelected(fPcrIdsValueStr);
                     }
                     XXStringsWidget teamWfWidget = (XXStringsWidget) getHeaderWidget(TEAM_WIDGET_ID);
                     if (teamWfWidget != null && Widgets.isAccessible(teamWfWidget.getLabelWidget())) {
                        teamWfWidget.setSelected(fTeamWfIdValueStr);
                     }
                     XXStringsWidget parentIdWidget = (XXStringsWidget) getHeaderWidget(PARENT_WIDGET_ID);
                     if (parentIdWidget != null && Widgets.isAccessible(parentIdWidget.getLabelWidget())) {
                        parentIdWidget.setSelected(fParentIdValueStr);
                     }
                     XXStringsWidget actionIdWidget = (XXStringsWidget) getHeaderWidget(ACTION_WIDGET_ID);
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

   public XWidget getWidget(long id) {
      return xWidgetPage.getXWidget(id);
   }

}
