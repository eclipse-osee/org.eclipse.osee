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

import java.util.logging.Level;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.AbstractTaskableArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.TransitionOption;
import org.eclipse.osee.ats.workflow.TransitionManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValueBase;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
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

   private final IWorkPage forState;
   private final IManagedForm managedForm;
   private final AbstractTaskableArtifact taskableArt;

   public TaskInfoXWidget(IManagedForm managedForm, final AbstractTaskableArtifact taskableArt, final IWorkPage forState, Composite composite, int horizontalSpan) {
      super("\"" + forState.getPageName() + "\" State Tasks");
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
                  "State \"" + forState.getPageName() + "\" has uncompleted Tasks", null, IMessageProvider.ERROR,
                  labelWidget);
            }
         } else {
            if (Widgets.isAccessible(managedForm.getForm())) {
               managedForm.getMessageManager().removeMessage("validation.error", labelWidget);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private String getStatus(AbstractTaskableArtifact taskableArt, IWorkPage state) throws OseeCoreException {
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
         if (AtsUtil.isAtsAdmin() && !AtsUtil.isProductionDb()) {
            labelWidget.addListener(SWT.MouseUp, new Listener() {
               @Override
               public void handleEvent(Event event) {
                  if (event.button == 3) {
                     if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Auto Complete Tasks",
                        "ATS Admin\n\nAuto Complete Tasks?")) {
                        return;
                     }
                     try {
                        SkynetTransaction transaction =
                           new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Auto Complete Tasks");
                        for (TaskArtifact taskArt : taskableArt.getTaskArtifacts(forState)) {
                           if (!taskArt.isCompletedOrCancelled()) {
                              if (taskArt.getStateMgr().isUnAssigned()) {
                                 taskArt.getStateMgr().setAssignee(UserManager.getUser());
                              }
                              TransitionManager transitionMgr = new TransitionManager(taskArt);
                              Result result =
                                 transitionMgr.transitionToCompleted("", transaction,
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
