/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.search;

import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.NotInRelationSearch;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class OrphanSearchFilter extends NotInRelationFilter {

   public OrphanSearchFilter(Control optionsControl) {
      super(optionsControl, null, null);
   }

   @Override
   protected String getFilterName() {
      return "Orphan Search";
   }

   @Override
   public void addFilterTo(FilterTableViewer filterViewer) {
      ISearchPrimitive primitive = new NotInRelationSearch(CoreRelationTypes.Default_Hierarchical__Child, false);
      filterViewer.addItem(primitive, getFilterName(), CoreRelationTypes.Default_Hierarchical__Child.getName(),
         "Child");
   }

   @Override
   public void loadFromStorageString(FilterTableViewer filterViewer, String type, String value, String storageString, boolean isNotEnabled) {
      // do nothing
   }

   @Override
   public String getSearchDescription() {
      return "Find all artifacts that have no parent default hierarchy relation.";
   }

}
