/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.exchange.transform;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;
import org.osgi.framework.Version;

public class ExchangeTransformer {

   private final Log logger;
   private final OrcsSession session;
   private final JdbcClient jdbcClient;
   private final IExchangeTransformProvider provider;
   private final ExchangeDataProcessor processor;

   private Collection<IOseeExchangeVersionTransformer> transformers;

   public ExchangeTransformer(Log logger, OrcsSession session, JdbcClient jdbcClient, IExchangeTransformProvider provider, ExchangeDataProcessor processor) {
      this.logger = logger;
      this.session = session;
      this.jdbcClient = jdbcClient;
      this.provider = provider;
      this.processor = processor;
   }

   public void applyTransforms() throws Exception {
      Version exchangeVersion = getExchangeManifestVersion();
      transformers = provider.getApplicableTransformers(exchangeVersion);

      ManifestVersionRule versionRule = new ManifestVersionRule();
      versionRule.setVersion(exchangeVersion);
      versionRule.setReplaceVersion(true);

      for (IOseeExchangeVersionTransformer transformer : transformers) {
         Version newVersion = transformer.applyTransform(processor, logger);
         versionRule.setVersion(newVersion);
         processor.transform(ExportItem.EXPORT_MANIFEST, versionRule);
      }
   }

   public void applyFinalTransforms() throws Exception {
      Conditions.checkNotNull(transformers, "transformers", "forgot to call apply transforms first");
      for (IOseeExchangeVersionTransformer transform : transformers) {
         transform.finalizeTransform(logger, session, jdbcClient, processor);
      }
      transformers = null;
   }

   public Version getExchangeManifestVersion() {
      ManifestVersionRule versionRule = new ManifestVersionRule();
      versionRule.setReplaceVersion(false);
      processor.transform(ExportItem.EXPORT_MANIFEST, versionRule);
      String version = versionRule.getVersion();
      return Strings.isValid(version) ? new Version(version) : new Version("0.0.0");
   }
}
