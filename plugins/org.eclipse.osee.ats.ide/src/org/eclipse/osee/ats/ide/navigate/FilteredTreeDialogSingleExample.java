/*********************************************************************
 * Copyright (c) 2014 Boeing
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

import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;

/**
 * Example of using FilteredTreeDialog single-selection
 *
 * @author Donald G. Dunne
 */
public class FilteredTreeDialogSingleExample extends XNavigateItemAction {

   public FilteredTreeDialogSingleExample() {
      super("FilteredTreeDialogSingle Example", FrameworkImage.EXAMPLE, XNavigateItem.UTILITY_EXAMPLES);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      FilteredTreeDialog dialog = FilteredTreeDialogExample.getDialog();
      dialog.setInput(FilteredDialogExampleUtil.getInput());
      dialog.setMultiSelect(false);
      FilteredDialogExampleUtil.openAndReport(dialog, getName());
   }

}
