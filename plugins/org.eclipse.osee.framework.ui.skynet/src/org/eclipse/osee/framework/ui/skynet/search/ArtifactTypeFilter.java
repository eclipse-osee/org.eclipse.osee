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

import static org.eclipse.osee.framework.skynet.core.artifact.search.DeprecatedOperator.EQUAL;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.NotSearch;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactTypeFilter extends SearchFilter {
   private ListViewer searchTypeList;

   public ArtifactTypeFilter(Control optionsControl, ListViewer searchTypeList) {
      super("Artifact Type", optionsControl);
      this.searchTypeList = searchTypeList;
   }

   @Override
   public void addFilterTo(FilterTableViewer filterViewer) {
      for (String type : searchTypeList.getList().getSelection()) {
         ISearchPrimitive primitive = new ArtifactTypeSearch(type, EQUAL);
         if (not) primitive = new NotSearch(primitive);
         filterViewer.addItem(primitive, getFilterName(), type, "");
      }
   }

   @Override
   public boolean isValid() {
      return true;
   }

   @Override
   public void loadFromStorageString(FilterTableViewer filterViewer, String type, String value, String storageString, boolean isNotEnabled) {
      ISearchPrimitive primitive = ArtifactTypeSearch.getPrimitive(storageString);
      if (isNotEnabled) primitive = new NotSearch(primitive);
      filterViewer.addItem(primitive, getFilterName(), type, value);
   }
}