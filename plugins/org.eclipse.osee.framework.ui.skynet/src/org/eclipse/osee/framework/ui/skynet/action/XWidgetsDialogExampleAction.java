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

package org.eclipse.osee.framework.ui.skynet.action;

import java.util.Arrays;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.XWidgetsExampleDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class XWidgetsDialogExampleAction extends Action {

   private static final String TITLE = "XWidgetsDialog Example";

   public XWidgetsDialogExampleAction() {
      super(TITLE);
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GEAR));
      setToolTipText(TITLE);
   }

   @Override
   public void run() {
      try {
         XWidgetsExampleDialog dialog = new XWidgetsExampleDialog(TITLE, "Enter and select Ok");
         if (dialog.open() == 0) {
            String title = dialog.getXtextString("title");
            String desc = dialog.getXtextString("desc");
            boolean valid = dialog.getXCheckBoxChecked("valid");
            String number = dialog.getXComboString("number");

            String html = AHTML.beginMultiColumnTable(95, 2);
            html += AHTML.addHeaderRowMultiColumnTable(Arrays.asList("key", "value"));
            html += AHTML.addRowMultiColumnTable("title", title);
            html += AHTML.addRowMultiColumnTable("desc", AHTML.textToHtml(desc));
            html += AHTML.addRowMultiColumnTable("valid", String.valueOf(valid));
            html += AHTML.addRowMultiColumnTable("number", number);

            HtmlDialog htmlDiag = new HtmlDialog(TITLE, TITLE + " Results", html);
            htmlDiag.open();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
