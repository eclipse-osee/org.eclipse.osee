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
package org.eclipse.osee.ats.ide.workflow.goal;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
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
public class GoalFilteredListDialog extends FilteredTreeDialog {

   private boolean removeFromGoal;

   public GoalFilteredListDialog(String dialogTitle, String dialogMessage, Collection<ArtifactToken> goals) {
      super(dialogTitle, dialogMessage, new ArrayTreeContentProvider(), new StringLabelProvider(),
         new StringNameComparator());
      setInput(goals);
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

      final XCheckBox checkbox = new XCheckBox("Remove from Goal");
      checkbox.setFillHorizontally(true);
      checkbox.setFocus();
      checkbox.setDisplayLabel(false);
      checkbox.set(removeFromGoal);
      checkbox.createWidgets(parent, 2);

      SelectionListener selectionListener = new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            removeFromGoal = checkbox.isSelected();
            if (removeFromGoal) {
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

   public boolean isRemoveFromGoal() {
      return removeFromGoal;
   }

}
