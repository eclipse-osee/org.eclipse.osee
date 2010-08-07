/*
 * Created on Aug 6, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.dsl.integration;

import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface OseeDslProvider {

   void loadDsl() throws OseeCoreException;

   OseeDsl getDsl() throws OseeCoreException;

   void storeDsl(OseeDsl dsl) throws OseeCoreException;
}
