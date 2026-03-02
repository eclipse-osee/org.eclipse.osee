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

import java.util.List;
import org.eclipse.osee.framework.core.widget.XWidgetData;

/**
 * @author Vaibhav Patel
 */
public class CreateEnumeratedArtifactDialog extends XWidgetsDialog {

   public static final long NAME_ID = 3245L;
   public static final long VALUE_ID = 3823L;

   public CreateEnumeratedArtifactDialog(String dialogTitle, String dialogMessage) {
      super(dialogTitle, dialogMessage);
   }

   @Override
   public List<XWidgetData> getXWidgetItems() { // TESTED
      createWidgetBuilder();
      wb.andXText("Name").andId(NAME_ID);
      wb.andXText("Values(s)").andId(VALUE_ID).andFillVertically();
      return wb.getXWidgetDatas();
   }

}
