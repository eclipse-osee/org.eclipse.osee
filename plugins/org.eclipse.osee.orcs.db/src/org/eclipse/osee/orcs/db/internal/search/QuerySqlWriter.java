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
package org.eclipse.osee.orcs.db.internal.search;

import java.util.List;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataPostProcessor;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

public class QuerySqlWriter extends AbstractSqlWriter {

   private final int branchId;
   private final QueryType queryType;

   public QuerySqlWriter(Log logger, IOseeDatabaseService dbService, SqlProvider sqlProvider, SqlContext<? extends DataPostProcessor<?>> context, QueryType queryType, int branchId) {
      super(logger, dbService, sqlProvider, context);
      this.queryType = queryType;
      this.branchId = branchId;
   }

   private void writeSelectHelper() throws OseeCoreException {
      String txAlias = getAliasManager().getFirstAlias(TableEnum.TXS_TABLE);
      String artAlias = getAliasManager().getFirstAlias(TableEnum.ARTIFACT_TABLE);

      write("SELECT%s ", getSqlHint());
      if (OptionsUtil.isHistorical(getOptions())) {
         write("max(%s.transaction_id) as transaction_id, %s.art_id, %s.branch_id", txAlias, artAlias, txAlias);
      } else {
         write("%s.art_id, %s.branch_id", artAlias, txAlias);
      }
   }

   @Override
   public void writeSelect(List<SqlHandler<?>> handlers) throws OseeCoreException {
      if (queryType == QueryType.COUNT_ARTIFACTS) {
         if (OptionsUtil.isHistorical(getOptions())) {
            write("SELECT count(xTable.art_id) FROM (\n ");
            writeSelectHelper();
         } else {
            String artAlias = getAliasManager().getFirstAlias(TableEnum.ARTIFACT_TABLE);
            write("SELECT%s count(%s.art_id)", getSqlHint(), artAlias);
         }
      } else {
         writeSelectHelper();
      }
   }

   @Override
   public void writeGroupAndOrder() throws OseeCoreException {
      if (OptionsUtil.isHistorical(getOptions())) {
         String txAlias = getAliasManager().getFirstAlias(TableEnum.TXS_TABLE);
         String artAlias = getAliasManager().getFirstAlias(TableEnum.ARTIFACT_TABLE);

         write("\n GROUP BY %s.art_id, %s.branch_id", artAlias, txAlias);
      }
      if (queryType != QueryType.COUNT_ARTIFACTS) {
         String txAlias = getAliasManager().getFirstAlias(TableEnum.TXS_TABLE);
         String artAlias = getAliasManager().getFirstAlias(TableEnum.ARTIFACT_TABLE);

         write("\n ORDER BY %s.art_id, %s.branch_id", artAlias, txAlias);
      } else {
         if (OptionsUtil.isHistorical(getOptions())) {
            write("\n) xTable");
         }
      }
   }

   @Override
   public String getTxBranchFilter(String txsAlias) {
      StringBuilder sb = new StringBuilder();
      writeTxFilter(txsAlias, sb);
      if (branchId > 0) {
         sb.append(" AND ");
         sb.append(txsAlias);
         sb.append(".branch_id = ?");
         addParameter(branchId);
      }
      return sb.toString();
   }

   private void writeTxFilter(String txsAlias, StringBuilder sb) {
      if (OptionsUtil.isHistorical(getOptions())) {
         sb.append(txsAlias);
         sb.append(".transaction_id <= ?");
         addParameter(OptionsUtil.getFromTransaction(getOptions()));
         if (!OptionsUtil.areDeletedIncluded(getOptions())) {
            sb.append(AND_WITH_NEWLINES);
            sb.append(txsAlias);
            sb.append(".tx_current");
            sb.append(" IN (");
            sb.append(String.valueOf(TxChange.CURRENT.getValue()));
            sb.append(", ");
            sb.append(String.valueOf(TxChange.NOT_CURRENT.getValue()));
            sb.append(")");
         }
      } else {
         sb.append(txsAlias);
         sb.append(".tx_current");
         if (OptionsUtil.areDeletedIncluded(getOptions())) {
            sb.append(" IN (");
            sb.append(String.valueOf(TxChange.CURRENT.getValue()));
            sb.append(", ");
            sb.append(String.valueOf(TxChange.DELETED.getValue()));
            sb.append(", ");
            sb.append(String.valueOf(TxChange.ARTIFACT_DELETED.getValue()));
            sb.append(")");
         } else {
            sb.append(" = ");
            sb.append(String.valueOf(TxChange.CURRENT.getValue()));
         }
      }
   }

}
