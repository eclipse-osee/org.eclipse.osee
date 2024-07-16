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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.ide.navigate.FilteredDialogExampleUtil.MyTask;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;

/**
 * @author Donald G. Dunne
 */
public class FilteredCheckboxTreeDialogSelectAllExample extends XNavigateItemAction {

   public FilteredCheckboxTreeDialogSelectAllExample() {
      super("FilteredCheckboxTreeDialogSelectAll Example", FrameworkImage.EXAMPLE, XNavigateItem.UTILITY_EXAMPLES);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      List<MyTask> input = FilteredDialogExampleUtil.getInput();
      List<FilteredDialogExampleUtil.MyTask> initialSelection = new ArrayList<>();
      initialSelection.add(FilteredDialogExampleUtil.getChild3());
      initialSelection.add(FilteredDialogExampleUtil.getParent11());
      FilteredCheckboxTreeDialog<MyTask> dialog = new FilteredCheckboxTreeDialog<MyTask>("My Title", "Message",
         new FilteredDialogExampleUtil.MyTreeContentProvider(), new FilteredDialogExampleUtil.FilterLabelProvider(),
         new FilteredDialogExampleUtil.MyViewSorter());
      dialog.setInput(input);
      dialog.setShowSelectButtons(true);
      dialog.setExpandChecked(true);
      FilteredDialogExampleUtil.openAndReport(dialog, getName());
   }

}
