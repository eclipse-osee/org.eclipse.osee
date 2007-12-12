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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.SQLException;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.Table;

/**
 * @author Jeff C. Phillips
 */
public class TempTable extends Table {
   private final String CREATE_TABLE;
   private final String DROP_TABLE;

   public TempTable(String tableName) throws SQLException {
      super(tableName);
      this.CREATE_TABLE =
            "CREATE GLOBAL TEMPORARY TABLE " + tableName + " (transaction_id DECIMAL, gamma_id DECIMAL, tx_type DECIMAL) ";
      this.DROP_TABLE = "DROP TABLE " + tableName;

      create();
   }

   private void create() throws SQLException {
      ConnectionHandler.runPreparedUpdate(CREATE_TABLE);
   }

   public void drop() throws SQLException {
      ConnectionHandler.runPreparedUpdate(DROP_TABLE);
   }
}
