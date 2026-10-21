/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXBranchSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXCheckBoxWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

/**
 * Dialog with branch selection and two entry boxes
 *
 * @author Donald G. Dunne
 */
public class BranchEntryEntryDialog extends EntryDialog {

   private XXBranchSelWidget branchWidget;
   private XXTextWidget text2;
   private String entryText2 = "";
   private final String text2Label;
   private Listener okListener;
   private BranchToken branch;
   private boolean fillVertically2 = true;
   private boolean addCheckBox = false;
   private String checkboxMessage;
   private boolean checked = false;

   public BranchEntryEntryDialog(String dialogTitle, String dialogMessage, String text1Label, String text2Label) {
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

      text2 = new XXTextWidget(text2Label);
      text2.setFillHorizontally(true);
      if (isFillVertically2()) {
         text2.setFillVertically(true);
         text2.setHeight(100);
         textWidget.setHeight(100);
         text2.setFont(getFont());
      }
      text2.set(entryText2);
      text2.createWidgets(customAreaParent, 1);

      text2.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            handleModified();
            entryText2 = text2.get();
         }
      });

      branchWidget = new XXBranchSelWidget();
      branchWidget.createWidgets(customAreaParent, 1);
      if (branch != null && branch.isValid()) {
         branchWidget.setSelected(branch);
      }
      branchWidget.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            branch = branchWidget.getSelectedFirst();
         }
      });

      if (addCheckBox) {
         final XXCheckBoxWidget checkbox = new XXCheckBoxWidget(checkboxMessage);
         checkbox.setFillHorizontally(true);
         checkbox.setFocus();
         checkbox.setDisplayLabel(false);
         checkbox.setSelected(checked);
         checkbox.createWidgets(customAreaParent, 1);

         SelectionListener selectionListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               handleModified();
               checked = checkbox.isSelected();
            }
         };
         checkbox.addSelectionListener(selectionListener);

      }

   }

   public boolean isChecked() {
      return checked;
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

   public BranchToken getBranch() {
      return branch;
   }

   public boolean isFillVertically2() {
      return fillVertically2;
   }

   public void setFillVertically2(boolean fillVertically2) {
      this.fillVertically2 = fillVertically2;
   }

   public void addCheckbox(String checkboxMessage) {
      this.addCheckBox = true;
      this.checkboxMessage = checkboxMessage;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   public void setBranch(BranchToken branch) {
      this.branch = branch;
   }

}
