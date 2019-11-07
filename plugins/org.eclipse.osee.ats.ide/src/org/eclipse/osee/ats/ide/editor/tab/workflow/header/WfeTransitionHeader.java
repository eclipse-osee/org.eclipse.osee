/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.event.IAtsWorkItemTopicEventListener;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelperAdapter;
import org.eclipse.osee.ats.core.workflow.transition.TransitionStatusData;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.stateitem.AtsStateItemManager;
import org.eclipse.osee.ats.ide.editor.tab.workflow.stateitem.IAtsStateItem;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TransitionStatusDialog;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.transition.TransitionToOperation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench.MessageType;
import org.eclipse.osee.framework.ui.plugin.util.ListSelectionDialogNoSave;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCancelWidgetDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeTransitionHeader extends Composite implements IAtsWorkItemTopicEventListener {

   private final Label transitionAssigneesLabel, transitionToStateLabel;
   private final AbstractWorkflowArtifact awa;
   private final WorkflowEditor editor;
   private IAtsStateDefinition userSelectedTransitionToState;
   private final boolean isEditable;
   private final Hyperlink transitionLabelLink;

   public WfeTransitionHeader(Composite parent, final WorkflowEditor editor, final boolean isEditable) {
      super(parent, SWT.NONE);
      this.editor = editor;
      this.isEditable = isEditable;

      awa = editor.getWorkItem();
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      GridLayout layout = new GridLayout(editor.getWorkFlowTab().getHeader().isShowTargetedVersion() ? 7 : 5, false);
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      setLayout(layout);
      editor.getWorkFlowTab().getManagedForm().getToolkit().adapt(this);

      // Register for events and deregister on dispose
      AtsClientService.get().getEventService().registerAtsWorkItemTopicEvent(this, AtsTopicEvent.WORK_ITEM_TRANSITIONED,
         AtsTopicEvent.WORK_ITEM_TRANSITION_FAILED);
      final WfeTransitionHeader fThis = this;
      addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            AtsClientService.get().getEventService().deRegisterAtsWorkItemTopicEvent(fThis);
         }
      });

      transitionLabelLink = editor.getToolkit().createHyperlink(this, "Transition", SWT.NONE);
      transitionLabelLink.addHyperlinkListener(new HyperlinkAdapter() {
         @Override
         public void linkActivated(HyperlinkEvent e) {
            if (editor.isDirty()) {
               editor.doSave(null);
            }
            transitionLabelLink.setEnabled(false);
            handleTransitionButtonSelection();
         }
      });
      transitionLabelLink.setFont(FontManager.getCourierNew12Bold());
      transitionLabelLink.setToolTipText("Select to transition workflow to the default or selected state");

      Hyperlink toStateLabelLink = editor.getToolkit().createHyperlink(this, "To State", SWT.NONE);
      toStateLabelLink.addHyperlinkListener(new HyperlinkAdapter() {
         @Override
         public void linkActivated(HyperlinkEvent e) {
            try {
               IAtsStateDefinition selState = handleChangeTransitionToState(awa, isEditable, getToState());
               if (selState != null) {
                  userSelectedTransitionToState = selState;
                  handleTransitionButtonSelection();
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });
      String toStateName = getToState() == null ? "<not set>" : getToState().getName();
      transitionToStateLabel = editor.getToolkit().createLabel(this, toStateName);
      transitionToStateLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      transitionToStateLabel.setToolTipText("Select to change state to transition to");

      if (editor.getWorkFlowTab().getHeader().isShowTargetedVersion()) {
         new WfeTargetedVersionHeader(this, SWT.NONE, (IAtsTeamWorkflow) awa, editor);
      }

      Hyperlink assigneesLabelLink = editor.getToolkit().createHyperlink(this, "Next State Assignee(s)", SWT.NONE);
      assigneesLabelLink.addHyperlinkListener(new HyperlinkAdapter() {
         @Override
         public void linkActivated(HyperlinkEvent e) {
            try {
               handleChangeTransitionAssignees(awa);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
      transitionAssigneesLabel =
         editor.getToolkit().createLabel(this, Strings.truncate(awa.getTransitionAssigneesStr(), 100, true));
      transitionAssigneesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      transitionAssigneesLabel.setToolTipText("Select to change assignee(s) upon transition to next state.");

   }

   public static IAtsStateDefinition handleChangeTransitionToState(AbstractWorkflowArtifact awa, final boolean isEditable, IAtsStateDefinition toStateDef) {
      List<IAtsStateDefinition> states = awa.getToStatesWithCompleteCancelReturnStates();

      Object[] stateArray = states.toArray();
      ListSelectionDialogNoSave dialog =
         new ListSelectionDialogNoSave(stateArray, Displays.getActiveShell().getShell(), "Select Transition-To State",
            null, "Select the state to transition to.\nTransition will happen upon selection and Tranistion button.\n" //
               + "Double-click will select, close and transition.",
            2, new String[] {"Transition", "Cancel"}, 0);

      if (dialog.open() == 0) {
         Object obj = stateArray[dialog.getSelection()];
         System.err.println("Selected " + obj);
         return (IAtsStateDefinition) obj;
      }

      return null;
   }

   public void handleTransitionButtonSelection() {
      final IAtsStateDefinition toStateDef = getToState();
      handleTransitionButtonSelection(awa, isEditable, toStateDef);
   }

   public static void handleTransitionButtonSelection(AbstractWorkflowArtifact awa, final boolean isEditable, IAtsStateDefinition toStateDef) {
      final IAtsStateDefinition fromStateDef = awa.getStateDefinition();

      ITransitionHelper helper = new TransitionHelperAdapter(AtsClientService.get().getServices()) {

         private IAtsChangeSet changes;

         @Override
         public String getToStateName() {
            return toStateDef.getName();
         }

         @Override
         public Collection<? extends IAtsUser> getToAssignees(IAtsWorkItem workItem) {
            AbstractWorkflowArtifact awa =
               (AbstractWorkflowArtifact) AtsClientService.get().getQueryService().getArtifact(workItem);
            return awa.getTransitionAssignees();
         }

         @Override
         public String getName() {
            return "Workflow Editor Transition";
         }

         @Override
         public Result handleExtraHoursSpent(final IAtsChangeSet changes) {
            final Result result = new Result(true, "");
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  boolean resultBool = false;
                  try {
                     resultBool = handlePopulateStateMetrics(awa, fromStateDef, toStateDef, changes);
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                     result.set(false);
                     result.setText(String.format("Error processing extra hours spent for [%s]", awa.toStringWithId()));
                  }
                  if (!resultBool) {
                     result.setCancelled(true);
                     result.set(false);
                  }
               }
            }, true);
            return result;
         }

         @Override
         public Result getCompleteOrCancellationReason() {
            final Result result = new Result(true, "");
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  IAtsStateDefinition toStateDef;
                  try {
                     toStateDef = AtsClientService.get().getWorkDefinitionService().getStateDefinitionByName(awa,
                        getToStateName());
                     if (toStateDef.getStateType().isCancelledState()) {
                        EntryDialog cancelDialog;
                        boolean useEntryCancelWidgetDialog = false;
                        for (IAtsLayoutItem layoutItem : toStateDef.getLayoutItems()) {
                           if (layoutItem.getName().contains("Cancel")) {
                              useEntryCancelWidgetDialog = true;
                              break;
                           }
                        }
                        if (useEntryCancelWidgetDialog) {
                           cancelDialog = new EntryCancelWidgetDialog("Cancellation Reason",
                              "Select cancellation reason.  If other, please specify with details in the text entry.");
                        } else {
                           cancelDialog = new EntryDialog("Cancellation Reason", "Enter cancellation reason.");
                        }
                        if (cancelDialog.open() != 0) {
                           result.setCancelled(true);
                        }
                        result.set(true);
                        if (useEntryCancelWidgetDialog) {
                           awa.setSoleAttributeFromString(AtsAttributeTypes.CancelReason,
                              ((EntryCancelWidgetDialog) cancelDialog).getEntry());
                           awa.setSoleAttributeFromString(AtsAttributeTypes.CancelledReasonDetails,
                              ((EntryCancelWidgetDialog) cancelDialog).getCancelledDetails());
                        } else {
                           result.setText(cancelDialog.getEntry());
                        }
                     }
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
               }
            }, true);
            return result;
         }

         @Override
         public Collection<IAtsWorkItem> getWorkItems() {
            return Arrays.asList(awa);
         }

         @Override
         public IAtsChangeSet getChangeSet() {
            if (changes == null) {
               changes = AtsClientService.get().createChangeSet(getName());
            }
            return changes;
         }

         @Override
         public Collection<ITransitionListener> getTransitionListeners() {
            try {
               return AtsClientService.get().getWorkItemService().getTransitionListeners();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            return java.util.Collections.emptyList();
         }

         @Override
         public AtsApi getServices() {
            return AtsClientService.get().getServices();
         }

      };
      final TransitionToOperation operation = new TransitionToOperation(helper);
      Operations.executeAsJob(operation, true, Job.SHORT, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            TransitionResults results = operation.getResults();
            if (!results.isEmpty()) {
               String resultStr = results.getResultString();
               AtsUtilClient.logExceptions(results);
               AWorkbench.popup(MessageType.Error, "Transition Failed", resultStr);
            }
         }

      });
   }

   private static boolean handlePopulateStateMetrics(AbstractWorkflowArtifact awa, IAtsStateDefinition fromStateDefinition, IAtsStateDefinition toStateDefinition, IAtsChangeSet changes) {
      int percent = 0;
      // If state weighting, always 100 cause state is completed
      if (AtsClientService.get().getWorkDefinitionService().isStateWeightingEnabled(awa.getWorkDefinition())) {
         percent = 100;
      } else {
         if (toStateDefinition.getStateType().isCompletedOrCancelledState()) {
            percent = 100;
         } else {
            percent = awa.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
         }
      }

      double hoursSpent = awa.getStateMgr().getHoursSpent(awa.getCurrentStateName());
      double additionalHours = 0.0;

      if (isRequireStateHoursSpentPrompt(fromStateDefinition) && !toStateDefinition.getStateType().isCancelledState()) {
         // Otherwise, open dialog to ask for hours complete
         String msg = awa.getStateMgr().getCurrentStateName() + " State\n\n" + AtsUtil.doubleToI18nString(
            hoursSpent) + " hours already spent on this state.\n" + "Enter the additional number of hours you spent on this state.";
         // Remove after ATS Resolution options is removed 0.9.9_SR5ish
         TransitionStatusData data = new TransitionStatusData(Arrays.asList(awa), false);
         TransitionStatusDialog dialog = new TransitionStatusDialog("Enter Hours Spent", msg, data);
         int result = dialog.open();
         if (result == 0) {
            additionalHours = dialog.getData().getAdditionalHours();
         } else {
            return false;
         }
      }
      awa.getStateMgr().updateMetrics(awa.getStateDefinition(), additionalHours, percent, true,
         AtsClientService.get().getUserService().getCurrentUser());
      changes.add(awa);
      return true;
   }

   private static boolean isRequireStateHoursSpentPrompt(IAtsStateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.RequireStateHourSpentPrompt.name());
   }

   public void updateTransitionToAssignees() {
      Collection<IAtsUser> assignees = null;
      // Determine if the is an override set of assignees
      for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
         String decisionValueIfApplicable = "";
         if (awa.isOfType(
            AtsArtifactTypes.DecisionReview) && editor.getWorkFlowTab().getCurrentStateSection().getPage().getLayoutData(
               AtsAttributeTypes.Decision.getName()) != null) {
            XComboDam xWidget = (XComboDam) editor.getWorkFlowTab().getCurrentStateSection().getPage().getLayoutData(
               AtsAttributeTypes.Decision.getName()).getXWidget();
            if (xWidget != null) {
               decisionValueIfApplicable = xWidget.get();
            }
         }
         assignees = item.getOverrideTransitionToAssignees(awa, decisionValueIfApplicable);
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

   public IAtsStateDefinition getToState() {
      if (userSelectedTransitionToState != null) {
         return userSelectedTransitionToState;
      }
      // Determine if there is a transitionToStateOverride for this page
      String transitionStateOverride = null;
      for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
         transitionStateOverride = item.getOverrideTransitionToStateName(editor);
         if (transitionStateOverride != null) {
            break;
         }
      }
      if (transitionStateOverride != null) {
         // Return if override state is same as selected
         if (awa.getStateDefinition().getDefaultToState().getName().equals(transitionStateOverride)) {
            return awa.getStateDefinition().getDefaultToState();
         }
         // Find page corresponding to override state name
         for (IAtsStateDefinition toState : awa.getStateDefinition().getToStates()) {
            if (toState.getName().equals(transitionStateOverride)) {
               return toState;
            }
         }
      }
      return awa.getStateDefinition().getDefaultToState();
   }

   public void refresh() {
      if (Widgets.isAccessible(transitionAssigneesLabel)) {
         IAtsStateDefinition toState = userSelectedTransitionToState;
         if (toState == null) {
            toState = awa.getStateDefinition().getDefaultToState();
         }
         if (toState == null) {
            transitionToStateLabel.setText("<Not Set>");
         } else {
            transitionToStateLabel.setText(toState.getName());
         }
         transitionToStateLabel.getParent().layout();

         transitionLabelLink.setEnabled(true);

         transitionAssigneesLabel.setText(awa.getTransitionAssigneesStr());
         transitionAssigneesLabel.getParent().layout();
      }
   }

   private void handleChangeTransitionAssignees(AbstractWorkflowArtifact aba) {
      userSelectedTransitionToState = getToState();
      if (userSelectedTransitionToState == null) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "No Transition State Selected");
         return;
      }
      if (userSelectedTransitionToState.getStateType().isCompletedOrCancelledState()) {
         AWorkbench.popup("ERROR", "No Assignees in Completed and Cancelled states");
         return;
      }
      UserCheckTreeDialog uld = new UserCheckTreeDialog();
      uld.setInitialSelections(aba.getTransitionAssignees());
      if (awa.getParentTeamWorkflow() != null) {
         uld.setTeamMembers(awa.getParentTeamWorkflow().getTeamDefinition().getMembersAndLeads());
      }
      if (uld.open() != 0) {
         return;
      }
      Collection<IAtsUser> users = uld.getUsersSelected();
      if (users.isEmpty()) {
         AWorkbench.popup("ERROR", "Must have at least one assignee");
         return;
      }
      awa.setTransitionAssignees(users);
      refresh();
      editor.onDirtied();
   }

   @Override
   public void handleEvent(AtsTopicEvent topicEvent, Collection<ArtifactId> workItems) {
      if (topicEvent.equals(AtsTopicEvent.WORK_ITEM_TRANSITIONED) || topicEvent.equals(
         AtsTopicEvent.WORK_ITEM_TRANSITION_FAILED)) {
         System.err.println("handleEvent " + topicEvent);
         if (this.isDisposed()) {
            AtsClientService.get().getEventService().deRegisterAtsWorkItemTopicEvent(this);
            return;
         }
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               userSelectedTransitionToState = null;
               refresh();
            }
         });
      }
   }

   public boolean isSelected() {
      return userSelectedTransitionToState != null;
   }

}
