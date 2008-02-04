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
package org.eclipse.osee.framework.skynet.core.exportImport;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.Table;

/**
 * @author Robert A. Fisher
 */
public class GuidCache {
   private final Table table;
   private final String idColumn;
   private final Map<Integer, String> idToGuidMap;
   private final Map<String, Integer> guidToIdMap;
   private final List<Object[]> newGuids;

   /**
    * @param table
    * @param idColumn
    * @throws IOException
    * @throws SQLException
    */
   public GuidCache(final Table table, final String idColumn) throws SQLException, IOException {
      this(table, idColumn, false);
   }

   /**
    * @param table
    * @param idColumn
    * @param writeNewGuidsBack TODO
    * @param idSequence
    * @throws SQLException
    * @throws IOException
    */
   public GuidCache(Table table, String idColumn, boolean writeNewGuidsBack) throws SQLException, IOException {
      super();
      this.table = table;
      this.idColumn = idColumn;
      this.idToGuidMap = new HashMap<Integer, String>();
      this.guidToIdMap = new HashMap<String, Integer>();
      if (writeNewGuidsBack) {
         this.newGuids = new LinkedList<Object[]>();
      } else {
         this.newGuids = null;
      }

      loadMaps();
   }

   public String getGuid(int id) throws SQLException, IOException {
      String guid;
      if (idToGuidMap.containsKey(id)) {
         guid = idToGuidMap.get(id);
      } else {
         guid = GUID.generateGuidStr();
         if (newGuids != null) {
            newGuids.add(new Object[] {SQL3DataType.VARCHAR, guid, SQL3DataType.INTEGER, id});
         }
         map(id, guid);
      }

      return guid;
   }

   public Integer getId(String guid) {
      return guidToIdMap.get(guid);
   }

   private void loadMaps() throws SQLException {
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(2000, "SELECT " + table.columns("guid", idColumn) + " FROM " + table);
         ResultSet rset = chStmt.getRset();

         while (rset.next()) {
            map(rset.getInt(idColumn), rset.getString("guid"));
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   public void map(int id, String guid) {
      idToGuidMap.put(id, guid);
      guidToIdMap.put(guid, id);
   }

   public void finalizeCachedGuids() throws SQLException {
      if (newGuids != null && !newGuids.isEmpty()) {
         ConnectionHandler.runPreparedUpdate("INSERT INTO " + table + " (guid, " + idColumn + ") VALUES (?,?)",
               newGuids);
      }
   }
}
