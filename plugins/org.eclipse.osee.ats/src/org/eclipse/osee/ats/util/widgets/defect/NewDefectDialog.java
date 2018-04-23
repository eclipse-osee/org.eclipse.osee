/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets.defect;

import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem.Severity;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

   private XText text2;
   private XCombo severityCombo;
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

      getErrorLabel().addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            super.mouseUp(e);
            if (e.button == 3) {
               text.set("description " + AtsClientService.get().getClientUtils().getAtsDeveloperIncrementingNum());
               severityCombo.set("Issue");
               text2.set("location");
            }
         }

      });

      severityCombo = new XCombo("Enter Defect Severity");
      severityCombo.setDataStrings(Severity.strValues().toArray(new String[Severity.strValues().size()]));
      severityCombo.createWidgets(customAreaParent, 1);
      severityCombo.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            String str = severityCombo.get();
            if (Strings.isValid(str)) {
               severity = Severity.valueOf(str);
            } else {
               severity = null;
            }
            handleModified();
         }
      });

      text2 = new XText(label2);
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
      return true;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control control = super.createButtonBar(parent);
      handleModified();
      return control;
   }

}
