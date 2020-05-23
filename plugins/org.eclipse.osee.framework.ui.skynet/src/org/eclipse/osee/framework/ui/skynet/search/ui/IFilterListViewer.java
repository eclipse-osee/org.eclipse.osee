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

package org.eclipse.osee.framework.ui.skynet.search.ui;

import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModel;

public interface IFilterListViewer {

   /**
    * Update the view to reflect the fact that a FilterModel was added to the FilterModel list
    */
   public void addFilter(FilterModel filter);

   /**
    * Update the view to reflect the fact that a FilterModel was removed from the FilterModel list
    */
   public void removeFilter(FilterModel filter);

   /**
    * Update the view to reflect the fact that one of the FilterModels was modified
    */
   public void updateFilter(FilterModel filter);
}
