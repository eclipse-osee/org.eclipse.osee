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

package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class EmbeddedStringEditor {

   private Color colorResource = null;
   private XText text;
   private String entryText = "";
   private String validationRegularExpression = null;
   private String validationErrorString = "";
   private Label errorLabel;
   private final String dialogMessage;
   private Composite composite;
   boolean fillVertically = false;

   public EmbeddedStringEditor(String dialogMessage) {
      this.dialogMessage = dialogMessage;
   }

   public void createEditor(Composite composite) {
      this.composite = composite;

      // Create error label
      errorLabel = new Label(composite, SWT.NONE);
      errorLabel.setSize(errorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

      if (colorResource == null) {
         colorResource = Displays.getColor(255, 0, 0);
      }
      errorLabel.setForeground(colorResource);
      errorLabel.setText("");
      errorLabel.addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            if (colorResource == null) {
               colorResource.dispose();
            }
         }

      });

      new Label(composite, SWT.NONE).setText(dialogMessage);
      text = new XText();
      text.setFillHorizontally(true);
      text.setFocus();
      text.setDisplayLabel(false);
      if (!entryText.equals("")) {
         text.set(entryText);
      }
      if (fillVertically) {
         text.setFillVertically(true);
         text.setHeight(200);
      }
      text.createWidgets(composite, 2);

      text.addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(ModifyEvent e) {
            handleModified();
         }
      });

      composite.layout();
   }

   public boolean handleModified() {
      if (text != null) {
         if (!isEntryValid()) {
            errorLabel.setText(validationErrorString);
            errorLabel.update();
            composite.layout();
            return false;
         } else {
            errorLabel.setText("");
            errorLabel.update();
            composite.layout();
            return true;
         }
      }
      return true;
   }

   public String getEntry() {
      return text.get();
   }

   public void setEntry(String entry) {
      if (text != null) {
         text.set(entry);
      }
      this.entryText = entry;
   }

   /**
    * override this method to make own checks on entry this will be called with every keystroke
    * 
    * @return true if entry is valid
    */
   public boolean isEntryValid() {
      if (validationRegularExpression == null) {
         return true;
      }
      // verify title is alpha-numeric with spaces and dashes
      Matcher m = Pattern.compile(validationRegularExpression).matcher(text.get());
      return m.find();
   }

   public void setValidationRegularExpression(String regExp) {
      validationRegularExpression = regExp;
   }

   public void setValidationErrorString(String errorText) {
      validationErrorString = errorText;
   }

}
