/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.widget;

import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.client.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.task.TaskStates;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValueBase;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;

/**
 * @author Donald G. Dunne
 */
public class TaskInfoXWidget extends XLabelValueBase {

   private final IStateToken forState;
   private final IManagedForm managedForm;
   private final AbstractTaskableArtifact taskableArt;

   public TaskInfoXWidget(IManagedForm managedForm, final AbstractTaskableArtifact taskableArt, final IStateToken forState, Composite composite, int horizontalSpan) {
      super("\"" + forState.getName() + "\" State Tasks");
      this.managedForm = managedForm;
      this.taskableArt = taskableArt;
      this.forState = forState;
      setToolTip("Tasks must be completed before transtion.  Select \"Task\" tab to view tasks");
      setFillHorizontally(true);
      createWidgets(managedForm, composite, horizontalSpan);
      addAdminRightClickOption();
   }

   @Override
   public String toString() {
      try {
         return "TaskInfoXWidget for SMA \"" + taskableArt + "\"";
      } catch (Exception ex) {
         return "TaskInfoXWidget " + ex.getLocalizedMessage();
      }
   }

   @Override
   public void refresh() {
      if (labelWidget == null || labelWidget.isDisposed() || managedForm == null || managedForm.getForm() == null || managedForm.getForm().isDisposed()) {
         dispose();
      }
      try {
         if (taskableArt.getTaskArtifacts(forState).size() > 0) {
            setValueText(getStatus(taskableArt, forState));
         } else {
            setValueText("No Tasks Created");
         }
         if (taskableArt.areTasksComplete(forState).isFalse()) {
            IMessageManager messageManager = managedForm.getMessageManager();
            if (messageManager != null) {
               messageManager.addMessage("validation.error",
                  "State \"" + forState.getName() + "\" has uncompleted Tasks", null, IMessageProvider.ERROR,
                  labelWidget);
            }
         } else {
            if (Widgets.isAccessible(managedForm.getForm())) {
               managedForm.getMessageManager().removeMessage("validation.error", labelWidget);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private String getStatus(AbstractTaskableArtifact taskableArt, IStateToken state) throws OseeCoreException {
      int completed = 0, cancelled = 0, inWork = 0;
      for (TaskArtifact taskArt : taskableArt.getTaskArtifacts(state)) {
         if (taskArt.isCompleted()) {
            completed++;
         } else if (taskArt.isCancelled()) {
            cancelled++;
         } else {
            inWork++;
         }
      }
      return String.format("Total: %d - InWork: %d - Completed: %d - Cancelled: %d",
         taskableArt.getTaskArtifacts(state).size(), inWork, completed, cancelled);
   }

   public void addAdminRightClickOption() {
      try {
         // If ATS Admin, allow right-click to auto-complete tasks
         if (AtsUtilClient.isAtsAdmin() && !AtsUtil.isProductionDb()) {
            labelWidget.addListener(SWT.MouseUp, new Listener() {
               @Override
               public void handleEvent(Event event) {
                  if (event.button == 3) {
                     if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Auto Complete Tasks",
                        "ATS Admin\n\nAuto Complete Tasks?")) {
                        return;
                     }
                     try {
                        AtsChangeSet changes = new AtsChangeSet("ATS Auto Complete Tasks");
                        for (TaskArtifact taskArt : taskableArt.getTaskArtifacts(forState)) {
                           if (!taskArt.isCompletedOrCancelled()) {
                              if (taskArt.getStateMgr().isUnAssigned()) {
                                 taskArt.getStateMgr().setAssignee(
                                    AtsClientService.get().getUserAdmin().getCurrentUser());
                              }
                              TransitionHelper helper =
                                 new TransitionHelper("Transition to Completed", Arrays.asList(taskArt),
                                    TaskStates.Completed.getName(), null, null,
                                    changes, TransitionOption.OverrideTransitionValidityCheck, TransitionOption.None);
                              TransitionManager transitionMgr = new TransitionManager(helper);
                              TransitionResults results = transitionMgr.handleAll();
                              if (!results.isEmpty()) {
                                 AWorkbench.popup(String.format("Transition Error %s", results.toString()));
                                 return;
                              }
                           }
                        }
                        changes.execute();
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

}
