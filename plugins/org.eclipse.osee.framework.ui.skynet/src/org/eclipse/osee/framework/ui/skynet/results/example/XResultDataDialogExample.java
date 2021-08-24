/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.results.example;

import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.XResultDataDialog;

/**
 * @author Donald G. Dunne
 */
public final class XResultDataDialogExample extends XNavigateItemAction {

   public static final String TITLE = "XResultDialogData Example";

   public XResultDataDialogExample() {
      super(TITLE, FrameworkImage.EXAMPLE, XNavigateItem.UTILITY_EXAMPLES);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      XResultData rd = new XResultData();
      rd.setTitle("Title");
      rd.log("This is a log message");
      rd.error("This is a error message");
      rd.warning("This is a warn message");
      XResultDataDialog.open(rd, getName(),
         "This is the message.\n\nTo XResultData contents, select the hyperlink below.");
   }

}
