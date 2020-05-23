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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Generic label and radiobutton field object for use by single entry artifact attributes
 * 
 * @author Donald G. Dunne
 */
public class XRadioButton extends XWidget {

   private Composite parent;
   private boolean selected = false;
   private Button button;
   public static enum ButtonType {
      Check,
      Radio
   };
   private ButtonType buttonType = ButtonType.Radio;
   private boolean labelAfter;

   public XRadioButton(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public Control getControl() {
      return button;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
      if (button != null) {
         button.setSelection(selected);
      }
   }

   @Override
   public String toString() {
      return getLabel() + ": " + selected;
   }

   /**
    * Create radio Widgets. Widgets Created: Label: "text entry" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      this.parent = parent;
      // Create Text Widgets
      if (!isLabelAfter() && isDisplayLabel()) {
         createLabel(parent);
      }

      button = new Button(parent, buttonType == ButtonType.Check ? SWT.CHECK : SWT.RADIO);
      if (Strings.isValid(getToolTip())) {
         button.setToolTipText(getToolTip());
      }
      if (Strings.isValid(getToolTip())) {
         button.setToolTipText(getToolTip());
      }
      GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      button.setLayoutData(gd);
      button.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent event) {
            Button b = (Button) event.getSource();
            setSelected(b.getSelection());
            notifyXModifiedListeners();
         }
      });
      if (isLabelAfter()) {
         createLabel(parent);
      }
      refresh();
   }

   public void createLabel(Composite parent) {
      labelWidget = new Label(parent, SWT.NONE);
      String str = getLabel();
      if (!isLabelAfter()) {
         str += ":";
      }
      labelWidget.setText(str);
      if (Strings.isValid(getToolTip())) {
         labelWidget.setToolTipText(getToolTip());
      }
   }

   @Override
   public void dispose() {
      button.dispose();
      if (labelWidget != null) {
         labelWidget.dispose();
      }
      if (parent != null && !parent.isDisposed()) {
         parent.layout();
      }
   }

   public void addSelectionListener(SelectionListener selectionListener) {
      if (button != null) {
         button.addSelectionListener(selectionListener);
      }
   }

   public void removeSelectionListener(SelectionListener selectionListener) {
      button.removeSelectionListener(selectionListener);
   }

   public boolean isSelected() {
      return selected;
   }

   @Override
   public void refresh() {
      if (button != null) {
         button.setSelection(selected);
      }
      validate();
   }

   @Override
   public IStatus isValid() {
      return Status.OK_STATUS;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, getLabel() + ": ");
   }

   public boolean isLabelAfter() {
      return labelAfter;
   }

   /**
    * @return the buttonType
    */
   public ButtonType getButtonType() {
      return buttonType;
   }

   /**
    * @param buttonType the buttonType to set
    */
   public void setButtonType(ButtonType buttonType) {
      this.buttonType = buttonType;
   }

   /**
    * @param labelAfter the labelAfter to set
    */
   public void setLabelAfter(boolean labelAfter) {
      this.labelAfter = labelAfter;
   }

   @Override
   public Object getData() {
      return selected;
   }

   @Override
   public void setFocus() {
      button.setFocus();
   }
}