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
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.osee.framework.branch.management.exchange.handler.ManifestSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.MetaData;
import org.eclipse.osee.framework.branch.management.exchange.handler.MetaDataSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.RelationalSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.RelationalTypeCheckSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.ManifestSaxHandler.ImportFile;
import org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction;
import org.eclipse.osee.framework.resource.management.Options;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
final class ImportController extends DbTransaction {

   private File importFile;
   private Options options;

   ImportController(File importFile, Options options) {
      this.importFile = importFile;
      this.options = options;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction#handleTxWork(java.sql.Connection)
    */
   @Override
   protected void handleTxWork(Connection connection) throws Exception {
      ZipFile zipFile = null;
      try {
         zipFile = new ZipFile(importFile);
         ManifestSaxHandler manifestHandler = new ManifestSaxHandler();
         processImportFile(zipFile, "export.manifest.xml", manifestHandler);

         MetaDataSaxHandler metadataHandler = new MetaDataSaxHandler();
         processImportFile(zipFile, manifestHandler.getMetadataFile(), metadataHandler);

         RelationalTypeCheckSaxHandler typeCheckHandler =
               RelationalTypeCheckSaxHandler.newCacheAllDataRelationalTypeCheckSaxHandler();

         RelationalSaxHandler relationalSaxHandler = RelationalSaxHandler.newCacheAllDataRelationalSaxHandler();

         processImportFiles(connection, zipFile, metadataHandler, manifestHandler.getTypeFiles(), typeCheckHandler);
         processImportFiles(connection, zipFile, metadataHandler, manifestHandler.getImportFiles(),
               relationalSaxHandler);
      } finally {
         if (zipFile != null) {
            zipFile.close();
         }
      }
   }

   private void processImportFiles(Connection connection, ZipFile zipFile, MetaDataSaxHandler metaData, Collection<ImportFile> importFiles, RelationalSaxHandler handler) throws Exception {
      for (ImportFile item : importFiles) {
         MetaData metadata = metaData.getMetadata(item.getSource());
         handler.setMetaData(metadata);
         handler.setConnection(connection);
         processImportFile(zipFile, item.getFileName(), handler);
         handler.store();
         handler.reset();
      }
   }

   private void processImportFile(ZipFile zipFile, String fileToProcess, ContentHandler handler) throws Exception {
      InputStream inputStream = null;
      try {
         ZipEntry entry = zipFile.getEntry(fileToProcess);
         inputStream = new BufferedInputStream(zipFile.getInputStream(entry));
         XMLReader reader = XMLReaderFactory.createXMLReader();
         reader.setContentHandler(handler);
         reader.parse(new InputSource(inputStream));
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }

}
