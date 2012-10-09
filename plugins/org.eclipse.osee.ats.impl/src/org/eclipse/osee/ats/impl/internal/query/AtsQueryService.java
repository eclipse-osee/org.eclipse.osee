/*
 * Created on Aug 2, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.impl.internal.query;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryService;

public class AtsQueryService implements IAtsQueryService {

   @Override
   public IAtsQuery createQuery(Collection<? extends IAtsWorkItem> workItems) {
      return new AtsQuery(workItems);
   }

}
