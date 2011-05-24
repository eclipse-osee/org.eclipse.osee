/*
 * Created on Feb 23, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.editor.stateItem.AtsStateItemManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.dialog.SMAStatusDialog;
import org.eclipse.osee.ats.workdef.StateDefinitionLabelProvider;
import org.eclipse.osee.ats.workdef.StateDefinitionViewSorter;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserCheckTreeDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

public class WETransitionComposite extends Composite {

   private final XComboViewer transitionToStateCombo;
   private final Label transitionAssigneesLabel;
   private final AbstractWorkflowArtifact awa;
   private final SMAWorkFlowSection workflowSection;
   private final SMAEditor editor;

   public WETransitionComposite(Composite parent, SMAWorkFlowSection workflowSection, final SMAEditor editor, final boolean isEditable) throws OseeCoreException {
      super(parent, SWT.NONE);
      this.workflowSection = workflowSection;
      this.editor = editor;

      awa = workflowSection.getSma();
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setLayout(new GridLayout((editor.getWorkFlowTab().isShowTargetedVersion() ? 7 : 5), false));
      setBackground(AtsUtil.ACTIVE_COLOR);

      Button transitionButton = editor.getToolkit().createButton(this, "Transition", SWT.PUSH);
      transitionButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleTransitionButtonSelection(editor, isEditable);
         }
      });
      transitionButton.setBackground(AtsUtil.ACTIVE_COLOR);

      Label label = editor.getToolkit().createLabel(this, "to");
      label.setBackground(AtsUtil.ACTIVE_COLOR);

      transitionToStateCombo = new XComboViewer("Transition To State Combo", SWT.NONE);
      transitionToStateCombo.setDisplayLabel(false);
      List<Object> allPages = new ArrayList<Object>();
      for (StateDefinition nextState : awa.getToStates()) {
         if (!allPages.contains(nextState)) {
            allPages.add(nextState);
         }
      }
      StateDefinition currState = awa.getStateDefinition();
      if (currState.isCompletedPage()) {
         StateDefinition completedFromState = awa.getWorkDefinition().getStateByName(awa.getCompletedFromState());
         if (!allPages.contains(completedFromState)) {
            allPages.add(completedFromState);
         }
      }
      if (currState.isCancelledPage()) {
         StateDefinition cancelledFromState = awa.getWorkDefinition().getStateByName(awa.getCancelledFromState());
         if (!allPages.contains(cancelledFromState)) {
            allPages.add(cancelledFromState);
         }
      }
      transitionToStateCombo.setInput(allPages);
      transitionToStateCombo.setLabelProvider(new StateDefinitionLabelProvider());
      transitionToStateCombo.setContentProvider(new ArrayContentProvider());
      transitionToStateCombo.setSorter(new StateDefinitionViewSorter());

      transitionToStateCombo.createWidgets(this, 1);

      // Set default page from workflow default
      ArrayList<Object> defaultPage = new ArrayList<Object>();
      if (workflowSection.getPage().getDefaultToPage() != null) {
         defaultPage.add(workflowSection.getPage().getDefaultToPage());
         transitionToStateCombo.setSelected(defaultPage);
      }
      if (workflowSection.getPage().isCancelledPage() && Strings.isValid(awa.getCancelledFromState())) {
         defaultPage.add(awa.getStateDefinitionByName(awa.getCancelledFromState()));
         transitionToStateCombo.setSelected(defaultPage);
      }
      if (workflowSection.getPage().isCompletedPage() && Strings.isValid(awa.getCompletedFromState())) {
         defaultPage.add(awa.getStateDefinitionByName(awa.getCompletedFromState()));
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

      if (editor.getWorkFlowTab().isShowTargetedVersion()) {
         SMATargetedVersionHeader smaTargetedVersionHeader = new SMATargetedVersionHeader(this, SWT.NONE, awa, editor);
         smaTargetedVersionHeader.setBackground(AtsUtil.ACTIVE_COLOR);
      }

      Hyperlink assigneesLabelLink = editor.getToolkit().createHyperlink(this, "Next State Assignee(s)", SWT.NONE);
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
               handleChangeTransitionAssignees(awa);
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

      });
      assigneesLabelLink.setBackground(AtsUtil.ACTIVE_COLOR);

      transitionAssigneesLabel =
         editor.getToolkit().createLabel(this, Strings.truncate(awa.getTransitionAssigneesStr(), 100, true));
      transitionAssigneesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      transitionAssigneesLabel.setBackground(AtsUtil.ACTIVE_COLOR);

   }

   private void handleTransitionButtonSelection(final SMAEditor editor, final boolean isEditable) {
      editor.doSave(null);

      StateDefinition toStateDef = (StateDefinition) transitionToStateCombo.getSelected();
      TransitionManager transMgr = new TransitionManager(awa, editor.isPriviledgedEditModeEnabled());
      String cancellationReason = "";
      if (toStateDef.isCancelledPage()) {
         EntryDialog cancelDialog = new EntryDialog("Cancellation Reason", "Enter cancellation reason.");
         if (cancelDialog.open() != 0) {
            return;
         }
         cancellationReason = cancelDialog.getEntry();
      }

      try {
         handlePopulateStateMetrics(toStateDef);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      Result result = transMgr.handleTransition(toStateDef, cancellationReason);
      if (result.isCancelled()) {
         return;
      } else if (result.isFalse()) {
         AWorkbench.popup(result);
      }
   }

   private int getCreationToNowDateDeltaMinutes() throws OseeCoreException {
      Date createDate = awa.getStateStartedData(awa.getStateDefinition()).getDate();
      long createDateLong = createDate.getTime();
      Date date = new Date();
      float diff = date.getTime() - createDateLong;
      // System.out.println("diff *" + diff + "*");
      Float min = diff / 60000;
      // System.out.println("min *" + min + "*");
      return min.intValue();
   }

   private boolean handlePopulateStateMetrics(StateDefinition toStateDefinition) throws OseeCoreException {
      // Don't log metrics for completed / cancelled states
      if (toStateDefinition.isCompletedOrCancelledPage()) {
         return true;
      }

      // Page has the ability to override the autofill of the metrics
      if (!isRequireStateHoursSpentPrompt(toStateDefinition) && awa.getStateMgr().getHoursSpent() == 0) {
         // First, try to autofill if it's only been < 5 min since creation
         double minSinceCreation = getCreationToNowDateDeltaMinutes();
         // System.out.println("minSinceCreation *" + minSinceCreation + "*");
         double hoursSinceCreation = minSinceCreation / 60.0;
         if (hoursSinceCreation < 0.02) {
            hoursSinceCreation = 0.02;
         }
         // System.out.println("hoursSinceCreation *" + hoursSinceCreation + "*");
         if (minSinceCreation < 5) {
            awa.getStateMgr().updateMetrics(hoursSinceCreation, 100, true);
            return true;
         }
      }

      if (isRequireStateHoursSpentPrompt(toStateDefinition)) {
         // Otherwise, open dialog to ask for hours complete
         String msg =
            awa.getStateMgr().getCurrentStateName() + " State\n\n" + AtsUtilCore.doubleToI18nString(awa.getStateMgr().getHoursSpent()) + " hours already spent on this state.\n" + "Enter the additional number of hours you spent on this state.";
         SMAStatusDialog tsd =
            new SMAStatusDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Enter Hours Spent",
               msg, false, Arrays.asList(awa));
         int result = tsd.open();
         if (result == 0) {
            awa.getStateMgr().updateMetrics(tsd.getHours().getFloat(), 100, true);
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   private boolean isRequireStateHoursSpentPrompt(StateDefinition stateDefinition) {
      return AtsWorkDefinitions.isRequireStateHoursSpentPrompt(stateDefinition);
   }

   public void updateTransitionToAssignees() throws OseeCoreException {
      Collection<IBasicUser> assignees = null;
      // Determine if the is an override set of assigness
      for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
         String decisionValueIfApplicable = null;
         if (awa.isOfType(AtsArtifactTypes.DecisionReview)) {
            XComboDam xWidget =
               (XComboDam) workflowSection.getPage().getLayoutData(AtsAttributeTypes.Decision.getName()).getXWidget();
            if (xWidget != null) {
               decisionValueIfApplicable = xWidget.get();
            }
         }
         assignees = item.getOverrideTransitionToAssignees(workflowSection.getSma(), decisionValueIfApplicable);
         if (assignees != null) {
            break;
         }
      }
      // If override set and isn't the same as already selected, update
      if (assignees != null && !awa.getTransitionAssignees().equals(assignees)) {
         awa.setTransitionAssignees(assignees);
         editor.onDirtied();
      }
      refresh();
   }

   public void updateTransitionToState() throws OseeCoreException {
      // Determine if there is a transitionToStateOverride for this page
      String transitionStateOverride = null;
      for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
         transitionStateOverride = item.getOverrideTransitionToStateName(workflowSection);
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
         for (StateDefinition toState : awa.getToStates()) {
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

   public void refresh() throws OseeCoreException {
      if (Widgets.isAccessible(transitionAssigneesLabel)) {
         StateDefinition toWorkPage = (StateDefinition) transitionToStateCombo.getSelected();
         if (toWorkPage == null) {
            transitionAssigneesLabel.setText("");
         } else {
            transitionAssigneesLabel.setText(workflowSection.getPage().getSma().getTransitionAssigneesStr());
         }
         transitionAssigneesLabel.getParent().layout();
      }
   }

   private void handleChangeTransitionAssignees(AbstractWorkflowArtifact aba) throws OseeCoreException {
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
      uld.setInitialSelections(aba.getTransitionAssignees());
      if (awa.getParentTeamWorkflow() != null) {
         uld.setTeamMembers(awa.getParentTeamWorkflow().getTeamDefinition().getMembersAndLeads());
      }
      if (uld.open() != 0) {
         return;
      }
      Collection<IBasicUser> users = uld.getUsersSelected();
      if (users.isEmpty()) {
         AWorkbench.popup("ERROR", "Must have at least one assignee");
         return;
      }
      awa.setTransitionAssignees(users);
      refresh();
      editor.onDirtied();
   }

   public XComboViewer getTransitionToStateCombo() {
      return transitionToStateCombo;
   }

}
