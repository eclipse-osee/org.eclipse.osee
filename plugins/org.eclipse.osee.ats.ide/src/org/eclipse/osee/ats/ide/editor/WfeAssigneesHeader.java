/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor;

import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeAssigneesHeader extends Composite {

   private final static String TARGET_VERSION = "Assignee(s):";
   Label valueLabel;

   public WfeAssigneesHeader(Composite parent, int style, final AbstractWorkflowArtifact sma, final boolean isEditable, final WorkflowEditor editor) {
      super(parent, style);
      setLayoutData(new GridData());
      setLayout(ALayout.getZeroMarginLayout(2, false));
      editor.getToolkit().adapt(this);

      if (!sma.isCancelled() && !sma.isCompleted()) {
         Hyperlink link = editor.getToolkit().createHyperlink(this, TARGET_VERSION, SWT.NONE);
         link.addHyperlinkListener(new IHyperlinkListener() {

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
                  if (editor.isDirty()) {
                     editor.doSave(null);
                  }
                  if (!isEditable && !sma.getStateMgr().getAssignees().contains(
                     AtsCoreUsers.UNASSIGNED_USER) && !sma.getStateMgr().getAssignees().contains(
                        AtsClientService.get().getUserService().getCurrentUser())) {
                     AWorkbench.popup("ERROR",
                        "You must be assigned to modify assignees.\nContact current Assignee or Select Privileged Edit for Authorized Overriders.");
                     return;
                  }
                  if (AssigneeColumnUI.promptChangeAssignees(sma, false)) {
                     editor.doSave(null);
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
      } else {
         Label origLabel = editor.getToolkit().createLabel(this, TARGET_VERSION);
         origLabel.setLayoutData(new GridData());
      }
      valueLabel = editor.getToolkit().createLabel(this, "Not Set");
      valueLabel.setLayoutData(new GridData());
      updateLabel(sma);

   }

   private void updateLabel(AbstractWorkflowArtifact sma) {
      String value = "";
      try {
         if (sma.getStateMgr().getAssignees().isEmpty()) {
            value = "Error: State has no assignees";
         } else {
            valueLabel.setToolTipText(sma.getStateMgr().getAssigneesStr());
            value = sma.getStateMgr().getAssigneesStr();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         value = ex.getLocalizedMessage();
         valueLabel.setToolTipText(value);
      }
      valueLabel.setText(Strings.truncate(value, 150, true));
      valueLabel.getParent().layout();
   }

}
