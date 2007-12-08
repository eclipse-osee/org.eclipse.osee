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
package org.eclipse.osee.framework.ui.plugin.util.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Robert A. Fisher
 */
public class QueryNonGeneric {

   /**
    * Builds a collection of items from an SQL statement from the basic DBConnection.
    * 
    * @param dbConn
    * @param collection - The collection to add the objects to.
    * @param sql - The SQL statement to use to acquire a ResultSet.
    * @param processor - The RsetProcessor used for providing and validating items.
    * @throws SQLException
    */
   @SuppressWarnings("unchecked")
   public static void acquireCollection(Connection dbConn, Collection collection, String sql, RsetProcessorNonGeneric processor) throws SQLException {
      ConnectionHandlerStatement chStmt;
      chStmt = ConnectionHandler.runPreparedQuery(100, sql);

      Object item;
      while (chStmt.next()) {
         item = processor.process(chStmt.getRset());
         if (processor.validate(item)) collection.add(item);
      }
      DbUtil.close(chStmt);
   }
}
