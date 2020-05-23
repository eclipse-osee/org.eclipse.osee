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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Single combo box with options that upon selecting moreInfoOption will provide second text widget for extra
 * information.<br/>
 * <br/>
 * Example: Does this have impact, select Yes or No. Upon selecting Yes, description box shows for more information.
 * <br/>
 * <br/>
 * Note: This widget has a main and can be run to see an example
 * 
 * @author Donald G. Dunne
 */
public class XComboWithText extends XCombo {

   private XText text;
   private final String textLabel;
   private final boolean textRequiredIfVisible;
   private final String moreInfoOption;
   private Composite composite;

   public XComboWithText(String comboLabel, String textLabel, String[] comboOptions, String moreInfoOption, boolean textRequiredIfVisible) {
      super(comboLabel);
      this.textLabel = textLabel;
      this.moreInfoOption = moreInfoOption;
      this.textRequiredIfVisible = textRequiredIfVisible;
      setDataStrings(comboOptions);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(4, false);
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(GridData.FILL_BOTH));

      super.createControls(composite, horizontalSpan);
      getComboBox().addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            refreshComposite();
         }
      });
      GridData comboGd = (GridData) getComboBox().getLayoutData();
      comboGd.verticalAlignment = SWT.TOP;
      getComboBox().setLayoutData(comboGd);
      comboGd = (GridData) getLabelWidget().getLayoutData();
      comboGd.verticalAlignment = SWT.TOP;
      getLabelWidget().setLayoutData(comboGd);

      refreshComposite();
   }

   @Override
   public void dispose() {
      composite.dispose();
   }

   @Override
   public boolean isSelected() {
      return get().equals(moreInfoOption);
   }

   @Override
   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      if (text != null) {
         text.setEnabled(enabled);
      }
   }

   protected int getTextHeightHint() {
      return 0;
   }

   protected void refreshComposite() {
      if (text != null && Widgets.isAccessible(text.getStyledText())) {
         text.getLabelWidget().dispose();
         text.dispose();
      }
      if (isSelected()) {
         text = new XText(textLabel);
         text.setFillHorizontally(true);
         text.setFillVertically(true);
         text.createWidgets(getManagedForm(), composite, 2);
         text.setRequiredEntry(textRequiredIfVisible);
         // Set default height hint of text box if it's empty
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.heightHint = getTextHeightHint();
         text.getStyledText().setLayoutData(gd);
         text.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
               notifyXModifiedListeners();
            }
         });
      }
      composite.layout();
      if (composite.getParent() != null) {
         composite.getParent().layout();
      }
   }

   public static void main(String[] args) {
      Display Display_1 = Display.getDefault();
      Shell shell = new Shell(Display_1, SWT.SHELL_TRIM);
      shell.setText("XCombWithEnablementTest Test");
      shell.setBounds(0, 0, 400, 250);
      shell.setLayout(new GridLayout(2, false));
      shell.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_BEGINNING));

      XComboWithText widget = new XComboWithText("Select", "Description", new String[] {"Yes", "No"}, "Yes", true);
      widget.setRequiredEntry(true);
      widget.createWidgets(shell, 2);

      shell.open();
      while (!shell.isDisposed()) {
         if (!Display_1.readAndDispatch()) {
            Display_1.sleep();
         }
      }

      Display_1.dispose();
   }

   public XText getText() {
      return text;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status == Status.OK_STATUS && text != null) {
         status = text.isValid();
      }
      return status;
   }

   @Override
   public void adaptControls(FormToolkit toolkit) {
      if (text != null) {
         text.adaptControls(toolkit);
      }
   }

   @Override
   public Collection<? extends XWidget> getChildrenXWidgets() {
      if (text != null) {
         return Arrays.asList(text);
      }
      return super.getChildrenXWidgets();
   }

   @Override
   public Control getControl() {
      if (text != null) {
         return text.getStyledText();
      }
      return null;
   }

   @Override
   public void validate() {
      super.validate();
      if (text != null) {
         text.validate();
      }
   }

}
