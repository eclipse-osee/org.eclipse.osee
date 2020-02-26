/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collections;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;

/**
 * @author Donald G. Dunne
 */
public class AtsObjectMultiChoiceSelect extends XSelectFromDialog<IAtsObject> {

   public static final String WIDGET_ID = AtsObjectMultiChoiceSelect.class.getSimpleName();

   public AtsObjectMultiChoiceSelect() {
      super("Select Artifact (s)");
      setSelectableItems(Collections.<IAtsObject> emptyList());
   }

   @Override
   public FilteredCheckboxTreeDialog<IAtsObject> createDialog() {
      FilteredCheckboxTreeDialog<IAtsObject> dialog = new FilteredCheckboxTreeDialog<IAtsObject>(getLabel(),
         "Select from the items below", new ArrayTreeContentProvider(), new LabelProvider(), new AtsObjectNameSorter());
      return dialog;
   }

}
