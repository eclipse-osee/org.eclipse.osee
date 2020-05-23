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

package org.eclipse.osee.framework.ui.skynet.search;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTree;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Ryan D. Brooks
 */
public class AttributeValueFilter extends SearchFilter {
   private final FilteredTree filteredTree;
   private final Text attributeValue;

   public AttributeValueFilter(Control optionsControl, FilteredTree filteredTree, Text attributeValue) {
      super("Attribute Value", optionsControl);
      this.filteredTree = filteredTree;
      this.attributeValue = attributeValue;
   }

   @Override
   public void addFilterTo(FilterTableViewer filterViewer) {
      ISelection selection = filteredTree.getViewer().getSelection();
      if (selection.isEmpty()) {
         return;
      }
      AttributeTypeId attributeType =
         (AttributeTypeId) ((StructuredSelection) filteredTree.getViewer().getSelection()).getFirstElement();

      String attVal = attributeValue.getText();
      ISearchPrimitive primitive = new AttributeValueSearch(attributeType, attVal);
      filterViewer.addItem(primitive, getFilterName(), attributeType.toString(), attVal);
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

   @Override
   public String getSearchDescription() {
      return "Using the attribute type and entering a value will return all artifacts that contain the attribute with the specified value.";
   }

}
