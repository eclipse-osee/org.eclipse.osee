/*
 * Created on Oct 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
