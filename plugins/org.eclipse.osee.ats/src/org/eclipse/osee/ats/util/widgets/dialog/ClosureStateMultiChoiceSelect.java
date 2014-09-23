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
package org.eclipse.osee.ats.util.widgets.dialog;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.MinMaxOSEECheckedFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromDialog;

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
   public MinMaxOSEECheckedFilteredTreeDialog createDialog() {
      SimpleCheckFilteredTreeDialog dialog =
         new SimpleCheckFilteredTreeDialog(getLabel(), "Select from the Closure States below",
            new ArrayTreeContentProvider(), new LabelProvider(), new AtsObjectNameSorter(), 1, 1000);
      return dialog;
   }

}
