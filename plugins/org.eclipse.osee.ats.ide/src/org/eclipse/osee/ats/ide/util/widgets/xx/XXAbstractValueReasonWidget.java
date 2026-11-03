/*********************************************************************
 * Copyright (c) 2026 Boeing
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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public abstract class XXAbstractValueReasonWidget extends XXAbstractBaseXWidget<String> {

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;
   private final String positive;
   private final String negative;
   private final OseeImage image;
   private final int color;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAbstractValueReasonWidget(WidgetId widgetId, String label, int color, OseeImage image, String positive, String negative) {
      super(widgetId, label);
      this.color = color;
      this.image = image;
      this.positive = positive;
      this.negative = negative;
      set(XOption.ICON_WIDGET);
      set(XOption.VAL_SINGLE_TEXT);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      labelLinkWidget.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      valueWidget.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      valueWidget.setToolTipText("Select to change reason");
      valueWidget.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            super.mouseUp(e);
            handleValueSelection();
         }

      });
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public boolean handleSelection() {
      if (isNegative()) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set " + positive + " status");
         boolean unblock = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            negative + " Workflow ", "Are you sure you wish to set this workflow to " + negative + "?");
         if (unblock) {
            changes.deleteSoleAttribute(xxWid.getWorkItem(), getAttributeType());
         }
         changes.executeIfNeeded();
         return true;
      } else {
         EntryDialog ed =
            new EntryDialog("Setting Workflow to " + positive, " Enter the reason to set this workflow to " + positive);
         return setReason(ed);
      }
   }

   @Override
   public String getSentinel() {
      return "";
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   public void refresh() {
      if (Widgets.isNotAccessible(comp)) {
         return;
      }
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
      labelLinkWidget.setText(label);

      String text = "Reason: " + reason;
      text = Strings.truncate(text, 60, true);
      valueWidget.setText(text);

      boolean showReason = Strings.isValid(reason);
      valueWidget.setVisible(showReason);
      if (showReason) {
         valueWidget.setForeground(Displays.getSystemColor(color));
         iconLabel.setImage(ImageManager.getImage(image));
      } else {
         valueWidget.setForeground(Displays.getSystemColor(SWT.NONE));
         iconLabel.setImage(null);
      }

      comp.layout();
      validate();
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   public String getSelectedFirst() {
      return getReason();
   }

   private String getReason() {
      if (xxWid.isWorkItem()) {
         return AtsApiService.get().getAttributeResolver().getSoleAttributeValue(xxWid.getWorkItem(),
            getAttributeType(), "");
      }
      return "";
   }

   private boolean isNegative() {
      return Strings.isValid(getReason());
   }

   private void handleValueSelection() {
      EntryDialog ed = new EntryDialog("View Or Change Reason for Workflow about " + positive,
         "Current " + positive + "\n\nEnter the new reason for setting this workflow to " + positive + " if you wish to change");
      ed.setEntry(getReason());
      setReason(ed);
   }

   private boolean setReason(EntryDialog ed) {
      String reason = "";
      if (ed.open() == 0) {
         reason = ed.getEntry();
         if (!Strings.isValid(reason)) {
            reason = getReason();
         }
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set " + positive + " reason");
         changes.setSoleAttributeValue(xxWid.getWorkItem(), getAttributeType(), reason);
         changes.executeIfNeeded();
         return true;
      }
      return false;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
