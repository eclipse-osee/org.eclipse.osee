/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.text.NumberFormat;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class IntegerDialog extends EntryDialog {

   private final int minValue;
   private final int maxValue;

   public IntegerDialog(String dialogTitle, String dialogMessage, int minValue, int maxValue) {
      super(dialogTitle, dialogMessage);
      this.minValue = minValue;
      this.maxValue = maxValue;
      setNumberFormat(NumberFormat.getIntegerInstance());
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      Control control = super.createDialogArea(parent);
      text.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            String value = text.get();
            Integer intValue = Integer.parseInt(value);
            if (intValue < minValue || intValue > maxValue) {
               setErrorString(String.format("Value must be between %s and %s", minValue, maxValue));
            } else {
               setErrorString("");
            }
         }
      });
      return control;
   }

   public Integer getInt() {
      String value = text.get();
      if (Strings.isValid(value)) {
         return Integer.parseInt(value);
      }
      return null;
   }

}
