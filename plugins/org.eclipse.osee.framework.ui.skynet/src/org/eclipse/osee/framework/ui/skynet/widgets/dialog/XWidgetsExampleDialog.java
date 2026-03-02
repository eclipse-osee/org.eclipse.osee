/*********************************************************************
 * Copyright (c) 2020 Boeing
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

import java.util.List;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;

/**
 * @author Donald G. Dunne
 */
public class XWidgetsExampleDialog extends XWidgetsDialog {

   public static final long NUMBER_ID = 4L;
   public static final long IS_VALID_ID = 3L;
   public static final long DESC_ID = 2L;
   public static final long TITLE_ID = 1L;

   public XWidgetsExampleDialog(String dialogTitle, String dialogMessage) {
      super(dialogTitle, dialogMessage);
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andWidget("Action Title", WidgetId.XTextWidget).andId(TITLE_ID);
      wb.andWidget("Enter Detailed Description", WidgetId.XTextWidget).andId(DESC_ID).andFillVertically();
      wb.andWidget("Is This Valid", WidgetId.XCheckBoxWidget).andId(IS_VALID_ID);
      wb.andWidget("Select number to create", WidgetId.XComboWidget).andId(NUMBER_ID).andValues("one", "two",
         "three").andHorizLabel();
      return wb.getXWidgetDatas();
   }

}
