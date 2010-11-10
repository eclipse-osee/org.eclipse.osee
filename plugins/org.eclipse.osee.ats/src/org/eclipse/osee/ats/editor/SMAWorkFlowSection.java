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
import org.eclipse.osee.ats.artifact.AbstractReviewArtifact;
import org.eclipse.osee.ats.artifact.AbstractReviewArtifact.ReviewBlockType;
import org.eclipse.osee.ats.artifact.AbstractTaskableArtifact;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.log.LogItem;
import org.eclipse.osee.ats.artifact.log.LogType;
import org.eclipse.osee.ats.editor.stateItem.AtsStateItemManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.editor.widget.ReviewInfoXWidget;
import org.eclipse.osee.ats.editor.widget.StateHoursSpentXWidget;
import org.eclipse.osee.ats.editor.widget.StatePercentCompleteXWidget;
import org.eclipse.osee.ats.editor.widget.TaskInfoXWidget;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.TransitionOption;
import org.eclipse.osee.ats.util.XCancellationReasonTextWidget;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.util.widgets.dialog.SMAStatusDialog;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactStoredWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValue;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserCheckTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinitionLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinitionViewSorter;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkFlowSection extends SectionPart {

   private XComboViewer transitionToStateCombo;
   private Label transitionAssigneesLabel;
   protected final AbstractWorkflowArtifact sma;
   private final AtsWorkPage atsWorkPage;
   private final boolean isEditable, isCurrentState, isGlobalEditable;
   private final XFormToolkit toolkit;
   private Composite mainComp;
   private final List<XWidget> allXWidgets = new ArrayList<XWidget>();
   private boolean sectionCreated = false;
   private Section section;

   public SMAWorkFlowSection(Composite parent, XFormToolkit toolkit, int style, AtsWorkPage page, AbstractWorkflowArtifact sma) throws OseeCoreException {
      super(parent, toolkit, style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.toolkit = toolkit;
      this.atsWorkPage = page;
      this.sma = sma;

      isEditable = isEditable(sma, page);
      isGlobalEditable =
         !sma.isReadOnly() && sma.isAccessControlWrite() && sma.getEditor().isPriviledgedEditModeEnabled();
      isCurrentState = sma.isCurrentState(page.getName());
      // parent.setBackground(Displays.getSystemColor(SWT.COLOR_CYAN));
   }

   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);

      section = getSection();
      try {
         section.setText(getCurrentStateTitle());
         if (sma.isCurrentState(atsWorkPage.getName())) {
            section.setTitleBarForeground(Displays.getSystemColor(SWT.COLOR_DARK_GREEN));
            section.setBackground(AtsUtil.ACTIVE_COLOR);
         }
         section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         // section.setBackground(Displays.getSystemColor(SWT.COLOR_MAGENTA));

         boolean isCurrentSectionExpanded = isCurrentSectionExpanded(atsWorkPage.getName());

         if (isCurrentSectionExpanded) {
            createSection(section);
         }
         // Only load when users selects section
         section.addListener(SWT.Activate, new Listener() {

            @Override
            public void handleEvent(Event e) {
               try {
                  createSection(section);
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
         });

         section.layout();
         section.setExpanded(isCurrentSectionExpanded);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * Override to apply different algorithm to current section expansion.
    */
   public boolean isCurrentSectionExpanded(String stateName) {
      return sma.getStateMgr().getCurrentStateName().equals(stateName);
   }

   private synchronized void createSection(Section section) throws OseeCoreException {
      if (sectionCreated) {
         return;
      }

      mainComp = toolkit.createClientContainer(section, 2);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      mainComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      // mainComp.setBackground(Displays.getSystemColor(SWT.COLOR_DARK_YELLOW));
      mainComp.layout();

      SMAWorkFlowTab.createStateNotesHeader(mainComp, toolkit, sma, 2, atsWorkPage.getName());

      Composite workComp = createWorkArea(mainComp, atsWorkPage, toolkit);

      if (isCurrentState) {
         createCurrentPageTransitionLine(mainComp, atsWorkPage, toolkit);
      }

      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
      gridData.widthHint = 400;
      workComp.setLayoutData(gridData);
      sectionCreated = true;
   }

   protected Composite createWorkArea(Composite comp, AtsWorkPage atsWorkPage, XFormToolkit toolkit) throws OseeCoreException {

      atsWorkPage.generateLayoutDatas(sma);

      // Create Page
      Composite workComp = toolkit.createContainer(comp, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      // workComp.setBackground(Displays.getSystemColor(SWT.COLOR_GREEN));

      createMetricsHeader(workComp);

      // Add any dynamic XWidgets declared for page by IAtsStateItem extensions
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(atsWorkPage.getId())) {
         for (XWidget xWidget : item.getDynamicXWidgetsPreBody(sma)) {
            xWidget.createWidgets(workComp, 2);
            allXWidgets.add(xWidget);
            allXWidgets.addAll(xWidget.getChildrenXWidgets());
         }
      }

      if (atsWorkPage.isCompleteCancelledState()) {
         Composite completeComp = new Composite(workComp, SWT.None);
         GridLayout layout = new GridLayout(1, false);
         completeComp.setLayout(layout);
         completeComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         if (atsWorkPage.isCancelledPage()) {
            createCancelledPageWidgets(completeComp);
         } else if (atsWorkPage.isCompletePage()) {
            createCompletedPageWidgets(completeComp);
         }
      }

      // Create dynamic XWidgets
      DynamicXWidgetLayout dynamicXWidgetLayout =
         atsWorkPage.createBody(getManagedForm(), workComp, sma, xModListener, isEditable || isGlobalEditable);
      for (XWidget xWidget : dynamicXWidgetLayout.getXWidgets()) {
         allXWidgets.add(xWidget);
         allXWidgets.addAll(xWidget.getChildrenXWidgets());
      }

      // Add any dynamic XWidgets declared for page by IAtsStateItem extensions
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(atsWorkPage.getId())) {
         for (XWidget xWidget : item.getDynamicXWidgetsPostBody(sma)) {
            xWidget.createWidgets(workComp, 2);
            allXWidgets.add(xWidget);
            allXWidgets.addAll(xWidget.getChildrenXWidgets());
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
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(atsWorkPage.getId())) {
         Result result = item.pageCreated(toolkit, atsWorkPage, sma, xModListener, isEditable || isGlobalEditable);
         if (result.isFalse()) {
            result.popup();
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Error in page creation => " + result.getText());
         }
      }

      return workComp;
   }

   private void createCancelledPageWidgets(Composite parent) throws OseeCoreException {
      XWidget xWidget = null;
      xWidget = new XLabelValue("Cancelled from State", sma.getLog().getCancelledFromState());
      xWidget.createWidgets(parent, 1);
      allXWidgets.add(xWidget);

      if (sma.getEditor().isPriviledgedEditModeEnabled()) {
         xWidget = new XCancellationReasonTextWidget(sma);
         xWidget.addXModifiedListener(xModListener);
      } else {
         xWidget = new XLabelValue("Cancellation Reason", sma.getLog().getCancellationReason());
      }
      xWidget.createWidgets(parent, 1);
      allXWidgets.add(xWidget);
   }

   private void createCompletedPageWidgets(Composite parent) throws OseeCoreException {
      XWidget xWidget = null;
      xWidget = new XLabelValue("Completed from State", sma.getLog().getCompletedFromState());
      xWidget.createWidgets(parent, 1);
      allXWidgets.add(xWidget);
   }

   private void createMetricsHeader(Composite parent) {
      if (!atsWorkPage.isCompleteCancelledState()) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = ALayout.getZeroMarginLayout(4, false);
         layout.marginLeft = 2;
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         allXWidgets.add(new StatePercentCompleteXWidget(getManagedForm(), atsWorkPage, sma, comp, 2, xModListener,
            isCurrentState));
         allXWidgets.add(new StateHoursSpentXWidget(getManagedForm(), atsWorkPage, sma, comp, 2, xModListener,
            isCurrentState));
      }
   }

   private void createReviewFooter(Composite parent, String forStateName) {
      if (isShowReviewInfo() && sma.isTeamWorkflow()) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = new GridLayout(1, false);
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         allXWidgets.add(new ReviewInfoXWidget(getManagedForm(), toolkit, (TeamWorkFlowArtifact) sma, forStateName,
            comp, 1));
      }
   }

   private void createTaskFooter(Composite parent, String forStateName) {
      if (sma instanceof AbstractTaskableArtifact) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = new GridLayout(6, false);
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         allXWidgets.add(new TaskInfoXWidget(getManagedForm(), ((AbstractTaskableArtifact) sma), forStateName, comp, 2));
      }
   }

   protected boolean isShowReviewInfo() {
      return sma.isTeamWorkflow();
   }

   public Result isXWidgetSavable() {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof IArtifactStoredWidget) {
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
         if (widget instanceof IArtifactStoredWidget) {
            IArtifactStoredWidget artifactStoredWidget = (IArtifactStoredWidget) widget;
            Result result = artifactStoredWidget.isDirty();
            if (result.isTrue()) {
               return result;
            }
         }
      }
      return Result.FalseResult;
   }

   public void getDirtyIArtifactWidgets(List<IArtifactStoredWidget> widgets) throws OseeCoreException {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof IArtifactStoredWidget) {
            IArtifactStoredWidget artifactStoredWidget = (IArtifactStoredWidget) widget;
            if (artifactStoredWidget.isDirty().isTrue()) {
               widgets.add(artifactStoredWidget);
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
            throw new OseeStateException("ats.Log: Cancelled state has no logItem for [%s]", sma.getGuid());
         }
         if (item.getState().equals(atsWorkPage.getName())) {
            sb.append(" - Cancelled");
            if (!item.getMsg().equals("")) {
               sb.append(" - Reason: ");
               sb.append(item.getMsg());
            }
         }
      }
      if (isCurrentState) {
         if (sma.isCompleted()) {
            sb.append(" - ");
            sb.append(DateUtil.getMMDDYYHHMM(sma.getCompletedDate()));
            LogItem item = sma.getLog().getStateEvent(LogType.StateEntered, atsWorkPage.getName());
            sb.append(" by ");
            sb.append(item.getUser().getName());
         } else if (sma.isCancelled()) {
            sb.append(" - ");
            sb.append(DateUtil.getMMDDYYHHMM(sma.getCancelledDate()));
            LogItem item = sma.getLog().getStateEvent(LogType.StateEntered, atsWorkPage.getName());
            sb.append(" by ");
            sb.append(item.getUser().getName());
         }
         if (sma.getStateMgr().getAssignees().size() > 0) {
            sb.append(" assigned to ");
            sb.append(sma.getStateMgr().getAssigneesStr(80));
         }
      } else {
         LogItem item = sma.getLog().getStateEvent(LogType.StateComplete, atsWorkPage.getName());
         if (item != null) {
            sb.append(" - State Completed ");
            sb.append(item.getDate(DateUtil.MMDDYYHHMM));
            sb.append(" by ");
            sb.append(item.getUser().getName());
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
      @Override
      public void widgetModified(XWidget xWidget) {
         try {
            if (sma.isDeleted()) {
               return;
            }
            // Notify extensions of widget modified
            for (IAtsStateItem item : AtsStateItemManager.getStateItems(atsWorkPage.getId())) {
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
      if (!Widgets.isAccessible(mainComp)) {
         return;
      }
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
      if (sma.getParentTeamWorkflow() != null) {
         uld.setTeamMembers(sma.getParentTeamWorkflow().getTeamDefinition().getMembersAndLeads());
      }
      if (uld.open() != 0) {
         return;
      }
      Collection<User> users = uld.getUsersSelected();
      if (users.isEmpty()) {
         AWorkbench.popup("ERROR", "Must have at least one assignee");
         return;
      }
      sma.setTransitionAssignees(users);
      refresh();
      sma.getEditor().onDirtied();
   }

   private void createCurrentPageTransitionLine(Composite parent, AtsWorkPage atsWorkPage, XFormToolkit toolkit) throws OseeCoreException {
      Composite comp = toolkit.createComposite(parent, SWT.NONE);
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      comp.setLayout(new GridLayout(5, false));
      comp.setBackground(AtsUtil.ACTIVE_COLOR);

      Button transitionButton = toolkit.createButton(comp, "Transition", SWT.PUSH);
      transitionButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleTransition();
         }
      });
      transitionButton.setBackground(AtsUtil.ACTIVE_COLOR);

      Label label = toolkit.createLabel(comp, "to");
      label.setBackground(AtsUtil.ACTIVE_COLOR);

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
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            try {
               updateTransitionToAssignees();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      });

      Hyperlink assigneesLabelLink = toolkit.createHyperlink(comp, "Next State Assignee(s)", SWT.NONE);
      assigneesLabelLink.addHyperlinkListener(new IHyperlinkListener() {

         @Override
         public void linkEntered(HyperlinkEvent e) {
            // do nothing
         }

         @Override
         public void linkExited(HyperlinkEvent e) {
            // do nothing
         }

         @Override
         public void linkActivated(HyperlinkEvent e) {
            try {
               handleChangeTransitionAssignees();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

      });
      assigneesLabelLink.setBackground(AtsUtil.ACTIVE_COLOR);

      transitionAssigneesLabel =
         toolkit.createLabel(comp, Strings.truncate(sma.getTransitionAssigneesStr(), 100, true));
      transitionAssigneesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      transitionAssigneesLabel.setBackground(AtsUtil.ACTIVE_COLOR);

   }

   public void updateTransitionToAssignees() throws OseeCoreException {
      Collection<User> assignees = null;
      // Determine if the is an override set of assigness
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(atsWorkPage.getId())) {
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
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(atsWorkPage.getId())) {
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
         if (!isWorkingBranchTransitionable()) {
            return;
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
            handleTransitionToCancelled();
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
         if (!sma.isReturnPage(toWorkPageDefinition) && !isWorkPageTransitionable(toWorkPageDefinition, toAssignees)) {
            return;
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
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      } finally {
         sma.setInTransition(false);
      }
   }

   private boolean isWorkPageTransitionable(WorkPageDefinition toWorkPageDefinition, Collection<User> toAssignees) throws OseeCoreException {
      // Validate XWidgets for transition
      Result result = atsWorkPage.isPageComplete();
      if (result.isFalse()) {
         result.popup();
         return false;
      }

      // Loop through this state's tasks to confirm complete
      if (sma instanceof AbstractTaskableArtifact && !sma.isCancelledOrCompleted()) {
         for (TaskArtifact taskArt : ((AbstractTaskableArtifact) sma).getTaskArtifactsFromCurrentState()) {
            if (taskArt.isInWork()) {
               AWorkbench.popup("Transition Blocked",
                  "Task Not Complete\n\nTitle: " + taskArt.getName() + "\n\nHRID: " + taskArt.getHumanReadableId());
               return false;
            }
         }
      }

      // Don't transition without targeted version if so configured
      boolean teamDefRequiresTargetedVersion =
         sma.teamDefHasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name());
      boolean pageRequiresTargetedVersion =
         sma.getWorkPageDefinition().hasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name());

      // Only check this if TeamWorkflow, not for reviews
      if (sma instanceof TeamWorkFlowArtifact && (teamDefRequiresTargetedVersion || pageRequiresTargetedVersion) && //
      sma.getTargetedVersion() == null && //
      !toWorkPageDefinition.isCancelledPage()) {
         AWorkbench.popup("Transition Blocked",
            "Actions must be targeted for a Version.\nPlease set \"Target Version\" before transition.");
         return false;
      }

      // Loop through this state's blocking reviews to confirm complete
      if (sma.isTeamWorkflow()) {
         for (AbstractReviewArtifact reviewArt : ReviewManager.getReviewsFromCurrentState((TeamWorkFlowArtifact) sma)) {
            if (reviewArt.getReviewBlockType() == ReviewBlockType.Transition && !reviewArt.isCancelledOrCompleted()) {
               AWorkbench.popup("Transition Blocked", "All Blocking Reviews must be completed before transition.");
               return false;
            }
         }
      }

      // Check extension points for valid transition
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(atsWorkPage.getId())) {
         try {
            result =
               item.transitioning(sma, sma.getStateMgr().getCurrentStateName(), toWorkPageDefinition.getPageName(),
                  toAssignees);
            if (result.isFalse()) {
               result.popup();
               return false;
            }
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }

      // Ask for metrics for this page (store in state versus task?)
      if (!handlePopulateStateMetrics()) {
         return false;
      }
      return true;
   }

   private void handleTransitionToCancelled() throws OseeCoreException {
      EntryDialog cancelDialog = new EntryDialog("Cancellation Reason", "Enter cancellation reason.");
      if (cancelDialog.open() != 0) {
         return;
      }
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Transition to Cancelled");
      Result result = sma.transitionToCancelled(cancelDialog.getEntry(), transaction, TransitionOption.Persist);
      transaction.execute();
      if (result.isFalse()) {
         result.popup();
         return;
      }
      sma.setInTransition(false);
      sma.getEditor().refreshPages();
   }

   private boolean isWorkingBranchTransitionable() throws OseeCoreException {
      if (sma.isTeamWorkflow() && ((TeamWorkFlowArtifact) sma).getBranchMgr().isWorkingBranchInWork()) {

         if (((WorkPageDefinition) transitionToStateCombo.getSelected()).getPageName().equals(
            DefaultTeamState.Cancelled.name())) {
            AWorkbench.popup("Transition Blocked",
               "Working Branch exists.\n\nPlease delete working branch before transition to cancel.");
            return false;
         }
         if (((TeamWorkFlowArtifact) sma).getBranchMgr().isBranchInCommit()) {
            AWorkbench.popup("Transition Blocked",
               "Working Branch is being Committed.\n\nPlease wait till commit completes to transition.");
            return false;
         }
         if (!atsWorkPage.isAllowTransitionWithWorkingBranch()) {
            AWorkbench.popup("Transition Blocked",
               "Working Branch exists.\n\nPlease commit or delete working branch before transition.");
            return false;
         }
      }
      return true;
   }

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
         new SMAStatusDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Enter Hours Spent", msg,
            false, Arrays.asList(sma));
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

   public AbstractWorkflowArtifact getSma() {
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

   public static boolean isEditable(AbstractWorkflowArtifact sma, AtsWorkPage page) throws OseeCoreException {
      // must be writeable
      return !sma.isReadOnly() &&
      // and access control writeable
      sma.isAccessControlWrite() &&
      // and current state
      (page == null || sma.isCurrentState(page.getName())) &&
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
