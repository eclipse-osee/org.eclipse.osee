/*
 * Created on May 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.osgi.framework.Version;

public class ExchangeTransformProvider implements IExchangeTransformProvider {

   private final IOseeCachingService cachingService;

   public ExchangeTransformProvider(IOseeCachingService cachingService) {
      this.cachingService = cachingService;
   }

   public Collection<IOseeExchangeVersionTransformer> getApplicableTransformers(Version versionToCheck) {
      List<IOseeExchangeVersionTransformer> toReturn = new ArrayList<IOseeExchangeVersionTransformer>();

      IOseeExchangeVersionTransformer[] transforms =
            new IOseeExchangeVersionTransformer[] {new V0_8_3Transformer(), new V0_9_0Transformer(cachingService),
                  new V0_9_2Transformer()};

      for (IOseeExchangeVersionTransformer transformer : transforms) {
         if (isApplicable(transformer.getMaxVersion(), versionToCheck)) {
            toReturn.add(transformer);
         }
      }
      return toReturn;
   }

   private static boolean isApplicable(Version maxVersion, Version versionToCheck) {
      return maxVersion.compareTo(versionToCheck) > 0;
   }
}
