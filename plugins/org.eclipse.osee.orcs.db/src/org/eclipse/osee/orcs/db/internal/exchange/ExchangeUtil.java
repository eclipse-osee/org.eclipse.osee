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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
public class ExchangeUtil {
   private static final String TEMP_NAME_PREFIX = "branch.xchng.";

   private ExchangeUtil() {
   }

   public static Writer createSqlWriter(File tempFolder, String name, int bufferSize) throws IOException {
      File indexFile = new File(tempFolder, name);
      Writer writer = new BufferedWriter(
         new OutputStreamWriter(new FileOutputStream(indexFile), ExportTableConstants.ENCODING), bufferSize);
      return writer;
   }

   public static Pair<Boolean, File> getTempExchangeFile(String exchangePath, Log logger, IResourceLocator locator,
      IResourceManager resourceManager) {
      File importSource = null;
      boolean wasZipExtractionRequired = false;
      IResource resource = resourceManager.acquire(locator, new PropertyStore());
      Conditions.checkExpressionFailOnTrue(!resourceManager.exists(locator), "Error locating [%s]",
         locator.getLocation());
      Objects.requireNonNull(resource, "Resource can not be null");
      File source = new File(resource.getLocation());
      if (source.isFile()) {
         wasZipExtractionRequired = true;
         importSource = ExchangeUtil.createTempFolder(exchangePath);
         logger.info("Extracting Exchange File: [%s] to [%s]", source.getName(), importSource);
         try {
            Lib.decompressStream(new FileInputStream(source), importSource);
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
         wasZipExtractionRequired = true;
      } else {
         wasZipExtractionRequired = false;
         importSource = source;
      }
      return new Pair<>(wasZipExtractionRequired, importSource);
   }

   public static void cleanUpTempExchangeFile(String exchangePath, Log logger, File exchangeSource,
      boolean wasZipExtractionRequired) {
      if (wasZipExtractionRequired && exchangeSource != null && exchangeSource.exists() && !exchangeSource.getAbsolutePath().equals(
         exchangePath)) {
         logger.info("Deleting Branch Import Temp Folder - [%s]", exchangeSource);
         Lib.deleteDir(exchangeSource);
      }
   }

   public static File createTempFolder(String exchangePath) {
      String fileName = TEMP_NAME_PREFIX + Lib.getDateTimeString();
      File rootDirectory = new File(exchangePath, fileName + File.separator);
      rootDirectory.mkdirs();
      return rootDirectory;
   }

   public static void readExchange(File file, ContentHandler handler) {
      InputStream byteStream = null;
      try {
         byteStream = new BufferedInputStream(new FileInputStream(file));
         XMLReader reader = XMLReaderFactory.createXMLReader();
         reader.setContentHandler(handler);
         reader.parse(new InputSource(byteStream));
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(byteStream);
      }
   }
}