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
package org.eclipse.osee.orcs.db.internal.attribute;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.orcs.core.ds.AttributeRow;
import org.eclipse.osee.orcs.core.ds.AttributeRowHandler;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Roberto E. Escobar
 */
public class AttributeLoader {

   public static interface DataProxyFactory {

      DataProxy createProxy(int proxyId, long typeUuid, String value, String uri) throws OseeCoreException;
   }

   private final SqlProvider sqlProvider;
   private final IOseeDatabaseService dbService;
   private final IdentityService identityService;
   private final DataProxyFactory proxyFactory;

   public AttributeLoader(SqlProvider sqlProvider, IOseeDatabaseService dbService, IdentityService identityService, DataProxyFactory proxyFactory) {
      this.sqlProvider = sqlProvider;
      this.dbService = dbService;
      this.identityService = identityService;
      this.proxyFactory = proxyFactory;
   }

   public String getSql(LoadOptions options) throws OseeCoreException {
      OseeSql sqlKey;
      if (options.isHistorical()) {
         sqlKey = OseeSql.LOAD_HISTORICAL_ATTRIBUTES;
      } else if (options.getLoadLevel().isHead()) {
         sqlKey = OseeSql.LOAD_ALL_CURRENT_ATTRIBUTES;
      } else if (options.areDeletedAllowed()) {
         sqlKey = OseeSql.LOAD_CURRENT_ATTRIBUTES_WITH_DELETED;
      } else {
         sqlKey = OseeSql.LOAD_CURRENT_ATTRIBUTES;
      }
      return sqlProvider.getSql(sqlKey);
   }

   private long toUuid(int localId) throws OseeCoreException {
      return identityService.getUniversalId(localId);
   }

   public void loadAttributeData(AttributeRowHandler handler, LoadOptions options, int queryId) throws OseeCoreException {
      if (options.getLoadLevel().isShallow() || options.getLoadLevel().isRelationsOnly()) {
         return;
      }

      IOseeStatement chStmt = dbService.getStatement();
      try {
         String sql = getSql(options);
         chStmt.runPreparedQuery(options.getFetchSize(), sql, queryId);

         AttributeRow previousAttr = new AttributeRow();

         List<AttributeRow> currentAttributes = new ArrayList<AttributeRow>();
         while (chStmt.next()) {
            int modId = chStmt.getInt("mod_type");

            AttributeRow nextAttr = new AttributeRow();
            nextAttr.setArtifactId(chStmt.getInt("art_id"));
            nextAttr.setBranchId(chStmt.getInt("branch_id"));
            nextAttr.setAttrId(chStmt.getInt("attr_id"));
            nextAttr.setGammaId(chStmt.getInt("gamma_id"));
            nextAttr.setTransactionId(chStmt.getInt("transaction_id"));
            nextAttr.setAttrTypeUuid(toUuid(chStmt.getInt("attr_type_id")));
            nextAttr.setModType(ModificationType.getMod(modId));
            nextAttr.setHistorical(options.isHistorical());

            String value = chStmt.getString("value");
            String uri = chStmt.getString("uri");
            DataProxy proxy =
               proxyFactory.createProxy(nextAttr.getArtifactId(), nextAttr.getAttrTypeUuid(), value, uri);
            nextAttr.setDataProxy(proxy);

            if (options.isHistorical()) {
               nextAttr.setStripeId(chStmt.getInt("stripe_transaction_id"));
            }

            if (!previousAttr.isSameArtifact(nextAttr)) {
               handler.onRow(currentAttributes);
               currentAttributes.clear();
            }
            currentAttributes.add(nextAttr);
            previousAttr = nextAttr;
         }
         handler.onRow(currentAttributes);
      } finally {
         chStmt.close();
      }
   }
}