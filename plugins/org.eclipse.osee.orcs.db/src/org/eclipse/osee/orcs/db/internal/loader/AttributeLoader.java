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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.AttributeDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Roberto E. Escobar
 */
public class AttributeLoader {

   private final SqlProvider sqlProvider;
   private final IOseeDatabaseService dbService;
   private final Log logger;
   private final AttributeObjectFactory factory;

   public AttributeLoader(Log logger, SqlProvider sqlProvider, IOseeDatabaseService dbService, AttributeObjectFactory factory) {
      this.sqlProvider = sqlProvider;
      this.dbService = dbService;
      this.logger = logger;
      this.factory = factory;
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

   private IdentityService getIdentityService() {
      return null;
   }

   private int toLocalId(Identity<Long> identity) throws OseeCoreException {
      return getIdentityService().getLocalId(identity);
   }

   private List<AbstractJoinQuery> TOTOTOTOTODO(LoadOptions options) throws OseeCoreException {
      List<AbstractJoinQuery> joins = new ArrayList<AbstractJoinQuery>();
      if (options.isSelectiveLoadingById()) {
         IdJoinQuery joinQuery = JoinUtility.createIdJoinQuery();
         for (Integer id : options.getAttributeIds()) {
            joinQuery.add(id);
         }
         joins.add(joinQuery);
      }

      if (options.isSelectiveLoadingByType()) {
         IdJoinQuery joinQuery = JoinUtility.createIdJoinQuery();
         for (IAttributeType type : options.getAttributeTypes()) {
            joinQuery.add(toLocalId(type));
         }
         joins.add(joinQuery);
      }
      return joins;
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

            int branchId = chStmt.getInt("branch_id");
            int txId = chStmt.getInt("transaction_id");
            long gamma = chStmt.getInt("gamma_id");

            VersionData version = factory.createVersion(branchId, txId, gamma, options.isHistorical());
            if (options.isHistorical()) {
               version.setStripeId(chStmt.getInt("stripe_transaction_id"));
            }

            int attrId = chStmt.getInt("attr_id");
            int artId = chStmt.getInt("art_id");
            int typeId = chStmt.getInt("attr_type_id");
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));

            String value = chStmt.getString("value");
            String uri = chStmt.getString("uri");

            AttributeData data = factory.createAttributeData(version, attrId, typeId, modType, artId, value, uri);
            handler.onData(data);
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