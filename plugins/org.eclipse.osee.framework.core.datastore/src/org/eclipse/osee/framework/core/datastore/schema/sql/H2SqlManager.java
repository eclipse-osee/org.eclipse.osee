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
package org.eclipse.osee.framework.core.datastore.schema.sql;

import org.eclipse.osee.framework.core.datastore.schema.data.TableElement;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;

/**
 * @author Roberto E. Escobar
 */
public class H2SqlManager extends SqlManagerImpl {

   public H2SqlManager(SqlDataType sqlDataType) {
      super(sqlDataType);
   }

   @Override
   public void createSchema(String schema) throws OseeCoreException {
      ConnectionHandler.runPreparedUpdate(String.format("%s SCHEMA IF NOT EXISTS \"%s\"", CREATE_STRING,
         schema.toUpperCase()));
   }

   @Override
   public void dropSchema(String schema) throws OseeCoreException {
      ConnectionHandler.runPreparedUpdate(String.format("%s SCHEMA IF EXISTS \"%s\"", DROP_STRING, schema.toUpperCase()));
   }

   @Override
   public void dropIndex(TableElement tableDef) {
      // Do Nothing -- Indexes are dropped during table drop
   }

}
