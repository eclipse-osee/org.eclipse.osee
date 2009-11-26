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

package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.io.File;
import org.eclipse.osee.framework.branch.management.exchange.ExchangeUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.xml.sax.ContentHandler;

public class StandardOseeDbExportDataProvider implements IOseeDbExportDataProvider {
   private final File exportDataRootPath;
   private final boolean wasZipExtractionRequired;

   public StandardOseeDbExportDataProvider(IResourceLocator exportDataLocator) throws Exception {
      Pair<Boolean, File> result = ExchangeUtil.getTempExchangeFile(exportDataLocator);
      this.wasZipExtractionRequired = result.getFirst();
      this.exportDataRootPath = result.getSecond();

   }

   @Override
   public void startSaxParsing(String xmlName, ContentHandler handler) throws OseeCoreException {
      ExchangeUtil.readExchange(exportDataRootPath, xmlName, handler);
   }

   @Override
   public boolean wasZipExtractionRequired() {
      return wasZipExtractionRequired;
   }

   @Override
   public void cleanUp() {
      ExchangeUtil.cleanUpTempExchangeFile(exportDataRootPath, wasZipExtractionRequired);
   }

   @Override
   public File getExportedDataRoot() {
      return exportDataRootPath;
   }
}