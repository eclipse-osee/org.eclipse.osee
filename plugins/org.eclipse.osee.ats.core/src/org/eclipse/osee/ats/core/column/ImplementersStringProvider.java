/*
 * Created on Mar 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface ImplementersStringProvider {

   String getImplementersStr(Object object) throws OseeCoreException;

}
