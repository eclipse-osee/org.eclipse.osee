/*********************************************************************
 * Copyright (c) 2019 Boeing
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.event.IWfeEventHandle;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Branden W. Phillips
 */
public class WfeBlockedWorkflowHeader extends Composite implements IWfeEventHandle {

   private final IAtsWorkItem workItem;
   private Hyperlink blockedLabelLink;
   private Hyperlink blockedReasonLink;

   WfeBlockedWorkflowHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setLayout(ALayout.getZeroMarginLayout(2, false));
      editor.getToolkit().adapt(this);

      try {
         blockedLabelLink = editor.getToolkit().createHyperlink(this, "", SWT.NONE);
         blockedLabelLink.addHyperlinkListener(new IHyperlinkListener() {

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
                  handleBlockedButtonSelection();
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         blockedReasonLink = editor.getToolkit().createHyperlink(this, "", SWT.NONE);
         blockedReasonLink.addHyperlinkListener(new IHyperlinkListener() {

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
                  EntryDialog ed = new EntryDialog("Change Reason for Workflow Blockage",
                     "Enter the new reason for this workflow being blocked");
                  setBlockedReason(ed);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

      } catch (OseeCoreException ex) {
         blockedLabelLink.setText("Error: " + ex.getLocalizedMessage());
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      refresh();
      editor.registerEvent(this, AtsAttributeTypes.BlockedReason);
   }

   @Override
   public void refresh() {
      String blockedReason = "";
      String blockedLabel = "";
      try {
         blockedReason = getBlockedReason();
         if (isBlocked()) {
            blockedLabel = "Set Workflow to Unblocked";
         } else {
            blockedLabel = "Set Workflow to Blocked";
         }
      } catch (OseeCoreException ex) {
         blockedReason = "Error: " + ex.getLocalizedMessage();
      }

      blockedLabelLink.setText(blockedLabel);
      blockedLabelLink.setLayoutData(new GridData());

      blockedReasonLink.setText("Reason: " + blockedReason);
      boolean showBlockedReason = Strings.isValid(blockedReason);
      blockedReasonLink.setVisible(showBlockedReason);
      if (showBlockedReason) {
         blockedReasonLink.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      } else {
         blockedReasonLink.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
      }
      blockedReasonLink.setLayoutData(new GridData());

      layout();
   }

   private String getBlockedReason() {
      return AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem,
         AtsAttributeTypes.BlockedReason, "");
   }

   private boolean isBlocked() {
      return Strings.isValid(getBlockedReason());
   }

   private void handleBlockedButtonSelection() {
      if (isBlocked()) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set blocked status");
         boolean unblock = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "Unblock Workflow", "Are you sure you wish to set this workflow to unblocked?");
         if (unblock) {
            changes.deleteSoleAttribute(workItem, AtsAttributeTypes.BlockedReason);
         }
         changes.executeIfNeeded();
      } else {
         EntryDialog ed =
            new EntryDialog("Setting Workflow to Blocked", "Enter the reason for this workflow being blocked");
         setBlockedReason(ed);
      }
   }

   private void setBlockedReason(EntryDialog ed) {
      String blockedReason = "";
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set blocked reason");
      if (ed.open() == 0) {
         blockedReason = ed.getEntry();
         if (!Strings.isValid(blockedReason)) {
            blockedReason = "No reason given, please enter one";
         }
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.BlockedReason, blockedReason);
      }
      changes.executeIfNeeded();
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return workItem;
   }
}
