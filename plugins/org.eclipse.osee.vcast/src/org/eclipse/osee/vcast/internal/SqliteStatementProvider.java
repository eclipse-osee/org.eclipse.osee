/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vcast.internal;

import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.vcast.internal.VCastDataStoreImpl.StatementProvider;

/**
 * @author Roberto E. Escobar
 */
public class SqliteStatementProvider implements StatementProvider {

   private final IOseeDatabaseService dbService;
   private final IDatabaseInfo dbInfo;

   public SqliteStatementProvider(IOseeDatabaseService dbService, IDatabaseInfo dbInfo) {
      super();
      this.dbService = dbService;
      this.dbInfo = dbInfo;
   }

   @Override
   public IOseeStatement getStatement() throws OseeCoreException {
      OseeConnection connection = dbService.getConnection(dbInfo);
      return dbService.getStatement(connection, true);
   }
}