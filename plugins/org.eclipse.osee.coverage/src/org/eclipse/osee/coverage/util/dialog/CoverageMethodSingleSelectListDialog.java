/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.coverage.util.dialog;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManager.EnabledOption;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ToStringViewerSorter;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class CoverageMethodSingleSelectListDialog extends ListDialog {

   public CoverageMethodSingleSelectListDialog(CoverageOptionManager optionManager, EnabledOption enabledOption) {
      super(Displays.getActiveShell());
      setTitle("Select Coverage Method");
      setMessage("Select Coverage Method");
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new CoverageMethodLabelProvider());
      setInput(optionManager.getEnabled(enabledOption));
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      getTableViewer().setSorter(new ToStringViewerSorter());
      return c;
   }

   @Override
   protected void okPressed() {
      if (getTableViewer().getSelection().isEmpty()) {
         AWorkbench.popup("ERROR", "Must make selection.");
         return;
      }
      super.okPressed();
   }

}
