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

package org.eclipse.osee.ats.ide.util.widgets.defect;

import org.eclipse.osee.ats.api.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

/**
 * Dialog with two entry boxes
 *
 * @author Donald G. Dunne
 */
public class NewDefectDialog extends EntryDialog {

   private XXTextWidget text2;
   private XXStringsWidget severityCombo;
   private String entryText2 = "";
   private final String label2;
   private Listener okListener;
   private Severity severity;

   public NewDefectDialog() {
      super("Enter New Defect", "Enter Defect Description and Severity");
      super.setLabel("Enter Defect Description");
      super.setTextHeight(100);
      this.label2 = "Enter Location of Defect";
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

      textWidget.addLabelMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            super.mouseUp(e);
            if (e.button == 3) {
               textWidget.setSelected("description " + AtsApiService.get().getRandomNum());
               severityCombo.setSelected("Issue");
               severity = Severity.Issue;
               text2.setSelected("location");
               handleModified();
            }
         }

      });

      severityCombo = new XXStringsWidget("Enter Defect Severity");
      severityCombo.setSelectable(Severity.strValues());
      severityCombo.createWidgets(parent, 1);
      severityCombo.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            String str = severityCombo.getSelectedFirst();
            if (Strings.isValid(str)) {
               severity = Severity.valueOf(str);
            } else {
               severity = null;
            }
            handleModified();
         }
      });

      text2 = new XXTextWidget(label2);
      text2.setFillHorizontally(true);
      if (isFillVertically()) {
         text2.setFillVertically(true);
         text2.setHeight(100);
         textWidget.setHeight(100);
         text2.setFont(getFont());
      }
      text2.setSelected(entryText2, false);
      text2.createWidgets(parent, 1);

      text2.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            handleModified();
            entryText2 = text2.get();
         }
      });

   }

   public String getEntry2() {
      return entryText2;
   }

   @Override
   protected void buttonPressed(int buttonId) {
      super.buttonPressed(buttonId);
      if (buttonId == 0 && okListener != null) {
         okListener.handleEvent(null);
      }
   }

   public Severity getSeverity() {
      return severity;
   }

   @Override
   public boolean isEntryValid() {
      if (!super.isEntryValid()) {
         return false;
      }
      if (!Strings.isValid(getEntry())) {
         setErrorString("Must enter Description");
         return false;
      }

      if (severity == null) {
         setErrorString("Must select Severity");
         return false;
      }
      setErrorString("");
      return true;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control control = super.createButtonBar(parent);
      handleModified();
      return control;
   }

   public void setOkListener(Listener okListener) {
      this.okListener = okListener;
   }

}
