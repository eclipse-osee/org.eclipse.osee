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
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkflowHook;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelperAdapter;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.transition.TransitionResultsUi;
import org.eclipse.osee.ats.ide.workflow.transition.TransitionToOperation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
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

   private final Label transitionAssigneesLabel;
   private final AbstractWorkflowArtifact awa;
   private final WorkflowEditor editor;
   private IAtsStateDefinition userSelectedTransitionToState;
   private final boolean isEditable;
   private final Hyperlink transitionLabelLink;
   private final Hyperlink stateLabelLink;

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

      Label transitionToLabel = editor.getToolkit().createLabel(this, "To");
      transitionToLabel.setLayoutData(new GridData());

      String toStateName = getToState() == null ? "<not set>" : getToState().getName();
      stateLabelLink = editor.getToolkit().createHyperlink(this, toStateName, SWT.NONE);
      stateLabelLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      stateLabelLink.addHyperlinkListener(new HyperlinkAdapter() {
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
         return (IAtsStateDefinition) obj;
      }

      return null;
   }

   public void handleTransitionButtonSelection() {
      final IAtsStateDefinition toStateDef = getToState();
      if (toStateDef == null) {
         AWorkbench.popup("Must select state to transition.");
         refresh();
         return;
      }
      if (editor.isDirty()) {
         editor.doSave(null);
      }
      handleTransitionButtonSelection(awa, isEditable, toStateDef);
   }

   public static void handleTransitionButtonSelection(AbstractWorkflowArtifact awa, final boolean isEditable, IAtsStateDefinition toStateDef) {
      ITransitionHelper helper = new TransitionHelperAdapter(AtsClientService.get().getServices()) {

         @Override
         public String getToStateName() {
            return toStateDef.getName();
         }

         @Override
         public Collection<AtsUser> getToAssignees(IAtsWorkItem workItem) {
            AbstractWorkflowArtifact awa =
               (AbstractWorkflowArtifact) AtsClientService.get().getQueryService().getArtifact(workItem);
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
                           transitionData.setDialogCancelled(true);
                           return;
                        }
                        if (useEntryCancelWidgetDialog) {
                           transitionData.setCancellationReason(((EntryCancelWidgetDialog) cancelDialog).getEntry());
                           transitionData.setCancellationReasonDetails(
                              ((EntryCancelWidgetDialog) cancelDialog).getCancelledDetails());
                        } else {
                           transitionData.setCancellationReason(cancelDialog.getEntry());
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
               return AtsClientService.get().getWorkItemService().getTransitionHooks();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            return java.util.Collections.emptyList();
         }

         @Override
         public AtsApi getServices() {
            return AtsClientService.get().getServices();
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
            if (results.isErrors()) {
               TransitionResultsUi.report("Transition Failed", results);
               AtsUtilClient.logExceptions(results);
            }
         }

      });
   }

   public void updateTransitionToAssignees() {
      Collection<AtsUser> assignees = null;
      // Determine if the is an override set of assignees
      for (IAtsWorkflowHook item : AtsClientService.get().getWorkItemService().getWorkflowHooks()) {
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
      for (IAtsTransitionHook item : AtsClientService.get().getWorkItemService().getTransitionHooks()) {
         transitionStateOverride = item.getOverrideTransitionToStateName(editor.getWorkItem());
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
            stateLabelLink.setText("<Not Set>");
         } else {
            stateLabelLink.setText(toState.getName());
         }
         stateLabelLink.getParent().layout();

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
         uld.setTeamMembers(AtsClientService.get().getTeamDefinitionService().getMembersAndLeads(
            awa.getParentTeamWorkflow().getTeamDefinition()));
      }
      if (uld.open() != 0) {
         return;
      }
      Collection<AtsUser> users = uld.getUsersSelected();
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
