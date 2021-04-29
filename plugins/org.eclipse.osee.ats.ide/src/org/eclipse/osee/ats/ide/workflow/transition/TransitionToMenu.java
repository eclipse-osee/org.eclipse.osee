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

package org.eclipse.osee.ats.ide.workflow.transition;

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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelperAdapter;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.util.widgets.dialog.CancelledReasonEnumDialog;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench.MessageType;
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
      final ITransitionHelper helper = new TransitionHelperAdapter(AtsApiService.get()) {

         @Override
         public String getToStateName() {
            return toStateName;
         }

         @Override
         public Collection<AtsUser> getToAssignees(IAtsWorkItem workItem) {
            return workItem.getAssignees();
         }

         @Override
         public String getName() {
            return "Transition-To " + getToStateName();
         }

         @Override
         public TransitionData getCancellationReason(TransitionData transitionData) {
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  IAtsWorkItem workItem = getWorkItems().iterator().next();
                  IAtsStateDefinition stateDef = null;
                  try {
                     stateDef = AtsApiService.get().getWorkDefinitionService().getStateDefinitionByName(workItem,
                        getToStateName());
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
                  if (stateDef != null && stateDef.getStateType().isCancelledState()) {
                     EntryDialog cancelDialog;
                     boolean useEntryCancelWidgetDialog = false;
                     for (IAtsLayoutItem layoutItem : stateDef.getLayoutItems()) {
                        if (layoutItem.getName().contains("Cancel")) {
                           useEntryCancelWidgetDialog = true;
                           break;
                        }
                     }
                     if (useEntryCancelWidgetDialog) {
                        cancelDialog = new CancelledReasonEnumDialog("Cancellation Reason",
                           "Select cancellation reason.  If other, please specify with details in the text entry.");
                     } else {
                        cancelDialog = new EntryDialog("Cancellation Reason", "Enter cancellation reason.");
                     }
                     if (cancelDialog.open() != 0) {
                        transitionData.setDialogCancelled(true);
                     }
                     if (useEntryCancelWidgetDialog) {
                        transitionData.setCancellationReason(((CancelledReasonEnumDialog) cancelDialog).getEntry());
                        transitionData.setCancellationReasonDetails(
                           ((CancelledReasonEnumDialog) cancelDialog).getCancelledDetails());
                     } else {
                        transitionData.setCancellationReason(cancelDialog.getEntry());
                     }
                  }
               }

            }, true);
            return transitionData;
         }

         @Override
         public Collection<IAtsWorkItem> getWorkItems() {
            return workItems;
         }

         @Override
         public IAtsChangeSet getChangeSet() {
            return null;
         }

         @Override
         public Collection<IAtsTransitionHook> getTransitionListeners() {
            try {
               Set<IAtsTransitionHook> listeners = new HashSet<>();
               listeners.addAll(AtsApiService.get().getWorkItemService().getTransitionHooks());
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
            if (results.isErrors()) {
               AtsUtilClient.logExceptions(results);
               if (helper.getWorkItems().size() == 1) {
                  String resultStr = results.getResultString();
                  AWorkbench.popup(MessageType.Error, "Transition Failed", resultStr);
               } else {
                  TransitionResultsUi.reportDialog("Transition Failed", results);
               }
            }
         }

      });
   }

}
