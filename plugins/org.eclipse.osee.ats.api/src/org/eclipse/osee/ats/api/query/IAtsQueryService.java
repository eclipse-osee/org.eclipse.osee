/*
 * Created on Oct 8, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.query;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;

public interface IAtsQueryService {

   IAtsQuery createQuery(Collection<? extends IAtsWorkItem> workItems);

}
