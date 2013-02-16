/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.api.components;

import java.util.Collection;

public interface PagingComponent {
   //How many items are in your collection?  i.e. Collection<T>.size()
   public void setManyItemsTotal(int manyItemsTotal);

   public void gotoFirstPage();

   public void gotoPrevPage();

   public void gotoNextPage();

   public void gotoLastPage();

   public Collection<Integer> getCurrentVisibleItemIndices();

   public void setManyItemsPerPage(int manyItemsPerPage);

   public void setAllItemsPerPage();

   public int getManyItemsPerPage();

}
