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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.service.ServicesArea;
import org.eclipse.osee.ats.task.TaskComposite;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.widgets.dialog.SMAStatusDialog;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
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
   private static String ASSIGNEES = "Assignee(s):";
   private Label currentAssigneesLabel;
   private Label transitionAssigneesLabel;
   protected final SMAManager smaMgr;
   private final AtsWorkPage atsWorkPage;
   private final boolean isEditable, isCurrentState, isGlobalEditable;
   private ServicesArea servicesArea;
   private final XFormToolkit toolkit;
   private TaskComposite xTask;
   public static String TRANSITION_TO_STATE_COMBO = "Transition To State Combo";
   private Composite mainComp;
   private DynamicXWidgetLayout dynamicXWidgetLayout;

   public SMAWorkFlowSection(Composite parent, XFormToolkit toolkit, int style, AtsWorkPage page, SMAManager smaMgr) throws OseeCoreException {
      super(parent, toolkit, style);
      this.toolkit = toolkit;
      this.atsWorkPage = page;
      this.smaMgr = smaMgr;
      isEditable =
            !smaMgr.getSma().isReadOnly() && smaMgr.isAccessControlWrite() && smaMgr.isCurrentState(page.getName()) && (smaMgr.getWorkPageDefinition().hasWorkRule(
                  AtsWorkDefinitions.RuleWorkItemId.atsAllowEditToAll.name()) || smaMgr.teamDefHasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsAllowEditToAll.name()) || smaMgr.getEditor().getPriviledgedEditMode() != SMAEditor.PriviledgedEditMode.Off || smaMgr.isAssigneeMe() || AtsPlugin.isAtsAdmin());
      isGlobalEditable =
            !smaMgr.getSma().isReadOnly() && smaMgr.isAccessControlWrite() && smaMgr.getEditor().getPriviledgedEditMode() == SMAEditor.PriviledgedEditMode.Global;
      isCurrentState = smaMgr.isCurrentState(page.getName());
      // parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
      createPage(parent);
   }

   protected Section createPage(Composite parent) throws OseeCoreException {

      Section section = toolkit.createSection(parent, Section.TWISTIE | Section.TITLE_BAR);
      section.setText(getCurrentStateTitle());
      if (smaMgr.isCurrentState(atsWorkPage.getName())) section.setBackground(AtsPlugin.ACTIVE_COLOR);
      section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      // section.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA));

      mainComp = toolkit.createClientContainer(section, 2);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      // mainComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));
      mainComp.layout();

      SMAWorkFlowTab.createStateNotesHeader(mainComp, toolkit, smaMgr, 2, atsWorkPage.getName());

      Composite rightComp = toolkit.createContainer(mainComp, 1);
      rightComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
      // rightComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));

      Composite workComp = createWorkArea(mainComp, atsWorkPage, toolkit);

      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
      gridData.widthHint = 400;
      workComp.setLayoutData(gridData);

      servicesArea = new ServicesArea(smaMgr);
      servicesArea.createSidebarServices(rightComp, atsWorkPage, toolkit, this);

      section.layout();
      section.setExpanded(smaMgr.isCurrentSectionExpanded(atsWorkPage.getName()));

      return section;
   }

   protected Composite createWorkArea(Composite comp, AtsWorkPage atsWorkPage, XFormToolkit toolkit) throws OseeCoreException {

      Composite workComp = toolkit.createContainer(comp, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      // workComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));

      if (isEditable) createCurrentPageHeader(workComp, atsWorkPage, toolkit);

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
            smaMgr.getWorkFlowDefinition(), atsWorkPage.getWorkPageDefinition(), smaMgr)) {
         if (workItemDefinition instanceof WorkWidgetDefinition) {
            DynamicXWidgetLayoutData data = ((WorkWidgetDefinition) workItemDefinition).get();
            data.setDynamicXWidgetLayout(atsWorkPage.getDynamicXWidgetLayout());
            dynamicDatas.add(data);
         }
      }
      atsWorkPage.addLayoutDatas(dynamicDatas);

      atsWorkPage.setSmaMgr(smaMgr);

      // Create main body
      dynamicXWidgetLayout =
            atsWorkPage.createBody(toolkit, workComp, smaMgr.getSma(), xModListener, isEditable || isGlobalEditable);

      // Check extenstion points for page creation
      for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(atsWorkPage.getId())) {
         Result result = item.pageCreated(toolkit, atsWorkPage, smaMgr, xModListener, isEditable || isGlobalEditable);
         if (result.isFalse()) {
            result.popup();
            OSEELog.logSevere(AtsPlugin.class, "Error in page creation => " + result.getText(), true);
         }
      }

      if (isEditable) createCurrentPageTransitionLine(workComp, atsWorkPage, toolkit);

      return workComp;
   }

   public Result isXWidgetSavable() throws OseeCoreException {
      if (dynamicXWidgetLayout == null) return Result.TrueResult;
      for (XWidget widget : dynamicXWidgetLayout.getXWidgets()) {
         if (widget instanceof IArtifactWidget) {
            Result result = widget.isValid();
            if (result.isFalse()) return result;
         }
      }
      return Result.TrueResult;
   }

   @Override
   public String toString() {
      return atsWorkPage + " for " + getSmaMgr().getSma();
   }

   public Result isXWidgetDirty() throws OseeCoreException {
      if (dynamicXWidgetLayout == null) return Result.FalseResult;
      for (XWidget widget : dynamicXWidgetLayout.getXWidgets()) {
         if (widget instanceof IArtifactWidget) {
            Result result = ((IArtifactWidget) widget).isDirty();
            if (result.isTrue()) return result;
         }
      }
      return Result.FalseResult;
   }

   public void saveXWidgetToArtifact() throws OseeCoreException {
      for (XWidget widget : dynamicXWidgetLayout.getXWidgets()) {
         if (widget instanceof IArtifactWidget) ((IArtifactWidget) widget).saveToArtifact();
      }
   }

   private String getCurrentStateTitle() throws OseeCoreException {
      StringBuffer sb = new StringBuffer(atsWorkPage.getName());
      if (isEditable && (!smaMgr.isCompleted() && !smaMgr.isCancelled())) {
         sb.append(" - Current State");
      }
      if (smaMgr.isCancelled()) {
         LogItem item = smaMgr.getLog().getStateEvent(LogType.StateCancelled);
         if (item == null) {
            throw new OseeStateException(
                  "ats.Log: Cancelled state has no logItem for " + smaMgr.getSma().getHumanReadableId());
         }
         if (item.getState().equals(atsWorkPage.getName())) {
            sb.append(" - Cancelled");
            if (!item.getMsg().equals("")) sb.append(" - Reason: " + item.getMsg());
         }
      }
      if (isCurrentState) {
         if (smaMgr.isCompleted()) {
            sb.append(" - ");
            sb.append(smaMgr.getSma().getWorldViewCompletedDateStr());
            LogItem item = smaMgr.getLog().getStateEvent(LogType.StateEntered, atsWorkPage.getName());
            sb.append(" by " + item.getUser().getName());
         } else if (smaMgr.isCancelled()) {
            sb.append(" - ");
            sb.append(smaMgr.getSma().getWorldViewCancelledDateStr());
            LogItem item = smaMgr.getLog().getStateEvent(LogType.StateEntered, atsWorkPage.getName());
            sb.append(" by " + item.getUser().getName());
         }
         if (smaMgr.getStateMgr().getAssignees().size() > 0) {
            sb.append(" assigned to ");
            sb.append(Artifacts.toString("; ", smaMgr.getStateMgr().getAssignees()));
         }
      } else {
         LogItem item = smaMgr.getLog().getStateEvent(LogType.StateComplete, atsWorkPage.getName());
         if (item != null) {
            sb.append(" - State Completed " + item.getDate(XDate.MMDDYYHHMM));
            sb.append(" by " + item.getUser().getName());
         }
      }
      return sb.toString();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.AbstractFormPart#dispose()
    */
   @Override
   public void dispose() {
      super.dispose();
      atsWorkPage.dispose();
      servicesArea.dispose();
   }

   final SMAWorkFlowSection fSection = this;
   final XModifiedListener xModListener = new XModifiedListener() {
      public void widgetModified(XWidget xWidget) {
         if (smaMgr.getSma().isDeleted()) return;
         // Notify extensions of widget modified
         try {
            for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(atsWorkPage.getId())) {
               try {
                  item.widgetModified(fSection, xWidget);
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
            updateTransitionToState();
            updateTransitionToAssignees();
            smaMgr.getEditor().onDirtied();
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   };

   @Override
   public void refresh() {
      super.refresh();
      if (isEditable) {
         try {
            if (currentAssigneesLabel != null && !currentAssigneesLabel.isDisposed()) {
               currentAssigneesLabel.setText(Artifacts.toString("; ", smaMgr.getStateMgr().getAssignees()));
               currentAssigneesLabel.getParent().layout();
            }
            if (transitionAssigneesLabel != null && !transitionAssigneesLabel.isDisposed()) {
               WorkPageDefinition toWorkPage = (WorkPageDefinition) transitionToStateCombo.getSelected();
               if (toWorkPage == null)
                  transitionAssigneesLabel.setText("");
               else
                  transitionAssigneesLabel.setText(smaMgr.getTransitionAssigneesStr());
               transitionAssigneesLabel.getParent().layout();
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex.toString(), ex);
         }
      }
      refreshStateServices();
   }

   private void createCurrentPageHeader(Composite parent, AtsWorkPage page, XFormToolkit toolkit) throws OseeCoreException {
      Composite comp = toolkit.createContainer(parent, 3);
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      if (isEditable && !smaMgr.isCancelled() && !smaMgr.isCompleted()) {
         toolkit.createLabel(comp, "\"" + page.getName() + "\" State  ");
         Hyperlink setAssigneesHyperlinkLabel = toolkit.createHyperlink(comp, ASSIGNEES, SWT.NONE);
         setAssigneesHyperlinkLabel.addHyperlinkListener(new IHyperlinkListener() {

            public void linkEntered(HyperlinkEvent e) {
            }

            public void linkExited(HyperlinkEvent e) {
            }

            public void linkActivated(HyperlinkEvent e) {
               try {
                  handleChangeCurrentAssignees();
               } catch (Exception ex) {
                  OSEELog.logException(AtsPlugin.class, ex, true);
               }
            }

         });
         currentAssigneesLabel =
               toolkit.createLabel(comp, Artifacts.toString("; ", smaMgr.getStateMgr().getAssignees()));
         currentAssigneesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         if (smaMgr.getStateMgr().getAssignees().size() == 0) {
            Label errorLabel = toolkit.createLabel(comp, "Error: State has no assignees");
            errorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         }
      } else if (smaMgr.getStateMgr().getAssignees().size() > 0) {
         Label errorLabel =
               toolkit.createLabel(comp,
                     "Error: Non-current/Cancelled/Completed state still assigned to " + Artifacts.toString("; ",
                           smaMgr.getStateMgr().getAssignees()));
         errorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      }
   }

   private void handleChangeCurrentAssignees() throws OseeCoreException {
      if (smaMgr.promptChangeAssignees()) {
         refresh();
         smaMgr.getEditor().onDirtied();
      }
   }

   private void handleChangeTransitionAssignees() throws OseeCoreException {
      WorkPageDefinition toWorkPage = (WorkPageDefinition) transitionToStateCombo.getSelected();
      if (toWorkPage == null) {
         OSEELog.logException(AtsPlugin.class, "No Transition State Selected", null, true);
         return;
      }
      if (toWorkPage.isCancelledPage() || toWorkPage.isCompletePage()) {
         AWorkbench.popup("ERROR", "No Assignees in Completed and Cancelled states");
         return;
      }
      UserCheckTreeDialog uld = new UserCheckTreeDialog(Display.getCurrent().getActiveShell());
      uld.setMessage("Select users to transition to.");
      uld.setInitialSelections(smaMgr.getTransitionAssignees());
      if (uld.open() != 0) return;
      Collection<User> users = uld.getUsersSelected();
      if (users.size() == 0) {
         AWorkbench.popup("ERROR", "Must have at least one assignee");
         return;
      }
      smaMgr.setTransitionAssignees(users);
      refresh();
      smaMgr.getEditor().onDirtied();
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

      transitionToStateCombo = new XComboViewer(TRANSITION_TO_STATE_COMBO);
      transitionToStateCombo.setDisplayLabel(false);
      ArrayList<Object> allPages = new ArrayList<Object>();
      for (WorkPageDefinition nextPage : smaMgr.getToWorkPages()) {
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
         LogItem item = smaMgr.getLog().getStateEvent(LogType.StateCancelled);
         if (item != null) {
            defaultPage.add(smaMgr.getWorkPageDefinitionByName(item.getState()));
            transitionToStateCombo.setSelected(defaultPage);
         }
      }
      // Update transition based on state items
      updateTransitionToState();

      transitionToStateCombo.getCombo().setVisibleItemCount(20);
      transitionToStateCombo.addSelectionChangedListener(new ISelectionChangedListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
          */
         public void selectionChanged(SelectionChangedEvent event) {
            try {
               updateTransitionToAssignees();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      });

      Hyperlink assigneesLabelLink = toolkit.createHyperlink(comp, ASSIGNEES, SWT.NONE);
      assigneesLabelLink.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            try {
               handleChangeTransitionAssignees();
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }

      });
      transitionAssigneesLabel = toolkit.createLabel(comp, smaMgr.getTransitionAssigneesStr());
      transitionAssigneesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

   }

   public void updateTransitionToAssignees() throws OseeCoreException {
      Collection<User> assignees = null;
      // Determine if the is an override set of assigness
      for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(atsWorkPage.getId())) {
         assignees = item.getOverrideTransitionToAssignees(this);
         if (assignees != null) break;
      }
      // If override set and isn't the same as already selected, update
      if (assignees != null && !smaMgr.getTransitionAssignees().equals(assignees)) {
         smaMgr.setTransitionAssignees(assignees);
         smaMgr.getEditor().onDirtied();
      }
      refresh();
   }

   public void updateTransitionToState() throws OseeCoreException {
      // Determine if there is a transitionToStateOverride for this page
      String transitionStateOverride = null;
      for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(atsWorkPage.getId())) {
         transitionStateOverride = item.getOverrideTransitionToStateName(this);
         if (transitionStateOverride != null) break;
      }
      if (transitionStateOverride != null) {
         // Return if override state is same as selected
         if (((WorkPageDefinition) transitionToStateCombo.getSelected()).getName().equals(transitionStateOverride)) return;
         // Find page corresponding to override state name
         for (WorkPageDefinition toWorkPageDefinition : smaMgr.getToWorkPages()) {
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
      for (WorkPageDefinition nextPage : smaMgr.getToWorkPages()) {
         if (nextPage.getPageName().equals(stateName)) allPages.add(nextPage);
      }
      transitionToStateCombo.setSelected(allPages);
   }

   private void handleTransition() {

      try {

         if (smaMgr.getBranchMgr().isWorkingBranch() && !atsWorkPage.isAllowTransitionWithWorkingBranch()) {
            AWorkbench.popup("ERROR",
                  "Working Branch exists.\n\nPlease commit or delete working branch before transition.");
            return;
         }

         smaMgr.setInTransition(true);
         smaMgr.getEditor().doSave(null);

         // Get transition to state
         WorkPageDefinition toWorkPageDefinition = (WorkPageDefinition) transitionToStateCombo.getSelected();

         if (toWorkPageDefinition == null) {
            OSEELog.logException(AtsPlugin.class, "No Transition State Selected", null, true);
            return;
         }
         if (toWorkPageDefinition.getPageName().equals(DefaultTeamState.Cancelled.name())) {
            EntryDialog cancelDialog = new EntryDialog("Cancellation Reason", "Enter cancellation reason.");
            if (cancelDialog.open() != 0) return;
            SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
            Result result = smaMgr.transitionToCancelled(cancelDialog.getEntry(), true, transaction);
            transaction.execute();
            if (result.isFalse()) {
               result.popup();
               return;
            }
            smaMgr.setInTransition(false);
            smaMgr.getEditor().refreshPages();
            return;
         }

         // Validate assignees
         if (smaMgr.getStateMgr().getAssignees().contains(UserManager.getUser(SystemUser.NoOne)) || smaMgr.getStateMgr().getAssignees().contains(
               UserManager.getUser(SystemUser.Guest)) || smaMgr.getStateMgr().getAssignees().contains(
               UserManager.getUser(SystemUser.UnAssigned))) {
            AWorkbench.popup("ERROR",
                  "Can not transition with \"Guest\", \"UnAssigned\" or \"NoOne\" user as assignee.");
            return;
         }

         // Get transition to assignees
         Collection<User> toAssignees;
         if (toWorkPageDefinition.isCancelledPage() || toWorkPageDefinition.isCompletePage())
            toAssignees = new HashSet<User>();
         else
            toAssignees = smaMgr.getTransitionAssignees();

         // If this is a return transition, don't require page/tasks to be complete
         if (!smaMgr.isReturnPage(toWorkPageDefinition)) {

            // Validate XWidgets for transition
            Result result = atsWorkPage.isPageComplete();
            if (result.isFalse()) {
               result.popup();
               return;
            }

            // Loop through this state's tasks to confirm complete
            if (smaMgr.isTaskable()) {
               for (TaskArtifact taskArt : smaMgr.getTaskMgr().getTaskArtifactsFromCurrentState()) {
                  if (taskArt.isInWork()) {
                     AWorkbench.popup(
                           "Error",
                           "Task Not Complete\n\nTitle: " + taskArt.getDescriptiveName() + "\n\nHRID: " + taskArt.getHumanReadableId());
                     return;
                  }
               }
            }

            // Don't transition without targeted version if so configured
            if (smaMgr.teamDefHasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name()) || smaMgr.getWorkPageDefinition().hasWorkRule(
                  AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name())) {
               if (smaMgr.getSma().getWorldViewTargetedVersion() == null && !toWorkPageDefinition.isCancelledPage()) {
                  AWorkbench.popup("Error",
                        "Actions must be targeted for a Version.\nPlease set \"Target Version\" before transition.");
                  return;
               }
            }

            // Loop through this state's blocking reviews to confirm complete
            for (ReviewSMArtifact reviewArt : smaMgr.getReviewManager().getReviewsFromCurrentState()) {
               if (reviewArt.getReviewBlockType() == ReviewBlockType.Transition && !reviewArt.getSmaMgr().isCancelledOrCompleted()) {
                  AWorkbench.popup(
                        "Error",
                        "Blocking Review must be completed before transition.\n\nReview Title: \"" + reviewArt.getDescriptiveName() + "\"\nHRID: " + reviewArt.getHumanReadableId());
                  return;
               }
            }

            // Check extension points for valid transition
            for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(atsWorkPage.getId())) {
               try {
                  result =
                        item.transitioning(smaMgr, smaMgr.getStateMgr().getCurrentStateName(),
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
            if (!handlePopulateStateMetrics()) return;
         }

         // Persist must be done prior and separate from transition 
         smaMgr.getSma().persistAttributesAndRelations();

         // Perform transition separate from persist of previous changes to state machine artifact
         SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         Result result = smaMgr.transition(toWorkPageDefinition.getPageName(), toAssignees, true, false, transaction);
         transaction.execute();
         if (result.isFalse()) {
            result.popup();
            return;
         }
         smaMgr.setInTransition(false);
         smaMgr.getEditor().refreshPages();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      } finally {
         smaMgr.setInTransition(false);
      }
   }

   public void refreshStateServices() {
      if (servicesArea != null) servicesArea.refresh();
      smaMgr.getEditor().onDirtied();
   }

   /**
    * @return Returns the isCurrentState.
    */
   public boolean isCurrentState() {
      return isCurrentState;
   }

   public boolean handlePopulateStateMetrics() throws OseeCoreException {

      // Page has the ability to override the autofill of the metrics
      if (!atsWorkPage.isRequireStateHoursSpentPrompt() && smaMgr.getStateMgr().getHoursSpent() == 0) {
         // First, try to autofill if it's only been < 5 min since creation
         int minSinceCreation = getCreationToNowDateDeltaMinutes();
         // System.out.println("minSinceCreation *" + minSinceCreation + "*");
         float hoursSinceCreation = minSinceCreation / 60;
         if (hoursSinceCreation < 0.02) hoursSinceCreation = (new Float(0.02)).floatValue();
         // System.out.println("hoursSinceCreation *" + hoursSinceCreation + "*");
         if (minSinceCreation < 5) {
            smaMgr.getStateMgr().updateMetrics(hoursSinceCreation, 100, true);
            return true;
         }
      }

      // Otherwise, open dialog to ask for hours complete
      String msg =
            smaMgr.getStateMgr().getCurrentStateName() + " State\n\n" + AtsLib.doubleToStrString(smaMgr.getStateMgr().getHoursSpent()) + " hours already spent on this state.\n" + "Enter the additional number of hours you spent on this state.";
      SMAStatusDialog tsd =
            new SMAStatusDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Enter Hours Spent",
                  msg, false, Arrays.asList(smaMgr.getSma()));
      int result = tsd.open();
      if (result == 0) {
         smaMgr.getStateMgr().updateMetrics(tsd.getHours().getFloat(), 100, true);
         return true;
      }
      return false;
   }

   public int getCreationToNowDateDeltaMinutes() throws OseeCoreException {
      Date createDate = smaMgr.getLog().getStateEvent(LogType.StateEntered, atsWorkPage.getName()).getDate();
      long createDateLong = createDate.getTime();
      Date date = new Date();
      float diff = (date.getTime() - createDateLong);
      // System.out.println("diff *" + diff + "*");
      Float min = diff / 60000;
      // System.out.println("min *" + min + "*");
      return min.intValue();
   }

   /**
    * @return Returns the xTask.
    */
   public TaskComposite getXTask() {
      return xTask;
   }

   /**
    * @return the transitionToStateCombo
    */
   public XComboViewer getTransitionToStateCombo() {
      return transitionToStateCombo;
   }

   /**
    * @return the smaMgr
    */
   public SMAManager getSmaMgr() {
      return smaMgr;
   }

   /**
    * @return the page
    */
   public AtsWorkPage getPage() {
      return atsWorkPage;
   }

   /**
    * @return the mainComp
    */
   public Composite getMainComp() {
      return mainComp;
   }

}
