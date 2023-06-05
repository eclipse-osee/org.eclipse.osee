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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench.MessageType;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class TransitionToMenu {
   public static MenuManager createTransitionToMenuManager(final XViewer xViewer, String name,
      final Set<Artifact> workflowArtifacts) {
      MenuManager editMenuManager =
         new MenuManager(name, ImageManager.getImageDescriptor(AtsImage.TRANSITION), "transition-to");
      final Set<IAtsWorkItem> workItems = new HashSet<>();
      List<StateDefinition> toStateDefs = new ArrayList<>();
      String workDefinitionId = null;
      Map<String, StateDefinition> stateNameToStateDef = new HashMap<>();
      boolean multipleWorkDefinitions = false;
      for (Artifact art : workflowArtifacts) {
         if (art instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) art;
            workItems.add(awa);
            if (!multipleWorkDefinitions) {
               if (workDefinitionId == null) {
                  workDefinitionId = awa.getWorkDefinition().getName();
               } else if (!workDefinitionId.equals(awa.getWorkDefinition().getName())) {
                  multipleWorkDefinitions = true;
               }
            }
            try {
               for (StateDefinition stateDef : AtsApiService.get().getWorkItemService().getAllToStates(awa)) {
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
         for (StateDefinition stateDef : toStateDefs) {
            if (!toStateNames.contains(stateDef.getName())) {
               toStateNames.add(stateDef.getName());
            }
         }
         if (multipleWorkDefinitions) {
            Collections.sort(toStateNames);
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

   private static String getTransitionToString(String stateDefName, boolean multipleWorkDefinitions,
      Map<String, StateDefinition> stateNameToStateDef) {
      if (multipleWorkDefinitions) {
         return stateDefName;
      } else {
         StateDefinition stateDef = stateNameToStateDef.get(stateDefName);
         return String.format("%s%s%s", stateDef.getName(), getStateTypeName(stateDef),
            getDefaultStatePercent(stateDef));
      }
   }

   private static Object getDefaultStatePercent(StateDefinition stateDef) {
      if (stateDef.getRecommendedPercentComplete() != null && stateDef.getRecommendedPercentComplete() != 0) {
         return String.format(" - %d%%", stateDef.getRecommendedPercentComplete());
      }
      return "";
   }

   private static String getStateTypeName(StateDefinition stateDef) {
      return stateDef.isWorking() || stateDef.getName().equals(
         TeamState.Completed.getName()) || stateDef.getName().equals(
            TeamState.Cancelled.getName()) ? "" : " (" + stateDef.getStateType().name() + ")";
   }

   private static void handleTransitionToSelected(final String toStateName, final Set<IAtsWorkItem> workItems) {
      TransitionData transData = new TransitionData("Transition-To " + toStateName, workItems, toStateName, null, null,
         null, TransitionOption.None);
      TransitionDataUi.getCancellationReason(transData);
      final TransitionToOperation operation = new TransitionToOperation(transData);
      Operations.executeAsJob(operation, true, Job.SHORT, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            TransitionResults results = operation.getResults();
            if (results.isErrors()) {
               AtsUtilClient.logExceptions(results);
               if (transData.getWorkItems().size() == 1) {
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
