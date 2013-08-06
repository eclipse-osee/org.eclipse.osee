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
package org.eclipse.osee.orcs.db.internal.loader;

import java.util.List;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class LoadSqlWriter extends AbstractSqlWriter {

   public LoadSqlWriter(Log logger, IOseeDatabaseService dbService, SqlProvider sqlProvider, SqlContext context) {
      super(logger, dbService, sqlProvider, context);
   }

   @Override
   public void writeSelect(List<SqlHandler<?>> handlers) throws OseeCoreException {
      String txAlias = getAliasManager().getFirstAlias(TableEnum.TXS_TABLE);
      String artJoinAlias = getAliasManager().getFirstAlias(TableEnum.ARTIFACT_JOIN_TABLE);

      write("SELECT%s ", getSqlHint());
      write("%s.gamma_id, %s.mod_type, %s.branch_id, %s.transaction_id", txAlias, txAlias, txAlias, txAlias);
      if (OptionsUtil.isHistorical(getOptions())) {
         write(", %s.transaction_id as stripe_transaction_id", txAlias);
      }
      write(",\n %s.art_id", artJoinAlias);
      int size = handlers.size();
      for (int index = 0; index < size; index++) {
         write(", ");
         SqlHandler<?> handler = handlers.get(index);
         handler.addSelect(this);
      }
   }

   @Override
   public void writeGroupAndOrder() throws OseeCoreException {
      String artAlias = getAliasManager().getFirstAlias(TableEnum.ARTIFACT_JOIN_TABLE);
      String txAlias = getAliasManager().getFirstAlias(TableEnum.TXS_TABLE);

      write("\n ORDER BY %s.branch_id, %s.art_id", txAlias, artAlias);
      if (getAliasManager().hasAlias(TableEnum.ATTRIBUTE_TABLE)) {
         write(", %s.attr_id", getAliasManager().getFirstAlias(TableEnum.ATTRIBUTE_TABLE));
      }
      if (getAliasManager().hasAlias(TableEnum.RELATION_TABLE)) {
         write(", %s.rel_link_id", getAliasManager().getFirstAlias(TableEnum.RELATION_TABLE));
      }
      write(", %s.transaction_id desc", txAlias);
   }

   @Override
   public String getTxBranchFilter(String txsAlias) {
      StringBuilder sb = new StringBuilder();
      String artJoinAlias = getAliasManager().getFirstAlias(TableEnum.ARTIFACT_JOIN_TABLE);
      writeTxFilter(txsAlias, artJoinAlias, sb);
      sb.append(" AND ");
      sb.append(txsAlias);
      sb.append(".branch_id = ");
      sb.append(artJoinAlias);
      sb.append(".branch_id");
      return sb.toString();
   }

   private void writeTxFilter(String txsAlias, String artJoinAlias, StringBuilder sb) {
      if (OptionsUtil.isHistorical(getOptions())) {
         sb.append(txsAlias);
         sb.append(".transaction_id <= ");
         sb.append(artJoinAlias);
         sb.append(".transaction_id");
         if (!OptionsUtil.areDeletedIncluded(getOptions())) {
            sb.append(" AND ");
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

   @Override
   public Options getOptions() {
      return getContext().getOptions();
   }
}
