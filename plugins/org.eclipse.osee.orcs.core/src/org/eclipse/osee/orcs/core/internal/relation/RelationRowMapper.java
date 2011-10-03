/*
 * Created on Sep 28, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.relation;

import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.RelationContainer;
import org.eclipse.osee.orcs.core.ds.RelationRow;
import org.eclipse.osee.orcs.core.ds.RelationRowHandler;

public class RelationRowMapper implements RelationRowHandler {

   //   private final Log logger;
   private final Map<Integer, ? extends RelationContainer> providersThatWillBeLoaded;

   public RelationRowMapper(Log logger, Map<Integer, ? extends RelationContainer> providersThatWillBeLoaded) {
      //      this.logger = logger;
      this.providersThatWillBeLoaded = providersThatWillBeLoaded;
   }

   @Override
   public void onRow(RelationRow nextRelation) throws OseeCoreException {
      int parentId = nextRelation.getParentId();
      RelationContainer parent = providersThatWillBeLoaded.get(parentId);
      Conditions.checkNotNull(parent, "RelationContainer",
         "We recieved a RelationRow that should be added to a parent that wasn't found [%d]", parentId);
      parent.add(nextRelation);
   }
}
