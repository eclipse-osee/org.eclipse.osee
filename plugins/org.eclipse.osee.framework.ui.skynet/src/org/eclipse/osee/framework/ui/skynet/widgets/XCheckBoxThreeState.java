/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Three state checkbox. Grey = Not set, Check = True, Not-Checked = False.
 *
 * @author Donald G. Dunne
 */
public class XCheckBoxThreeState extends GenericXWidget implements LabelAfterWidget {

   public static String WIDGET_ID = XCheckBoxThreeState.class.getSimpleName();
   protected Label checkLabel;
   private Composite parent;
   private boolean labelAfter = true;
   protected CheckState checkState = CheckState.UnSet;
   private Composite composite;

   public XCheckBoxThreeState(String displayLabel) {
      super(displayLabel);
   }

   public static enum CheckState {
      UnSet,
      Checked,
      UnChecked;

      public boolean isUnSet() {
         return this == CheckState.UnSet;
      }

      public boolean isChecked() {
         return this == CheckState.Checked;
      }

      public boolean isUnChecked() {
         return this == CheckState.UnChecked;
      }
   }

   @Override
   public Control getControl() {
      return checkLabel;
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (getControl() != null && !getControl().isDisposed()) {
         getControl().setEnabled(editable);
      }
   }

   /**
    * Create Check Widgets. Widgets Created: Label: "text entry" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }
      this.parent = parent;

      this.parent = parent;
      if (fillVertically) {
         composite = new Composite(parent, SWT.NONE);
         GridLayout layout = ALayout.getZeroMarginLayout(1, false);
         composite.setLayout(layout);
         composite.setLayoutData(new GridData());
      } else {
         composite = new Composite(parent, SWT.NONE);
         GridLayout layout = ALayout.getZeroMarginLayout(horizontalSpan, false);
         composite.setLayout(layout);
         GridData gd = new GridData();
         gd.horizontalSpan = horizontalSpan;
         composite.setLayoutData(gd);
      }

      // Create Text Widgets
      if (!labelAfter) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
      }

      checkLabel = new Label(composite, SWT.PUSH);
      GridData gd2 = new GridData(GridData.BEGINNING);
      checkLabel.setLayoutData(gd2);
      checkLabel.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            handleSetCheckState();
            updateCheckWidget();
            validate();
            notifyXModifiedListeners();
         }

      });

      GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      gd.horizontalSpan = horizontalSpan - 1;

      if (labelAfter) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(getLabel());
      }
      if (getToolTip() != null) {
         labelWidget.setToolTipText(getToolTip());
      }
      checkLabel.setLayoutData(gd);
      updateCheckWidget();
      checkLabel.setEnabled(isEditable());
   }

   protected void handleSetCheckState() {
      if (checkLabel.getImage().equals(ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_TRUE))) {
         checkState = CheckState.UnChecked;
      } else if (checkLabel.getImage().equals(ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_FALSE))) {
         checkState = CheckState.UnSet;
      } else if (checkLabel.getImage().equals(ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_UNSET))) {
         checkState = CheckState.Checked;
      }
   }

   @Override
   public void dispose() {
      labelWidget.dispose();
      checkLabel.dispose();
      if (parent != null && !parent.isDisposed()) {
         parent.layout();
      }
   }

   public CheckState getCheckState() {
      return checkState;
   }

   protected void updateCheckWidget() {
      if (checkLabel != null && !checkLabel.isDisposed()) {
         checkLabel.setImage(getImage());
      }
      validate();
   }

   private Image getImage() {
      if (checkState == CheckState.Checked) {
         return ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_TRUE);
      } else if (checkState == CheckState.UnChecked) {
         return ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_FALSE);
      } else if (checkState == CheckState.UnSet) {
         return ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_UNSET);
      }
      return null;
   }

   /**
    * If set, label will be displayed after the check box NOTE: Has to be set before call to createWidgets
    *
    * @param labelAfter The labelAfter to set.
    */
   @Override
   public void setLabelAfter(boolean labelAfter) {
      this.labelAfter = labelAfter;
   }

   @Override
   public boolean isLabelAfter() {
      return labelAfter;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getCheckState() == CheckState.UnSet) {
               status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format("Must Select [%s]", getLabel()));
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error Validating " + getLabel(), ex);
         }
      }
      return status;
   }

   public void setCheckState(CheckState checked) {
      checkState = checked;
      updateCheckWidget();
   }

}