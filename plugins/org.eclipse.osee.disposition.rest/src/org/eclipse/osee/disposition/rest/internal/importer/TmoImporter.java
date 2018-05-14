/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSummarySeverity;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.DispoImporterApi;
import org.eclipse.osee.disposition.rest.internal.DispoDataFactory;
import org.eclipse.osee.disposition.rest.internal.importer.DiscrepancyParser.MutableString;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;

/**
 * @author Angel Avila
 */
public class TmoImporter implements DispoImporterApi {

   private final DispoDataFactory dataFactory;
   private final ExecutorAdmin executor;
   private final Log logger;

   TmoImporter(DispoDataFactory dataFactory, ExecutorAdmin executor, Log logger) {
      this.dataFactory = dataFactory;
      this.executor = executor;
      this.logger = logger;
   }

   @Override
   public List<DispoItem> importDirectory(Map<String, DispoItem> exisitingItems, File tmoDirectory, OperationReport report) {
      List<DispoItem> toReturn = new LinkedList<>();
      if (tmoDirectory.isDirectory()) {

         List<File> listOfFiles = Lib.recursivelyListFiles(tmoDirectory, Pattern.compile(".*\\.tmo"));
         int numThreads = 8;
         int partitionSize = listOfFiles.size() / numThreads;

         int remainder = listOfFiles.size() % numThreads;
         int startIndex = 0;
         int endIndex = 0;
         List<Future<List<DispoItem>>> futures = new LinkedList<>();
         for (int i = 0; i < numThreads; i++) {
            startIndex = endIndex;
            endIndex = startIndex + partitionSize;
            if (i == 0) {
               endIndex += remainder;
            }
            List<File> sublist = listOfFiles.subList(startIndex, endIndex);
            Worker worker = new Worker(sublist, dataFactory, exisitingItems, report);
            Future<List<DispoItem>> future;
            try {
               future = executor.schedule("Dispo tmo importer", worker);
               futures.add(future);
            } catch (Exception ex) {
               report.addEntry("FATAL", ex.getLocalizedMessage(), DispoSummarySeverity.ERROR);
            }
         }
         for (Future<List<DispoItem>> future : futures) {
            try {
               toReturn.addAll(future.get());
            } catch (Exception ex) {
               report.addEntry("FATAL", ex.getLocalizedMessage(), DispoSummarySeverity.ERROR);
            }
         }
      }
      return toReturn;
   }

   private static final class Worker implements Callable<List<DispoItem>> {

      private final List<File> sublist;
      private final DispoDataFactory dataFactory;
      Map<String, DispoItem> exisitingItems;
      private final OperationReport operationReport;

      public Worker(List<File> sublist, DispoDataFactory dataFactory, Map<String, DispoItem> exisitingItems, OperationReport operationReport) {
         super();
         this.sublist = sublist;
         this.dataFactory = dataFactory;
         this.exisitingItems = exisitingItems;
         this.operationReport = operationReport;
      }

      @Override
      public List<DispoItem> call() throws Exception {
         List<DispoItem> fromThread = new LinkedList<>();
         for (File file : sublist) {
            InputStream inputStream = null;
            try {
               inputStream = new FileInputStream(file);
               String sanitizedFileName = file.getName().replaceAll("\\..*", "");
               DispoItemData itemToBuild = new DispoItemData();
               MutableString message = new MutableString();
               MutableBoolean isSameFile = new MutableBoolean(false);
               MutableBoolean isExeptioned = new MutableBoolean(false);

               // We already have an item with this name so we now have to check the dates in the parsing
               if (exisitingItems.containsKey(sanitizedFileName)) {
                  DispoItem oldItem = exisitingItems.get(sanitizedFileName);
                  Date lastUpdate = oldItem.getLastUpdate();
                  DiscrepancyParser.buildItemFromFile(itemToBuild, sanitizedFileName, inputStream, false, lastUpdate,
                     isSameFile, isExeptioned, message);
                  if (isExeptioned.getValue()) {
                     operationReport.addEntry(sanitizedFileName, message.getValue(), DispoSummarySeverity.ERROR);
                  } else if (!isSameFile.getValue()) {
                     // Copy Id to tell callee that this is not a new Item
                     itemToBuild.setGuid(oldItem.getGuid());
                     itemToBuild.setAnnotationsList(new ArrayList<DispoAnnotationData>());

                     // If Item has no Discrepancies then don't both copying over Annotations
                     if (itemToBuild.getDiscrepanciesList().size() > 0) {
                        DispoItemDataCopier.copyOldItemData(oldItem, itemToBuild, operationReport);
                     }
                     dataFactory.setStatus(itemToBuild);
                     fromThread.add(itemToBuild);
                  }
               } else {
                  DiscrepancyParser.buildItemFromFile(itemToBuild, sanitizedFileName, inputStream, true, new Date(),
                     isSameFile, isExeptioned, message);
                  if (isExeptioned.getValue()) {
                     operationReport.addEntry(sanitizedFileName, message.getValue(), DispoSummarySeverity.ERROR);
                  } else {
                     dataFactory.initDispoItem(itemToBuild);
                     fromThread.add(itemToBuild);
                  }

               }
            } catch (Exception ex) {
               operationReport.addEntry("ALL", ex.getMessage(), DispoSummarySeverity.ERROR);
            } finally {
               Lib.close(inputStream);
            }
         }
         return fromThread;
      }
   };

}
