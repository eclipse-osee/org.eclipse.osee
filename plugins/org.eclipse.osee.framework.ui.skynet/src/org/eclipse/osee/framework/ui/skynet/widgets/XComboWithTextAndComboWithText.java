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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.ui.swt.ALayout;
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
public class XComboWithTextAndComboWithText extends XCombo {

   private XText text;
   private XComboWithText comboWithText;
   private final String textLabel;
   private final boolean textRequiredIfVisible;
   private final String moreInfoOption;
   private Composite composite;
   private final String comboLabel2;
   private final String textLabel2;
   private final String[] comboOptions2;
   private final String moreInfoOption2;
   private final boolean textRequiredIfVisible2;
   private final boolean combo2RequiredIfVisible;

   public XComboWithTextAndComboWithText(String comboLabel, String textLabel, String[] comboOptions, String moreInfoOption, boolean textRequiredIfVisible, boolean combo2RequiredIfVisible, String comboLabel2, String textLabel2, String[] comboOptions2, String moreInfoOption2, boolean textRequiredIfVisible2) {
      super(comboLabel);
      this.textLabel = textLabel;
      this.moreInfoOption = moreInfoOption;
      this.textRequiredIfVisible = textRequiredIfVisible;
      this.combo2RequiredIfVisible = combo2RequiredIfVisible;
      this.comboLabel2 = comboLabel2;
      this.textLabel2 = textLabel2;
      this.comboOptions2 = comboOptions2;
      this.moreInfoOption2 = moreInfoOption2;
      this.textRequiredIfVisible2 = textRequiredIfVisible2;
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
      if (comboGd != null) {
         comboGd.verticalAlignment = SWT.TOP;
         getLabelWidget().setLayoutData(comboGd);
      }

      refreshComposite();
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
      if (comboWithText != null) {
         comboWithText.setEnabled(enabled);
      }
   }

   protected int getTextHeightHint() {
      return 0;
   }

   private void refreshComposite() {
      if (text != null && Widgets.isAccessible(text.getStyledText())) {
         text.getLabelWidget().dispose();
         text.dispose();
      }
      if (comboWithText != null) {
         comboWithText.dispose();
      }
      if (isSelected()) {
         text = new XText(textLabel);
         text.setFillHorizontally(true);
         text.setFillVertically(true);
         text.createWidgets(getManagedForm(), composite, 2);
         text.setRequiredEntry(textRequiredIfVisible);
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.heightHint = getTextHeightHint();
         gd.widthHint = 100;
         text.getStyledText().setLayoutData(gd);
         text.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
               notifyXModifiedListeners();
            }
         });
         comboWithText =
            new XComboWithText(comboLabel2, textLabel2, comboOptions2, moreInfoOption2, textRequiredIfVisible2);
         comboWithText.setRequiredEntry(combo2RequiredIfVisible);
         comboWithText.createWidgets(getManagedForm(), composite, 2);
         // Set default height hint of text box if it's empty
         if (comboWithText.getText() != null) {
            GridData gd2 = (GridData) comboWithText.getText().getStyledText().getLayoutData();
            gd2.heightHint = getTextHeightHint();
            gd2.widthHint = 100;
            comboWithText.getText().getStyledText().setLayoutData(gd);
         }
         // Since embedding XComboWithText, make that internal composite zero margin so it aligns
         Composite composite = comboWithText.getComboBox().getParent();
         composite.setLayout(ALayout.getZeroMarginLayout(4, false));
         comboWithText.refresh();
         comboWithText.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
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

      XComboWithTextAndComboWithText widget =
         new XComboWithTextAndComboWithText("Impact", "Impact Description", new String[] {"Yes", "No"}, "Yes", true,
            true, "Workaround", "Workaround Description", new String[] {"Yes", "No"}, "Yes", true);
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
      if (status == Status.OK_STATUS && comboWithText != null) {
         status = comboWithText.isValid();
      }
      return status;
   }

   @Override
   public void adaptControls(FormToolkit toolkit) {
      if (text != null) {
         text.adaptControls(toolkit);
      }
      if (comboWithText != null) {
         comboWithText.adaptControls(toolkit);
      }
   }

   @Override
   public Collection<? extends XWidget> getChildrenXWidgets() {
      List<XWidget> widgets = new ArrayList<>();
      if (text != null) {
         widgets.add(text);
      }
      if (comboWithText != null) {
         widgets.add(comboWithText);
         widgets.addAll(comboWithText.getChildrenXWidgets());
      }
      return widgets;
   }

   public XComboWithText getComboWithText() {
      return comboWithText;
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
      if (comboWithText != null) {
         comboWithText.validate();
      }
   }

}
