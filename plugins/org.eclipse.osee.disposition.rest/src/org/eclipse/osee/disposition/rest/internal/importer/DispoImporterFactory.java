/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal.importer;

import org.eclipse.osee.disposition.rest.DispoApiConfiguration;
import org.eclipse.osee.disposition.rest.DispoImporterApi;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.DispoDataFactory;
import org.eclipse.osee.disposition.rest.internal.importer.coverage.LisFileParser;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.logger.Log;

/**
 * @author John Misinco
 */
public class DispoImporterFactory {

   private final DispoDataFactory dataFactory;
   private final ExecutorAdmin executor;
   private final Log logger;
   private final DispoApiConfiguration config;

   public enum ImportFormat {
      TMO,
      TMZ,
      LIS
   };

   public DispoImporterFactory(DispoDataFactory dataFactory, ExecutorAdmin executor, DispoApiConfiguration config, Log logger) {
      this.dataFactory = dataFactory;
      this.executor = executor;
      this.logger = logger;
      this.config = config;
   }

   public DispoImporterApi createImporter(ImportFormat format, DispoConnector connector) {
      switch (format) {
         case TMO:
            return new TmoImporter(dataFactory, executor, logger);
         case TMZ:
            return new TmzImporter(logger, dataFactory);
         case LIS:
            return new LisFileParser(logger, dataFactory, config, connector);
         default:
            throw new OseeArgumentException("Unsupported format type: [%s]", format);
      }
   }

}
