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
package org.eclipse.osee.framework.server.admin.conversion;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;

/**
 * @author Roberto E. Escobar
 */
public class Util {

   private static final String sqlExtensionTypeId =
         "Select attrt1.ATTR_TYPE_ID from osee_attribute_type attrt1 where name = ?";

   private static final String sqlExtensionTypes =
         "SELECT attr1.art_id, attr1.value FROM osee_attribute attr1 WHERE attr1.ATTR_TYPE_ID = ?";

   public static Map<Long, String> getArtIdMap(String attrTypeName) throws OseeDataStoreException {
      Map<Long, String> toReturn = new HashMap<Long, String>(250);

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         int typeId = ConnectionHandler.runPreparedQueryFetchInt(-1, sqlExtensionTypeId, attrTypeName);
         chStmt.runPreparedQuery(sqlExtensionTypes, typeId);
         while (chStmt.next()) {
            toReturn.put(chStmt.getLong("art_id"), chStmt.getString("value"));
         }
      } finally {
         chStmt.close();
      }
      return toReturn;
   }
}
