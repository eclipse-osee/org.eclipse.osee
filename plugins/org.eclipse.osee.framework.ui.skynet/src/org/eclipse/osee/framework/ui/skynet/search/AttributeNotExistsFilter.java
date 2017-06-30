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

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeNotExistsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTree;
import org.eclipse.swt.widgets.Control;

/**
 * @author John Misinco
 */
public class AttributeNotExistsFilter extends SearchFilter {
   private final FilteredCheckboxTree attributeTypeList;

   public AttributeNotExistsFilter(Control optionsControl, FilteredCheckboxTree attributeTypeList) {
      super("Attribute Not Exists", optionsControl);
      this.attributeTypeList = attributeTypeList;
   }

   @Override
   public void addFilterTo(FilterTableViewer filterViewer) {
      Collection<AttributeTypeId> attrTypes = attributeTypeList.getChecked();
      List<AttributeTypeId> attributeTypes = Collections.castAll(attrTypes);

      ISearchPrimitive primitive = new AttributeNotExistsSearch(attributeTypes);
      filterViewer.addItem(primitive, getFilterName(), attributeTypes.toString(), "");
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

   @Override
   public String getSearchDescription() {
      return "Using multiple attribute types in the same filter will return artifacts where at least one does not exist." + "\nUsing separate attribute type filters will only return artifacts where all the attribute types do not exist.";
   }

}
