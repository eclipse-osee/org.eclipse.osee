/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.disposition.rest.internal.importer;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.DispoImporterApi;
import org.eclipse.osee.disposition.rest.internal.DispoDataFactory;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;

/**
 * @author John Misinco
 */
public class TmzImporter implements DispoImporterApi {

   private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy H:mm:ss aa", Locale.US);

   private final Log logger;
   private final DispoDataFactory dataFactory;
   private final JaxRsApi jaxRsApi;

   public TmzImporter(Log logger, DispoDataFactory dataFactory, JaxRsApi jaxRsApi) {
      this.logger = logger;
      this.dataFactory = dataFactory;
      this.jaxRsApi = jaxRsApi;
   }

   @Override
   public List<DispoItem> importDirectory(Map<String, DispoItem> exisitingItems, File filesDir,
      OperationReport report) {
      List<DispoItem> toReturn = new LinkedList<>();
      if (!filesDir.exists() || !filesDir.isDirectory()) {
         throw new OseeArgumentException("Input directory does not exists or is not a directory [%s]",
            filesDir.getAbsolutePath());
      }

      File[] files = filesDir.listFiles(new FileFilter() {

         @Override
         public boolean accept(File pathname) {
            return Lib.getExtension(pathname.getName()).equals("tmz");
         }
      });

      if (files != null) {
         for (File file : files) {
            String scriptName = file.getName().replaceAll("\\..*", "");

            DispoItem oldItem = exisitingItems.get(scriptName);
            Date lastUpdate = oldItem != null ? oldItem.getLastUpdate() : new Date(0);

            DispoItemData itemToBuild = null;
            Map<String, Discrepancy> discrepancies = null;
            ZipFile zf = null;
            try {
               zf = new ZipFile(file);
               ZipEntry entry = zf.getEntry("Overview.json");

               if (entry != null) {
                  InputStream inputStream = zf.getInputStream(entry);
                  String json = Lib.inputStreamToString(inputStream);
                  itemToBuild = new DispoItemData();
                  itemToBuild.setName(scriptName);
                  processOverview(json, itemToBuild);

                  if (oldItem == null || !itemToBuild.getLastUpdate().after(lastUpdate)) {
                     discrepancies = new HashMap<>();
                     itemToBuild.setDiscrepanciesList(discrepancies);
                     entry = zf.getEntry("TestPointSummary.json");
                     if (entry != null) {
                        inputStream = zf.getInputStream(entry);
                        json = Lib.inputStreamToString(inputStream);
                        processTestPointSummary(json, discrepancies);
                     }

                     if (oldItem != null) {
                        DispoItemDataCopier.copyOldItemData(oldItem, itemToBuild, report);
                     } else {
                        dataFactory.initDispoItem(itemToBuild);
                     }
                     toReturn.add(itemToBuild);
                  }
               }

            } catch (Exception ex) {
               logger.info(ex, "Unable to process: [%s]", file.getAbsolutePath());
            } finally {
               // ZipFile doesn't implement Closeable in 1.6
               if (zf != null) {
                  try {
                     zf.close();
                  } catch (Exception ex) {
                     // do nothing
                  }
               }
            }
         }
      }
      return toReturn;
   }

   private void processOverview(String json, DispoItemData dispoItem) {
      try {
         TmzProperties properties = jaxRsApi.readValue(json, TmzProperties.class);
         dispoItem.setVersion(properties.getVersion_revision());
         Date date = DATE_FORMAT.parse(properties.getVersion_lastModificationDate());
         dispoItem.setCreationDate(date);
         dispoItem.setLastUpdate(date);
      } catch (Exception ex) {
         throw new OseeCoreException("Could not parse Tmz Properties Json", ex);
      }
   }

   private void processTestPointSummary(String json, Map<String, Discrepancy> discrepancies) {
      String node = jaxRsApi.readValue(json, "childRecords");
      List<TmzChildRecord> childRecords = jaxRsApi.readCollectionValue(node, List.class, TmzChildRecord.class);

      for (TmzChildRecord record : childRecords) {
         int number = record.getNumber();
         List<TmzTestPoint> testPoints = record.getTestPoint();
         for (TmzTestPoint testPoint : testPoints) {
            boolean passed = testPoint.getPass();
            if (!passed) {
               Discrepancy discrepancy = new Discrepancy();
               discrepancy.setLocation(String.valueOf(number));
               String id = GUID.create();
               discrepancy.setId(id);
               boolean groupNameIsNull = testPoint.getGroupName() == null || testPoint.getGroupName().equals("");
               if (groupNameIsNull) {
                  String name = testPoint.getTestPointName();
                  String actual = testPoint.getActual();
                  String expected = testPoint.getExpected();
                  String text = String.format("Failure at Test Point %s. Check Point: %s. Expected: %s. Actual: %s. ",
                     number, name, expected, actual);
                  discrepancy.setText(text);
               } else {
                  List<TmzTestPoint> tmzTestPoints = testPoint.getTestPoints();
                  StringBuilder text = new StringBuilder(
                     String.format("Failure at Test Point %s. Check Group with Checkpoint Failures: ", number));
                  for (TmzTestPoint checkPoint : tmzTestPoints) {
                     String name = checkPoint.getTestPointName();
                     String actual = checkPoint.getActual();
                     String expected = checkPoint.getExpected();
                     text.append(String.format("Check Point: %s. Expected: %s. Actual: %s. ", name, expected, actual));
                  }
                  discrepancy.setText(text.toString());
               }
               discrepancies.put(id, discrepancy);
            }
         }
      }
   }
}
