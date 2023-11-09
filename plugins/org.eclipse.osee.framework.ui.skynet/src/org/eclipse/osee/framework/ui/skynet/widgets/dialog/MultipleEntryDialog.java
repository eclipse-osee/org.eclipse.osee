/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Vaibhav Patel
 */
public class MultipleEntryDialog extends EntryDialog {

   private Map<String, XText> xtextList;
   private Map<String, String> entries = new HashMap<String, String>();
   private final List<String> labels;
   private Listener okListener;

   public MultipleEntryDialog(String dialogTitle, String dialogMessage, List<String> labels) {
      super(dialogTitle, dialogMessage);
      super.setTextHeight(100);
      this.labels = labels;
   }

   @Override
   protected void createOpenInEditorHyperlink(Composite parent) {
      // do nothing, we don't want this here
   }

   @Override
   protected void createClearFixedFontWidgets(Composite headerComp) {
      // do nothing
   }

   @Override
   protected void createExtendedArea(Composite parent) {
      text.dispose();
      for (String label : labels) {
         XText newXText = new XText(label);
         newXText.setFillHorizontally(true);
         if (isFillVertically()) {
            newXText.setFillVertically(true);
            newXText.setHeight(100);
            newXText.setFont(getFont());
         }
         newXText.set(entries.get(label));
         newXText.createWidgets(customAreaParent, 1);
         newXText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
               handleModified();
               entries.put(label, newXText.get());
            }
         });
      }
   }

   public Map<String, String> getEntries() {
      return entries;
   }

   public void setEntries(Map<String, String> entries) {
      for (String label : entries.keySet()) {
         if (xtextList.get(label) != null) {
            xtextList.get(label).set(entries.get(label));
         }
      }
      this.entries = entries;
   }

   public void setOkListener(Listener okListener) {
      this.okListener = okListener;
   }

   @Override
   protected void buttonPressed(int buttonId) {
      super.buttonPressed(buttonId);
      if (buttonId == 0 && okListener != null) {
         okListener.handleEvent(null);
      }
   }

}
