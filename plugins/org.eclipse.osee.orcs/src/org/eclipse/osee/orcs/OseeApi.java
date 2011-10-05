/*
 * Created on Oct 4, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs;

import org.eclipse.osee.orcs.search.QueryFactory;

public interface OseeApi {

   QueryFactory getQueryFactory(ApplicationContext context);

}
