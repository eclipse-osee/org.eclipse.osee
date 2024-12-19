/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.agile;

import java.util.Collection;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class SprintFilteredListDialog extends FilteredTreeDialog {

   public SprintFilteredListDialog(String dialogTitle, String dialogMessage, Collection<? extends IAgileSprint> values) {
      super(dialogTitle, dialogMessage, new ArrayTreeContentProvider(), new StringLabelProvider(),
         new StringNameComparator());
      setInput(values);
   }

   @Override
   protected Control createCustomArea(Composite parent) {
      super.createCustomArea(parent);

      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
      gd.widthHint = 500;
      composite.setLayoutData(gd);

      createCheckbox(composite);

      composite.layout();
      parent.layout();
      return composite;
   }

   private void createCheckbox(Composite parent) {

      final XCheckBox checkbox = new XCheckBox("Remove from Sprint");
      checkbox.setFillHorizontally(true);
      checkbox.setFocus();
      checkbox.setDisplayLabel(false);
      checkbox.createWidgets(parent, 2);

      SelectionListener selectionListener = new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            clearSelected = checkbox.isSelected();
            if (clearSelected) {
               getButton(getDefaultButtonIndex()).setEnabled(true);
            } else {
               getButton(getDefaultButtonIndex()).setEnabled(false);
               updateSelected();
               updateStatusLabel();
            }
         }
      };
      checkbox.addSelectionListener(selectionListener);

   }

   public boolean isRemoveFromSprint() {
      return clearSelected;
   }

}
