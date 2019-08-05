/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class EntryComboComboDialog extends EntryComboDialog {

   private final String comboMessage2;
   private List<Object> options2 = new ArrayList<>();
   private Object defaultSelection2 = null;
   private Object selection2 = "";
   private XCombo combo2;
   private boolean combo2Required = false;

   public EntryComboComboDialog(String dialogTitle, String dialogMessage, String comboMessage, String comboMessage2) {
      super(dialogTitle, dialogMessage, comboMessage);
      this.comboMessage2 = comboMessage2;
   }

   @Override
   protected void createExtendedArea(Composite parent) {
      super.createExtendedArea(parent);

      combo2 = new XCombo(comboMessage2);
      combo2.setFillHorizontally(true);
      combo2.setFocus();
      combo2.setRequiredEntry(combo2Required);
      Map<String, Object> nameToObjectMap = new HashMap<String, Object>();
      for (Object obj : options2) {
         nameToObjectMap.put(obj.toString(), obj);
      }
      List<String> names = new ArrayList<>();
      names.addAll(nameToObjectMap.keySet());
      Collections.sort(names);
      combo2.setDataStrings(names.toArray(new String[names.size()]));
      if (defaultSelection2 != null) {
         combo2.setDataStrings(new String[] {defaultSelection2.toString()});
      }
      combo2.createWidgets(parent, 2);

      XModifiedListener listener = new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            handleModified();
            selection2 = nameToObjectMap.get(combo2.get());
         }
      };
      combo2.addXModifiedListener(listener);
   }

   @Override
   public void handleModified() {
      entryText = text.get();
      if (Strings.isInValid(entryText)) {
         updateErrorLabel(true, "");
         return;
      } else if (combo2 != null) {
         String combo2Selection = combo2.get();
         if (!Strings.isValid(combo2Selection)) {
            updateErrorLabel(true, String.format("Must Select [%s]", comboMessage2));
            return;
         }
      }
      updateErrorLabel(false, "");
   }

   public List<Object> getOptions2() {
      return options2;
   }

   public void setOptions2(List<Object> options2) {
      this.options2 = options2;
   }

   public Object getDefaultSelection2() {
      return defaultSelection2;
   }

   public void setDefaultSelection2(Object defaultSelection2) {
      this.defaultSelection2 = defaultSelection2;
   }

   public Object getSelection2() {
      return selection2;
   }

   public void setSelection2(Object selection2) {
      this.selection2 = selection2;
   }

   public String getComboMessage2() {
      return comboMessage2;
   }

   public void setOptions2(Collection<? extends Object> objects) {
      options2 = new ArrayList<>();
      options2.addAll(objects);
   }

   public void setCombo2Required(boolean required) {
      this.combo2Required = required;
      if (combo2 != null) {
         combo2.setRequiredEntry(required);
      }
   }

   @Override
   protected void createButtonsForButtonBar(Composite parent) {
      super.createButtonsForButtonBar(parent);
      handleModified();
   }

}
