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
package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.branch.management.exchange.ExchangeUtil;
import org.eclipse.osee.framework.branch.management.exchange.IOseeExchangeDataProvider;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.branch.management.exchange.handler.IExportItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.SaxTransformer;
import org.xml.sax.ContentHandler;

public final class ExchangeDataProcessor {
   private final IOseeExchangeDataProvider dataProvider;

   public IOseeExchangeDataProvider getDataProvider() {
      return dataProvider;
   }

   public ExchangeDataProcessor(IOseeExchangeDataProvider exportDataProvider) {
      this.dataProvider = exportDataProvider;
   }

   public void transform(String fileName, SaxTransformer transformer) throws OseeCoreException {
      transform(dataProvider.getFile(fileName), transformer);
   }

   public void transform(ExportItem exportItem, SaxTransformer transformer) throws OseeCoreException {
      transform(dataProvider.getFile(exportItem), transformer);
   }

   private void transform(File targetFile, SaxTransformer transformer) throws OseeCoreException {
      File tempFile = new File(Lib.changeExtension(targetFile.getPath(), "temp"));
      Writer fileWriter = null;
      try {
         fileWriter = startTransform(targetFile, tempFile, transformer);
         ExchangeUtil.readExchange(tempFile, transformer);
         tempFile.delete();
      } catch (Exception ex) {
         renameExportItem(tempFile, targetFile);
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         try {
            transformer.finish();
         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
         } finally {
            Lib.close(fileWriter);
         }
      }
   }

   public Writer startTransform(File targetFile, File tempFile, SaxTransformer transformer) throws OseeCoreException, XMLStreamException, IOException {
      renameExportItem(targetFile, tempFile);

      Writer fileWriter = new BufferedWriter(new FileWriter(targetFile));
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(fileWriter);
      transformer.setWriter(xmlWriter);
      return fileWriter;
   }

   public void deleteExportItem(String fileName) {
      dataProvider.getFile(fileName).delete();
   }

   public void renameExportItem(String originalFileName, String newFileName) throws OseeCoreException {
      renameExportItem(dataProvider.getFile(originalFileName), dataProvider.getFile(newFileName));
   }

   private void renameExportItem(File original, File destination) throws OseeCoreException {
      boolean wasSuccessful = original.renameTo(destination);
      if (!wasSuccessful) {
         throw new OseeStateException(String.format("Error renaming [%s] to [%s]", original, destination));
      }
   }

   public void copyExportItem(String sourceFileName, String destinationFileName) throws OseeCoreException {
      File source = dataProvider.getFile(sourceFileName);
      File destination = dataProvider.getFile(destinationFileName);
      try {
         Lib.copyFile(source, destination);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public void parse(String fileName, ContentHandler handler) throws OseeCoreException {
      ExchangeUtil.readExchange(dataProvider.getFile(fileName), handler);
   }

   public void parse(IExportItem exportItem, ContentHandler handler) throws OseeCoreException {
      try {
         ExchangeUtil.readExchange(dataProvider.getFile(exportItem), handler);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public void transform(ExportItem exportItem, Rule rule) throws OseeCoreException {
      try {
         rule.process(dataProvider.getFile(exportItem));
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public void cleanUp() {
      ExchangeUtil.cleanUpTempExchangeFile(dataProvider.getExportedDataRoot(), dataProvider.wasZipExtractionRequired());
   }
}