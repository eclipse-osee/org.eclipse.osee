/*********************************************************************
 * Copyright (c) 2011 Boeing
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.StateOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelperAdapter;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog;
import org.eclipse.osee.ats.ide.util.widgets.dialog.CancelledReasonEnumDialog;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.cr.sibling.base.XSiblingActionBar;
import org.eclipse.osee.ats.ide.workflow.transition.TransitionResultsUi;
import org.eclipse.osee.ats.ide.workflow.transition.TransitionToOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ListSelectionDialogNoSave;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
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
public class WfeTransitionHeader extends Composite {

   private final Label transitionAssigneesLabel;
   private final AbstractWorkflowArtifact workItem;
   private final WorkflowEditor editor;
   private StateDefinition userSelectedTransitionToState;
   private final boolean isEditable;
   private final Hyperlink transitionLabelLink;
   private final Hyperlink stateLabelLink;
   private Hyperlink createSiblingLink;

   public WfeTransitionHeader(Composite parent, final WorkflowEditor editor, final boolean isEditable) {
      super(parent, SWT.NONE);
      this.editor = editor;
      this.isEditable = isEditable;

      workItem = editor.getWorkItem();
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      GridLayout layout = new GridLayout(editor.getWorkFlowTab().getHeader().isShowTargetedVersion() ? 7 : 5, false);
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      setLayout(layout);
      editor.getWorkFlowTab().getManagedForm().getToolkit().adapt(this);

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
      transitionLabelLink.setFont(FontManager.getDefaultLabelFont());
      transitionLabelLink.setToolTipText("Select to transition workflow to the default or selected state");

      Label transitionToLabel = editor.getToolkit().createLabel(this, "To");
      transitionToLabel.setLayoutData(new GridData());

      String toStateName = getToState() == null ? "<not set>" : getToState().getName();
      stateLabelLink = editor.getToolkit().createHyperlink(this, toStateName, SWT.NONE);
      stateLabelLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      stateLabelLink.addHyperlinkListener(new HyperlinkAdapter() {
         @Override
         public void linkActivated(HyperlinkEvent e) {
            try {
               StateDefinition selState = handleChangeTransitionToState(workItem, isEditable, getToState());
               if (selState != null) {
                  userSelectedTransitionToState = selState;
                  transitionLabelLink.setEnabled(false);
                  handleTransitionButtonSelection();
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });

      Hyperlink assigneesLabelLink = editor.getToolkit().createHyperlink(this, "Next State Assignee(s)", SWT.NONE);
      assigneesLabelLink.addHyperlinkListener(new HyperlinkAdapter() {
         @Override
         public void linkActivated(HyperlinkEvent e) {
            try {
               handleChangeTransitionAssignees(workItem);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
      transitionAssigneesLabel =
         editor.getToolkit().createLabel(this, Strings.truncate(workItem.getTransitionAssigneesStr(), 100, true));
      transitionAssigneesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      transitionAssigneesLabel.setToolTipText("Select to change assignee(s) upon transition to next state.");

      if (workItem.isTeamWorkflow()) {
         boolean createSiblingWorkflowEnabled = true;
         for (IAtsWorkItemHook hook : AtsApiService.get().getWorkItemService().getWorkItemHooks()) {
            if (!hook.createSiblingWorkflowEnabled(workItem)) {
               createSiblingWorkflowEnabled = false;
               break;
            }
         }
         if (createSiblingWorkflowEnabled) {
            createSiblingLink = editor.getToolkit().createHyperlink(this, "Create Sibling Workflow(s)", SWT.NONE);
            createSiblingLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            createSiblingLink.setToolTipText("Create new Team Workflows off same Action");
            createSiblingLink.addHyperlinkListener(new HyperlinkAdapter() {
               @Override
               public void linkActivated(HyperlinkEvent e) {
                  try {
                     XSiblingActionBar.openCreateSiblingWorkflowBlam((IAtsTeamWorkflow) workItem);
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
               }
            });
         }
      }
   }

   public static StateDefinition handleChangeTransitionToState(AbstractWorkflowArtifact awa, final boolean isEditable, StateDefinition toStateDef) {
      List<StateDefinition> states = AtsApiService.get().getWorkItemService().getAllToStates(awa);
      ListSelectionDialogNoSave dialog =
         new ListSelectionDialogNoSave(Collections.castAll(states), Displays.getActiveShell().getShell(),
            "Select Transition-To State", null, "Select the state to transition to.\n\n" //
               + "Transition will happen upon selection and Transition button.\n\n" //
               + "Double-click will select, close and transition.",
            2, new String[] {"Transition", "Cancel"}, 0);

      if (dialog.open() == Window.OK) {
         Object obj = dialog.getSelected();
         return (StateDefinition) obj;
      }

      return null;
   }

   public void handleTransitionButtonSelection() {
      final StateDefinition toStateDef = getToState();
      if (toStateDef == null) {
         AWorkbench.popup("Must select state to transition.");
         refresh();
         return;
      }
      if (editor.isDirty()) {
         editor.doSave(null);
      }
      editor.getWorkFlowTab().setLoading(true);
      handleTransitionButtonSelection(workItem, isEditable, toStateDef, editor, this);
   }

   public static void handleTransitionButtonSelection(AbstractWorkflowArtifact awa, final boolean isEditable, StateDefinition toStateDef, final WorkflowEditor editor, final WfeTransitionHeader transitionHeader) {
      ITransitionHelper helper = new TransitionHelperAdapter(AtsApiService.get()) {

         @Override
         public String getToStateName() {
            return toStateDef.getName();
         }

         @Override
         public Collection<AtsUser> getToAssignees(IAtsWorkItem workItem) {
            AbstractWorkflowArtifact awa =
               (AbstractWorkflowArtifact) AtsApiService.get().getQueryService().getArtifact(workItem);
            return awa.getTransitionAssignees();
         }

         @Override
         public String getName() {
            return "Workflow Editor Transition";
         }

         @Override
         public TransitionData getCancellationReason(final TransitionData transitionData) {
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  StateDefinition toStateDef;
                  try {
                     toStateDef =
                        AtsApiService.get().getWorkDefinitionService().getStateDefinitionByName(awa, getToStateName());
                     if (toStateDef.getStateType().isCancelledState()) {
                        EntryDialog cancelDialog;
                        boolean useCancelledReasonEnumDialog =
                           toStateDef.getStateOptions().contains(StateOption.USE_CANCELLED_REASON_ENUM_DIALOG);
                        if (useCancelledReasonEnumDialog) {
                           cancelDialog = new CancelledReasonEnumDialog("Cancellation Reason",
                              "Select cancellation reason.  If other, please specify with details.");
                        } else {
                           cancelDialog = new EntryDialog("Cancellation Reason", "Enter cancellation reason.");
                        }
                        if (cancelDialog.open() != 0) {
                           transitionData.setDialogCancelled(true);
                           return;
                        }
                        if (useCancelledReasonEnumDialog) {
                           transitionData.setCancellationReason(((CancelledReasonEnumDialog) cancelDialog).getEntry());
                           transitionData.setCancellationReasonAttrType(AtsAttributeTypes.CancelledReasonEnum);
                           transitionData.setCancellationReasonDetails(
                              ((CancelledReasonEnumDialog) cancelDialog).getCancelledDetails());
                        } else {
                           transitionData.setCancellationReason(cancelDialog.getEntry());
                           transitionData.setCancellationReasonAttrType(AtsAttributeTypes.CancelledReason);
                        }
                     }
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
               }
            }, true);
            return transitionData;
         }

         @Override
         public Collection<IAtsWorkItem> getWorkItems() {
            return Arrays.asList(awa);
         }

         @Override
         public IAtsChangeSet getChangeSet() {
            return null;
         }

         @Override
         public Collection<IAtsTransitionHook> getTransitionListeners() {
            try {
               return AtsApiService.get().getWorkItemService().getTransitionHooks();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            return java.util.Collections.emptyList();
         }

         @Override
         public AtsApi getServices() {
            return AtsApiService.get();
         }

         @Override
         public String getCancellationReasonDetails() {
            return null;
         }

         @Override
         public String getCancellationReason() {
            return null;
         }

      };
      final TransitionToOperation operation = new TransitionToOperation(helper);
      Operations.executeAsJob(operation, true, Job.SHORT, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            TransitionResults results = operation.getResults();
            if (results != null) {
               results.setAtsApi(AtsApiService.get());
               if (results.isErrors()) {
                  TransitionResultsUi.reportDialog("Transition Failed", results);
                  AtsUtilClient.logExceptions(results);
               }
            }
            editor.getWorkFlowTab().setLoading(false);
            transitionHeader.refresh();
         }

      });
   }

   public void updateTransitionToAssignees() {
      Collection<AtsUser> assignees = null;
      // Determine if the is an override set of assignees
      for (IAtsWorkItemHook item : AtsApiService.get().getWorkItemService().getWorkItemHooks()) {
         String decisionValueIfApplicable = "";
         if (workItem.isOfType(
            AtsArtifactTypes.DecisionReview) && editor.getWorkFlowTab().getCurrentStateSection().getPage().getLayoutData(
               AtsAttributeTypes.Decision.getName()) != null) {
            XComboDam xWidget = (XComboDam) editor.getWorkFlowTab().getCurrentStateSection().getPage().getLayoutData(
               AtsAttributeTypes.Decision.getName()).getXWidget();
            if (xWidget != null) {
               decisionValueIfApplicable = xWidget.get();
            }
         }
         assignees = item.getOverrideTransitionToAssignees(workItem, decisionValueIfApplicable);
         if (assignees != null) {
            break;
         }
      }
      // If override set and isn't the same as already selected, update
      if (assignees != null && !workItem.getTransitionAssignees().equals(assignees)) {
         workItem.setTransitionAssignees(assignees);
         editor.onDirtied();
      }
      refresh();
   }

   public StateDefinition getToState() {
      StateDefinition state = AtsApiService.get().getWorkItemService().getDefaultToState(workItem);
      if (userSelectedTransitionToState != null) {
         return userSelectedTransitionToState;
      }
      // Determine if there is a transitionToStateOverride for this page
      String transitionStateOverride = null;
      for (IAtsTransitionHook item : AtsApiService.get().getWorkItemService().getTransitionHooks()) {
         transitionStateOverride = item.getOverrideTransitionToStateName(editor.getWorkItem());
         if (transitionStateOverride != null) {
            break;
         }
      }
      if (transitionStateOverride != null) {
         // Return if override state is same as selected
         if (AtsApiService.get().getWorkItemService().getDefaultToState(workItem).getName().equals(
            transitionStateOverride)) {
            state = AtsApiService.get().getWorkItemService().getDefaultToState(workItem);
         }
         // Find page corresponding to override state name
         for (StateDefinition toState : workItem.getStateDefinition().getToStates()) {
            if (toState.getName().equals(transitionStateOverride)) {
               state = toState;
               break;
            }
         }
      }
      return state;
   }

   public void refresh() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (Widgets.isAccessible(transitionAssigneesLabel)) {
               StateDefinition toState = userSelectedTransitionToState;
               if (toState == null) {
                  toState = AtsApiService.get().getWorkItemService().getDefaultToState(workItem);
               }
               if (toState == null) {
                  stateLabelLink.setText("<Not Set>");
               } else {
                  stateLabelLink.setText(toState.getName());
               }
               stateLabelLink.getParent().layout();

               transitionLabelLink.setEnabled(true);

               transitionAssigneesLabel.setText(workItem.getTransitionAssigneesStr());
               transitionAssigneesLabel.getParent().layout();

               userSelectedTransitionToState = null;
            }
         }
      });
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
      if (workItem.getParentTeamWorkflow() != null) {
         uld.setTeamMembers(AtsApiService.get().getTeamDefinitionService().getMembersAndLeads(
            workItem.getParentTeamWorkflow().getTeamDefinition()));
      }
      if (uld.open() != 0) {
         return;
      }
      Collection<AtsUser> users = uld.getUsersSelected();
      if (users.isEmpty()) {
         AWorkbench.popup("ERROR", "Must have at least one assignee");
         return;
      }
      workItem.setTransitionAssignees(users);
      refresh();
      editor.onDirtied();
   }

   public boolean isSelected() {
      return userSelectedTransitionToState != null;
   }

}
