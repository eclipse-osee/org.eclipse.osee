/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.util.Arrays;
import java.util.HashMap;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TaskStates;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.ats.ide.workdef.StateColorToSwtColor;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXLabelValueWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXTaskInfoWfeWidget extends XXLabelValueWidget {

   public static WidgetId ID = WidgetIdAts.XXTaskInfoWfeWidget;

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;
   private final Composite comp;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXTaskInfoWfeWidget() {
      this(null, null);
      remove(XOption.VAL_SINGLE_SEL);
      set(XOption.VAL_NOT_APPLICABLE);
   }

   public XXTaskInfoWfeWidget(final TeamWorkFlowArtifact teamWf, Composite comp) {
      super(ID, "Tasks", "");
      this.comp = comp;
      if (teamWf != null) {
         this.xxWid = XXWorkItemData.get((Artifact) teamWf);
      }
      setToolTip("Tasks must be completed before transtion.  Select \"Task\" tab to view tasks");
      if (Widgets.isAccessible(comp)) {
         createControls(comp, 6);
      }
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createLabelWidget() {
      super.createLabelWidget();
      addAdminRightClickOption();
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public void addAdminRightClickOption() {
      try {
         final TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) xxWid.getTeamWf();
         // If ATS Admin, allow right-click to auto-complete tasks
         if (AtsApiService.get().getUserService().isAtsAdmin() && !AtsApiService.get().getStoreService().isProductionDb()) {
            labelWidget.addListener(SWT.MouseUp, new Listener() {
               @Override
               public void handleEvent(Event event) {
                  if (event.button == 3) {
                     if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Auto Complete Tasks",
                        "ATS Admin\n\nAuto Complete Tasks?")) {
                        return;
                     }
                     try {
                        for (IAtsTask task : AtsApiService.get().getTaskService().getTasks(teamWf)) {
                           TransitionData transData = new TransitionData("Transition to Completed", Arrays.asList(task),
                              TaskStates.Completed.getName(), null, null, null,
                              TransitionOption.OverrideTransitionValidityCheck, TransitionOption.OverrideAssigneeCheck,
                              TransitionOption.None);
                           TransitionResults results =
                              AtsApiService.get().getWorkItemServiceIde().transition(transData);
                           if (!results.isEmpty()) {
                              AWorkbench.popup(String.format("Transition Error %s", results.toString()));
                              return;
                           }
                        }
                     } catch (OseeCoreException ex) {
                        OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                     }
                  }
               }
            });
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

   @Override
   protected void initializeSelected() {
      HashMap<String, Integer> countingMap = new HashMap<String, Integer>();
      HashMap<String, Integer> colorMap = new HashMap<String, Integer>();
      for (IAtsTask task : xxWid.getTasks()) {
         if (task.getStateDefinition() == null) {
            continue;
         }
         String currTaskName = task.getStateDefinition().getName();
         if (countingMap.containsKey(currTaskName)) {
            countingMap.put(currTaskName, countingMap.get(currTaskName) + 1);
         } else {
            countingMap.put(currTaskName, 1);
            StateColor currColor = TeamState.resolveStateColor(task.getStateDefinition());
            colorMap.put(currTaskName, StateColorToSwtColor.convert(currColor));
         }
      }
      String label = String.format(" Total: %d", xxWid.getTasks().size());
      setSelected(label, false);
      for (String key : countingMap.keySet()) {
         Label lab = new Label(comp, SWT.NONE);
         lab.setText("\"" + key + "\"" + ": " + countingMap.get(key) + " ");
         lab.setForeground(Displays.getSystemColor(colorMap.get(key)));
      }
   }

}
