/*
 * Created on Mar 26, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import java.util.Collection;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IResourceProvider {
   Collection<String> getErrors() throws OseeCoreException;

   AtsDsl getContents(String uri, String xTextData) throws OseeCoreException;

}
