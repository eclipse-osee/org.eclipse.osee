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
package org.eclipse.osee.framework.ui.skynet.search;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeNotExistsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Control;

/**
 * @author John Misinco
 */
public class AttributeNotExistsFilter extends SearchFilter {
   private final ComboViewer attributeTypeList;

   public AttributeNotExistsFilter(Control optionsControl, ComboViewer attributeTypeList) {
      super("Attribute Not Exists", optionsControl);
      this.attributeTypeList = attributeTypeList;
   }

   @Override
   public void addFilterTo(FilterTableViewer filterViewer) {
      String typeName = attributeTypeList.getCombo().getText();

      IAttributeType attributeType = (IAttributeType) attributeTypeList.getData(typeName);
      ISearchPrimitive primitive = new AttributeNotExistsSearch(attributeType);
      filterViewer.addItem(primitive, getFilterName(), typeName, Strings.EMPTY_STRING);
   }

   @Override
   public boolean isValid() {
      return true;
   }

   @Override
   public void loadFromStorageString(FilterTableViewer filterViewer, String type, String value, String storageString, boolean isNotEnabled) {
      ISearchPrimitive primitive = AttributeNotExistsSearch.getPrimitive(storageString);
      filterViewer.addItem(primitive, getFilterName(), type, value);
   }

}
