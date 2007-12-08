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
import java.sql.Statement;

/**
 * Statment object created by the ConnectionHandler. It contains:
 * <li> ResultSet
 * <li> Statement
 * 
 * @author Jeff C. Phillips
 */
public class ConnectionHandlerStatement {

   private ResultSet rset;
   private Statement statement;

   public ConnectionHandlerStatement() {
      super();
   }

   public boolean next() throws SQLException {
      if (rset != null) return rset.next();
      return false;
   }

   /**
    * @return Returns the rset.
    */
   public ResultSet getRset() {
      return rset;
   }

   /**
    * @param rset The rset to set.
    */
   public void setRset(ResultSet rset) {
      this.rset = rset;
   }

   /**
    * @return Returns the statement.
    */
   public Statement getStatement() {
      return statement;
   }

   /**
    * @param statement The statement to set.
    */
   public void setStatement(Statement statement) {
      this.statement = statement;
   }

}
