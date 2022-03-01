/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.ats.ide.actions;

import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Bhawana Mishra
 */
public class AbstractWfeSubWorkflow extends Composite {
   protected IAtsWorkItem workItem;
   private Hyperlink labelLink;
   private Hyperlink reasonLink;

   private final String positive;
   private final String negative;
   private final AttributeTypeToken attrType;
   private final int color;
   private Label iconLabel;
   private final KeyedImage image;

   protected AbstractWfeSubWorkflow(Composite parent, int style, IAtsWorkItem workItem, WorkflowEditor editor, String positive, //
      String negative, AttributeTypeToken attrType, int color, KeyedImage image, String tooltip) {
      super(parent, style);
      this.workItem = workItem;
      this.image = image;
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setLayout(ALayout.getZeroMarginLayout(5, false));
      editor.getToolkit().adapt(this);
      this.workItem = workItem;
      this.positive = positive;
      this.negative = negative;
      this.attrType = attrType;
      this.color = color;
      try {

         labelLink = editor.getToolkit().createHyperlink(this, "", SWT.NONE);
         labelLink.setToolTipText(tooltip);
         labelLink.addHyperlinkListener(new IHyperlinkListener() {

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
                  handlePositiveButtonSelection();
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         iconLabel = editor.getToolkit().createLabel(this, "");

         reasonLink = editor.getToolkit().createHyperlink(this, "", SWT.NONE);
         reasonLink.addHyperlinkListener(new IHyperlinkListener() {

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
                  EntryDialog ed = new EntryDialog("View Or Change Reason for Workflow about " + positive,
                     "Current " + positive + " Reason: " + getReason() + "\n\n\nEnter the new reason for setting this workflow to " + positive + " if you wish to change");
                  setReason(ed);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

      } catch (OseeCoreException ex) {
         labelLink.setText("Error: " + ex.getLocalizedMessage());
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      refresh();
   }

   public void refresh() {
      String reason = "";
      String label = "";
      try {
         reason = getReason();
         if (isNegative()) {
            label = "Remove " + positive;
         } else {
            label = "Set Workflow to " + positive;
         }
      } catch (OseeCoreException ex) {
         reason = "Error: " + ex.getLocalizedMessage();
      }

      labelLink.setText(label);
      labelLink.setLayoutData(new GridData());

      String text = "Reason: " + reason;
      text = Strings.truncate(text, 60, true);
      reasonLink.setText(text);
      reasonLink.setToolTipText(reason);

      boolean showReason = Strings.isValid(reason);
      reasonLink.setVisible(showReason);
      if (showReason) {
         reasonLink.setForeground(Displays.getSystemColor(color));
         iconLabel.setImage(ImageManager.getImage(image));
      } else {
         reasonLink.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
         iconLabel.setImage(null);
      }
      reasonLink.setLayoutData(new GridData());

      layout();
   }

   private String getReason() {
      return AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem, attrType, "");
   }

   private boolean isNegative() {
      return Strings.isValid(getReason());
   }

   private void handlePositiveButtonSelection() {
      if (isNegative()) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set " + positive + " status");
         boolean unblock = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            negative + " Workflow ", "Are you sure you wish to set this workflow to " + negative + "?");
         if (unblock) {
            changes.deleteSoleAttribute(workItem, attrType);
         }
         changes.executeIfNeeded();
      } else {
         EntryDialog ed =
            new EntryDialog("Setting Workflow to " + positive, " Enter the reason to set this workflow to " + positive);
         setReason(ed);
      }
   }

   private void setReason(EntryDialog ed) {
      String Reason = "";
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set " + positive + " reason");
      if (ed.open() == 0) {
         Reason = ed.getEntry();
         if (!Strings.isValid(Reason)) {
            Reason = getReason();
         }
         changes.setSoleAttributeValue(workItem, attrType, Reason);
      }
      changes.executeIfNeeded();
   }

}
