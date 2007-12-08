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
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.NotSearch;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Ryan D. Brooks
 */
public class AttributeValueFilter extends SearchFilter {
   private ComboViewer attributeTypeList;
   private Text attributeValue;

   public AttributeValueFilter(Control optionsControl, ComboViewer attributeTypeList, Text attributeValue) {
      super("Attribute Value", optionsControl);
      this.attributeTypeList = attributeTypeList;
      this.attributeValue = attributeValue;
   }

   /* (non-Javadoc)
    * @see osee.define.artifact.search.SearchFilter#addFilterTo(osee.define.artifact.search.filter.FilterTableViewer)
    */
   @Override
   public void addFilterTo(FilterTableViewer filterViewer) {
      String type = attributeTypeList.getCombo().getText();
      String value = attributeValue.getText();

      OperatorAndValue result = handleWildCard(value);
      ISearchPrimitive primitive = new AttributeValueSearch(type, result.value, result.operator);
      if (not) primitive = new NotSearch(primitive);
      filterViewer.addItem(primitive, filterName, type, result.value);
   }

   /* (non-Javadoc)
    * @see osee.define.artifact.search.SearchFilter#isValid()
    */
   @Override
   public boolean isValid() {
      return !attributeValue.getText().equals("");
   }
}
