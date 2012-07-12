/*
 * Created on Mar 26, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.dsl;

import java.util.Collection;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;

/**
 * @author Donald G. Dunne
 */
public interface IResourceProvider {
   Collection<String> getErrors();

   AtsDsl getContents(String uri, String xTextData) throws Exception;

}
