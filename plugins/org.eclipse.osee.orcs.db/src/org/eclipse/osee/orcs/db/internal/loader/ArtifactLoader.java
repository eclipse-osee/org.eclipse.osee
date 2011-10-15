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
import org.eclipse.osee.orcs.core.ds.ArtifactRow;
import org.eclipse.osee.orcs.core.ds.ArtifactRowHandler;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactLoader {

   private static int TRANSACTION_SENTINEL = -1;

   @SuppressWarnings("unused")
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
         chStmt.runPreparedQuery(fetchSize, sql, queryId);
         int previousArtId = -1;
         int previousBranchId = -1;
         while (chStmt.next()) {
            int artifactId = chStmt.getInt("art_id");
            int branchId = chStmt.getInt("branch_id");
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
            // assumption: SQL is returning rows ordered by branch_id, art_id, transaction_id in descending order
            if (previousArtId != artifactId || previousBranchId != branchId) {
               // assumption: SQL is returning unwanted deleted artifacts only in the historical case
               if (!options.isHistorical() || options.areDeletedIncluded() || modType != ModificationType.DELETED) {

                  ArtifactRow row = new ArtifactRow();

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
      } finally {
         chStmt.close();
      }
   }

   /*
    * public void loadFromIds(Collection<Integer> artIds, int branchId, int transactionId, ArtifactRowHandler handler,
    * LoadOptions options) throws OseeCoreException { if (artIds != null && !artIds.isEmpty()) { Collection<Integer>
    * unique; if (artIds instanceof Set) { unique = artIds; } else { unique = new HashSet<Integer>();
    * unique.addAll(artIds); } Integer transactionInfo = null; if (options.isHistorical()) { transactionInfo =
    * transactionId; } ArtifactJoinQuery query = JoinUtility.createArtifactJoinQuery(); for (int artId : unique) {
    * query.add(artId, branchId, transactionInfo); } if (!query.isEmpty()) { OseeConnection connection =
    * dbService.getConnection(); try { try { query.store(connection); loadFromQueryId(handler, options, query.size(),
    * query.getQueryId()); } finally { query.delete(connection); } } finally { connection.close(); } } } }
    */
   //   /**
   //    * (re)loads the artifacts selected by sql and then returns them in a list
   //    */
   //   public static List<Artifact> getArtifacts(String sql, Object[] queryParameters, int artifactCountEstimate, LoadLevel loadLevel, LoadType reload, ISearchConfirmer confirmer, TransactionRecord transactionId, DeletionFlag allowDeleted) throws OseeCoreException {
   //      List<Artifact> artifacts = new ArrayList<Artifact>(artifactCountEstimate);
   //      int queryId = getNewQueryId();
   //      CompositeKeyHashMap<Integer, Integer, Object[]> insertParameters =
   //         new CompositeKeyHashMap<Integer, Integer, Object[]>(artifactCountEstimate, false);
   //      selectArtifacts(artifacts, queryId, insertParameters, sql, queryParameters, artifactCountEstimate, transactionId,
   //         reload);
   //
   //      boolean historical = transactionId != null;
   //      if (!insertParameters.isEmpty()) {
   //         artifacts.addAll(loadArtifacts(queryId, loadLevel, confirmer,
   //            new ArrayList<Object[]>(insertParameters.values()), reload, historical, allowDeleted));
   //      } else if (confirmer != null) {
   //         confirmer.canProceed(artifacts.size());
   //      }
   //      return artifacts;
   //   }

   //   /**
   //    * Populates artifacts with any artifact already in cache and populates insertParameters with necessary data to load
   //    * the rest.
   //    * 
   //    * @param reload will attempt to use cache if INCLUDE_CACHE
   //    * @param insertParameters populated by this method
   //    */
   //   private void selectArtifacts(List<Artifact> artifacts, int queryId, CompositeKeyHashMap<Integer, Integer, Object[]> insertParameters, String sql, Object[] queryParameters, int artifactCountEstimate, TransactionRecord transactionId, LoadType reload) throws OseeCoreException {
   //      IOseeStatement chStmt = dbService.getStatement();
   //      long time = System.currentTimeMillis();
   //      try {
   //         chStmt.runPreparedQuery(artifactCountEstimate, sql, queryParameters);
   //         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
   //
   //         while (chStmt.next()) {
   //            int artId = chStmt.getInt("art_id");
   //            int branchId = chStmt.getInt("branch_id");
   //            Artifact artifact = getArtifactFromCache(artId, transactionId, branchId);
   //            if (artifact != null && reload == LoadType.INCLUDE_CACHE) {
   //               artifacts.add(artifact);
   //            } else {
   //               Object transactionParameter = transactionId == null ? SQL3DataType.INTEGER : transactionId.getId();
   //               insertParameters.put(artId, branchId, new Object[] {
   //                  queryId,
   //                  insertTime,
   //                  artId,
   //                  branchId,
   //                  transactionParameter});
   //            }
   //         }
   //      } finally {
   //         chStmt.close();
   //      }
   //      logger.debug(new Exception("Artifact Selection Time"), "Artifact Selection Time [%s], [%d] artifacts selected",
   //         Lib.getElapseString(time), insertParameters.size());
   //   }
}