/*
 * Created on Aug 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.search.engine.internal.search;

import java.util.Collection;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;

public interface ISearchFactory {

   Collection<AttributeData> queryMatches();
}
