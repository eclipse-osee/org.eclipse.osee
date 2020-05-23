/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.exchange;

import java.io.File;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.handler.IExportItem;

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