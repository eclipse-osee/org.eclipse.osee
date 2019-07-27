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
package org.eclipse.osee.ats.ide.editor.tab.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelperAdapter;
import org.eclipse.osee.ats.core.workflow.transition.TransitionStatusData;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeTargetedVersionHeader;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeWorkflowSection;
import org.eclipse.osee.ats.ide.editor.tab.workflow.stateitem.AtsStateItemManager;
import org.eclipse.osee.ats.ide.editor.tab.workflow.stateitem.IAtsStateItem;
import org.eclipse.osee.ats.ide.editor.tab.workflow.widget.XTransitionToStateComboWidget;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TransitionStatusDialog;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.transition.TransitionToOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench.MessageType;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeTransitionComposite extends Composite {

   private final XTransitionToStateComboWidget transitionToStateCombo;
   private final Label transitionAssigneesLabel;
   private final AbstractWorkflowArtifact awa;
   private final WfeWorkflowSection workflowSection;
   private final WorkflowEditor editor;
   private final Button transitionButton;
   public final static Color ACTIVE_COLOR = new Color(null, 206, 212, 239);

   public WfeTransitionComposite(Composite parent, WfeWorkflowSection workflowSection, final WorkflowEditor editor, final boolean isEditable) {
      super(parent, SWT.NONE);
      this.workflowSection = workflowSection;
      this.editor = editor;

      awa = workflowSection.getSma();
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setLayout(new GridLayout(editor.getWorkFlowTab().getHeader().isShowTargetedVersion() ? 7 : 5, false));
      setBackground(ACTIVE_COLOR);

      transitionButton = editor.getToolkit().createButton(this, "Transition", SWT.PUSH);
      transitionButton.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            super.mouseUp(e);
            /**
             * Only respond to first click and not to double click. After system configured time, count will return to
             * 1.
             */
            if (e.count == 1) {
               transitionButton.setEnabled(false);
               try {
                  if (editor.isDirty()) {
                     editor.doSave(null);
                  }
                  IAtsStateDefinition toStateDef = (IAtsStateDefinition) transitionToStateCombo.getSelected();
                  Conditions.assertNotNull(toStateDef, "toStateDef");
                  handleTransitionButtonSelection(awa, isEditable, toStateDef);
               } finally {
                  transitionButton.setEnabled(true);
               }
            }
         }

      });
      transitionButton.setBackground(ACTIVE_COLOR);

      Label label = editor.getToolkit().createLabel(this, "to");
      label.setBackground(ACTIVE_COLOR);

      transitionToStateCombo = new XTransitionToStateComboWidget();
      transitionToStateCombo.setArtifact(awa);
      transitionToStateCombo.createWidgets(this, 1);

      updateTransitionToState();

      transitionToStateCombo.addSelectionChangedListener(new ISelectionChangedListener() {
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            try {
               updateTransitionToAssignees();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });

      if (editor.getWorkFlowTab().getHeader().isShowTargetedVersion()) {
         WfeTargetedVersionHeader smaTargetedVersionHeader =
            new WfeTargetedVersionHeader(this, SWT.NONE, (IAtsTeamWorkflow) awa, editor);
         smaTargetedVersionHeader.setBackground(ACTIVE_COLOR);
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
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

      });
      assigneesLabelLink.setBackground(ACTIVE_COLOR);

      transitionAssigneesLabel =
         editor.getToolkit().createLabel(this, Strings.truncate(awa.getTransitionAssigneesStr(), 100, true));
      transitionAssigneesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      transitionAssigneesLabel.setBackground(ACTIVE_COLOR);

   }

   public static void handleTransitionButtonSelection(AbstractWorkflowArtifact awa, final boolean isEditable, IAtsStateDefinition toStateDef) {
      Conditions.assertNotNull(awa, "awa");
      Conditions.assertNotNull(toStateDef, "toStateDef");
      final List<IAtsWorkItem> workItems = Arrays.asList((IAtsWorkItem) awa);
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
                     result.setText(String.format("Error processing extra hours spent for [%s]",
                        workItems.iterator().next().toStringWithId()));
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
                        EntryDialog cancelDialog = new EntryDialog("Cancellation Reason", "Enter cancellation reason.");
                        if (cancelDialog.open() != 0) {
                           result.setCancelled(true);
                        }
                        result.set(true);
                        result.setText(cancelDialog.getEntry());
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
            return workItems;
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
      // Determine if the is an override set of assigness
      for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
         String decisionValueIfApplicable = "";
         if (awa.isOfType(AtsArtifactTypes.DecisionReview) && workflowSection.getPage().getLayoutData(
            AtsAttributeTypes.Decision.getName()) != null) {
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

   public void updateTransitionToState() {
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
         if (((IAtsStateDefinition) transitionToStateCombo.getSelected()).getName().equals(transitionStateOverride)) {
            return;
         }
         // Find page corresponding to override state name
         for (IAtsStateDefinition toState : awa.getStateDefinition().getToStates()) {
            if (toState.getName().equals(transitionStateOverride)) {
               // Reset selection
               ArrayList<Object> defaultPage = new ArrayList<>();
               defaultPage.add(toState);
               transitionToStateCombo.setSelected(defaultPage);
               return;
            }
         }
      }
   }

   public void refresh() {
      if (Widgets.isAccessible(transitionAssigneesLabel)) {
         IAtsStateDefinition toWorkPage = (IAtsStateDefinition) transitionToStateCombo.getSelected();
         if (toWorkPage == null) {
            transitionAssigneesLabel.setText("");
         } else {
            transitionAssigneesLabel.setText(workflowSection.getPage().getSma().getTransitionAssigneesStr());
         }
         transitionAssigneesLabel.getParent().layout();
      }
   }

   private void handleChangeTransitionAssignees(AbstractWorkflowArtifact aba) {
      IAtsStateDefinition toWorkPage = (IAtsStateDefinition) transitionToStateCombo.getSelected();
      if (toWorkPage == null) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "No Transition State Selected");
         return;
      }
      if (toWorkPage.getStateType().isCompletedOrCancelledState()) {
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

   public XComboViewer getTransitionToStateCombo() {
      return transitionToStateCombo;
   }

}
