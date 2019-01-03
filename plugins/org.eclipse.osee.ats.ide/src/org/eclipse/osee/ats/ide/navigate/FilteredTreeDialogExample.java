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

import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;

/**
 * Example of using FilteredTreeDialog multi-selection
 * 
 * @author Donald G. Dunne
 */
public class FilteredTreeDialogExample extends XNavigateItemAction {

   public FilteredTreeDialogExample(XNavigateItem parent) {
      super(parent, "FilteredTreeDialog Example", FrameworkImage.GEAR);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      FilteredTreeDialog dialog = getDialog();
      dialog.setInput(FilteredDialogExampleUtil.getInput());
      FilteredDialogExampleUtil.openAndReport(dialog, getName());
   }

   protected static FilteredTreeDialog getDialog() {
      return new FilteredTreeDialog("My Title", "Message", new FilteredDialogExampleUtil.MyTreeContentProvider(),
         new FilteredDialogExampleUtil.FilterLabelProvider(), new FilteredDialogExampleUtil.MyViewSorter());
   }

}
