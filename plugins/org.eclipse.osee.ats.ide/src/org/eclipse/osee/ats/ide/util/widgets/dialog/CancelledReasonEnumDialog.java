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

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
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
   private XComboDam reasonCombo;

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

   private String[] getCancelledReasonValues() {
      List<String> values = AtsAttributeTypes.CancelledReasonEnum.getEnumStrValues();
      String[] valueArray = values.toArray(new String[values.size()]);
      Arrays.sort(valueArray);
      return valueArray;
   }

   public String getCancelledDetails() {
      return text.get();
   }

   public void controlEnabled() {
      this.selection = this.reasonCombo.get();
      if (this.selection.contains("Other")) {
         if (this.text.get().equals("")) {
            ok.setEnabled(false);
            text.getLabelWidget().setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         } else {
            ok.setEnabled(true);
            text.getLabelWidget().setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
         }
      } else if (this.selection.equals("")) {
         ok.setEnabled(false);
      } else {
         ok.setEnabled(true);
      }
   }

   @Override
   protected void createExtendedArea(Composite parent) {
      this.reasonCombo = new XComboDam("Cancellation Reason");
      this.reasonCombo.setFillHorizontally(true);
      this.reasonCombo.setFocus();
      this.reasonCombo.setDataStrings(getCancelledReasonValues());
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
      text = new XTextDam("Cancelled Details");
      text.setFillHorizontally(true);
      text.setFocus();
      text.setDisplayLabel(true);
      if (fillVertically) {
         text.setFillVertically(true);
         text.setHeight(textHeight == null ? 200 : textHeight);
         text.setFont(getFont());
      }
      text.createWidgets(areaComposite, 2);
      text.setFocus();
      if (Strings.isValid(entryText)) {
         text.set(entryText);
         text.selectAll();
      }
      text.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            controlEnabled();
         }
      });
      addContextMenu(text.getStyledText());
   }

}
