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
package org.eclipse.osee.framework.db.connection.info;

import java.sql.Connection;
import java.sql.SQLException;

public enum SupportedDatabase {
   oracle, derby, foxpro, mysql, postgresql;

   public static SupportedDatabase getDatabaseType(Connection connection) throws SQLException {
      SupportedDatabase toReturn = null;
      String dbName = connection.getMetaData().getDatabaseProductName();
      String lowerCaseName = dbName.toLowerCase();
      if (lowerCaseName.contains(SupportedDatabase.derby.toString())) {
         toReturn = SupportedDatabase.derby;
      } else if (lowerCaseName.contains(SupportedDatabase.oracle.toString())) {
         toReturn = SupportedDatabase.oracle;
      } else if (lowerCaseName.contains(SupportedDatabase.foxpro.toString())) {
         toReturn = SupportedDatabase.foxpro;
      } else if (lowerCaseName.contains(SupportedDatabase.mysql.toString())) {
         toReturn = SupportedDatabase.mysql;
      } else if (lowerCaseName.contains(SupportedDatabase.postgresql.toString())) {
         toReturn = SupportedDatabase.postgresql;
      }
      return toReturn;
   }
}
