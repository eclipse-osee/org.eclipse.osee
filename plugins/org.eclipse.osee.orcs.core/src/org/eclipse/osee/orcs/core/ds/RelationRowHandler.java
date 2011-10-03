/*
 * Created on Sep 28, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface RelationRowHandler {

   void onRow(RelationRow nextRelation) throws OseeCoreException;

}
