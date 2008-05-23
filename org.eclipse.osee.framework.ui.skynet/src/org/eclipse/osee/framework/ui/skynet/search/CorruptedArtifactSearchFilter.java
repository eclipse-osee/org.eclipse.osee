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

import org.eclipse.osee.framework.skynet.core.artifact.search.CorruptedArtifactSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.NotSearch;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ryan D. Brooks
 */
public class CorruptedArtifactSearchFilter extends SearchFilter {

   public CorruptedArtifactSearchFilter(Control optionsControl) {
      super("Corrupted Artifacts", optionsControl);
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.search.SearchFilter#addFilterTo(osee.define.artifact.search.filter.FilterTableViewer)
    */
   @Override
   public void addFilterTo(FilterTableViewer filterViewer) {
      ISearchPrimitive primitive = new CorruptedArtifactSearch();
      if (not) primitive = new NotSearch(primitive);
      filterViewer.addItem(primitive, getFilterName(), "", "");
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.search.SearchFilter#isValid()
    */
   @Override
   public boolean isValid() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.SearchFilter#loadFromStorageString(org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer, java.lang.String, java.lang.String, java.lang.String, boolean)
    */
   @Override
   public void loadFromStorageString(FilterTableViewer filterViewer, String type, String value, String storageString, boolean isNotEnabled) {
      ISearchPrimitive primitive = new CorruptedArtifactSearch();
      if (isNotEnabled) primitive = new NotSearch(primitive);
      filterViewer.addItem(primitive, getFilterName(), type, value);
   }
}
