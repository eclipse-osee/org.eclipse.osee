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
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.util.Map;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class RelationalTypeCheckSaxHandler extends RelationalSaxHandler {

   public static RelationalTypeCheckSaxHandler createWithCacheAll(IOseeDbExportDataProvider exportDataProvider) {
      return new RelationalTypeCheckSaxHandler(exportDataProvider, true, 0);
   }

   public static RelationalTypeCheckSaxHandler createWithLimitedCache(IOseeDbExportDataProvider exportDataProvider, int cacheLimit) {
      return new RelationalTypeCheckSaxHandler(exportDataProvider, false, cacheLimit);
   }

   private final StringBuffer errorCheck;

   private RelationalTypeCheckSaxHandler(IOseeDbExportDataProvider exportDataProvider, boolean isCacheAll, int cacheLimit) {
      super(exportDataProvider, isCacheAll, cacheLimit);
      this.errorCheck = new StringBuffer();
   }

   @Override
   protected void processData(Map<String, String> fieldMap) throws Exception {
      String typeField = "art_type_id";
      String nameField = "name";
      String name = fieldMap.get(nameField);
      String typeId = fieldMap.get(typeField);
      if (Strings.isValid(name)) {
         if (!Strings.isValid(typeId)) {
            typeField = "attr_type_id";
         }
      } else {
         typeField = "rel_link_type_id";
         nameField = "type_name";
      }
      name = fieldMap.get(nameField);
      typeId = fieldMap.get(typeField);
      Long original = Strings.isValid(typeId) ? new Long(typeId) : -1;

      IOseeStatement chStmt = ConnectionHandler.getStatement(getConnection());
      try {
         chStmt.runPreparedQuery(String.format("select %s from %s where %s = ?", typeField,
               getMetaData().getTableName(), nameField), name);
         if (chStmt.next()) {
            getTranslator().checkIdMapping(typeField, original, chStmt.getLong(chStmt.getColumnName(1)));
         } else {
            this.errorCheck.append(String.format("Type not found in target db. type:[%s] - [%s (%s)]\n", name,
                  typeField, typeId));
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   protected void finishData() {
      if (this.errorCheck.length() > 0) {
         throw new IllegalStateException(this.errorCheck.toString());
      }
   }
}
