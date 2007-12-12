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
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.InRelationSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.NotSearch;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLinkDescriptor;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ryan D. Brooks
 */
public class InRelationFilter extends SearchFilter {
   private ComboViewer relationTypeList;
   private ComboViewer relationSideList;

   public InRelationFilter(Control optionsControl, ComboViewer relationTypeList, ComboViewer relationSideList) {
      super("Artifact in Relation", optionsControl);
      this.relationTypeList = relationTypeList;
      this.relationSideList = relationSideList;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.search.SearchFilter#addFilterTo(osee.define.artifact.search.filter.FilterTableViewer)
    */
   @Override
   public void addFilterTo(FilterTableViewer filterViewer) {
      String type = relationTypeList.getCombo().getText();
      String sideName = relationSideList.getCombo().getText();

      IRelationLinkDescriptor linkDescriptor =
            (IRelationLinkDescriptor) relationTypeList.getData(relationTypeList.getCombo().getText());
      ISearchPrimitive primitive = new InRelationSearch(type, linkDescriptor.isSideAName(sideName));
      if (not) primitive = new NotSearch(primitive);

      filterViewer.addItem(primitive, filterName, type, sideName);
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
}
