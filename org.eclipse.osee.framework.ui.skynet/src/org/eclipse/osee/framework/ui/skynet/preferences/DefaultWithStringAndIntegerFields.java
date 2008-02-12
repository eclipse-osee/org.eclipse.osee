/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Roberto E. Escobar
 */
public class DefaultWithStringAndIntegerFields extends FieldEditor {
   private static final String CUSTOM_BUTTON_KEY = ".custom";
   private static final String STRING_FIELD_KEY = ".stringField";
   private static final String INTEGER_FIELD_KEY = ".integerField";

   private Button defaultButton;
   private Button userButton;
   private Label defaultLabel;
   private Text text1;
   private Text text2;
   private String defaultValue;
   private String labelText1;
   private String labelText2;
   private Composite textComposite;

   public DefaultWithStringAndIntegerFields(String name, String defaultValue, String labelText1, String labelText2, Composite parent) {
      super();
      Assert.isNotNull(defaultValue);
      this.defaultValue = defaultValue;
      this.labelText1 = labelText1;
      this.labelText2 = labelText2;

      init(name, "");
      createControl(parent);
   }

   @Override
   protected void adjustForNumColumns(int numColumns) {
   }

   @Override
   protected void doFillIntoGrid(Composite parent, int numColumns) {
   }

   @Override
   protected void createControl(Composite parent) {
      Composite baseComposite = new Composite(parent, SWT.NONE);
      baseComposite.setLayout(new GridLayout());
      baseComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Composite buttonComposite = new Composite(baseComposite, SWT.NONE);
      buttonComposite.setLayout(new GridLayout(2, false));
      buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      defaultButton = new Button(buttonComposite, SWT.RADIO);
      defaultButton.setText("Default: ");

      defaultLabel = new Label(buttonComposite, SWT.NONE);
      defaultLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false));
      defaultLabel.setText(defaultValue);

      userButton = new Button(buttonComposite, SWT.RADIO);
      userButton.setText("User Defined: ");

      createCustomArea(baseComposite);
      attachListeners();
   }

   private void createCustomArea(Composite parent) {
      textComposite = new Composite(parent, SWT.BORDER);
      GridLayout gridLayout = new GridLayout(2, false);
      textComposite.setLayout(gridLayout);
      textComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Label text1Label = new Label(textComposite, SWT.NONE);
      text1Label.setText(labelText1);

      text1 = new Text(textComposite, SWT.BORDER | SWT.SINGLE);
      text1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Label text2Label = new Label(textComposite, SWT.NONE);
      text2Label.setText(labelText2);

      text2 = new Text(textComposite, SWT.BORDER | SWT.SINGLE);
      text2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
   }

   @Override
   protected void doLoad() {
      performLoad();
   }

   @Override
   protected void doLoadDefault() {
      performLoad();
   }

   @Override
   protected void doStore() {
      IPreferenceStore store = getPreferenceStore();
      if (userButton.getSelection()) {
         String textField = text1.getText();
         String integerField = text2.getText();
         if (areValuesValid(textField, integerField)) {
            store.setValue(getPreferenceName() + CUSTOM_BUTTON_KEY, true);
            store.setValue(getPreferenceName() + STRING_FIELD_KEY, textField);
            store.setValue(getPreferenceName() + INTEGER_FIELD_KEY, integerField);
            store.setValue(getPreferenceName(), textField + ":" + integerField);
         }
      } else {
         store.setValue(getPreferenceName() + CUSTOM_BUTTON_KEY, false);
         store.setValue(getPreferenceName(), defaultValue);
      }
   }

   @Override
   public int getNumberOfControls() {
      return 2;
   }

   private boolean areValuesValid(String textField, String integerField) {
      boolean toReturn = false;
      if (Strings.isValid(textField)) {
         try {
            Integer integer = new Integer(integerField);
            if (integer != null) {
               toReturn = true;
            }
         } catch (NumberFormatException ex) {
         }
      }
      return toReturn;
   }

   private void performLoad() {
      IPreferenceStore store = getPreferenceStore();
      boolean wasCustomSelected = store.getBoolean(getPreferenceName() + CUSTOM_BUTTON_KEY);
      String stringField = store.getString(getPreferenceName() + STRING_FIELD_KEY);
      String integerField = store.getString(getPreferenceName() + INTEGER_FIELD_KEY);
      if (wasCustomSelected && areValuesValid(stringField, integerField)) {
         selectDefaultButton(false);
         text1.setText(stringField);
         text2.setText(integerField);
      } else {
         selectDefaultButton(true);
      }
   }

   private void attachListeners() {
      defaultButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            selectDefaultButton(defaultButton.getSelection());
         }
      });
   }

   private void selectDefaultButton(boolean isSelected) {
      defaultButton.setSelection(isSelected);
      defaultLabel.setEnabled(isSelected);
      userButton.setSelection(!isSelected);
      textComposite.setEnabled(!isSelected);
      for (Control child : textComposite.getChildren()) {
         child.setEnabled(!isSelected);
      }
   }
}
