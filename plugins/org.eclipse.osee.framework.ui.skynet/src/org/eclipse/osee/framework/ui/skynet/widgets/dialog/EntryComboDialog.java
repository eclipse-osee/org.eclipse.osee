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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class EntryComboDialog extends EntryDialog {

   private final String comboMessage;
   private List<String> options = new ArrayList<>();
   private String defaultSelection = null;
   private String selection = "";

   public EntryComboDialog(String dialogTitle, String dialogMessage, String comboMessage) {
      super(dialogTitle, dialogMessage);
      this.comboMessage = comboMessage;
   }

   @Override
   protected void createExtendedArea(Composite parent) {

      final XCombo combo = new XCombo(comboMessage);
      combo.setFillHorizontally(true);
      combo.setFocus();
      combo.setDataStrings(options.toArray(new String[options.size()]));
      if (Strings.isValid(defaultSelection)) {
         combo.setDataStrings(new String[] {defaultSelection});
      }
      combo.createWidgets(parent, 2);

      XModifiedListener listener = new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            handleModified();
            selection = combo.get();
         }
      };
      combo.addXModifiedListener(listener);
   }

   public void setOptions(List<String> options) {
      this.options = options;
   }

   public void setDefaultSelection(String defaultSelection) {
      this.defaultSelection = defaultSelection;
   }

   public String getSelection() {
      return selection;
   }

}
