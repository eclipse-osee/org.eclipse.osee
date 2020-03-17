/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;

/**
 * Multi selection of attribute types with checkbox dialog and filtering
 *
 * @author Donald G. Dunne
 */
public class XAttributeTypeMultiChoiceSelect extends XSelectFromDialog<AttributeType> {

   public static final String WIDGET_ID = XAttributeTypeMultiChoiceSelect.class.getSimpleName();

   public XAttributeTypeMultiChoiceSelect() {
      super("Select Attribute Type(s)");
      try {
         setSelectableItems(AttributeTypeManager.getAllTypes());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public FilteredCheckboxTreeDialog<AttributeType> createDialog() {
      FilteredCheckboxTreeDialog<AttributeType> dialog = new FilteredCheckboxTreeDialog<AttributeType>(getLabel(),
         "Select from the items below", new ArrayTreeContentProvider(), new LabelProvider(), new ArtifactNameSorter());
      dialog.setInput(AttributeTypeManager.getAllTypes());
      return dialog;
   }

}
