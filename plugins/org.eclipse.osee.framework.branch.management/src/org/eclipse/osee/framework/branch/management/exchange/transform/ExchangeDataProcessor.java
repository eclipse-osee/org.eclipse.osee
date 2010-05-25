package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

   public ExchangeDataProcessor(IOseeExchangeDataProvider exportDataProvider) {
      this.dataProvider = exportDataProvider;
   }

   public void transform(String fileName, SaxTransformer transformer) throws OseeCoreException {
      File target = new File(dataProvider.getExportedDataRoot(), fileName);
      transform(target, transformer);
   }

   public void transform(ExportItem exportItem, SaxTransformer transformer) throws OseeCoreException {
      File target = dataProvider.getFile(exportItem);
      transform(target, transformer);
   }

   private void transform(File targetFile, SaxTransformer transformer) throws OseeCoreException {
      File tempFile = new File(Lib.changeExtension(targetFile.getPath(), "temp"));
      if (!targetFile.renameTo(tempFile)) {
         throw new OseeStateException("not able to rename " + targetFile);
      }

      XMLStreamWriter writer = null;
      try {
         XMLOutputFactory factory = XMLOutputFactory.newInstance();
         writer = factory.createXMLStreamWriter(new FileWriter(targetFile));
         transformer.setWriter(writer);
         ExchangeUtil.readExchange(tempFile, transformer);
         tempFile.delete();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         if (writer != null) {
            try {
               writer.close();
            } catch (XMLStreamException ex) {
               // Do Nothing;
            }
         }
      }
   }

   public void deleteExportItem(String fileName) {
      new File(dataProvider.getExportedDataRoot(), fileName).delete();
   }

   public void renameExportItem(String originalFileName, String newFileName) throws OseeCoreException {
      File original = new File(dataProvider.getExportedDataRoot(), originalFileName);
      File destination = new File(dataProvider.getExportedDataRoot(), newFileName);
      boolean wasSuccessful = original.renameTo(destination);
      if (!wasSuccessful) {
         throw new OseeStateException(String.format("Error renaming [%s] to [%s]", originalFileName, newFileName));
      }
   }

   public void copyExportItem(String sourceFileName, String destinationFileName) throws OseeCoreException {
      File source = new File(dataProvider.getExportedDataRoot(), sourceFileName);
      File destination = new File(dataProvider.getExportedDataRoot(), destinationFileName);
      try {
         Lib.copyFile(source, destination);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public void parse(String fileName, ContentHandler handler) throws OseeCoreException {
      try {
         ExchangeUtil.readExchange(new File(dataProvider.getExportedDataRoot(), fileName), handler);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
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