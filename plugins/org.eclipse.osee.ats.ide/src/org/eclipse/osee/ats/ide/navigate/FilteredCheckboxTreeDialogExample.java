/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
public class FilteredCheckboxTreeDialogExample extends XNavigateItemAction {

   public FilteredCheckboxTreeDialogExample(XNavigateItem parent) {
      super(parent, "FilteredCheckboxTreeDialog Example", FrameworkImage.GEAR);
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
      dialog.setInitialSelections(initialSelection);
      dialog.setExpandChecked(true);
      FilteredDialogExampleUtil.openAndReport(dialog, getName());
   }

}
