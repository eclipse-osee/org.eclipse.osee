/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.panels;

import java.util.Collection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.AttributeTypeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxAttributeTypeDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeSelectPanel extends AbstractItemSelectPanel<Collection<AttributeTypeToken>> {

   private Collection<AttributeTypeToken> attributeTypes;
   private String title;
   private String message;

   public AttributeTypeSelectPanel() {
      super(new AttributeTypeLabelProvider(), new ArrayContentProvider());
      this.title = "";
      this.message = "";
   }

   public void setAllowedAttributeTypes(Collection<AttributeTypeToken> attributeTypes) {
      this.attributeTypes = attributeTypes;
   }

   public void setDialogTitle(String title) {
      this.title = title;
   }

   public void setDialogMessage(String message) {
      this.message = message;
   }

   @Override
   protected Dialog createSelectDialog(Shell shell, Collection<AttributeTypeToken> lastSelected) {
      FilteredCheckboxAttributeTypeDialog dialog =
         new FilteredCheckboxAttributeTypeDialog(title, message, attributeTypes);
      if (lastSelected != null) {
         dialog.setInitialSelections(lastSelected);
      }
      return dialog;
   }

   @Override
   protected boolean updateFromDialogResult(Dialog dialog) {
      boolean wasUpdated = false;
      FilteredCheckboxAttributeTypeDialog castedDialog = (FilteredCheckboxAttributeTypeDialog) dialog;
      Collection<AttributeTypeToken> attributeTypes = castedDialog.getChecked();
      if (attributeTypes != null) {
         setSelected(attributeTypes);
         wasUpdated = true;
      }
      return wasUpdated;
   }
}
