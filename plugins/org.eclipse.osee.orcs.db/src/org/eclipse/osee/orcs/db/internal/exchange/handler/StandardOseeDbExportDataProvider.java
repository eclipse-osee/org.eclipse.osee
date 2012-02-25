/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.orcs.db.internal.exchange.handler;

import java.io.File;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.IOseeExchangeDataProvider;

/**
 * @author Ryan D. Brooks
 */
public class StandardOseeDbExportDataProvider implements IOseeExchangeDataProvider {
   private final File exportDataRootPath;
   private final boolean wasZipExtractionRequired;
   private final Log logger;
   private final String exchangeBasePath;

   public StandardOseeDbExportDataProvider(String exchangeBasePath, Log logger, File exportDataRootPath, boolean wasZipExtractionRequired) {
      this.exchangeBasePath = exchangeBasePath;
      this.logger = logger;
      this.wasZipExtractionRequired = wasZipExtractionRequired;
      this.exportDataRootPath = exportDataRootPath;

   }

   @Override
   public File getFile(IExportItem item) {
      return getFile(item.getFileName());
   }

   @Override
   public File getFile(String fileName) {
      return new File(exportDataRootPath, fileName);
   }

   @Override
   public boolean wasZipExtractionRequired() {
      return wasZipExtractionRequired;
   }

   @Override
   public File getExportedDataRoot() {
      return exportDataRootPath;
   }

   @Override
   public Log getLogger() {
      return logger;
   }

   @Override
   public String getExchangeBasePath() {
      return exchangeBasePath;
   }

}