/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.logging.Level;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.column.TargetedVersionColumnUI;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeTargetedVersionHeader extends Composite {

   private final static String TARGET_VERSION = "Target Version:";
   private final static String TARGET_VERSION_POPUP =
      "This Team Workflow has a Working Branch based on the current Targeted Version.  Changing this version will commit changes to the Branch associated with the new Version.  Are you sure?";
   Label valueLabel;
   Hyperlink link;
   private final IAtsTeamWorkflow teamWf;
   private final WorkflowEditor editor;

   public WfeTargetedVersionHeader(Composite parent, int style, final IAtsTeamWorkflow teamWf, final WorkflowEditor editor) {
      super(parent, style);
      this.teamWf = teamWf;
      this.editor = editor;
      setLayoutData(new GridData());
      setLayout(ALayout.getZeroMarginLayout(2, false));
      editor.getToolkit().adapt(this);

      try {
         link = editor.getToolkit().createHyperlink(this, TARGET_VERSION, SWT.NONE);
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
               if (editor.isDirty()) {
                  editor.doSave(null);
               }
               promptChangeVersion();
            }
         });

         valueLabel = editor.getToolkit().createLabel(this, Widgets.NOT_SET);
         valueLabel.setLayoutData(new GridData());
         refresh();
      } catch (OseeCoreException ex) {
         Label errorLabel = editor.getToolkit().createLabel(this, "Error: " + ex.getLocalizedMessage());
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   public boolean promptChangeVersion() {
      return promptChangeVersion(editor, teamWf);
   }

   public static boolean promptChangeVersion(final IAtsTeamWorkflow teamWf) {
      WorkflowEditor editor = WorkflowEditor.getWorkflowEditor(teamWf);
      return promptChangeVersion(editor, teamWf);
   }

   public static boolean promptChangeVersion(WorkflowEditor editor, final IAtsTeamWorkflow teamWf) {
      try {

         if (editor.isDirty()) {
            editor.doSave(null);
         }
         if (AtsApiService.get().getBranchService().isWorkingBranchInWork(teamWf)) {
            if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Warning: Changing target branch",
               TARGET_VERSION_POPUP)) {
               return false;
            }
         }
         if (TargetedVersionColumnUI.getInstance().promptChangeVersion((TeamWorkFlowArtifact) teamWf,
            AtsApiService.get().getUserService().isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
            VersionLockedType.UnLocked)) {

            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public void refresh() {
      if (Widgets.isAccessible(link)) {
         String value = Widgets.NOT_SET;
         if (AtsApiService.get().getVersionService().hasTargetedVersion(teamWf)) {
            value = AtsApiService.get().getVersionService().getTargetedVersion(teamWf).getName();
         }
         valueLabel.setText(value);
         valueLabel.getParent().getParent().layout();

         if (teamWf.getWorkDefinition().hasOption(WorkDefOption.RequireTargetedVersion)) {
            IManagedForm managedForm = editor.getWorkFlowTab().getManagedForm();
            if (managedForm != null && !managedForm.getForm().isDisposed()) {
               IMessageManager messageManager = managedForm.getMessageManager();
               if (Widgets.NOT_SET.equals(value)) {
                  messageManager.addMessage(link, "Must Select Targeted Version", null, IMessageProvider.ERROR, link);
               } else {
                  messageManager.removeMessages(link);
               }
            }
         }
      }
   }

   @Override
   public void setBackground(Color color) {
      super.setBackground(color);
      if (Widgets.isAccessible(valueLabel)) {
         valueLabel.setBackground(color);
      }
      if (Widgets.isAccessible(link)) {
         link.setBackground(color);
      }
   }

}
