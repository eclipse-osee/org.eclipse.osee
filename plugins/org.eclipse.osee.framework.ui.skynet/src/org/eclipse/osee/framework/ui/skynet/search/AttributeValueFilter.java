/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
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
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Ryan D. Brooks
 */
public class AttributeValueFilter extends SearchFilter {
   private final ComboViewer attributeTypeList;
   private final Text attributeValue;

   public AttributeValueFilter(Control optionsControl, ComboViewer attributeTypeList, Text attributeValue) {
      super("Attribute Value", optionsControl);
      this.attributeTypeList = attributeTypeList;
      this.attributeValue = attributeValue;
   }

   @Override
   public void addFilterTo(FilterTableViewer filterViewer) {
      String typeName = attributeTypeList.getCombo().getText();
      String value = attributeValue.getText();

      IAttributeType attributeType = (IAttributeType) attributeTypeList.getData(typeName);
      ISearchPrimitive primitive = new AttributeValueSearch(attributeType, value);
      filterViewer.addItem(primitive, getFilterName(), typeName, value);
   }

   @Override
   public boolean isValid() {
      return !attributeValue.getText().equals("");
   }

   @Override
   public void loadFromStorageString(FilterTableViewer filterViewer, String type, String value, String storageString, boolean isNotEnabled) {
      ISearchPrimitive primitive = AttributeValueSearch.getPrimitive(storageString);
      filterViewer.addItem(primitive, getFilterName(), type, value);
   }

}
