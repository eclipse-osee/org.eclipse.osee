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
package org.eclipse.osee.framework.branch.management.exchange;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import org.eclipse.osee.framework.branch.management.Activator;
import org.eclipse.osee.framework.branch.management.exchange.resource.ExchangeProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
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

   public static Writer createXmlWriter(File tempFolder, String name, int bufferSize) throws Exception {
      File indexFile = new File(tempFolder, name);
      Writer writer =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(indexFile), ExportImportXml.XML_ENCODING),
                  bufferSize);
      writer.write(ExportImportXml.XML_HEADER);
      return writer;
   }

   public static ObjectPair<Boolean, File> getTempExchangeFile(IResourceLocator locator) throws Exception {
      File importSource = null;
      boolean wasZipExtractionRequired = false;
      IResource resource = Activator.getInstance().getResourceManager().acquire(locator, new Options());
      File source = new File(resource.getLocation());
      if (source.isFile()) {
         wasZipExtractionRequired = true;
         importSource = ExchangeUtil.createTempFolder();
         OseeLog.log(ExchangeUtil.class, Level.INFO, String.format("Extracting Exchange File: [%s] to [%s]",
               source.getName(), importSource));
         Lib.decompressStream(new FileInputStream(source), importSource);
         wasZipExtractionRequired = true;
      } else {
         wasZipExtractionRequired = false;
         importSource = source;
      }
      return new ObjectPair<Boolean, File>(wasZipExtractionRequired, importSource);
   }

   public static void cleanUpTempExchangeFile(File exchangeSource, boolean wasZipExtractionRequired) {
      if (wasZipExtractionRequired && exchangeSource != null && exchangeSource.exists() && exchangeSource.getAbsolutePath() != ExchangeProvider.getExchangeFilePath()) {
         OseeLog.log(ExchangeUtil.class, Level.INFO, String.format("Deleting Branch Import Temp Folder - [%s]",
               exchangeSource));
         Lib.deleteDir(exchangeSource);
      }
   }

   public static File createTempFolder() {
      String basePath = ExchangeProvider.getExchangeFilePath();
      String fileName = TEMP_NAME_PREFIX + Lib.getDateTimeString();
      File rootDirectory = new File(basePath, fileName + File.separator);
      rootDirectory.mkdirs();
      return rootDirectory;
   }

   public static void readExchange(File zipFile, String fileToProcess, ContentHandler handler) throws OseeCoreException {
      InputStream inputStream = null;
      try {
         File entry = new File(zipFile, fileToProcess);
         inputStream = new BufferedInputStream(new FileInputStream(entry));
         XMLReader reader = XMLReaderFactory.createXMLReader();
         reader.setContentHandler(handler);
         reader.parse(new InputSource(inputStream));
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               throw new OseeWrappedException(ex);
            }
         }
      }
   }
}
