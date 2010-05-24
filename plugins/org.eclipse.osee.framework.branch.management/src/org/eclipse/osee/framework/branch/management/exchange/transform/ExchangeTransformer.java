/*
 * Created on May 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.branch.management.exchange.OseeServices;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;

public class ExchangeTransformer {

   private final List<IOseeExchangeVersionTransformer> transformers;
   private final OseeServices oseeServices;

   private final ExchangeDataProcessor processor;

   public ExchangeTransformer(OseeServices oseeServices, ExchangeDataProcessor processor) {
      this.transformers = new ArrayList<IOseeExchangeVersionTransformer>();
      this.oseeServices = oseeServices;
      this.processor = processor;
   }

   public void applyTransforms() throws Exception {
      IOseeExchangeVersionTransformer[] transforms =
            new IOseeExchangeVersionTransformer[] {new V0_8_3Transformer(),
                  new V0_9_0Transformer(oseeServices.getCachingService()), new V0_9_2Transformer()};

      ManifestVersionRule versionRule = new ManifestVersionRule();
      versionRule.setReplaceVersion(false);
      processor.transform(ExportItem.EXPORT_MANIFEST, versionRule);
      String version = versionRule.getVersion();
      versionRule.setReplaceVersion(true);

      for (IOseeExchangeVersionTransformer transformer : transforms) {
         if (transformer.isApplicable(version)) {
            version = transformer.applyTransform(processor);
            versionRule.setVersion(version);
            processor.transform(ExportItem.EXPORT_MANIFEST, versionRule);
            transformers.add(transformer);
         }
      }
   }

   public void applyFinalTransforms() throws Exception {
      for (IOseeExchangeVersionTransformer transform : transformers) {
         transform.finalizeTransform(processor);
      }
   }
}
