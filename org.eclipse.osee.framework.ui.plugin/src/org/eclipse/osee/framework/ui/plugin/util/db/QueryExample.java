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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Ryan D. Brooks
 */
public class QueryExample {
   private static final String elementQueryStr =
         "SELECT SIGNAL_TYPE, LOGIC_ID, STARTING_FRAME, ELEMENT_NAME, WORD_OFFSET, MSB, LSB, TYPE_ID, VERSION_DETAIL FROM ACDB_LMSG_V WHERE SYSTEM_ID = 'MCAP_BLD3' AND DATA_VIEW = 'PROPOSED' AND INTERFACE_NAME = 'MP_NETWORK' AND SIGNAL_TYPE <> 'SPARE' AND SIGNAL_TYPE <> 'RESERVED' AND SIGNAL_TYPE <> '0' AND LMSG_NAME = ? ORDER BY WORD_OFFSET, MSB";
   private PreparedStatement elementQuery;

   public QueryExample() throws SQLException {
      super();
      Connection connection = ConnectionHandler.getConnection();
      this.elementQuery =
            connection.prepareStatement(elementQueryStr, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
   }

   public ResultSet getElementResults(String messageName) throws SQLException {
      elementQuery.setString(1, messageName);
      return elementQuery.executeQuery();
   }

   public static void main(String[] args) throws SQLException {
      QueryExample query = new QueryExample();
      ResultSet elementResults = query.getElementResults("WPN_SEL_PWR_BIT_STAT");
      elementResults.last();
      System.out.println(elementResults.getRow());
   }
}