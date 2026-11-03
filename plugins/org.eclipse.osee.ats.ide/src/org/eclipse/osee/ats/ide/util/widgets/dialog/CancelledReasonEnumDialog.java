/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Jeremy A. Midvidy
 */
public class CancelledReasonEnumDialog extends EntryDialog {

   private String selection = "";
   private XXStringsWidget reasonCombo;

   public CancelledReasonEnumDialog(String dialogTitle, String dialogMessage) {
      super(dialogTitle, dialogMessage);
   }

   @Override
   protected Control createCustomArea(Composite parent) {
      this.customAreaParent = parent;
      areaComposite = new Composite(parent, SWT.NONE);
      areaComposite.setLayout(new GridLayout(2, false));
      GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
      gd.widthHint = 600;
      areaComposite.setLayoutData(gd);
      areaComposite.addMouseMoveListener(compListener);
      createErrorLabel(areaComposite);
      createExtendedArea(areaComposite);
      createTextBox();
      createOpenInEditorHyperlink(parent);
      areaComposite.layout();
      parent.layout();
      return areaComposite;
   }

   @Override
   public String getEntry() {
      return this.selection;
   }

   private List<String> getCancelledReasonValues() {
      List<String> values = AtsAttributeTypes.CancelledReasonEnum.getEnumStrValues();
      return values;
   }

   public String getCancelledDetails() {
      return textWidget.get();
   }

   public void controlEnabled() {
      this.selection = this.reasonCombo.getSelectedFirst();
      if (this.selection.contains("Other")) {
         if (this.textWidget.get().equals("")) {
            okButton.setEnabled(false);
            textWidget.setLabelFgColor(SWT.COLOR_RED);
         } else {
            okButton.setEnabled(true);
            textWidget.setLabelFgColor(SWT.COLOR_BLACK);
         }
      } else if (this.selection.equals("")) {
         okButton.setEnabled(false);
      } else {
         okButton.setEnabled(true);
      }
   }

   @Override
   protected void createExtendedArea(Composite parent) {
      this.reasonCombo = new XXStringsWidget("Cancellation Reason");
      this.reasonCombo.setFillHorizontally(true);
      this.reasonCombo.setFocus();
      this.reasonCombo.setSelectable(getCancelledReasonValues());
      this.reasonCombo.createWidgets(parent, 2);
      XModifiedListener listener = new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            controlEnabled();
         }
      };
      reasonCombo.addXModifiedListener(listener);
   }

   @Override
   protected void createTextBox() {
      textWidget = new XXTextWidget("Cancelled Details");
      textWidget.setFocus();
      if (fillVertically) {
         textWidget.setFillVertically(true);
         textWidget.setHeight(textHeight == null ? 200 : textHeight);
         textWidget.setFont(getFont());
      }
      textWidget.createWidgets(areaComposite, 2);
      textWidget.setFocus();
      if (Strings.isValid(entryText)) {
         textWidget.setSelected(entryText);
         textWidget.selectAll();
      }
      textWidget.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            controlEnabled();
         }
      });
      addContextMenu(textWidget.getStyledText());
   }

}
