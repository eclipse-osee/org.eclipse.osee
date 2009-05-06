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
package org.eclipse.osee.ats.editor;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.editor.SMAManager.TransitionOption;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;

/**
 * @author Donald G. Dunne
 */
public class SMATaskInfoComposite extends Composite {

   private final SMAManager smaMgr;
   private final Label valueLabel;
   private final Label label;
   private final ArrayList<Label> labelWidgets = new ArrayList<Label>();
   private final IManagedForm managedForm;

   public SMATaskInfoComposite(final SMAManager smaMgr, Composite parent, IManagedForm managedForm, final String forStateName) throws OseeCoreException {
      super(parent, SWT.NONE);
      this.smaMgr = smaMgr;
      this.managedForm = managedForm;
      setLayout(new GridLayout(2, false));
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      Collection<TaskArtifact> taskArts = smaMgr.getTaskMgr().getTaskArtifacts(forStateName);

      label = new Label(this, SWT.NONE);
      labelWidgets.add(label);
      label.setText("\"" + forStateName + "\" State Tasks: ");
      SMAEditor.setLabelFonts(label, SMAEditor.getBoldLabelFont());
      label.setToolTipText("Tasks must be completed before transtion.  Select \"Task\" tab to view tasks");
      // If ATS Admin, allow right-click to auto-complete tasks
      if (AtsPlugin.isAtsAdmin() && !AtsPlugin.isProductionDb()) {
         label.addListener(SWT.MouseUp, new Listener() {
            /* (non-Javadoc)
                         * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
                         */
            @Override
            public void handleEvent(Event event) {
               if (event.button == 3) {
                  if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Auto Complete Tasks",
                        "ATS Admin\n\nAuto Complete Tasks?")) {
                     return;
                  }
                  try {
                     SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
                     for (TaskArtifact taskArt : smaMgr.getTaskMgr().getTaskArtifacts(forStateName)) {
                        if (!taskArt.getSmaMgr().isCancelledOrCompleted()) {
                           if (taskArt.getSmaMgr().getStateMgr().isUnAssigned()) {
                              taskArt.getSmaMgr().getStateMgr().setAssignee(UserManager.getUser());
                           }
                           Result result =
                                 taskArt.getSmaMgr().transitionToCompleted("", transaction,
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
      valueLabel = new Label(this, SWT.NONE);
      if (taskArts.size() > 0) {
         valueLabel.setText(smaMgr.getTaskMgr().getStatus(forStateName));
      } else {
         valueLabel.setText("No Tasks Created");
      }
      if (smaMgr.getTaskMgr().areTasksComplete(forStateName).isFalse()) {
         IMessageManager messageManager = managedForm.getMessageManager();
         if (messageManager != null) {
            messageManager.addMessage("validation.error", "State \"" + forStateName + "\" has uncompleted Tasks", null,
                  IMessageProvider.ERROR, label);
         }
      }
   }

   public void clearFormMessages() {
      for (Label label : labelWidgets) {
         if (Widgets.isAccessible(managedForm.getForm())) {
            managedForm.getMessageManager().removeMessage("validation.error", label);
         }
      }
   }

   @Override
   public String toString() {
      try {
         return "SMATaskInfoComposite for SMA \"" + smaMgr.getSma() + "\"";
      } catch (Exception ex) {
         return "SMATaskInfoComposite " + ex.getLocalizedMessage();
      }
   }

   public void disposeTaskInfoComposite() {
   }

   public String toHTML() throws OseeCoreException {
      return "";
   }

}
