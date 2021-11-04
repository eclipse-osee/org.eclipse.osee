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

package org.eclipse.osee.ats.ide.navigate;

import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;

/**
 * @author Donald G. Dunne
 */
public class CommaDelimitLines extends XNavigateItemAction {

   public CommaDelimitLines() {
      super("Comma Delimit Lines", AtsImage.REPORT, XNavigateItem.UTILITY);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {

      EntryCheckDialog dialog =
         new EntryCheckDialog(getName(), "Enter Lines.  Lines to be commma delimited.", "Remove pre/post spaces");
      dialog.setChecked(true);
      dialog.setFillVertically(true);
      if (dialog.open() == Window.OK) {
         XResultData resultData = new XResultData();
         boolean cleanSpaces = dialog.isChecked();
         StringBuffer sb = new StringBuffer();
         for (String str : dialog.getEntry().split("[\n\r]+")) {
            if (cleanSpaces) {
               str = str.replaceAll("^ ", "");
               str = str.replaceAll(" $", "");
            }
            if (Strings.isValid(str)) {
               sb.append(str + ",");
            }
         }
         resultData.log(sb.toString().replace(",$", ""));
         XResultDataUI.report(resultData, getName());
      }
   }
}
