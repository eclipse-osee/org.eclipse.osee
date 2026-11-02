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

package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.util.BooleanState;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXBooleanWidget extends XXStringsWidget {

   public static final WidgetId ID = WidgetId.XXBooleanWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXBooleanWidget() {
      this("");
   }

   public XXBooleanWidget(String label) {
      super(ID, label);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public boolean handleSelection() {
      try {
         List<String> buttonLabels = new ArrayList<String>();
         buttonLabels.addAll(BooleanState.getStrs());
         buttonLabels.add("Cancel");

         MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), getLabel(), null, getLabel(),
            MessageDialog.QUESTION, 3, buttonLabels.toArray(new String[buttonLabels.size()]));

         int selectednum = dialog.open();

         // not cancelled
         if (selectednum >= 0 && selectednum != buttonLabels.size() - 1) {
            String selectedStr = buttonLabels.toArray(new String[buttonLabels.size()])[selectednum];
            BooleanState bool = BooleanState.valueOf(selectedStr);
            if (bool.isYes()) {
               setSelected("true", false);
            } else if (bool.isNo()) {
               setSelected("false", false);
            } else {
               clear();
            }
            handleSelectedPersist();
            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public void setSelected(BooleanState boolState) {
      if (boolState.isUnSet()) {
         clear();
      }
      setSelected(String.valueOf(boolState.isYes()));
   }

   public BooleanState getBoolean() {
      String sel = getSelectedFirst();
      if (Strings.isInvalid(sel)) {
         return BooleanState.UnSet;
      }
      if ("true".equals(sel)) {
         return BooleanState.Yes;
      } else if ("false".equals(sel)) {
         return BooleanState.No;
      }
      return null;
   }

   @Override
   public String getDisplayValue() {
      String selected = getSelectedFirst();
      if ("true".equals(selected)) {
         return "Yes";
      } else if ("false".equals(selected)) {
         return "No";
      }
      return super.getDisplayValue();
   }

}
