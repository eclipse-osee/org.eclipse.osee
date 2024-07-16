/*********************************************************************
 * Copyright (c) 2013 Boeing
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
