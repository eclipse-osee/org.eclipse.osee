/*
 * Created on Sep 28, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.relation;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.RelationContainer;
import org.eclipse.osee.orcs.core.ds.RelationRow;
import org.eclipse.osee.orcs.core.ds.RelationRowHandler;

public class RelationRowMapper implements RelationRowHandler {

   //   private final Log logger;
   private final List<? extends RelationContainer> providersThatWillBeLoaded;
   RelationRowComparator relationRowComparator = new RelationRowComparator();
   private final SearchContainer searchContainer;

   public RelationRowMapper(Log logger, List<? extends RelationContainer> providersThatWillBeLoaded) {
      //      this.logger = logger;
      Collections.sort(providersThatWillBeLoaded, relationRowComparator);
      this.providersThatWillBeLoaded = providersThatWillBeLoaded;
      this.searchContainer = new SearchContainer();
   }

   @Override
   public void onRow(RelationRow nextRelation) throws OseeCoreException {
      RelationContainer parent = findParentContainer(nextRelation.getParentId());
      parent.add(nextRelation);
   }

   private RelationContainer findParentContainer(int parentId) throws OseeCoreException {
      searchContainer.setParentId(parentId);
      int index = Collections.binarySearch(providersThatWillBeLoaded, searchContainer, relationRowComparator);
      if (index == -1) {
         throw new OseeCoreException(
            "We recieved a RelationRow that should be added to a parent that wasn't found [%d]", parentId);
      } else {
         return providersThatWillBeLoaded.get(index);
      }
   }
}
