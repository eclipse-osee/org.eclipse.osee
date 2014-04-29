/*******************************************************************************
 * Copyright (c) 2014 Boeing.
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
import java.io.FileFilter;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.rest.internal.DispoDataFactory;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author John Misinco
 */
public class TmzImporter implements AbstractDispoImporter {

   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy H:mm:ss aa", Locale.US);

   private final Log logger;
   private final DispoDataFactory dataFactory;

   public TmzImporter(Log logger, DispoDataFactory dataFactory) {
      this.logger = logger;
      this.dataFactory = dataFactory;
   }

   @Override
   public List<DispoItem> importDirectory(Map<String, DispoItem> exisitingItems, File filesDir) {
      List<DispoItem> toReturn = new LinkedList<DispoItem>();
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

      for (File file : files) {
         String scriptName = file.getName().replaceAll("\\..*", "");

         DispoItem oldItem = exisitingItems.get(scriptName);
         Date lastUpdate = oldItem != null ? oldItem.getLastUpdate() : new Date(0);

         DispoItemData itemToBuild = null;
         JSONObject discrepancies = null;
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
                  discrepancies = new JSONObject();
                  itemToBuild.setDiscrepanciesList(discrepancies);
                  entry = zf.getEntry("TestPointSummary.json");
                  if (entry != null) {
                     inputStream = zf.getInputStream(entry);
                     json = Lib.inputStreamToString(inputStream);
                     processTestPointSummary(json, discrepancies);
                  }

                  if (oldItem != null) {
                     DispoItemDataCopier.copyOldItemData(oldItem, itemToBuild);
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
      return toReturn;
   }

   private void processOverview(String json, DispoItemData dispoItem) throws JSONException, ParseException {
      JSONObject record = new JSONObject(json);
      JSONObject properties = record.getJSONObject("properties");
      dispoItem.setVersion(properties.getString("version_revision"));
      Date date = DATE_FORMAT.parse(properties.getString("version_lastModificationDate"));
      dispoItem.setCreationDate(date);
      dispoItem.setLastUpdate(date);
   }

   private void processTestPointSummary(String json, JSONObject discrepancies) throws JSONException {
      JSONObject contents = new JSONObject(json);
      JSONArray records = contents.getJSONArray("childRecords");
      for (int i = 0; i < records.length(); i++) {
         JSONObject record = records.getJSONObject(i);
         int number = record.getInt("number");
         JSONObject testPoint = record.getJSONObject("testPoint");
         boolean passed = testPoint.getBoolean("pass");
         if (!passed) {
            Discrepancy discrepancy = new Discrepancy();
            discrepancy.setLocation(number);
            String id = GUID.create();
            discrepancy.setId(id);
            boolean groupNameIsNull = testPoint.isNull("groupName");
            if (groupNameIsNull) {
               String name = testPoint.getString("testPointName");
               String actual = testPoint.getString("actual");
               String expected = testPoint.getString("expected");

               String text =
                  String.format("Failure at Test Point %d. Check Point: %s. Expected: %s. Actual: %s. ", number, name,
                     expected, actual);
               discrepancy.setText(text);
            } else {
               JSONArray testPoints = testPoint.getJSONArray("testPoints");
               StringBuilder text =
                  new StringBuilder(String.format("Failure at Test Point %d. Check Group with Checkpoint Failures: ",
                     number));
               for (int j = 0; j < testPoints.length(); j++) {
                  JSONObject checkPoint = testPoints.getJSONObject(j);
                  String name = checkPoint.getString("testPointName");
                  String actual = checkPoint.getString("actual");
                  String expected = checkPoint.getString("expected");
                  text.append(String.format("Check Point: %s. Expected: %s. Actual: %s. ", name, expected, actual));
               }
               discrepancy.setText(text.toString());
            }
            JSONObject discrepancyAsJson = DispoUtil.discrepancyToJsonObj(discrepancy);
            discrepancies.put(id, discrepancyAsJson);
         }
      }
   }

}
