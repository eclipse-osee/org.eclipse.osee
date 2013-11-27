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
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;
import org.osgi.framework.Version;

public class ExchangeTransformer {

   private final IOseeDatabaseService dbService;
   private final IExchangeTransformProvider provider;
   private final ExchangeDataProcessor processor;

   private Collection<IOseeExchangeVersionTransformer> transformers;

   public ExchangeTransformer(IOseeDatabaseService dbService, IExchangeTransformProvider provider, ExchangeDataProcessor processor) {
      this.dbService = dbService;
      this.provider = provider;
      this.processor = processor;
   }

   public void applyTransforms(OperationLogger logger) throws Exception {
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

   public void applyFinalTransforms(OperationLogger logger) throws Exception {
      Conditions.checkNotNull(transformers, "transformers", "forgot to call apply transforms first");
      for (IOseeExchangeVersionTransformer transform : transformers) {
         transform.finalizeTransform(dbService, processor, logger);
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
