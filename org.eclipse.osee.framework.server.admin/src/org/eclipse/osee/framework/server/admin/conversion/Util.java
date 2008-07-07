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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class Util {

   private static final String sqlExtensionTypeId =
         "Select attrt1.ATTR_TYPE_ID from osee_define_attribute_type attrt1 where name = ?";

   private static final String sqlExtensionTypes =
         "SELECT attr1.art_id,  attr1.value FROM osee_define_attribute attr1 WHERE attr1.ATTR_TYPE_ID = ?";

   private Util() {
   }

   private static int getAttrTypeId(Connection connection, String attrTypeName) throws SQLException {
      int typeId = -1;
      ResultSet rs = null;
      PreparedStatement prepared = null;
      try {
         prepared = connection.prepareStatement(sqlExtensionTypeId);
         prepared.setString(1, attrTypeName);
         rs = prepared.executeQuery();
         while (rs.next()) {
            typeId = rs.getInt(1);
         }
      } finally {
         if (rs != null) {
            rs.close();
         }
         if (prepared != null) {
            prepared.close();
         }
      }
      return typeId;
   }

   public static Map<Long, String> getArtIdMap(Connection connection, String attrTypeName) throws SQLException {
      Map<Long, String> toReturn = new HashMap<Long, String>(250);

      ResultSet rs = null;
      PreparedStatement prepared = null;
      try {
         int typeId = getAttrTypeId(connection, attrTypeName);

         prepared = connection.prepareStatement(sqlExtensionTypes);
         prepared.setInt(1, typeId);
         rs = prepared.executeQuery();
         while (rs.next()) {
            toReturn.put(rs.getLong(1), rs.getString(2));
         }
      } finally {
         if (rs != null) {
            rs.close();
         }
         if (prepared != null) {
            prepared.close();
         }
      }
      return toReturn;
   }
}
