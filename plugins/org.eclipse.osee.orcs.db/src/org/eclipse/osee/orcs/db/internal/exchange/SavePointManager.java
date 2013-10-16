/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.exchange;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public class SavePointManager {

   private static final String SAVE_POINT_PREFIX = "save.point.";

   private static final String LOAD_SAVE_POINT_ID = "load.save.points";

   private static final String INSERT_INTO_IMPORT_SAVE_POINT =
      "INSERT INTO osee_import_save_point (import_id, save_point_name, status, state_error) VALUES (?, ?, ?, ?)";

   private static final String QUERY_SAVE_POINTS_FROM_IMPORT_MAP =
      "SELECT save_point_name from osee_import_save_point oisp, osee_import_source ois WHERE ois.import_id = oisp.import_id AND oisp.status = 1 AND ois.db_source_guid = ? AND ois.source_export_date = ?";

   private final Map<String, SavePoint> savePoints = new LinkedHashMap<String, SavePoint>();

   private final IOseeDatabaseService dbService;

   private String currentSavePoint;

   public SavePointManager(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   private IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   public void storeSavePoints(OseeConnection connection, int importIdIndex) throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      for (SavePoint savePoint : savePoints.values()) {
         int status = 1;
         String comment = "";
         if (savePoint.hasErrors()) {
            status = -1;
            StringBuilder builder = new StringBuilder();
            for (Throwable ex : savePoint.getErrors()) {
               builder.append(Lib.exceptionToString(ex).replaceAll("\n", " "));
            }
            if (builder.length() < 4000) {
               comment = builder.toString();
            } else {
               comment = builder.substring(0, 3999);
            }
         }
         data.add(new Object[] {importIdIndex, savePoint.getName(), status, comment});
      }
      getDatabaseService().runBatchUpdate(connection, INSERT_INTO_IMPORT_SAVE_POINT, data);

   }

   public void loadSavePoints(String sourceDatabaseId, Date sourceExportDate) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         setCurrentSetPointId(LOAD_SAVE_POINT_ID);
         chStmt.runPreparedQuery(QUERY_SAVE_POINTS_FROM_IMPORT_MAP, sourceDatabaseId,
            new Timestamp(sourceExportDate.getTime()));
         while (chStmt.next()) {
            String key = chStmt.getString("save_point_name");
            savePoints.put(key, new SavePoint(key));
         }
         addCurrentSavePointToProcessed();
      } finally {
         chStmt.close();
      }
   }

   public String getCurrentSetPointId() {
      return currentSavePoint;
   }

   public void setCurrentSetPointId(String savePointId) {
      this.currentSavePoint = savePointId;
   }

   public void clear() {
      savePoints.clear();
   }

   public String asSavePointName(String sourceName) {
      return SAVE_POINT_PREFIX + sourceName;
   }

   public boolean isCurrentInProcessed() {
      return doesSavePointExist(getCurrentSetPointId());
   }

   private boolean doesSavePointExist(String sourceName) {
      return savePoints.containsKey(asSavePointName(sourceName));
   }

   public void addCurrentSavePointToProcessed() {
      addSavePoint(getCurrentSetPointId());
   }

   private void addSavePoint(String sourceName) {
      String key = asSavePointName(sourceName);
      SavePoint point = savePoints.get(key);
      if (point == null) {
         point = new SavePoint(key);
         savePoints.put(key, point);
      }
   }

   public void reportError(Throwable ex) {
      reportError(getCurrentSetPointId(), ex);
   }

   private void reportError(String sourceName, Throwable ex) {
      String key = asSavePointName(sourceName);
      SavePoint point = savePoints.get(key);
      if (point == null) {
         point = new SavePoint(key);
         savePoints.put(key, point);
      }
      point.addError(ex);
   }

   private final class SavePoint {
      private final String savePointName;
      private List<Throwable> errors;

      public SavePoint(String name) {
         this.savePointName = name;
         this.errors = null;
      }

      public String getName() {
         return savePointName;
      }

      public void addError(Throwable ex) {
         if (errors == null) {
            errors = new ArrayList<Throwable>();
         }
         if (!errors.contains(ex)) {
            errors.add(ex);
         }
      }

      public List<Throwable> getErrors() {
         if (errors == null) {
            return Collections.emptyList();
         } else {
            return this.errors;
         }
      }

      public boolean hasErrors() {
         return errors != null;
      }
   }

}
