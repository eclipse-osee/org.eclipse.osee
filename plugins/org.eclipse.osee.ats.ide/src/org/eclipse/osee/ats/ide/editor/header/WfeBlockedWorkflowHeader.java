/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.header;

import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.editor.IWfeEventHandle;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
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
   private Hyperlink blockedStatusLink;
   private String blockedText;
   private Hyperlink blockedReasonLink;
   private String blockedReason;
   private final static Color BLOCKED_COLOR = new Color(null, 244, 80, 66);

   WfeBlockedWorkflowHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setLayout(ALayout.getZeroMarginLayout(2, false));
      editor.getToolkit().adapt(this);

      try {
         blockedReason = AtsClientService.get().getAttributeResolver().getSoleAttributeValue(workItem,
            AtsAttributeTypes.BlockedReason, "");

         if (Strings.isValid(blockedReason)) {
            blockedText = "Set Workflow to Unblocked:";
         } else {
            blockedText = "Set Workflow to Blocked";
         }

         blockedStatusLink = editor.getToolkit().createHyperlink(this, blockedText, SWT.NONE);
         blockedStatusLink.addHyperlinkListener(new IHyperlinkListener() {

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

         blockedReasonLink = editor.getToolkit().createHyperlink(this, blockedReason, SWT.NONE);
         blockedReasonLink.setForeground(BLOCKED_COLOR);
         blockedReasonLink.setVisible(Strings.isValid(blockedReason));
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
         blockedStatusLink.setText("Error: " + ex.getLocalizedMessage());
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   private void handleBlockedButtonSelection() {
      if (!Strings.isValid(blockedReason)) {
         EntryDialog ed =
            new EntryDialog("Setting Workflow to Blocked", "Enter the reason for this workflow being blocked");
         setBlockedReason(ed);
      } else {
         IAtsChangeSet changes = AtsClientService.get().createChangeSet("Set blocked status");
         boolean unblock = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "Unblock Workflow", "Are you sure you wish to set this workflow to unblocked?");
         if (unblock) {
            changes.deleteSoleAttribute(workItem, AtsAttributeTypes.BlockedReason);
         }
         changes.executeIfNeeded();
      }
   }

   private void setBlockedReason(EntryDialog ed) {
      String blockedReason = "";
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Set blocked reason");
      if (ed.open() == 0) {
         blockedReason = ed.getEntry();
         if (!Strings.isValid(blockedReason)) {
            blockedReason = "No reason given, please enter one";
         }
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.BlockedReason, "Blocked - " + blockedReason);
      }
      changes.executeIfNeeded();
   }

   @Override
   public void refresh() {
      //do nothing
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return null;
   }
}
