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
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.AttributeDataHandler;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Roberto E. Escobar
 */
public class AttributeLoader {

   public static interface ProxyDataFactory {

      DataProxy createProxy(long typeUuid, String value, String uri) throws OseeCoreException;
   }

   private final SqlProvider sqlProvider;
   private final IOseeDatabaseService dbService;
   private final IdentityService identityService;
   private final ProxyDataFactory proxyFactory;
   private final Log logger;

   public AttributeLoader(Log logger, SqlProvider sqlProvider, IOseeDatabaseService dbService, IdentityService identityService, ProxyDataFactory proxyFactory) {
      this.sqlProvider = sqlProvider;
      this.dbService = dbService;
      this.identityService = identityService;
      this.proxyFactory = proxyFactory;
      this.logger = logger;
   }

   public String getSql(LoadOptions options) throws OseeCoreException {
      OseeSql sqlKey;
      if (options.isHistorical()) {
         sqlKey = OseeSql.LOAD_HISTORICAL_ATTRIBUTES;
      } else if (options.getLoadLevel().isHead()) {
         sqlKey = OseeSql.LOAD_ALL_CURRENT_ATTRIBUTES;
      } else if (options.areDeletedIncluded()) {
         sqlKey = OseeSql.LOAD_CURRENT_ATTRIBUTES_WITH_DELETED;
      } else {
         sqlKey = OseeSql.LOAD_CURRENT_ATTRIBUTES;
      }
      return sqlProvider.getSql(sqlKey);
   }

   private long toUuid(int localId) throws OseeCoreException {
      return identityService.getUniversalId(localId);
   }

   public void loadFromQueryId(AttributeDataHandler handler, LoadOptions options, int fetchSize, int queryId) throws OseeCoreException {
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
         while (chStmt.next()) {
            rowCount++;
            AttributeData nextAttr = new AttributeData();
            nextAttr.setArtifactId(chStmt.getInt("art_id"));
            nextAttr.setBranchId(chStmt.getInt("branch_id"));
            nextAttr.setAttrId(chStmt.getInt("attr_id"));
            nextAttr.setGammaId(chStmt.getInt("gamma_id"));
            nextAttr.setTransactionId(chStmt.getInt("transaction_id"));
            nextAttr.setAttrTypeUuid(toUuid(chStmt.getInt("attr_type_id")));

            int modId = chStmt.getInt("mod_type");
            nextAttr.setModType(ModificationType.getMod(modId));
            nextAttr.setHistorical(options.isHistorical());

            String value = chStmt.getString("value");
            String uri = chStmt.getString("uri");
            nextAttr.setValue(value);
            nextAttr.setUri(uri);
            DataProxy proxy = proxyFactory.createProxy(nextAttr.getAttrTypeUuid(), value, uri);
            nextAttr.setDataProxy(proxy);

            if (options.isHistorical()) {
               nextAttr.setStripeId(chStmt.getInt("stripe_transaction_id"));
            }
            handler.onData(nextAttr);
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