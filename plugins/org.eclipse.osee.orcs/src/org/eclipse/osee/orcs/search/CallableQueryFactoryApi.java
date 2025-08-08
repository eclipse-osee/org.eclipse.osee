/*
 * Created on Mar 5, 2025
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.search;

import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;

public interface CallableQueryFactoryApi {

   ResultSet<ArtifactReadable> createSearch(QueryData queryData); // TBD

   public ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> createSearchWithMatches(OrcsSession session,
      QueryData queryData);

}
