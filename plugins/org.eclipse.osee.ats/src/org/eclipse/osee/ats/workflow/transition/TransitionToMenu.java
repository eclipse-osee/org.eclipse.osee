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
package org.eclipse.osee.ats.workflow.transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelperAdapter;
import org.eclipse.osee.ats.core.workflow.transition.TransitionStatusData;
import org.eclipse.osee.ats.editor.WfePromptChangeStatus;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtilClient;
import org.eclipse.osee.ats.util.widgets.dialog.TransitionStatusDialog;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench.MessageType;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class TransitionToMenu {
   public static MenuManager createTransitionToMenuManager(final XViewer xViewer, String name, final Collection<TreeItem> selectedTreeItems) {
      MenuManager editMenuManager =
         new MenuManager(name, ImageManager.getImageDescriptor(AtsImage.TRANSITION), "transition-to");
      final Set<IAtsWorkItem> workItems = new HashSet<>();
      Set<IAtsStateDefinition> toStateDefs = new HashSet<>();
      String workDefinitionId = null;
      Map<String, IAtsStateDefinition> stateNameToStateDef = new HashMap<>();
      boolean multipleWorkDefinitions = false;
      for (TreeItem treeItem : selectedTreeItems) {
         if (treeItem.getData() instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) treeItem.getData();
            workItems.add(awa);
            if (!multipleWorkDefinitions) {
               if (workDefinitionId == null) {
                  workDefinitionId = awa.getWorkDefinition().getName();
               } else if (!workDefinitionId.equals(awa.getWorkDefinition().getName())) {
                  multipleWorkDefinitions = true;
               }
            }
            try {
               for (IAtsStateDefinition stateDef : awa.getToStatesWithCompleteCancelReturnStates()) {
                  toStateDefs.add(stateDef);
                  stateNameToStateDef.put(stateDef.getName(), stateDef);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
      if (toStateDefs.isEmpty()) {
         editMenuManager.add(
            new Action("No Transitionable Selections", ImageManager.getImageDescriptor(AtsImage.TRANSITION)) {

               @Override
               public void run() {
                  AWorkbench.popup("Error", "No selection is in a transitionable state or transitionable together.");
               }

            });
      } else {
         List<String> toStateNames = new ArrayList<>();
         if (multipleWorkDefinitions) {
            for (IAtsStateDefinition stateDef : toStateDefs) {
               if (!toStateNames.contains(stateDef.getName())) {
                  toStateNames.add(stateDef.getName());
               }
            }
            Collections.sort(toStateNames);
         } else {
            Set<Integer> stateOrdinals = new HashSet<>();
            for (final IAtsStateDefinition stateDef : toStateDefs) {
               stateOrdinals.add(stateDef.getOrdinal());
            }
            Integer[] toStates = stateOrdinals.toArray(new Integer[stateOrdinals.size()]);
            Arrays.sort(toStates);
            for (Integer stateOrdinal : stateOrdinals) {
               for (final IAtsStateDefinition stateDef : toStateDefs) {
                  if (stateDef.getOrdinal() == stateOrdinal) {
                     toStateNames.add(stateDef.getName());
                  }
               }
            }
         }
         for (String stateName : toStateNames) {
            editMenuManager.add(
               new Action(getTransitionToString(stateName, multipleWorkDefinitions, stateNameToStateDef),
                  ImageManager.getImageDescriptor(AtsImage.TRANSITION)) {

                  @Override
                  public void run() {
                     handleTransitionToSelected(stateName, workItems);
                  }

               });
         }
      }
      return editMenuManager;
   }

   private static String getTransitionToString(String stateDefName, boolean multipleWorkDefinitions, Map<String, IAtsStateDefinition> stateNameToStateDef) {
      if (multipleWorkDefinitions) {
         return stateDefName;
      } else {
         IAtsStateDefinition stateDef = stateNameToStateDef.get(stateDefName);
         return String.format("%s%s%s", stateDef.getName(), getStateTypeName(stateDef),
            getDefaultStatePercent(stateDef));
      }
   }

   private static Object getDefaultStatePercent(IAtsStateDefinition stateDef) {
      if (stateDef.getRecommendedPercentComplete() != null && stateDef.getRecommendedPercentComplete() != 0) {
         return String.format(" - %d%%", stateDef.getRecommendedPercentComplete());
      }
      return "";
   }

   private static String getStateTypeName(IAtsStateDefinition stateDef) {
      return stateDef.getStateType().isWorkingState() || stateDef.getName().equals(
         TeamState.Completed.getName()) || stateDef.getName().equals(
            TeamState.Cancelled.getName()) ? "" : " (" + stateDef.getStateType().name() + ")";
   }

   private static void handleTransitionToSelected(final String toStateName, final Set<IAtsWorkItem> workItems) {
      final ITransitionHelper helper = new TransitionHelperAdapter(AtsClientService.get().getServices()) {

         private IAtsChangeSet changes;

         @Override
         public Result handleExtraHoursSpent(final IAtsChangeSet changes) {
            final Result result = new Result(true, "");
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  try {
                     IAtsStateDefinition toStateDef =
                        AtsClientService.get().getWorkDefinitionService().getStateDefinitionByName(
                           workItems.iterator().next(), toStateName);

                     IAtsStateDefinition fromStateDefinition = workItems.iterator().next().getStateDefinition();
                     if (isRequireStateHoursSpentPrompt(
                        fromStateDefinition) && !toStateDef.getStateType().isCancelledState()) {

                        boolean showPercentCompleted = !toStateDef.getStateType().isCompletedOrCancelledState();
                        TransitionStatusData data = new TransitionStatusData(getWorkItems(), showPercentCompleted);
                        if (toStateDef.getRecommendedPercentComplete() != null) {
                           data.setDefaultPercent(toStateDef.getRecommendedPercentComplete());
                           data.setPercent(100);
                        } else if (toStateDef.getStateType().isCompletedOrCancelledState()) {
                           data.setDefaultPercent(100);
                           data.setPercent(100);
                        }
                        String title = null;
                        String message = null;
                        if (data.isPercentRequired()) {
                           title = "Enter Percent and Hours Spent";
                           message = "Enter percent complete and additional hours spent in current state(s)";
                        } else {
                           title = "Enter Hours Spent";
                           message = "Enter additional hours spent in current state(s)";
                        }
                        TransitionStatusDialog dialog = new TransitionStatusDialog(title, message, data);

                        int dialogResult = dialog.open();
                        if (dialogResult == 0) {
                           try {
                              WfePromptChangeStatus.performChangeStatus(workItems, null, data.getAdditionalHours(),
                                 data.getPercent(), data.isSplitHoursBetweenItems(), changes);
                           } catch (OseeCoreException ex) {
                              OseeLog.log(Activator.class, Level.SEVERE, ex);
                              result.set(false);
                              result.setTextWithFormat(
                                 "Exception handling extra hours spent for transition to [%s] (see log)",
                                 getToStateName());
                           }
                        } else {
                           result.setCancelled(true);
                        }
                     }
                  } catch (OseeCoreException ex1) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex1);
                  }
               }
            }, true);
            return result;
         }

         @Override
         public String getToStateName() {
            return toStateName;
         }

         @Override
         public Collection<? extends IAtsUser> getToAssignees(IAtsWorkItem workItem) {
            return workItem.getAssignees();
         }

         @Override
         public String getName() {
            return "Transition-To " + getToStateName();
         }

         @Override
         public Result getCompleteOrCancellationReason() {
            final Result result = new Result(false, "");
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  IAtsWorkItem workItem = getWorkItems().iterator().next();
                  IAtsStateDefinition stateDef = null;
                  try {
                     stateDef = AtsClientService.get().getWorkDefinitionService().getStateDefinitionByName(workItem,
                        getToStateName());
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
                  if (stateDef != null && stateDef.getStateType().isCancelledState()) {
                     EntryDialog dialog = new EntryDialog("Enter Cancellation Reason", "Enter Cancellation Reason");
                     if (dialog.open() != 0) {
                        result.setCancelled(true);
                     } else {
                        result.setText(dialog.getEntry());
                        result.set(true);
                     }
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
               AtsUtilClient.logExceptions(results);
               if (helper.getWorkItems().size() == 1) {
                  String resultStr = results.getResultString();
                  AWorkbench.popup(MessageType.Error, "Transition Failed", resultStr);
               } else {
                  XResultData resultData = results.getResultXResultData();
                  XResultDataUI.report(resultData, "Transition Failed");
               }
            }
         }

      });
   }

   private static boolean isRequireStateHoursSpentPrompt(IAtsStateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.RequireStateHourSpentPrompt.name());
   }

}
