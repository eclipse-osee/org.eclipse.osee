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
package org.eclipse.osee.framework.ui.skynet.search.ui;

import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModel;

public interface IFilterListViewer {

   /**
    * Update the view to reflect the fact that a FilterModel was added to the FilterModel list
    * 
    * @param filter
    */
   public void addFilter(FilterModel filter);

   /**
    * Update the view to reflect the fact that a FilterModel was removed from the FilterModel list
    * 
    * @param filter
    */
   public void removeFilter(FilterModel filter);

   /**
    * Update the view to reflect the fact that one of the FilterModels was modified
    * 
    * @param filter
    */
   public void updateFilter(FilterModel filter);
}
