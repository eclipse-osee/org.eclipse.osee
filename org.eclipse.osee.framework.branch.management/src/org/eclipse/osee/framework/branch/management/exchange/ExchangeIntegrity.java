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

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.branch.management.exchange.handler.IExportItem;
import org.eclipse.osee.framework.branch.management.exchange.handler.IOseeDbExportDataProvider;
import org.eclipse.osee.framework.branch.management.exchange.handler.ManifestSaxHandler;
import org.eclipse.osee.framework.branch.management.exchange.handler.RelationalSaxHandler;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class ExchangeIntegrity {
   private final IOseeDbExportDataProvider exportDataProvider;
   private String checkExchange;

   public ExchangeIntegrity(IOseeDbExportDataProvider exportDataProvider) {
      this.exportDataProvider = exportDataProvider;
   }

   public String getExchangeCheckFileName() {
      return checkExchange;
   }

   public void execute() throws Exception {
      long startTime = System.currentTimeMillis();
      try {
         ManifestSaxHandler manifestSaxHandler = new ManifestSaxHandler();
         exportDataProvider.saxParse(ExportItem.EXPORT_MANIFEST, manifestSaxHandler);

         List<IExportItem> filesToCheck = new ArrayList<IExportItem>();
         filesToCheck.addAll(manifestSaxHandler.getImportFiles());
         filesToCheck.add(manifestSaxHandler.getBranchFile());

         final List<IndexCollector> checkList = ExchangeDb.createCheckList();
         for (final IExportItem importFile : filesToCheck) {
            exportDataProvider.saxParse(importFile, new CheckSaxHandler(exportDataProvider, checkList,
                  importFile.getFileName()));
         }
         checkExchange = exportDataProvider.getExportedDataRoot() + ".verify.xml";
         writeResults(exportDataProvider.getExportedDataRoot().getParentFile(), checkExchange, checkList);
      } finally {
         exportDataProvider.cleanUp();
         OseeLog.log(this.getClass(), Level.INFO, String.format("Verified [%s] in [%s]",
               exportDataProvider.getExportedDataRoot(), Lib.getElapseString(startTime)));
      }
   }

   private void writeResults(File writeLocation, String fileName, List<IndexCollector> checkList) throws Exception {
      Writer writer = null;
      try {
         writer = ExchangeUtil.createXmlWriter(writeLocation, fileName, (int) Math.pow(2, 20));
         ExportImportXml.openXmlNode(writer, ExportImportXml.DATA);

         for (IndexCollector integrityCheck : checkList) {
            boolean passedCheck = !integrityCheck.hasErrors();
            writer.append("\t");
            ExportImportXml.openPartialXmlNode(writer, ExportImportXml.ENTRY);
            ExportImportXml.addXmlAttribute(writer, ExportImportXml.ID, integrityCheck.getSource());
            ExportImportXml.addXmlAttribute(writer, "status", passedCheck ? "OK" : "FAILED");

            if (passedCheck) {
               ExportImportXml.closePartialXmlNode(writer);
            } else {
               ExportImportXml.endOpenedPartialXmlNode(writer);
               Map<String, Set<Long>> results = integrityCheck.getItemsNotFound();
               for (String key : results.keySet()) {
                  Set<Long> values = results.get(key);
                  writer.append("\t\t");
                  ExportImportXml.openPartialXmlNode(writer, "error");
                  ExportImportXml.addXmlAttribute(writer, ExportImportXml.ID, key);
                  ExportImportXml.endOpenedPartialXmlNode(writer);
                  Xml.writeAsCdata(writer, "\t\t\t" + values.toString());
                  writer.append("\n\t\t");
                  ExportImportXml.closeXmlNode(writer, "error");
               }
               writer.append("\t");
               ExportImportXml.closeXmlNode(writer, ExportImportXml.ENTRY);
            }
         }
         ExportImportXml.closeXmlNode(writer, ExportImportXml.DATA);
      } finally {
         if (writer != null) {
            writer.close();
         }
      }
   }

   private final class CheckSaxHandler extends RelationalSaxHandler {
      private final List<IndexCollector> checkList;
      private final String fileBeingProcessed;

      protected CheckSaxHandler(IOseeDbExportDataProvider exportDataProvider, List<IndexCollector> checkList, String fileBeingProcessed) throws OseeCoreException {
         super(exportDataProvider, true, 0);
         this.checkList = checkList;
         this.fileBeingProcessed = Lib.removeExtension(fileBeingProcessed);
         System.out.println(String.format("Verifying: [%s]", fileBeingProcessed));
      }

      @Override
      protected void processData(Map<String, String> fieldMap) throws Exception {
         String conflictId = fieldMap.get(ExchangeDb.CONFLICT_ID);
         String conflictType = fieldMap.get(ExchangeDb.CONFLICT_TYPE);
         if (Strings.isValid(conflictId) && Strings.isValid(conflictType)) {
            int conflictOrdinal = Integer.valueOf(conflictType);
            for (ConflictType type : ConflictType.values()) {
               if (type.getValue() == conflictOrdinal) {
                  String keyName = ExchangeDb.CONFLICT_ID;
                  switch (type) {
                     case ARTIFACT:
                        keyName = ExchangeDb.ARTIFACT_ID;
                        break;
                     case ATTRIBUTE:
                        keyName = ExchangeDb.ATTRIBUTE_ID;
                        break;
                     case RELATION:
                        keyName = ExchangeDb.RELATION_ID;
                        break;
                     default:
                        break;
                  }
                  fieldMap.put(keyName, conflictId);
                  break;
               }
            }
         }
         for (IndexCollector integrityCheck : checkList) {
            integrityCheck.processData(fileBeingProcessed, fieldMap);
         }
      }

      @Override
      protected void finishData() {
         for (IndexCollector integrityCheck : checkList) {
            integrityCheck.removeFalsePositives();
         }
         super.finishData();
      }
   }

}
