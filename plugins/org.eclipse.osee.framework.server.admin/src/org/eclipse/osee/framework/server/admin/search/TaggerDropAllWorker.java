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
package org.eclipse.osee.framework.server.admin.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;

/**
 * @author Roberto E. Escobar
 */
class TaggerDropAllWorker extends BaseServerCommand {
   private static final String TRUNCATE_SQL = "TRUNCATE osee_search_tags";
   private static final String DELETE_TABLE_SQL = "DELETE FROM osee_search_tags";

   protected TaggerDropAllWorker() {
      super("Drop All Search Tags");
   }

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      String deleteSql = null;
      if (SupportedDatabase.isDatabaseType(ConnectionHandler.getMetaData(), SupportedDatabase.derby)) {
         deleteSql = DELETE_TABLE_SQL;
      } else {
         deleteSql = TRUNCATE_SQL;
      }
      ConnectionHandler.runPreparedUpdate(deleteSql);
   }
}
