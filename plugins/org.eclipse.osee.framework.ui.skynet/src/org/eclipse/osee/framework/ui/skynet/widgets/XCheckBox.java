/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class XCheckBox extends XButtonCommon implements LabelAfterWidget {

   public static String WIDGET_ID = XCheckBox.class.getSimpleName();
   protected Button checkButton;
   private Composite parent;
   private boolean labelAfter = true;
   private Composite composite;

   public XCheckBox(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public Control getControl() {
      return checkButton;
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
      if (!verticalLabel && horizontalSpan < 2) {
         horizontalSpan = 2;
      }

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

      checkButton = new Button(composite, SWT.CHECK);
      GridData gd2 = new GridData(GridData.BEGINNING);
      checkButton.setLayoutData(gd2);
      checkButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent event) {
            selected = checkButton.getSelection();
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
      checkButton.setLayoutData(gd);
      updateCheckWidget();
      checkButton.setEnabled(isEditable());
      if (defaultValueObj != null) {
         Object obj = getDefaultValueObj();
         if (obj instanceof Boolean) {
            selected = (Boolean) obj;
            checkButton.setSelection(selected);
         }
      }
   }

   @Override
   public void dispose() {
      labelWidget.dispose();
      checkButton.dispose();
      if (parent != null && !parent.isDisposed()) {
         parent.layout();
      }
   }

   public void addSelectionListener(SelectionListener selectionListener) {
      checkButton.addSelectionListener(selectionListener);
   }

   public boolean isChecked() {
      if (!Displays.isDisplayThread() || checkButton == null || checkButton.isDisposed()) {
         return selected;
      } else {
         return checkButton.getSelection();
      }
   }

   @Override
   protected void updateCheckWidget() {
      if (checkButton != null && !checkButton.isDisposed()) {
         checkButton.setSelection(selected);
      }
      validate();
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

   public Button getCheckButton() {
      return checkButton;
   }

   @Override
   public boolean isLabelAfter() {
      return labelAfter;
   }

}