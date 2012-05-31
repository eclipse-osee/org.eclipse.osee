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
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactRowHandler;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactLoader {

   private static int TRANSACTION_SENTINEL = -1;

   private final Log logger;
   private final SqlProvider sqlProvider;
   private final IOseeDatabaseService dbService;
   private final IdentityService identityService;

   public ArtifactLoader(Log logger, SqlProvider sqlProvider, IOseeDatabaseService dbService, IdentityService identityService) {
      super();
      this.logger = logger;
      this.sqlProvider = sqlProvider;
      this.dbService = dbService;
      this.identityService = identityService;
   }

   private String getSql(LoadOptions options) throws OseeCoreException {
      OseeSql sqlKey;
      if (options.isHistorical()) {
         sqlKey = OseeSql.LOAD_HISTORICAL_ARTIFACTS;
      } else if (options.areDeletedIncluded()) {
         sqlKey = OseeSql.LOAD_CURRENT_ARTIFACTS_WITH_DELETED;
      } else {
         sqlKey = OseeSql.LOAD_CURRENT_ARTIFACTS;
      }
      return sqlProvider.getSql(sqlKey);
   }

   private long toUuid(int localId) throws OseeCoreException {
      return identityService.getUniversalId(localId);
   }

   public void loadFromQueryId(ArtifactRowHandler handler, LoadOptions options, int fetchSize, int queryId) throws OseeCoreException {
      String sql = getSql(options);
      IOseeStatement chStmt = dbService.getStatement();
      try {
         long startTime = 0;
         int rowCount = 0;
         if (logger.isTraceEnabled()) {
            startTime = System.currentTimeMillis();
         }
         chStmt.runPreparedQuery(fetchSize, sql, queryId);
         if (logger.isTraceEnabled()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.trace("%d ms, for SQL[%s] fetchSize[%d] queryid[%d]", elapsedTime, sql, fetchSize, queryId);
            startTime = System.currentTimeMillis();
         }
         int previousArtId = -1;
         int previousBranchId = -1;
         while (chStmt.next()) {
            if (logger.isTraceEnabled()) {
               rowCount++;
            }
            int artifactId = chStmt.getInt("art_id");
            int branchId = chStmt.getInt("branch_id");
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
            // assumption: SQL is returning rows ordered by branch_id, art_id, transaction_id in descending order
            if (previousArtId != artifactId || previousBranchId != branchId) {
               // assumption: SQL is returning unwanted deleted artifacts only in the historical case
               if (!options.isHistorical() || options.areDeletedIncluded() || modType != ModificationType.DELETED) {

                  ArtifactData row = new ArtifactData();

                  row.setArtifactId(artifactId);
                  row.setBranchId(branchId);
                  row.setArtTypeUuid(toUuid(chStmt.getInt("art_type_id")));

                  row.setTransactionId(TRANSACTION_SENTINEL);
                  row.setGammaId(chStmt.getInt("gamma_id"));
                  row.setModType(modType);

                  row.setGuid(chStmt.getString("guid"));
                  row.setHumanReadableId(chStmt.getString("human_readable_id"));
                  row.setHistorical(options.isHistorical());

                  if (options.isHistorical()) {
                     row.setStripeId(chStmt.getInt("stripe_transaction_id"));
                     row.setTransactionId(row.getTransactionId());
                  }
                  handler.onRow(row);
               }
               previousArtId = artifactId;
               previousBranchId = branchId;
            }
         }
         if (logger.isTraceEnabled()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.trace("%d ms for %d rows, to iterate over resultset", elapsedTime, rowCount);
         }
      } finally {
         chStmt.close();
      }
   }
}