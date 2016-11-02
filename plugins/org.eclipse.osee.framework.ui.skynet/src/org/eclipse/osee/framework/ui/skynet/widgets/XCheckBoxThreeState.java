/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Three state checkbox. Grey = Not set, Check = True, Not-Checked = False.
 *
 * @author Donald G. Dunne
 */
public class XCheckBoxThreeState extends GenericXWidget {

   protected Label checkLabel;
   private Composite parent;
   private boolean labelAfter = true;
   protected CheckState checkState = CheckState.UnSet;

   public XCheckBoxThreeState(String displayLabel) {
      super(displayLabel);
   }

   public static enum CheckState {
      UnSet,
      Checked,
      UnChecked;
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

      // Create Text Widgets
      if (!labelAfter) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
      }

      checkLabel = new Label(parent, SWT.PUSH);
      GridData gd2 = new GridData(GridData.BEGINNING);
      checkLabel.setLayoutData(gd2);
      checkLabel.addMouseListener(new MouseListener() {

         @Override
         public void mouseUp(MouseEvent e) {
            if (checkLabel.getImage().equals(ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_TRUE))) {
               checkState = CheckState.UnChecked;
            } else if (checkLabel.getImage().equals(ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_FALSE))) {
               checkState = CheckState.UnSet;
            } else if (checkLabel.getImage().equals(ImageManager.getImage(FrameworkImage.CHECKBOX_CHECK_UNSET))) {
               checkState = CheckState.Checked;
            }
            updateCheckWidget();
            validate();
            notifyXModifiedListeners();
         }

         @Override
         public void mouseDown(MouseEvent e) {
            // do nothing
         }

         @Override
         public void mouseDoubleClick(MouseEvent e) {
            // do nothing
         }
      });

      GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      gd.horizontalSpan = horizontalSpan - 1;

      if (labelAfter) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(getLabel());
      }
      if (getToolTip() != null) {
         labelWidget.setToolTipText(getToolTip());
      }
      checkLabel.setLayoutData(gd);
      updateCheckWidget();
      checkLabel.setEnabled(isEditable());
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
   public void setLabelAfter(boolean labelAfter) {
      this.labelAfter = labelAfter;
   }

}