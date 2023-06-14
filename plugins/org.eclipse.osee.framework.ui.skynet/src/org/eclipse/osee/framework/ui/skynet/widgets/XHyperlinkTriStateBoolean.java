/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonsBooleanTriState.BooleanState;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkTriStateBoolean extends XHyperlinkLabelValueSelection {

   protected BooleanState selected = BooleanState.UnSet;

   public XHyperlinkTriStateBoolean() {
      super("");
   }

   public Collection<String> getSelectable() {
      return Arrays.asList(BooleanState.Yes.name(), BooleanState.No.name(), BooleanState.UnSet.name());
   }

   @Override
   public String getCurrentValue() {
      return selected.name();
   }

   public String getSelectedStr() {
      return selected.name();
   }

   public BooleanState getSelected() {
      return selected;
   }

   protected void handleSelectionPersist(BooleanState selected) {
      // for subclass implementation
   }

   @Override
   public boolean handleSelection() {
      try {
         List<String> buttonLabels = new ArrayList<String>();
         buttonLabels.addAll(getSelectable());
         buttonLabels.add("Cancel");

         MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), getLabel(), null, getLabel(),
            MessageDialog.QUESTION, 3, buttonLabels.toArray(new String[buttonLabels.size()]));

         int selectednum = dialog.open();

         // not cancelled
         if (selectednum >= 0 && selectednum != buttonLabels.size() - 1) {
            String selectedStr = getSelectable().toArray(new String[getSelectable().size()])[selectednum];
            selected = BooleanState.valueOf(selectedStr);
            handleSelectionPersist(selected);
            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public void setSelected(BooleanState selected) {
      this.selected = selected;
      refresh();
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() && isEmpty()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be selected.");
      }
      return Status.OK_STATUS;
   }

   @Override
   public boolean isEmpty() {
      return selected == null || selected.isUnSet();
   }

   @Override
   public Object getData() {
      return selected;
   }

}
