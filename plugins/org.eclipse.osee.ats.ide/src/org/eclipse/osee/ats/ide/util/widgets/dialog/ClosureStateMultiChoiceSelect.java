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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Angel Avila
 */
public class ClosureStateMultiChoiceSelect extends XSelectFromDialog<String> {

   public ClosureStateMultiChoiceSelect() {
      super("Select Version(s)");
      setSelectableItems(AtsAttributeTypes.ClosureState.getEnumStrValues());
   }

   @Override
   public void createControls(Composite parent, int horizontalSpan, boolean fillText) {
      super.createControls(parent, horizontalSpan, fillText);
      GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
      layoutData.heightHint = 80;
      getStyledText().setLayoutData(layoutData);
   }

   @Override
   public FilteredCheckboxTreeDialog<String> createDialog() {
      FilteredCheckboxTreeDialog<String> dialog =
         new FilteredCheckboxTreeDialog<String>(getLabel(), "Select from the Closure States below",
            new ArrayTreeContentProvider(), new LabelProvider(), new AtsObjectNameSorter());
      dialog.setInput(AtsAttributeTypes.ClosureState.getEnumStrValues());
      return dialog;
   }
}
