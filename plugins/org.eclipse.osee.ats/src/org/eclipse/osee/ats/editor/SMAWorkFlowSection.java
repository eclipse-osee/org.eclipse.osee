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
import org.eclipse.osee.ats.editor.stateItem.AtsStateItemManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.editor.widget.ReviewInfoXWidget;
import org.eclipse.osee.ats.editor.widget.StateHoursSpentXWidget;
import org.eclipse.osee.ats.editor.widget.StatePercentCompleteXWidget;
import org.eclipse.osee.ats.editor.widget.TaskInfoXWidget;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.TeamState;
import org.eclipse.osee.ats.util.TransitionOption;
import org.eclipse.osee.ats.util.XCancellationReasonTextWidget;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.util.widgets.dialog.SMAStatusDialog;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.ats.workdef.StateDefinitionLabelProvider;
import org.eclipse.osee.ats.workdef.StateDefinitionViewSorter;
import org.eclipse.osee.ats.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.workflow.TransitionManager;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
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
   private final StateXWidgetPage statePage;
   private final boolean isEditable, isCurrentState, isGlobalEditable;
   private Composite mainComp;
   private final List<XWidget> allXWidgets = new ArrayList<XWidget>();
   private boolean sectionCreated = false;
   private Section section;
   private final SMAEditor editor;

   public SMAWorkFlowSection(Composite parent, int style, StateXWidgetPage page, AbstractWorkflowArtifact sma, final SMAEditor editor) throws OseeCoreException {
      super(parent, editor.getToolkit(), style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.statePage = page;
      this.sma = sma;
      this.editor = editor;

      isEditable = isEditable(sma, page, editor);
      isGlobalEditable = !sma.isReadOnly() && sma.isAccessControlWrite() && editor.isPriviledgedEditModeEnabled();
      isCurrentState = sma.isInState(page);
      // parent.setBackground(Displays.getSystemColor(SWT.COLOR_CYAN));
   }

   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);

      section = getSection();
      try {
         section.setText(getCurrentStateTitle());
         if (sma.isInState(statePage)) {
            section.setTitleBarForeground(Displays.getSystemColor(SWT.COLOR_DARK_GREEN));
            section.setBackground(AtsUtil.ACTIVE_COLOR);
         }
         section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         // section.setBackground(Displays.getSystemColor(SWT.COLOR_MAGENTA));

         boolean isCurrentSectionExpanded = isCurrentSectionExpanded(statePage);

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
   public boolean isCurrentSectionExpanded(IWorkPage state) {
      return sma.isInState(state);
   }

   private synchronized void createSection(Section section) throws OseeCoreException {
      if (sectionCreated) {
         return;
      }

      mainComp = editor.getToolkit().createClientContainer(section, 2);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      mainComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      // mainComp.setBackground(Displays.getSystemColor(SWT.COLOR_DARK_YELLOW));
      mainComp.layout();

      SMAWorkFlowTab.createStateNotesHeader(mainComp, editor.getToolkit(), sma, 2, statePage.getPageName());

      Composite workComp = createWorkArea(mainComp, statePage, editor.getToolkit());

      if (isCurrentState) {
         createCurrentPageTransitionLine(mainComp, statePage, editor.getToolkit());
      }

      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
      gridData.widthHint = 400;
      workComp.setLayoutData(gridData);
      sectionCreated = true;
   }

   protected Composite createWorkArea(Composite comp, StateXWidgetPage statePage, XFormToolkit toolkit) throws OseeCoreException {

      statePage.generateLayoutDatas(sma);

      // Create Page
      Composite workComp = toolkit.createContainer(comp, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      // workComp.setBackground(Displays.getSystemColor(SWT.COLOR_GREEN));

      createMetricsHeader(workComp);

      // Add any dynamic XWidgets declared for page by IAtsStateItem extensions
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(statePage.getStateDefinition())) {
         for (XWidget xWidget : item.getDynamicXWidgetsPreBody(sma)) {
            xWidget.createWidgets(workComp, 2);
            allXWidgets.add(xWidget);
            allXWidgets.addAll(xWidget.getChildrenXWidgets());
         }
      }

      if (statePage.isCompletedOrCancelledPage()) {
         Composite completeComp = new Composite(workComp, SWT.None);
         GridLayout layout = new GridLayout(1, false);
         completeComp.setLayout(layout);
         completeComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         if (statePage.isCancelledPage()) {
            createCancelledPageWidgets(completeComp);
         } else if (statePage.isCompletedPage()) {
            createCompletedPageWidgets(completeComp);
         }
      }

      // Create dynamic XWidgets
      DynamicXWidgetLayout dynamicXWidgetLayout =
         statePage.createBody(getManagedForm(), workComp, sma, xModListener, isEditable || isGlobalEditable);
      for (XWidget xWidget : dynamicXWidgetLayout.getXWidgets()) {
         allXWidgets.add(xWidget);
         allXWidgets.addAll(xWidget.getChildrenXWidgets());
      }

      // Add any dynamic XWidgets declared for page by IAtsStateItem extensions
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(statePage.getStateDefinition())) {
         for (XWidget xWidget : item.getDynamicXWidgetsPostBody(sma)) {
            xWidget.createWidgets(workComp, 2);
            allXWidgets.add(xWidget);
            allXWidgets.addAll(xWidget.getChildrenXWidgets());
         }
      }

      createTaskFooter(workComp, statePage);
      createReviewFooter(workComp, statePage);

      // Set all XWidget labels to bold font
      for (XWidget xWidget : allXWidgets) {
         if (xWidget.getLabelWidget() != null) {
            SMAEditor.setLabelFonts(xWidget.getLabelWidget(), FontManager.getDefaultLabelFont());
         }
      }

      // Check extension points for page creation
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(statePage.getStateDefinition())) {
         Result result = item.pageCreated(toolkit, statePage, sma, xModListener, isEditable || isGlobalEditable);
         if (result.isFalse()) {
            result.popup();
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Error in page creation => " + result.getText());
         }
      }

      return workComp;
   }

   private void createCancelledPageWidgets(Composite parent) throws OseeCoreException {
      XWidget xWidget = null;
      xWidget = new XLabelValue("Cancelled from State", sma.getCancelledFromState());
      xWidget.createWidgets(parent, 1);
      allXWidgets.add(xWidget);

      if (editor.isPriviledgedEditModeEnabled()) {
         xWidget = new XCancellationReasonTextWidget(sma);
         xWidget.addXModifiedListener(xModListener);
      } else {
         xWidget = new XLabelValue("Cancellation Reason", sma.getCancelledReason());
      }
      xWidget.createWidgets(parent, 1);
      allXWidgets.add(xWidget);
   }

   private void createCompletedPageWidgets(Composite parent) throws OseeCoreException {
      XWidget xWidget = null;
      xWidget = new XLabelValue("Completed from State", sma.getCompletedFromState());
      xWidget.createWidgets(parent, 1);
      allXWidgets.add(xWidget);
   }

   private void createMetricsHeader(Composite parent) {
      if (!statePage.isCompletedOrCancelledPage()) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = ALayout.getZeroMarginLayout(4, false);
         layout.marginLeft = 2;
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         allXWidgets.add(new StatePercentCompleteXWidget(getManagedForm(), statePage, sma, comp, 2, xModListener,
            isCurrentState, editor));
         allXWidgets.add(new StateHoursSpentXWidget(getManagedForm(), statePage, sma, comp, 2, xModListener,
            isCurrentState, editor));
      }
   }

   private void createReviewFooter(Composite parent, IWorkPage forState) {
      if (isShowReviewInfo() && sma.isTeamWorkflow()) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = new GridLayout(1, false);
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         allXWidgets.add(new ReviewInfoXWidget(getManagedForm(), editor.getToolkit(), (TeamWorkFlowArtifact) sma,
            forState, comp, 1));
      }
   }

   private void createTaskFooter(Composite parent, IWorkPage state) {
      if (sma instanceof AbstractTaskableArtifact) {
         Composite comp = new Composite(parent, SWT.None);
         GridLayout layout = new GridLayout(6, false);
         comp.setLayout(layout);
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         allXWidgets.add(new TaskInfoXWidget(getManagedForm(), ((AbstractTaskableArtifact) sma), state, comp, 2));
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
      return statePage + " for " + getSma();
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
      StringBuffer sb = new StringBuffer(statePage.getPageName());
      if (isEditable && !sma.isCompleted() && !sma.isCancelled()) {
         sb.append(" - Current State");
      }
      if (sma.isCancelled()) {
         if (statePage.isCancelledPage()) {
            sb.append(" - Cancelled");
            if (Strings.isValid(sma.getCancelledReason())) {
               sb.append(" - Reason: ");
               sb.append(Strings.isValid(sma.getCancelledReason()));
            }
         }
      }
      if (isCurrentState) {
         if (sma.isCompleted()) {
            sb.append(" - ");
            sb.append(DateUtil.getMMDDYYHHMM(sma.getCompletedDate()));
            LogItem item = sma.getStateStartedData(statePage);
            sb.append(" by ");
            sb.append(item.getUser().getName());
         } else if (sma.isCancelled()) {
            sb.append(" - ");
            sb.append(DateUtil.getMMDDYYHHMM(sma.internalGetCancelledDate()));
            LogItem item = sma.getStateStartedData(statePage);
            sb.append(" by ");
            sb.append(item.getUser().getName());
         }
         if (sma.getStateMgr().getAssignees().size() > 0) {
            sb.append(" assigned to ");
            sb.append(sma.getStateMgr().getAssigneesStr(80));
         }
      } else {
         LogItem item = sma.getStateCompletedData(statePage);
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
      statePage.dispose();
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
            for (IAtsStateItem item : AtsStateItemManager.getStateItems(statePage.getStateDefinition())) {
               try {
                  item.widgetModified(fSection, xWidget);
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
            updateTransitionToState();
            updateTransitionToAssignees();
            editor.onDirtied();
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
            StateDefinition toWorkPage = (StateDefinition) transitionToStateCombo.getSelected();
            if (toWorkPage == null) {
               transitionAssigneesLabel.setText("");
            } else {
               transitionAssigneesLabel.setText(sma.getTransitionAssigneesStr());
            }
            transitionAssigneesLabel.getParent().layout();
         }
         editor.onDirtied();
         for (XWidget xWidget : allXWidgets) {
            xWidget.refresh();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void handleChangeTransitionAssignees() throws OseeCoreException {
      StateDefinition toWorkPage = (StateDefinition) transitionToStateCombo.getSelected();
      if (toWorkPage == null) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "No Transition State Selected");
         return;
      }
      if (toWorkPage.isCancelledPage() || toWorkPage.isCompletedPage()) {
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
      editor.onDirtied();
   }

   private void createCurrentPageTransitionLine(Composite parent, StateXWidgetPage statePage, XFormToolkit toolkit) throws OseeCoreException {
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
      List<Object> allPages = new ArrayList<Object>();
      for (StateDefinition nextState : sma.getToStates()) {
         if (!allPages.contains(nextState)) {
            allPages.add(nextState);
         }
      }
      transitionToStateCombo.setInput(allPages);
      transitionToStateCombo.setLabelProvider(new StateDefinitionLabelProvider());
      transitionToStateCombo.setContentProvider(new ArrayContentProvider());
      transitionToStateCombo.setSorter(new StateDefinitionViewSorter());

      transitionToStateCombo.createWidgets(comp, 1);

      // Set default page from workflow default
      ArrayList<Object> defaultPage = new ArrayList<Object>();
      if (statePage.getDefaultToPage() != null) {
         defaultPage.add(statePage.getDefaultToPage());
         transitionToStateCombo.setSelected(defaultPage);
      }
      if (statePage.isCancelledPage() && Strings.isValid(sma.getCancelledFromState())) {
         defaultPage.add(sma.getStateDefinitionByName(sma.getCancelledFromState()));
         transitionToStateCombo.setSelected(defaultPage);
      }
      if (statePage.isCompletedPage() && Strings.isValid(sma.getCompletedFromState())) {
         defaultPage.add(sma.getStateDefinitionByName(sma.getCompletedFromState()));
         transitionToStateCombo.setSelected(defaultPage);
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
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(statePage.getStateDefinition())) {
         assignees = item.getOverrideTransitionToAssignees(this);
         if (assignees != null) {
            break;
         }
      }
      // If override set and isn't the same as already selected, update
      if (assignees != null && !sma.getTransitionAssignees().equals(assignees)) {
         sma.setTransitionAssignees(assignees);
         editor.onDirtied();
      }
      refresh();
   }

   public void updateTransitionToState() throws OseeCoreException {
      // Determine if there is a transitionToStateOverride for this page
      String transitionStateOverride = null;
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(statePage.getStateDefinition())) {
         transitionStateOverride = item.getOverrideTransitionToStateName(this);
         if (transitionStateOverride != null) {
            break;
         }
      }
      if (transitionStateOverride != null) {
         // Return if override state is same as selected
         if (((StateDefinition) transitionToStateCombo.getSelected()).getName().equals(transitionStateOverride)) {
            return;
         }
         // Find page corresponding to override state name
         for (StateDefinition toState : sma.getToStates()) {
            if (toState.getPageName().equals(transitionStateOverride)) {
               // Reset selection
               ArrayList<Object> defaultPage = new ArrayList<Object>();
               defaultPage.add(toState);
               transitionToStateCombo.setSelected(defaultPage);
               return;
            }
         }
      }
   }

   public void setTransitionToStateSelection(String stateName) {
      ArrayList<Object> allPages = new ArrayList<Object>();
      for (StateDefinition nextState : sma.getToStates()) {
         if (nextState.getPageName().equals(stateName)) {
            allPages.add(nextState);
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
         editor.doSave(null);

         // Get transition to state
         StateDefinition toStateDefinition = (StateDefinition) transitionToStateCombo.getSelected();

         if (toStateDefinition == null) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "No Transition State Selected");
            return;
         }
         if (toStateDefinition.isCancelledPage()) {
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
         if (toStateDefinition.isCancelledPage() || toStateDefinition.isCompletedPage()) {
            toAssignees = new HashSet<User>();
         } else {
            toAssignees = sma.getTransitionAssignees();
            if (toAssignees.isEmpty()) {
               toAssignees.add(UserManager.getUser());
            }
         }

         // If this is a return transition, don't require page/tasks to be complete
         if (!sma.isReturnPage(toStateDefinition) && !isStateTransitionable(toStateDefinition, toAssignees)) {
            return;
         }

         // Persist must be done prior and separate from transition
         sma.persist();

         // Perform transition separate from persist of previous changes to state machine artifact
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Transition");
         TransitionManager transitionMgr = new TransitionManager(sma);
         Result result =
            transitionMgr.transition(toStateDefinition, toAssignees, transaction, TransitionOption.Persist);
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

   private boolean isStateTransitionable(StateDefinition toStateDefinition, Collection<User> toAssignees) throws OseeCoreException {
      // Validate XWidgets for transition
      Result result = statePage.isPageComplete();
      if (result.isFalse()) {
         result.popup();
         return false;
      }

      // Loop through this state's tasks to confirm complete
      if (sma instanceof AbstractTaskableArtifact && !sma.isCompletedOrCancelled()) {
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
         sma.getStateDefinition().hasRule(AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name());

      // Only check this if TeamWorkflow, not for reviews
      if (sma instanceof TeamWorkFlowArtifact && (teamDefRequiresTargetedVersion || pageRequiresTargetedVersion) && //
      sma.getTargetedVersion() == null && //
      !toStateDefinition.isCancelledPage()) {
         AWorkbench.popup("Transition Blocked",
            "Actions must be targeted for a Version.\nPlease set \"Target Version\" before transition.");
         return false;
      }

      // Loop through this state's blocking reviews to confirm complete
      if (sma.isTeamWorkflow()) {
         for (AbstractReviewArtifact reviewArt : ReviewManager.getReviewsFromCurrentState((TeamWorkFlowArtifact) sma)) {
            if (reviewArt.getReviewBlockType() == ReviewBlockType.Transition && !reviewArt.isCompletedOrCancelled()) {
               AWorkbench.popup("Transition Blocked", "All Blocking Reviews must be completed before transition.");
               return false;
            }
         }
      }

      // Check extension points for valid transition
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(statePage.getStateDefinition())) {
         try {
            result = item.transitioning(sma, sma.getStateMgr().getCurrentState(), toStateDefinition, toAssignees);
            if (result.isFalse()) {
               result.popup();
               return false;
            }
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Exception occurred during transition; Aborting.", ex);
            return false;
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
      TransitionManager transitionMgr = new TransitionManager(sma);
      Result result =
         transitionMgr.transitionToCancelled(cancelDialog.getEntry(), transaction, TransitionOption.Persist);
      transaction.execute();
      if (result.isFalse()) {
         result.popup();
         return;
      }
      sma.setInTransition(false);
      editor.refreshPages();
   }

   private boolean isWorkingBranchTransitionable() throws OseeCoreException {
      if (sma.isTeamWorkflow() && ((TeamWorkFlowArtifact) sma).getBranchMgr().isWorkingBranchInWork()) {

         if (((StateDefinition) transitionToStateCombo.getSelected()).getPageName().equals(
            TeamState.Cancelled.getPageName())) {
            AWorkbench.popup("Transition Blocked",
               "Working Branch exists.\n\nPlease delete working branch before transition to cancel.");
            return false;
         }
         if (((TeamWorkFlowArtifact) sma).getBranchMgr().isBranchInCommit()) {
            AWorkbench.popup("Transition Blocked",
               "Working Branch is being Committed.\n\nPlease wait till commit completes to transition.");
            return false;
         }
         if (!statePage.isAllowTransitionWithWorkingBranch()) {
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
      // Don't log metrics for completed / cancelled states
      if (statePage.isCompletedOrCancelledPage()) {
         return true;
      }

      // Page has the ability to override the autofill of the metrics
      if (!statePage.isRequireStateHoursSpentPrompt() && sma.getStateMgr().getHoursSpent() == 0) {
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
      Date createDate = sma.getStateStartedData(statePage).getDate();
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

   public StateXWidgetPage getPage() {
      return statePage;
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

   public static boolean isEditable(AbstractWorkflowArtifact sma, StateXWidgetPage page, SMAEditor editor) throws OseeCoreException {
      // must be writeable
      return !sma.isReadOnly() &&
      // and access control writeable
      sma.isAccessControlWrite() &&
      // and current state
      (page == null || sma.isInState(page)) &&
      // and one of these
      //
      // page is define to allow anyone to edit
      (sma.getStateDefinition().hasRule(AtsWorkDefinitions.RuleWorkItemId.atsAllowEditToAll.name()) ||
      // team definition has allowed anyone to edit
      sma.teamDefHasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsAllowEditToAll.name()) ||
      // priviledged edit mode is on
      editor.isPriviledgedEditModeEnabled() ||
      // current user is assigned
      sma.isAssigneeMe() ||
      // current user is ats admin
      AtsUtil.isAtsAdmin());
   }
}
