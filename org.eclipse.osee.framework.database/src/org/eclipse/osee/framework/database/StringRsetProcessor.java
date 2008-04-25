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
package org.eclipse.osee.framework.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.core.RsetProcessor;

/**
 * Processes a list of strings from a column of a query.
 * 
 * @author Robert A. Fisher
 */
public class StringRsetProcessor implements RsetProcessor<String> {
   private final String columnName;

   /**
    * @param columnName The name of the column to acquire the integer value from.
    */
   public StringRsetProcessor(String columnName) {
      this.columnName = columnName;
   }

   public String process(ResultSet set) throws SQLException {
      return set.getString(columnName);
   }

   public boolean validate(String item) {
      return item != null;
   }

}
