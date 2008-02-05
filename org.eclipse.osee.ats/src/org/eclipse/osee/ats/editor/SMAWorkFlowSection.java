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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.NoteItem;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.editor.service.ServicesArea;
import org.eclipse.osee.ats.util.widgets.dialog.SMAStatusDialog;
import org.eclipse.osee.ats.util.widgets.task.XTaskViewer;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserCheckTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageViewSorter;
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
   private final AtsWorkPage page;
   private final boolean isEditable, isCurrentState, isGlobalEditable;
   private ServicesArea servicesArea;
   private final XFormToolkit toolkit;
   private XTaskViewer xTask;
   public static String TRANSITION_TO_STATE_COMBO = "Transition To State Combo";
   private Composite mainComp;

   public SMAWorkFlowSection(Composite parent, XFormToolkit toolkit, int style, AtsWorkPage page, SMAManager smaMgr) {
      super(parent, toolkit, style);
      this.toolkit = toolkit;
      this.page = page;
      this.smaMgr = smaMgr;
      isEditable =
            !smaMgr.getSma().isReadOnly() && smaMgr.isAccessControlWrite() && smaMgr.isCurrentState(page) && (smaMgr.getEditor().getPriviledgedEditMode() != SMAEditor.PriviledgedEditMode.Off || smaMgr.isAssigneeMe() || AtsPlugin.isAtsAdmin());
      isGlobalEditable =
            !smaMgr.getSma().isReadOnly() && smaMgr.isAccessControlWrite() && smaMgr.getEditor().getPriviledgedEditMode() == SMAEditor.PriviledgedEditMode.Global;
      isCurrentState = smaMgr.isCurrentState(page);
      // parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
      createPage(parent);
   }

   protected Section createPage(Composite comp) {

      Section section = toolkit.createSection(comp, Section.TWISTIE | Section.TITLE_BAR);
      section.setText(getCurrentStateTitle());
      if (smaMgr.isCurrentState(page)) section.setBackground(AtsPlugin.ACTIVE_COLOR);
      section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      // section.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA));

      mainComp = toolkit.createClientContainer(section, 2);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      // mainComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));
      mainComp.layout();

      createStateNotesHeader(smaMgr, mainComp, toolkit, 2);

      Composite rightComp = toolkit.createContainer(mainComp, 1);
      rightComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
      // rightComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));

      Composite workComp = createWorkArea(mainComp, page, toolkit);

      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
      gridData.widthHint = 400;
      workComp.setLayoutData(gridData);

      servicesArea = new ServicesArea(smaMgr);
      servicesArea.createSidebarServices(rightComp, page, toolkit, this);

      section.layout();
      section.setExpanded(smaMgr.isCurrentSectionExpanded(page));
      return section;
   }

   public static void createStateNotesHeader(SMAManager smaMgr, Composite comp, XFormToolkit toolkit, int horizontalSpan) {
      // Display global Notes
      for (NoteItem noteItem : smaMgr.getSma().getNotes().getNoteItems()) {
         if (noteItem.getState().equals(smaMgr.getCurrentStateName())) {
            Label label = toolkit.createLabel(comp, noteItem.toHTML());
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = horizontalSpan;
            label.setLayoutData(gd);
         }
      }
   }

   private String getCurrentStateTitle() {
      StringBuffer sb = new StringBuffer(page.getName());
      if (isEditable && (!smaMgr.isCompleted() && !smaMgr.isCancelled())) {
         sb.append(" - Current State");
      }
      if (smaMgr.isCancelled()) {
         LogItem item = smaMgr.getSma().getLog().getStateEvent(LogType.StateCancelled);
         if (item.getState().equals(page.getName())) {
            sb.append(" - Cancelled");
            if (!item.getMsg().equals("")) sb.append(" - Reason: " + item.getMsg());
         }
      }
      if (isCurrentState) {
         if (smaMgr.isCompleted()) {
            sb.append(" - ");
            sb.append(smaMgr.getSma().getWorldViewCompletedDateStr());
            LogItem item = smaMgr.getSma().getLog().getStateEvent(LogType.StateEntered, page.getName());
            sb.append(" by " + item.getUser().getName());
         } else if (smaMgr.isCancelled()) {
            sb.append(" - ");
            sb.append(smaMgr.getSma().getWorldViewCancelledDateStr());
            LogItem item = smaMgr.getSma().getLog().getStateEvent(LogType.StateEntered, page.getName());
            sb.append(" by " + item.getUser().getName());
         }
         if (smaMgr.getAssignees().size() > 0) {
            sb.append(" assigned to ");
            sb.append(smaMgr.getAssigneesStr());
         }
      } else {
         LogItem item = smaMgr.getSma().getLog().getStateEvent(LogType.StateComplete, page.getName());
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
      page.dispose();
      servicesArea.dispose();
   }

   protected Composite createWorkArea(Composite comp, AtsWorkPage page, XFormToolkit toolkit) {

      Composite workComp = toolkit.createContainer(comp, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      // workComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));

      if (isEditable) createCurrentPageHeader(workComp, page, toolkit);

      page.setSmaMgr(smaMgr);
      page.createBody(toolkit, workComp, smaMgr.getSma(), xModListener, isEditable || isGlobalEditable);

      // Check extenstion points for page creation
      for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(page.getId())) {
         Result result = item.pageCreated(toolkit, page, smaMgr, xModListener, isEditable || isGlobalEditable);
         if (result.isFalse()) {
            result.popup();
            OSEELog.logSevere(AtsPlugin.class, "Error in page creation => " + result.getText(), true);
         }
      }

      if (isEditable && !smaMgr.isCancelled() && !smaMgr.isCompleted()) createCurrentPageTransitionLine(workComp, page,
            toolkit);

      return workComp;
   }

   final SMAWorkFlowSection fSection = this;
   final XModifiedListener xModListener = new XModifiedListener() {
      public void widgetModified(XWidget xWidget) {
         if (smaMgr.getSma().isDeleted()) return;
         // Notify extensions of widget modified
         for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(page.getId())) {
            item.widgetModified(fSection, xWidget);
         }
         updateTransitionToState();
         updateTransitionToAssignees();
         smaMgr.getEditor().onDirtied();
      }
   };

   @Override
   public void refresh() {
      super.refresh();
      if (isEditable) {
         if (currentAssigneesLabel != null && !currentAssigneesLabel.isDisposed()) {
            currentAssigneesLabel.setText(smaMgr.getAssigneesStr());
            currentAssigneesLabel.getParent().layout();
         }
         if (transitionAssigneesLabel != null && !transitionAssigneesLabel.isDisposed()) {
            AtsWorkPage toWorkPage = (AtsWorkPage) transitionToStateCombo.getSelected();
            if (toWorkPage.isCancelledPage() || toWorkPage.isCompletePage())
               transitionAssigneesLabel.setText("");
            else
               transitionAssigneesLabel.setText(smaMgr.getTransitionAssigneesStr());
            transitionAssigneesLabel.getParent().layout();
         }
      }
      refreshStateServices();
   }

   private void createCurrentPageHeader(Composite parent, AtsWorkPage page, XFormToolkit toolkit) {
      Composite comp = toolkit.createContainer(parent, 3);
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      if (isEditable && !smaMgr.isCancelled() && !smaMgr.isCompleted()) {
         toolkit.createLabel(comp, "\"" + page.getName() + "\" state assigned to ");
         Hyperlink link = toolkit.createHyperlink(comp, ASSIGNEES, SWT.NONE);
         link.addHyperlinkListener(new IHyperlinkListener() {

            public void linkEntered(HyperlinkEvent e) {
            }

            public void linkExited(HyperlinkEvent e) {
            }

            public void linkActivated(HyperlinkEvent e) {
               handleChangeCurrentAssignees();
            }

         });
         currentAssigneesLabel = toolkit.createLabel(comp, smaMgr.getAssigneesStr());
         currentAssigneesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         if (smaMgr.getAssignees().size() == 0) {
            Label errorLabel = toolkit.createLabel(comp, "Error: State has no assignees");
            errorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         }
      } else if (smaMgr.getAssignees().size() > 0) {
         Label errorLabel =
               toolkit.createLabel(comp,
                     "Error: Non-current/Cancelled/Completed state still assigned to " + smaMgr.getAssigneesStr());
         errorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      }
   }

   private void handleChangeCurrentAssignees() {
      if (smaMgr.promptChangeAssignees()) {
         refresh();
         smaMgr.getEditor().onDirtied();
      }
   }

   private void handleChangeTransitionAssignees() {
      AtsWorkPage toWorkPage = (AtsWorkPage) transitionToStateCombo.getSelected();
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

   private void createCurrentPageTransitionLine(Composite parent, AtsWorkPage page, XFormToolkit toolkit) {
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
      for (AtsWorkPage nextPage : page.getToAtsPages()) {
         allPages.add(nextPage);
      }
      transitionToStateCombo.setInput(allPages);
      transitionToStateCombo.setLabelProvider(new WorkPageLabelProvider());
      transitionToStateCombo.setContentProvider(new ArrayContentProvider());
      transitionToStateCombo.setSorter(new WorkPageViewSorter());

      transitionToStateCombo.createWidgets(comp, 1);

      // Set default page from workflow default
      ArrayList<Object> defaultPage = new ArrayList<Object>();
      if (page.getDefaultToPage() != null) {
         defaultPage.add(page.getDefaultToPage());
         transitionToStateCombo.setSelected(defaultPage);
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
            updateTransitionToAssignees();
         }
      });

      Hyperlink assigneesLabelLink = toolkit.createHyperlink(comp, ASSIGNEES, SWT.NONE);
      assigneesLabelLink.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            handleChangeTransitionAssignees();
         }

      });
      transitionAssigneesLabel = toolkit.createLabel(comp, smaMgr.getTransitionAssigneesStr());
      transitionAssigneesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

   }

   public void updateTransitionToAssignees() {
      Collection<User> assignees = null;
      // Determine if the is an override set of assigness
      for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(page.getId())) {
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

   public void updateTransitionToState() {
      // Determine if there is a transitionToStateOverride for this page
      String transitionStateOverride = null;
      for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(page.getId())) {
         transitionStateOverride = item.getOverrideTransitionToStateName(this);
         if (transitionStateOverride != null) break;
      }
      if (transitionStateOverride != null) {
         // Return if override state is same as selected
         if (((AtsWorkPage) transitionToStateCombo.getSelected()).getName().equals(transitionStateOverride)) return;
         // Find page corresponding to override state name
         for (WorkPage toPage : page.getToAtsPages()) {
            if (toPage.getName().equals(transitionStateOverride)) {
               // Reset selection
               ArrayList<Object> defaultPage = new ArrayList<Object>();
               defaultPage.add(toPage);
               transitionToStateCombo.setSelected(defaultPage);
               return;
            }
         }
      }
   }

   public void setTransitionToStateSelection(String stateName) {
      ArrayList<Object> allPages = new ArrayList<Object>();
      for (AtsWorkPage nextPage : page.getToAtsPages()) {
         if (nextPage.getName().equals(stateName)) allPages.add(nextPage);
      }
      transitionToStateCombo.setSelected(allPages);
   }

   private void handleTransition() {
      // System.out.println("Transition to " + ((AtsWorkPage)
      // transitionToStateCombo.getSelected()).getName());

      try {
         // if (smaMgr.getBranchMgr().getBranchId() != 0) {
         // AWorkbench.popup("ERROR",
         // "Can't transition with working branch present.\n\n" +
         // "Delete branch first, then transition.");
         // return;
         // }
         smaMgr.setInTransition(true);

         smaMgr.getSma().persist(true);
         smaMgr.getEditor().onDirtied();

         // Get transition to state
         AtsWorkPage toWorkPage = (AtsWorkPage) transitionToStateCombo.getSelected();

         if (toWorkPage == null) {
            AWorkbench.popup("ERROR", "Can't retrieve transition to state from combo");
            return;
         }
         if (toWorkPage.isCancelledPage()) {
            EntryDialog cancelDialog = new EntryDialog("Cancellation Reason", "Enter cancellation reason.");
            if (cancelDialog.open() != 0) return;
            Result result = smaMgr.transitionToCancelled(cancelDialog.getEntry(), true);
            if (result.isFalse()) {
               result.popup();
               return;
            }
            smaMgr.setInTransition(false);
            smaMgr.getEditor().redrawPages();
            return;
         }

         // Get transition to assignees
         Set<User> toAssignees;
         if (toWorkPage.isCancelledPage() || toWorkPage.isCompletePage())
            toAssignees = new HashSet<User>();
         else
            toAssignees = smaMgr.getTransitionAssignees();

         // If this is a return transition, don't require page/tasks to be complete
         if (!smaMgr.getWorkPage().isReturnPage(toWorkPage)) {

            // Validate XWidgets for transition
            Result result = page.isPageComplete();
            if (result.isFalse()) {
               result.popup();
               return;
            }

            // Loop throught this state's tasks to confirm complete
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

            // Loop throught this state's blocking reviews to confirm complete
            for (ReviewSMArtifact reviewArt : smaMgr.getReviewManager().getReviewsFromCurrentState()) {
               SMAManager smaMgr = new SMAManager(reviewArt);
               if (reviewArt.isBlocking() && (!smaMgr.isCancelled() && !smaMgr.isCompleted())) {
                  AWorkbench.popup(
                        "Error",
                        "Blocking Review Not Complete\n\nTitle: " + reviewArt.getDescriptiveName() + "\n\nHRID: " + reviewArt.getHumanReadableId());
                  return;
               }
            }

            // Check extension points for valid transition
            for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(page.getId())) {
               try {
                  result = item.transitioning(smaMgr, smaMgr.getCurrentStateName(), toWorkPage.getName(), toAssignees);
                  if (result.isFalse()) {
                     result.popup();
                     return;
                  }
               } catch (Exception ex) {
                  OSEELog.logException(AtsPlugin.class, ex, false);
               }
            }

            // Ask for metrics for this page (store in state versus task?)
            if (!handlePopulateStateMetrics()) return;
         }

         try {
            smaMgr.getSma().persistAttributes();
         } catch (SQLException ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }

         Result result = smaMgr.transition(toWorkPage.getName(), toAssignees, true, false);
         if (result.isFalse()) {
            result.popup();
            return;
         }
         smaMgr.setInTransition(false);
         smaMgr.getEditor().redrawPages();
      } catch (SQLException ex) {
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

   public boolean handlePopulateStateMetrics() throws IllegalStateException, SQLException {

      // Page has the ability to override the autofill of the metrics
      if (!page.isRequireStateHoursSpentPrompt() && smaMgr.getSma().getCurrentState().getHoursSpent() == 0) {
         // First, try to autofill if it's only been < 5 min since creation
         int minSinceCreation = getCreationToNowDateDeltaMinutes();
         // System.out.println("minSinceCreation *" + minSinceCreation + "*");
         float hoursSinceCreation = minSinceCreation / 60;
         if (hoursSinceCreation < 0.02) hoursSinceCreation = (new Float(0.02)).floatValue();
         // System.out.println("hoursSinceCreation *" + hoursSinceCreation + "*");
         if (minSinceCreation < 5) {
            smaMgr.getCurrentStateDam().setPercentComplete(100);
            smaMgr.getCurrentStateDam().setHoursSpent(hoursSinceCreation);
            return true;
         }
      }

      // Otherwise, open dialog to ask for hours complete
      String msg =
            smaMgr.getCurrentStateName() + " State\n\n" + smaMgr.getSma().getCurrentState().getHoursSpentStr() + " hours already spent on this state.\n" + "Enter the additional number of hours you spent on this state.";
      SMAStatusDialog tsd =
            new SMAStatusDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Enter Hours Spent",
                  msg, false, Arrays.asList(new StateMachineArtifact[] {smaMgr.getSma()}));
      int result = tsd.open();
      if (result == 0) {
         smaMgr.getCurrentStateDam().setPercentComplete(100);
         smaMgr.getCurrentStateDam().setHoursSpent(
               smaMgr.getSma().getCurrentState().getHoursSpent() + tsd.getHours().getFloat());
         return true;
      }
      return false;
   }

   public int getCreationToNowDateDeltaMinutes() {
      Date createDate = smaMgr.getSma().getLog().getStateEvent(LogType.StateEntered, page.getName()).getDate();
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
   public XTaskViewer getXTask() {
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
      return page;
   }

   /**
    * @return the mainComp
    */
   public Composite getMainComp() {
      return mainComp;
   }

}
