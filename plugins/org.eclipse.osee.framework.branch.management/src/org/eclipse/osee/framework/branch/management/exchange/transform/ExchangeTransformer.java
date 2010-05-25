/*
 * Created on May 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.util.Collection;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.osgi.framework.Version;

public class ExchangeTransformer {

   private final IExchangeTransformProvider provider;
   private final ExchangeDataProcessor processor;

   private Collection<IOseeExchangeVersionTransformer> transformers;

   public ExchangeTransformer(IExchangeTransformProvider provider, ExchangeDataProcessor processor) {
      this.provider = provider;
      this.processor = processor;
   }

   public void applyTransforms() throws Exception {
      Version exchangeVersion = getExchangeManifestVersion();
      transformers = provider.getApplicableTransformers(exchangeVersion);

      ManifestVersionRule versionRule = new ManifestVersionRule();
      versionRule.setVersion(exchangeVersion.toString());
      versionRule.setReplaceVersion(true);

      for (IOseeExchangeVersionTransformer transformer : transformers) {
         String newVersion = transformer.applyTransform(processor);
         versionRule.setVersion(newVersion);
         processor.transform(ExportItem.EXPORT_MANIFEST, versionRule);
      }
   }

   public void applyFinalTransforms() throws Exception {
      Conditions.checkNotNull(transformers, "transformers", "forgot to call apply transforms first");
      for (IOseeExchangeVersionTransformer transform : transformers) {
         transform.finalizeTransform(processor);
      }
      transformers = null;
   }

   public Version getExchangeManifestVersion() throws OseeCoreException {
      ManifestVersionRule versionRule = new ManifestVersionRule();
      versionRule.setReplaceVersion(false);
      processor.transform(ExportItem.EXPORT_MANIFEST, versionRule);
      String version = versionRule.getVersion();
      return Strings.isValid(version) ? new Version(version) : new Version("0.0.0");
   }
}
