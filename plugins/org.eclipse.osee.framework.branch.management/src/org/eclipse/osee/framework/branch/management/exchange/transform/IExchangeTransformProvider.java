/*
 * Created on May 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.util.Collection;
import org.osgi.framework.Version;

public interface IExchangeTransformProvider {

   Collection<IOseeExchangeVersionTransformer> getApplicableTransformers(Version versionToCheck);
}
