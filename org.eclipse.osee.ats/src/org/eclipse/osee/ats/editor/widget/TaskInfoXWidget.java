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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact.TransitionOption;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValue;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;

/**
 * @author Donald G. Dunne
 */
public class TaskInfoXWidget extends XLabelValue implements IFrameworkTransactionEventListener {

   private final String forStateName;
   private final IManagedForm managedForm;
   private final TaskableStateMachineArtifact taskableArt;

   public TaskInfoXWidget(IManagedForm managedForm, final TaskableStateMachineArtifact taskableArt, final String forStateName, Composite composite, int horizontalSpan) {
      super("\"" + forStateName + "\" State Tasks");
      this.managedForm = managedForm;
      this.taskableArt = taskableArt;
      this.forStateName = forStateName;
      setToolTip("Tasks must be completed before transtion.  Select \"Task\" tab to view tasks");
      setFillHorizontally(true);
      OseeEventManager.addListener(this);
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
      if (labelWidget == null || !labelWidget.isDisposed() || managedForm == null || managedForm.getForm() == null || managedForm.getForm().isDisposed()) {
         dispose();
      }
      try {
         if (taskableArt.getTaskArtifacts(forStateName).size() > 0) {
            setValueText(taskableArt.getStatus(forStateName));
         } else {
            setValueText("No Tasks Created");
         }
         if (taskableArt.areTasksComplete(forStateName).isFalse()) {
            IMessageManager messageManager = managedForm.getMessageManager();
            if (messageManager != null) {
               messageManager.addMessage("validation.error", "State \"" + forStateName + "\" has uncompleted Tasks",
                     null, IMessageProvider.ERROR, labelWidget);
            }
         } else {
            if (Widgets.isAccessible(managedForm.getForm())) {
               managedForm.getMessageManager().removeMessage("validation.error", labelWidget);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
      if (taskableArt.isInTransition()) {
         return;
      }
      if (transData.branchId != AtsUtil.getAtsBranch().getId()) {
         return;
      }
      for (TaskArtifact taskArt : taskableArt.getTaskArtifacts(forStateName)) {
         if (transData.isHasEvent(taskArt)) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  refresh();
               }
            });
         }
      }
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   public void addAdminRightClickOption() {
      try {
         // If ATS Admin, allow right-click to auto-complete tasks
         if (AtsUtil.isAtsAdmin() && !AtsUtil.isProductionDb()) {
            labelWidget.addListener(SWT.MouseUp, new Listener() {
               @Override
               public void handleEvent(Event event) {
                  if (event.button == 3) {
                     if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Auto Complete Tasks",
                           "ATS Admin\n\nAuto Complete Tasks?")) {
                        return;
                     }
                     try {
                        SkynetTransaction transaction =
                              new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Auto Complete Tasks");
                        for (TaskArtifact taskArt : taskableArt.getTaskArtifacts(forStateName)) {
                           if (!taskArt.isCancelledOrCompleted()) {
                              if (taskArt.getStateMgr().isUnAssigned()) {
                                 taskArt.getStateMgr().setAssignee(UserManager.getUser());
                              }
                              Result result =
                                    taskArt.transitionToCompleted("", transaction,
                                          TransitionOption.OverrideTransitionValidityCheck, TransitionOption.Persist);
                              if (result.isFalse()) {
                                 result.popup();
                                 return;
                              }
                           }
                        }
                        transaction.execute();
                     } catch (OseeCoreException ex) {
                        OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                     }
                  }
               }
            });
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
