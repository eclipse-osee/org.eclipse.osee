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

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

/**
 * Dialog with two entry boxes
 *
 * @author Donald G. Dunne
 */
public class EntryEntryDialog extends EntryDialog {

   private XText text2;
   private String entryText2 = "";
   private final String text2Label;
   private Listener okListener;

   public EntryEntryDialog(String dialogTitle, String dialogMessage, String text1Label, String text2Label) {
      super(dialogTitle, dialogMessage);
      super.setLabel(text1Label);
      super.setTextHeight(100);
      this.text2Label = text2Label;
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

      text2 = new XText(text2Label);
      text2.setFillHorizontally(true);
      if (isFillVertically()) {
         text2.setFillVertically(true);
         text2.setHeight(100);
         text.setHeight(100);
         text2.setFont(getFont());
      }
      text2.set(entryText2);
      text2.createWidgets(customAreaParent, 1);

      text2.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            handleModified();
            entryText2 = text2.get();
         }
      });

   }

   public String getEntry2() {
      return entryText2;
   }

   public void setEntry2(String entry2) {
      if (text2 != null) {
         text2.set(entry2);
      }
      this.entryText2 = entry2;
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
