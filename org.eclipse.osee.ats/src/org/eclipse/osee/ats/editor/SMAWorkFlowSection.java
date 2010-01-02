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
package org.eclipse.osee.ats.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.artifact.StateMachineArtifact.TransitionOption;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.editor.widget.CurrentAssigneesXWidget;
import org.eclipse.osee.ats.editor.widget.ReviewInfoXWidget;
import org.eclipse.osee.ats.editor.widget.StateHoursSpentXWidget;
import org.eclipse.osee.ats.editor.widget.StatePercentCompleteXWidget;
import org.eclipse.osee.ats.editor.widget.TargetVersionXWidget;
import org.eclipse.osee.ats.editor.widget.TaskInfoXWidget;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.util.widgets.dialog.SMAStatusDialog;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FontManager;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserCheckTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinitionLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinitionViewSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkFlowSection extends SectionPart {

   private XComboViewer transitionToStateCombo;
   private Button transitionButton;
   private Label transitionAssigneesLabel;
   protected final StateMachineArtifact sma;
   private final AtsWorkPage atsWorkPage;
   private final boolean isEditable, isCurrentState, isGlobalEditable;
   private final XFormToolkit toolkit;
   private Composite mainComp;
   private final List<XWidget> allXWidgets = new ArrayList<XWidget>();

   public SMAWorkFlowSection(Composite parent, XFormToolkit toolkit, int style, AtsWorkPage page, StateMachineArtifact sma) throws OseeCoreException {
      super(parent, toolkit, style | Section.TWISTIE | Section.TITLE_BAR);
      this.toolkit = toolkit;
      this.atsWorkPage = page;
      this.sma = sma;
      isEditable = isEditable(page);
      isGlobalEditable =
            !sma.isReadOnly() && sma.isAccessControlWrite() && sma.getEditor().isPriviledgedEditModeEnabled();
      isCurrentState = sma.isCurrentState(page.getName());
      // parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
   }

   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);

      Section section = getSection();
      try {
         section.setText(getCurrentStateTitle());
         if (sma.isCurrentState(atsWorkPage.getName())) {
            section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
         }
         section.setBackground(AtsUtil.ACTIVE_COLOR);
         section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         // section.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA));

         mainComp = toolkit.createClientContainer(section, 2);
         mainComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
         // mainComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));
         mainComp.layout();

         SMAWorkFlowTab.createStateNotesHeader(mainComp, toolkit, sma, 2, atsWorkPage.getName());

         Composite workComp = createWorkArea(mainComp, atsWorkPage, toolkit);

         GridData gridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
         gridData.widthHint = 400;
         workComp.setLayoutData(gridData);

         section.layout();
         section.setExpanded(sma.isCurrentSectionExpanded(atsWorkPage.getName()));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   protected Composite createWorkArea(Composite comp, AtsWorkPage atsWorkPage, XFormToolkit toolkit) throws OseeCoreException {

      // Add static layoutDatas to atsWorkPage
      List<DynamicXWidgetLayoutData> staticDatas = new ArrayList<DynamicXWidgetLayoutData>();
      for (WorkItemDefinition workItemDefinition : atsWorkPage.getWorkPageDefinition().getWorkItems(true)) {
         if (workItemDefinition instanceof WorkWidgetDefinition) {
            DynamicXWidgetLayoutData data = ((WorkWidgetDefinition) workItemDefinition).get();
            data.setDynamicXWidgetLayout(atsWorkPage.getDynamicXWidgetLayout());
            staticDatas.add(data);
         }
      }
      atsWorkPage.addLayoutDatas(staticDatas);

      // Add dynamic WorkItemDefinitions to atsWorkPage
      List<DynamicXWidgetLayoutData> dynamicDatas = new ArrayList<DynamicXWidgetLayoutData>();
      for (WorkItemDefinition workItemDefinition : WorkItemDefinitionFactory.getDynamicWorkItemDefintions(
            sma.getWorkFlowDefinition(), atsWorkPage.getWorkPageDefinition(), sma)) {
         if (workItemDefinition instanceof WorkWidgetDefinition) {
            DynamicXWidgetLayoutData data = ((WorkWidgetDefinition) workItemDefinition).get();
            data.setDynamicXWidgetLayout(atsWorkPage.getDynamicXWidgetLayout());
            dynamicDatas.add(data);
         }
      }
      atsWorkPage.addLayoutDatas(dynamicDatas);

      atsWorkPage.setsma(sma);

      // Create Page
      Composite workComp = toolkit.createContainer(comp, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      // workComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));

      createTargetVersionAndAssigneeHeader(workComp, atsWorkPage, toolkit);
      createMetricsHeader(workComp);

      // Add any dynamic XWidgets declared for page by IAtsStateItem extensions
      for (IAtsStateItem item : sma.getStateItems().getStateItems(atsWorkPage.getId())) {
         for (XWidget xWidget : item.getDynamicXWidgetsPreBody(sma)) {
            xWidget.createWidgets(workComp, 2);
            allXWidgets.add(xWidget);
         }
      }

      // Create dynamic XWidgets
      DynamicXWidgetLayout dynamicXWidgetLayout =
            atsWorkPage.createBody(getManagedForm(), workComp, sma, xModListener, isEditable || isGlobalEditable);
      allXWidgets.addAll(dynamicXWidgetLayout.getXWidgets());

      // Add any dynamic XWidgets declared for page by IAtsStateItem extensions
      for (IAtsStateItem item : sma.getStateItems().getStateItems(atsWorkPage.getId())) {
         for (XWidget xWidget : item.getDynamicXWidgetsPostBody(sma)) {
            xWidget.createWidgets(workComp, 2);
            allXWidgets.add(xWidget);
         }
      }

      createTaskFooter(workComp, atsWorkPage.getName());
      createReviewFooter(workComp, atsWorkPage.getName());

      // Set all XWidget labels to bold font
      for (XWidget xWidget : allXWidgets) {
         if (xWidget.getLabelWidget() != null) {
            SMAEditor.setLabelFonts(xWidget.getLabelWidget(), FontManager.getDefaultLabelFont());
         }
      }

      // Check extension points for page creation
      for (IAtsStateItem item : sma.getStateItems().getStateItems(atsWorkPage.getId())) {
         Result result = item.pageCreated(toolkit, atsWorkPage, sma, xModListener, isEditable || isGlobalEditable);
         if (result.isFalse()) {
            result.popup();
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Error in page creation => " + result.getText());
         }
      }

      createCurrentPageTransitionLine(workComp, atsWorkPage, toolkit);

      return workComp;
   }

   private void createMetricsHeader(Composite parent) throws OseeCoreException {
      if (!atsWorkPage.isCompleteCancelledState()) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = ALayout.getZeroMarginLayout(4, false);
         layout.marginLeft = 2;
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         allXWidgets.add(new StatePercentCompleteXWidget(getManagedForm(), atsWorkPage, sma, comp, 2, xModListener));
         allXWidgets.add(new StateHoursSpentXWidget(getManagedForm(), atsWorkPage, sma, comp, 2, xModListener));
      }
   }

   private void createReviewFooter(Composite parent, String forStateName) throws OseeCoreException {
      if (isShowReviewInfo() && sma.isTeamWorkflow()) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = new GridLayout(1, false);
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         allXWidgets.add(new ReviewInfoXWidget(getManagedForm(), toolkit, (TeamWorkFlowArtifact) sma, forStateName,
               comp, 1));
      }
   }

   private void createTaskFooter(Composite parent, String forStateName) throws OseeCoreException {
      if (isShowTaskInfo()) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = new GridLayout(6, false);
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         allXWidgets.add(new TaskInfoXWidget(getManagedForm(), ((TaskableStateMachineArtifact) sma), forStateName,
               comp, 2));
      }
   }

   protected boolean isShowTaskInfo() throws OseeCoreException {
      return sma.isTaskable();
   }

   protected boolean isShowTargetedVersion() throws OseeCoreException {
      return sma.isTargetedVersionable();
   }

   protected boolean isShowReviewInfo() throws OseeCoreException {
      return sma.isTeamWorkflow();
   }

   public Result isXWidgetSavable() throws OseeCoreException {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof IArtifactWidget) {
            IStatus status = widget.isValid();
            if (!status.isOK()) {
               return new Result(false, status.getMessage());
            }
         }
      }
      return Result.TrueResult;
   }

   @Override
   public String toString() {
      return atsWorkPage + " for " + getSma();
   }

   public Result isXWidgetDirty() throws OseeCoreException {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof IArtifactWidget) {
            Result result = ((IArtifactWidget) widget).isDirty();
            if (result.isTrue()) {
               return result;
            }
         }
      }
      return Result.FalseResult;
   }

   public void getDirtyIArtifactWidgets(List<IArtifactWidget> widgets) throws OseeCoreException {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof IArtifactWidget) {
            if (((IArtifactWidget) widget).isDirty().isTrue()) {
               widgets.add((IArtifactWidget) widget);
            }
         }
      }
   }

   private String getCurrentStateTitle() throws OseeCoreException {
      StringBuffer sb = new StringBuffer(atsWorkPage.getName());
      if (isEditable && !sma.isCompleted() && !sma.isCancelled()) {
         sb.append(" - Current State");
      }
      if (sma.isCancelled()) {
         LogItem item = sma.getLog().getStateEvent(LogType.StateCancelled);
         if (item == null) {
            throw new OseeStateException("ats.Log: Cancelled state has no logItem for " + sma.getHumanReadableId());
         }
         if (item.getState().equals(atsWorkPage.getName())) {
            sb.append(" - Cancelled");
            if (!item.getMsg().equals("")) {
               sb.append(" - Reason: " + item.getMsg());
            }
         }
      }
      if (isCurrentState) {
         if (sma.isCompleted()) {
            sb.append(" - ");
            sb.append(sma.getWorldViewCompletedDateStr());
            LogItem item = sma.getLog().getStateEvent(LogType.StateEntered, atsWorkPage.getName());
            sb.append(" by " + item.getUser().getName());
         } else if (sma.isCancelled()) {
            sb.append(" - ");
            sb.append(sma.getWorldViewCancelledDateStr());
            LogItem item = sma.getLog().getStateEvent(LogType.StateEntered, atsWorkPage.getName());
            sb.append(" by " + item.getUser().getName());
         }
         if (sma.getStateMgr().getAssignees().size() > 0) {
            sb.append(" assigned to ");
            sb.append(sma.getStateMgr().getAssigneesStr(80));
         }
      } else {
         LogItem item = sma.getLog().getStateEvent(LogType.StateComplete, atsWorkPage.getName());
         if (item != null) {
            sb.append(" - State Completed " + item.getDate(XDate.MMDDYYHHMM));
            sb.append(" by " + item.getUser().getName());
         }
      }
      return sb.toString();
   }

   @Override
   public void dispose() {
      super.dispose();
      for (XWidget xWidget : allXWidgets) {
         xWidget.dispose();
      }
      atsWorkPage.dispose();
   }

   final SMAWorkFlowSection fSection = this;
   final XModifiedListener xModListener = new XModifiedListener() {
      public void widgetModified(XWidget xWidget) {
         try {
            if (sma.isDeleted()) {
               return;
            }
            // Notify extensions of widget modified
            for (IAtsStateItem item : sma.getStateItems().getStateItems(atsWorkPage.getId())) {
               try {
                  item.widgetModified(fSection, xWidget);
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
            updateTransitionToState();
            updateTransitionToAssignees();
            sma.getEditor().onDirtied();
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   };

   @Override
   public void refresh() {
      super.refresh();
      try {
         if (Widgets.isAccessible(transitionAssigneesLabel)) {
            WorkPageDefinition toWorkPage = (WorkPageDefinition) transitionToStateCombo.getSelected();
            if (toWorkPage == null) {
               transitionAssigneesLabel.setText("");
            } else {
               transitionAssigneesLabel.setText(sma.getTransitionAssigneesStr());
            }
            transitionAssigneesLabel.getParent().layout();
         }
         sma.getEditor().onDirtied();
         for (XWidget xWidget : allXWidgets) {
            xWidget.refresh();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createTargetVersionAndAssigneeHeader(Composite parent, AtsWorkPage page, XFormToolkit toolkit) throws OseeCoreException {
      Composite comp = toolkit.createContainer(parent, 6);
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      // Create Privileged Edit label
      if (sma.getEditor().isPriviledgedEditModeEnabled()) {
         Label label = toolkit.createLabel(comp, "Priviledged Edit");
         label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         label.setToolTipText("Priviledged Edit Mode is Enabled.  Editing any field in any state is authorized.  Select icon to disable");
      }

      // Targeted Version
      if (isShowTargetedVersion()) {
         allXWidgets.add(new TargetVersionXWidget(getManagedForm(), sma, comp, 2, xModListener));
      }

      // Current Assignees
      if (page.isCurrentNonCompleteCancelledState(sma)) {
         allXWidgets.add(new CurrentAssigneesXWidget(getManagedForm(), sma, comp, 2, xModListener, isEditable));
      }

   }

   private void handleChangeTransitionAssignees() throws OseeCoreException {
      WorkPageDefinition toWorkPage = (WorkPageDefinition) transitionToStateCombo.getSelected();
      if (toWorkPage == null) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "No Transition State Selected");
         return;
      }
      if (toWorkPage.isCancelledPage() || toWorkPage.isCompletePage()) {
         AWorkbench.popup("ERROR", "No Assignees in Completed and Cancelled states");
         return;
      }
      UserCheckTreeDialog uld = new UserCheckTreeDialog();
      uld.setMessage("Select users to transition to.");
      uld.setInitialSelections(sma.getTransitionAssignees());
      if (uld.open() != 0) {
         return;
      }
      Collection<User> users = uld.getUsersSelected();
      if (users.size() == 0) {
         AWorkbench.popup("ERROR", "Must have at least one assignee");
         return;
      }
      sma.setTransitionAssignees(users);
      refresh();
      sma.getEditor().onDirtied();
   }

   private void createCurrentPageTransitionLine(Composite parent, AtsWorkPage atsWorkPage, XFormToolkit toolkit) throws OseeCoreException {
      Composite comp = toolkit.createContainer(parent, 5);
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      transitionButton = toolkit.createButton(comp, "Transition", SWT.PUSH);
      transitionButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleTransition();
         }
      });

      toolkit.createLabel(comp, "to");

      transitionToStateCombo = new XComboViewer("Transition To State Combo");
      transitionToStateCombo.setDisplayLabel(false);
      ArrayList<Object> allPages = new ArrayList<Object>();
      for (WorkPageDefinition nextPage : sma.getToWorkPages()) {
         allPages.add(nextPage);
      }
      transitionToStateCombo.setInput(allPages);
      transitionToStateCombo.setLabelProvider(new WorkPageDefinitionLabelProvider());
      transitionToStateCombo.setContentProvider(new ArrayContentProvider());
      transitionToStateCombo.setSorter(new WorkPageDefinitionViewSorter());

      transitionToStateCombo.createWidgets(comp, 1);

      // Set default page from workflow default
      ArrayList<Object> defaultPage = new ArrayList<Object>();
      if (atsWorkPage.getDefaultToPage() != null) {
         defaultPage.add(atsWorkPage.getDefaultToPage());
         transitionToStateCombo.setSelected(defaultPage);
      }
      if (atsWorkPage.isCancelledPage()) {
         LogItem item = sma.getLog().getStateEvent(LogType.StateCancelled);
         if (item != null) {
            defaultPage.add(sma.getWorkPageDefinitionByName(item.getState()));
            transitionToStateCombo.setSelected(defaultPage);
         }
      }
      // Update transition based on state items
      updateTransitionToState();

      transitionToStateCombo.getCombo().setVisibleItemCount(20);
      transitionToStateCombo.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            try {
               updateTransitionToAssignees();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      });

      Hyperlink assigneesLabelLink = toolkit.createHyperlink(comp, "Assignee(s)", SWT.NONE);
      assigneesLabelLink.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            try {
               handleChangeTransitionAssignees();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

      });
      transitionAssigneesLabel = toolkit.createLabel(comp, sma.getTransitionAssigneesStr());
      transitionAssigneesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

   }

   public void updateTransitionToAssignees() throws OseeCoreException {
      Collection<User> assignees = null;
      // Determine if the is an override set of assigness
      for (IAtsStateItem item : sma.getStateItems().getStateItems(atsWorkPage.getId())) {
         assignees = item.getOverrideTransitionToAssignees(this);
         if (assignees != null) {
            break;
         }
      }
      // If override set and isn't the same as already selected, update
      if (assignees != null && !sma.getTransitionAssignees().equals(assignees)) {
         sma.setTransitionAssignees(assignees);
         sma.getEditor().onDirtied();
      }
      refresh();
   }

   public void updateTransitionToState() throws OseeCoreException {
      // Determine if there is a transitionToStateOverride for this page
      String transitionStateOverride = null;
      for (IAtsStateItem item : sma.getStateItems().getStateItems(atsWorkPage.getId())) {
         transitionStateOverride = item.getOverrideTransitionToStateName(this);
         if (transitionStateOverride != null) {
            break;
         }
      }
      if (transitionStateOverride != null) {
         // Return if override state is same as selected
         if (((WorkPageDefinition) transitionToStateCombo.getSelected()).getName().equals(transitionStateOverride)) {
            return;
         }
         // Find page corresponding to override state name
         for (WorkPageDefinition toWorkPageDefinition : sma.getToWorkPages()) {
            if (toWorkPageDefinition.getPageName().equals(transitionStateOverride)) {
               // Reset selection
               ArrayList<Object> defaultPage = new ArrayList<Object>();
               defaultPage.add(toWorkPageDefinition);
               transitionToStateCombo.setSelected(defaultPage);
               return;
            }
         }
      }
   }

   public void setTransitionToStateSelection(String stateName) throws OseeCoreException {
      ArrayList<Object> allPages = new ArrayList<Object>();
      for (WorkPageDefinition nextPage : sma.getToWorkPages()) {
         if (nextPage.getPageName().equals(stateName)) {
            allPages.add(nextPage);
         }
      }
      transitionToStateCombo.setSelected(allPages);
   }

   private void handleTransition() {

      try {

         if (!isEditable && !sma.getStateMgr().getAssignees().contains(UserManager.getUser(SystemUser.UnAssigned))) {
            AWorkbench.popup(
                  "ERROR",
                  "You must be assigned to transition this workflow.\nContact Assignee or Select Priviledged Edit for Authorized Overriders.");
            return;
         }
         // As a convenience, if assignee is UnAssigned and user selects to transition, make user current assignee
         if (sma.getStateMgr().getAssignees().contains(UserManager.getUser(SystemUser.UnAssigned))) {
            sma.getStateMgr().removeAssignee(UserManager.getUser(SystemUser.UnAssigned));
            sma.getStateMgr().addAssignee(UserManager.getUser());
         }
         if (sma.isTeamWorkflow() && ((TeamWorkFlowArtifact) sma).getBranchMgr().isWorkingBranchInWork()) {

            if (((WorkPageDefinition) transitionToStateCombo.getSelected()).getPageName().equals(
                  DefaultTeamState.Cancelled.name())) {
               AWorkbench.popup("Transition Blocked",
                     "Working Branch exists.\n\nPlease delete working branch before transition to cancel.");
               return;
            }
            if (((TeamWorkFlowArtifact) sma).getBranchMgr().isBranchInCommit()) {
               AWorkbench.popup("Transition Blocked",
                     "Working Branch is being Committed.\n\nPlease wait till commit completes to transition.");
               return;
            }
            if (!atsWorkPage.isAllowTransitionWithWorkingBranch()) {
               AWorkbench.popup("Transition Blocked",
                     "Working Branch exists.\n\nPlease commit or delete working branch before transition.");
               return;
            }

         }

         sma.setInTransition(true);
         sma.getEditor().doSave(null);

         // Get transition to state
         WorkPageDefinition toWorkPageDefinition = (WorkPageDefinition) transitionToStateCombo.getSelected();

         if (toWorkPageDefinition == null) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "No Transition State Selected");
            return;
         }
         if (toWorkPageDefinition.getPageName().equals(DefaultTeamState.Cancelled.name())) {
            EntryDialog cancelDialog = new EntryDialog("Cancellation Reason", "Enter cancellation reason.");
            if (cancelDialog.open() != 0) {
               return;
            }
            SkynetTransaction transaction =
                  new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Transition to Cancelled");
            Result result = sma.transitionToCancelled(cancelDialog.getEntry(), transaction, TransitionOption.Persist);
            transaction.execute();
            if (result.isFalse()) {
               result.popup();
               return;
            }
            sma.setInTransition(false);
            sma.getEditor().refreshPages();
            return;
         }

         // Validate assignees
         if (sma.getStateMgr().getAssignees().contains(UserManager.getUser(SystemUser.OseeSystem)) || sma.getStateMgr().getAssignees().contains(
               UserManager.getUser(SystemUser.Guest)) || sma.getStateMgr().getAssignees().contains(
               UserManager.getUser(SystemUser.UnAssigned))) {
            AWorkbench.popup("Transition Blocked",
                  "Can not transition with \"Guest\", \"UnAssigned\" or \"OseeSystem\" user as assignee.");
            return;
         }

         // Get transition to assignees
         Collection<User> toAssignees;
         if (toWorkPageDefinition.isCancelledPage() || toWorkPageDefinition.isCompletePage()) {
            toAssignees = new HashSet<User>();
         } else {
            toAssignees = sma.getTransitionAssignees();
         }

         // If this is a return transition, don't require page/tasks to be complete
         if (!sma.isReturnPage(toWorkPageDefinition)) {

            // Validate XWidgets for transition
            Result result = atsWorkPage.isPageComplete();
            if (result.isFalse()) {
               result.popup();
               return;
            }

            // Loop through this state's tasks to confirm complete
            if (sma.isTaskable()) {
               if (sma instanceof TaskableStateMachineArtifact) {
                  for (TaskArtifact taskArt : ((TaskableStateMachineArtifact) sma).getTaskArtifactsFromCurrentState()) {
                     if (taskArt.isInWork()) {
                        AWorkbench.popup(
                              "Transition Blocked",
                              "Task Not Complete\n\nTitle: " + taskArt.getName() + "\n\nHRID: " + taskArt.getHumanReadableId());
                        return;
                     }
                  }
               }
            }

            // Don't transition without targeted version if so configured
            if (sma.teamDefHasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name()) || sma.getWorkPageDefinition().hasWorkRule(
                  AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name())) {
               if (sma.getWorldViewTargetedVersion() == null && !toWorkPageDefinition.isCancelledPage()) {
                  AWorkbench.popup("Transition Blocked",
                        "Actions must be targeted for a Version.\nPlease set \"Target Version\" before transition.");
                  return;
               }
            }

            // Loop through this state's blocking reviews to confirm complete
            if (sma.isTeamWorkflow()) {
               for (ReviewSMArtifact reviewArt : ReviewManager.getReviewsFromCurrentState((TeamWorkFlowArtifact) sma)) {
                  if (reviewArt.getReviewBlockType() == ReviewBlockType.Transition && !reviewArt.isCancelledOrCompleted()) {
                     AWorkbench.popup("Transition Blocked", "All Blocking Reviews must be completed before transition.");
                     return;
                  }
               }
            }

            // Check extension points for valid transition
            for (IAtsStateItem item : sma.getStateItems().getStateItems(atsWorkPage.getId())) {
               try {
                  result =
                        item.transitioning(sma, sma.getStateMgr().getCurrentStateName(),
                              toWorkPageDefinition.getPageName(), toAssignees);
                  if (result.isFalse()) {
                     result.popup();
                     return;
                  }
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }

            // Ask for metrics for this page (store in state versus task?)
            if (!handlePopulateStateMetrics()) {
               return;
            }
         }

         // Persist must be done prior and separate from transition 
         sma.persist();

         // Perform transition separate from persist of previous changes to state machine artifact
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Transition");
         Result result =
               sma.transition(toWorkPageDefinition.getPageName(), toAssignees, transaction, TransitionOption.Persist);
         transaction.execute();
         if (result.isFalse()) {
            result.popup();
            return;
         }
         sma.setInTransition(false);
         sma.getEditor().refreshPages();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      } finally {
         sma.setInTransition(false);
      }
   }

   /**
    * @return Returns the isCurrentState.
    */
   public boolean isCurrentState() {
      return isCurrentState;
   }

   public boolean handlePopulateStateMetrics() throws OseeCoreException {

      // Page has the ability to override the autofill of the metrics
      if (!atsWorkPage.isRequireStateHoursSpentPrompt() && sma.getStateMgr().getHoursSpent() == 0) {
         // First, try to autofill if it's only been < 5 min since creation
         double minSinceCreation = getCreationToNowDateDeltaMinutes();
         // System.out.println("minSinceCreation *" + minSinceCreation + "*");
         double hoursSinceCreation = minSinceCreation / 60.0;
         if (hoursSinceCreation < 0.02) {
            hoursSinceCreation = 0.02;
         }
         // System.out.println("hoursSinceCreation *" + hoursSinceCreation + "*");
         if (minSinceCreation < 5) {
            sma.getStateMgr().updateMetrics(hoursSinceCreation, 100, true);
            return true;
         }
      }

      // Otherwise, open dialog to ask for hours complete
      String msg =
            sma.getStateMgr().getCurrentStateName() + " State\n\n" + AtsUtil.doubleToI18nString(sma.getStateMgr().getHoursSpent()) + " hours already spent on this state.\n" + "Enter the additional number of hours you spent on this state.";
      SMAStatusDialog tsd =
            new SMAStatusDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Enter Hours Spent",
                  msg, false, Arrays.asList(sma));
      int result = tsd.open();
      if (result == 0) {
         sma.getStateMgr().updateMetrics(tsd.getHours().getFloat(), 100, true);
         return true;
      }
      return false;
   }

   public int getCreationToNowDateDeltaMinutes() throws OseeCoreException {
      Date createDate = sma.getLog().getStateEvent(LogType.StateEntered, atsWorkPage.getName()).getDate();
      long createDateLong = createDate.getTime();
      Date date = new Date();
      float diff = date.getTime() - createDateLong;
      // System.out.println("diff *" + diff + "*");
      Float min = diff / 60000;
      // System.out.println("min *" + min + "*");
      return min.intValue();
   }

   public XComboViewer getTransitionToStateCombo() {
      return transitionToStateCombo;
   }

   public StateMachineArtifact getSma() {
      return sma;
   }

   public AtsWorkPage getPage() {
      return atsWorkPage;
   }

   public Composite getMainComp() {
      return mainComp;
   }

   public List<XWidget> getXWidgets(Class<?> clazz) {
      List<XWidget> widgets = new ArrayList<XWidget>();
      for (XWidget widget : allXWidgets) {
         if (clazz.isInstance(widget)) {
            widgets.add(widget);
         }
      }
      return widgets;
   }

   private boolean isEditable(AtsWorkPage page) throws OseeCoreException {
      // must be writeable
      return !sma.isReadOnly() &&
      // and access control writeable
      sma.isAccessControlWrite() &&
      // and current state
      sma.isCurrentState(page.getName()) &&
      // and one of these
      //
      // page is define to allow anyone to edit
      (sma.getWorkPageDefinition().hasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsAllowEditToAll.name()) ||
      // team definition has allowed anyone to edit
      sma.teamDefHasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsAllowEditToAll.name()) ||
      // priviledged edit mode is on
      sma.getEditor().isPriviledgedEditModeEnabled() ||
      // current user is assigned
      sma.isAssigneeMe() ||
      // current user is ats admin
      AtsUtil.isAtsAdmin());
   }
}
