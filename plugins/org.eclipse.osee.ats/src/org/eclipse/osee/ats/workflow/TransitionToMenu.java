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
package org.eclipse.osee.ats.workflow;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelperAdapter;
import org.eclipse.osee.ats.core.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionToOperation;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.dialog.SMAStatusDialog;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.WorkPageType;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench.MessageType;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class TransitionToMenu {
   public static MenuManager createTransitionToMenuManager(final XViewer xViewer, String name, final Collection<TreeItem> selectedTreeItems) {
      MenuManager editMenuManager =
         new MenuManager(name, ImageManager.getImageDescriptor(AtsImage.TRANSITION), "transition-to");
      final Set<AbstractWorkflowArtifact> awas = new HashSet<AbstractWorkflowArtifact>();
      Set<StateDefinition> toStateDefs = new HashSet<StateDefinition>();
      for (TreeItem treeItem : selectedTreeItems) {
         if (treeItem.getData() instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) treeItem.getData();
            awas.add(awa);
            toStateDefs.addAll(awa.getStateDefinition().getToStates());
         }
      }
      if (toStateDefs.isEmpty()) {
         editMenuManager.add(new Action("No Transitionable Selections",
            ImageManager.getImageDescriptor(AtsImage.TRANSITION)) {

            @Override
            public void run() {
               AWorkbench.popup("Error", "No selection is in a transitionable state or transitionable together.");
            }

         });
      } else {
         Map<String, StateDefinition> nameToState = new HashMap<String, StateDefinition>();
         for (final StateDefinition stateDef : toStateDefs) {
            nameToState.put(stateDef.getPageName(), stateDef);
         }
         String[] toStates = nameToState.keySet().toArray(new String[nameToState.size()]);
         Arrays.sort(toStates);
         for (String toState : toStates) {
            final StateDefinition stateDef = nameToState.get(toState);
            editMenuManager.add(new Action(getTransitionToString(stateDef),
               ImageManager.getImageDescriptor(AtsImage.TRANSITION)) {

               @Override
               public void run() {
                  handleTransitionToSelected(stateDef.getName(), awas);
               }

            });
         }
      }
      return editMenuManager;
   }

   private static String getTransitionToString(StateDefinition stateDef) {
      return stateDef.getPageName() + (stateDef.getWorkPageType() == WorkPageType.Working || stateDef.getPageName().equals(
         TeamState.Completed.getPageName()) || stateDef.getPageName().equals(TeamState.Cancelled.getPageName()) ? "" : " (" + stateDef.getWorkPageType().name() + ")");
   }

   private static void handleTransitionToSelected(final String toStateName, final Set<AbstractWorkflowArtifact> awas) {
      final ITransitionHelper helper = new TransitionHelperAdapter() {

         @Override
         public Result handleExtraHoursSpent() {
            final Result result = new Result(true, "");
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  SMAStatusDialog tsd =
                     new SMAStatusDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Enter Hours Spent", "Enter additional hours spent in current state(s)", false, getAwas());
                  int dialogResult = tsd.open();
                  if (dialogResult == 0) {
                     try {
                        SMAPromptChangeStatus.performChangeStatus(awas, null, null, tsd.getHours().getFloat(),
                           tsd.getPercent().getInt(), tsd.isSplitHours(), true);
                     } catch (OseeCoreException ex) {
                        OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                        result.set(false);
                        result.setTextWithFormat(
                           "Exception handling extra hours spent for transition to [%s] (see log)", getToStateName());
                     }
                  } else {
                     result.setCancelled(true);
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
         public Collection<User> getToAssignees() {
            return Collections.emptyList();
         }

         @Override
         public String getName() {
            return "Transition-To " + getToStateName();
         }

         @Override
         public Result getCompleteOrCancellationReason() {
            AbstractWorkflowArtifact awa = getAwas().iterator().next();
            StateDefinition stateDef = awa.getStateDefinitionByName(getToStateName());
            Result result = new Result(false, "");
            if (stateDef.isCancelledPage()) {
               EntryDialog dialog = new EntryDialog("Enter Cancellation Reason", "Enter Cancellation Reason");
               if (dialog.open() != 0) {
                  result.setCancelled(true);
               } else {
                  result.setText(dialog.getEntry());
                  result.set(true);
               }
            }
            return result;
         }

         @Override
         public Collection<AbstractWorkflowArtifact> getAwas() {
            return awas;
         }
      };
      final TransitionToOperation operation = new TransitionToOperation(helper);
      Operations.executeAsJob(operation, true, Job.SHORT, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            TransitionResults results = operation.getResults();
            if (!results.isEmpty()) {
               results.logExceptions();
               if (helper.getAwas().size() == 1) {
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
}
