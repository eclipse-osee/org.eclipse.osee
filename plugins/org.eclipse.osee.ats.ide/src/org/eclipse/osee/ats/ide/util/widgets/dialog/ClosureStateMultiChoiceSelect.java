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
package org.eclipse.osee.ats.ide.util.widgets.dialog;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
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

   public static final String WIDGET_ID = ClosureStateMultiChoiceSelect.class.getSimpleName();

   public ClosureStateMultiChoiceSelect() {
      super("Select Version(s)");
      setSelectableItems(AttributeTypeManager.getEnumerationValues(AtsAttributeTypes.ClosureState));
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
      dialog.setInput(AttributeTypeManager.getEnumerationValues(AtsAttributeTypes.ClosureState));
      return dialog;
   }
}
