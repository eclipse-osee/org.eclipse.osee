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
package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.RelationDataHandler;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationLoader {

   private final IOseeDatabaseService dbService;
   private final SqlProvider sqlProvider;
   private final Log logger;
   private final RelationObjectFactory factory;

   public RelationLoader(Log logger, SqlProvider sqlProvider, IOseeDatabaseService dbService, RelationObjectFactory factory) {
      this.sqlProvider = sqlProvider;
      this.dbService = dbService;
      this.logger = logger;
      this.factory = factory;
   }

   public void loadFromQueryId(RelationDataHandler handler, LoadOptions options, int fetchSize, int queryId) throws OseeCoreException {
      if (options.isHistorical()) {//should this be done by the MasterLoader
         return; // TODO: someday we might have a use for historical relations, but not now
      }
      String sqlQuery = sqlProvider.getSql(OseeSql.LOAD_RELATIONS_NEWER);
      IOseeStatement statement = dbService.getStatement();
      try {
         long startTime = 0;
         int rowCount = 0;
         if (logger.isTraceEnabled()) {
            startTime = System.currentTimeMillis();
         }
         statement.runPreparedQuery(fetchSize, sqlQuery, queryId);
         if (logger.isTraceEnabled()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.trace("%d ms, for SQL[%s] fetchSize[%d] queryid[%d]", elapsedTime, sqlQuery, fetchSize, queryId);
            startTime = System.currentTimeMillis();
         }
         while (statement.next()) {
            rowCount++;

            int branchId = statement.getInt("branch_id");
            int txId = statement.getInt("transaction_id");
            long gamma = statement.getInt("gamma_id");

            VersionData version = factory.createVersion(branchId, txId, gamma, options.isHistorical());

            int localId = statement.getInt("rel_link_id");
            int typeId = statement.getInt("rel_link_type_id");
            ModificationType modType = ModificationType.getMod(statement.getInt("mod_type"));

            int parentId = statement.getInt("art_id");
            int aArtId = statement.getInt("a_art_id");
            int bArtId = statement.getInt("b_art_id");
            String rationale = statement.getString("rationale");

            RelationData data =
               factory.createRelationData(version, localId, typeId, modType, parentId, aArtId, bArtId, rationale);
            handler.onData(data);
         }
         if (logger.isTraceEnabled()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.trace("%d ms for %d rows, to iterate over resultset", elapsedTime, rowCount);
         }
      } finally {
         statement.close();
      }
   }
}
