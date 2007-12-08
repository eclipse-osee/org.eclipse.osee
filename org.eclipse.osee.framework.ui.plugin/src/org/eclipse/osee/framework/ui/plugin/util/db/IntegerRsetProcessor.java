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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Processes a list of values from a column of a query.
 * 
 * @author Robert A. Fisher
 */
public class IntegerRsetProcessor implements RsetProcessor<Integer> {
   private final String columnName;

   /**
    * @param columnName The name of the column to acquire the integer value from.
    */
   public IntegerRsetProcessor(String columnName) {
      this.columnName = columnName;
   }

   public Integer process(ResultSet set) throws SQLException {
      int value = set.getInt(columnName);
      if (set.wasNull())
         return null;
      else
         return value;
   }

   public boolean validate(Integer item) {
      return item != null;
   }

}
