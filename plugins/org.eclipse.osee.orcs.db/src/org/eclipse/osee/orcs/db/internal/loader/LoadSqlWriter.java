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

import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.QueryType;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class LoadSqlWriter extends AbstractSqlWriter {

   public LoadSqlWriter(Log logger, SqlJoinFactory joinFactory, JdbcClient jdbcClient, SqlContext context) {
      super(logger, joinFactory, jdbcClient, context, QueryType.SELECT);
   }

   @Override
   public void writeSelect(Iterable<SqlHandler<?>> handlers) throws OseeCoreException {
      String txAlias = getLastAlias(TableEnum.TXS_TABLE);
      String artJoinAlias = getLastAlias(TableEnum.JOIN_ID4_TABLE);

      write("SELECT%s ", getSqlHint());
      write("%s.gamma_id, %s.mod_type, %s.branch_id, %s.transaction_id, %s.app_id", txAlias, txAlias, txAlias, txAlias,
         txAlias);
      if (OptionsUtil.isHistorical(getOptions())) {
         write(", %s.transaction_id as stripe_transaction_id", txAlias);
      }
      write(",\n %s.id2, %s.id4", artJoinAlias, artJoinAlias);
      for (SqlHandler<?> handler : handlers) {
         setHandlerLevel(handler);
         write(", ");
         handler.addSelect(this);
      }
   }

   @Override
   public void writeGroupAndOrder() throws OseeCoreException {
      String artAlias = getLastAlias(TableEnum.JOIN_ID4_TABLE);
      String txAlias = getLastAlias(TableEnum.TXS_TABLE);

      write("\n ORDER BY %s.branch_id, %s.id2", txAlias, artAlias);
      if (hasAlias(TableEnum.ATTRIBUTE_TABLE)) {
         write(", %s.attr_id", getLastAlias(TableEnum.ATTRIBUTE_TABLE));
      }
      if (hasAlias(TableEnum.RELATION_TABLE)) {
         write(", %s.rel_link_id", getLastAlias(TableEnum.RELATION_TABLE));
      }
      write(", %s.transaction_id desc", txAlias);
   }

   @Override
   public String getTxBranchFilter(String txsAlias) {
      boolean allowDeletedAtrifacts = OptionsUtil.areDeletedArtifactsIncluded(getOptions());
      boolean allowDeletedAttributes = OptionsUtil.areDeletedAttributesIncluded(getOptions());
      boolean allowDeletedRelations = OptionsUtil.areDeletedRelationsIncluded(getOptions());
      boolean areDeletedIncluded = allowDeletedAtrifacts || allowDeletedAttributes || allowDeletedRelations;
      return getTxBranchFilter(txsAlias, areDeletedIncluded);
   }

   @Override
   public String getTxBranchFilter(String txsAlias, boolean allowDeleted) {
      StringBuilder sb = new StringBuilder();
      String artJoinAlias = getLastAlias(TableEnum.JOIN_ID4_TABLE);
      writeTxFilter(txsAlias, artJoinAlias, sb, allowDeleted);
      sb.append(" AND ");
      sb.append(txsAlias);
      sb.append(".branch_id = ");
      sb.append(artJoinAlias);
      sb.append(".id1");
      return sb.toString();
   }

   private void writeTxFilter(String txsAlias, String artJoinAlias, StringBuilder sb, boolean areDeletedIncluded) {
      //@formatter:off
      /*********************************************************************
       * The clause handling the inclusion of deleted items changes based on case
       *   note this applies to the 3 tables ARTIFACT_TABLE, ATTRIBUTE_TABLE, RELATION_TABLE
       * case 1: No items allow deleted
       * case 2: One table in query
       * case 3: All tables that are in the query allow deleted
       * case 4: More than one table with differing deletion flags
       */
      //@formatter:on
      boolean hasTable[] =
         {hasAlias(TableEnum.ARTIFACT_TABLE), hasAlias(TableEnum.ATTRIBUTE_TABLE), hasAlias(TableEnum.RELATION_TABLE)};

      /**********************************************************************
       * Allow deleted artifacts applies even if the artifact table is not in the query. The other two only make sense
       * when the table is also used
       */

      boolean areDeletedSame = true;
      if (areDeletedIncluded) {
         /********************************
          * there must be at least 2 table in the query for a difference
          */
         int count = 0;
         for (boolean add : hasTable) {
            if (add) {
               count++;
            }
         }
         if (count > 1) {
            boolean allowDeletedAtrifacts = OptionsUtil.areDeletedArtifactsIncluded(getOptions());
            boolean allowDeletedAttributes = OptionsUtil.areDeletedAttributesIncluded(getOptions());
            boolean allowDeletedRelations = OptionsUtil.areDeletedRelationsIncluded(getOptions());
            areDeletedSame =
               !(hasTable[0] && !allowDeletedAtrifacts || hasTable[1] && !allowDeletedAttributes || hasTable[2] && !allowDeletedRelations);
         }
      }
      if (OptionsUtil.isHistorical(getOptions())) {
         sb.append(txsAlias);
         sb.append(".transaction_id <= ");
         sb.append(artJoinAlias);
         sb.append(".id3");
         if (!areDeletedIncluded) {
            sb.append(" AND ");
            sb.append(txsAlias);
            sb.append(".mod_type");
            sb.append(" != ");
            sb.append(String.valueOf(ModificationType.DELETED.getValue()));
         } else if (!areDeletedSame) {
            sb.append(" AND ");
            buildDeletedClause(sb, txsAlias);
         }
      } else {
         if (areDeletedIncluded) {
            if (areDeletedSame) {
               sb.append(txsAlias);
               sb.append(".tx_current");
               sb.append(" IN (");
               sb.append(String.valueOf(TxChange.CURRENT));
               sb.append(", ");
               sb.append(String.valueOf(TxChange.DELETED));
               sb.append(", ");
               sb.append(String.valueOf(TxChange.ARTIFACT_DELETED));
               sb.append(")");
            } else {
               buildDeletedClause(sb, txsAlias);
            }
         } else {
            sb.append(txsAlias);
            sb.append(".tx_current = ");
            sb.append(String.valueOf(TxChange.CURRENT));
         }
      }
   }

   @Override
   public Options getOptions() {
      return getContext().getOptions();
   }

   private void buildDeletedClause(StringBuilder sb, String txsAlias) {
      /*****************************************************************
       * It is assumed this is called only if at least one type of deleted is allowed and they differ. These checks are
       * not made
       */
      int count = 0;
      if (hasAlias(TableEnum.ARTIFACT_TABLE)) {
         List<String> artTables = getAliases(TableEnum.ARTIFACT_TABLE);
         if (OptionsUtil.areDeletedArtifactsIncluded(getOptions())) {
            sb.append("(");
            buildTableGamma(sb, artTables, txsAlias);
            sb.append(" AND ");
            buildTxClause(sb, txsAlias);
            sb.append(")");
            count++;
         }
      }
      if (hasAlias(TableEnum.ATTRIBUTE_TABLE)) {
         List<String> attrTables = getAliases(TableEnum.ATTRIBUTE_TABLE);
         if (OptionsUtil.areDeletedAttributesIncluded(getOptions())) {
            if (count > 1) {
               sb.append(" AND ");
            }
            sb.append("(");
            buildTableGamma(sb, attrTables, txsAlias);
            sb.append(" AND ");
            buildTxClause(sb, txsAlias);
            sb.append(")");
            count++;
         }
      }
      if (hasAlias(TableEnum.RELATION_TABLE)) {
         List<String> relationTables = getAliases(TableEnum.RELATION_TABLE);
         if (OptionsUtil.areDeletedAttributesIncluded(getOptions())) {
            if (count > 1) {
               sb.append(" AND ");
            }
            sb.append("(");
            buildTableGamma(sb, relationTables, txsAlias);
            sb.append(" AND ");
            buildTxClause(sb, txsAlias);
            sb.append(")");
            count++;
         }
      }
   }

   private void buildTableGamma(StringBuilder sb, List<String> tableAliases, String txsAlias) {
      if (tableAliases.size() == 1) {
         sb.append(tableAliases.get(0));
         sb.append(".gamma_id = ");
         sb.append(txsAlias);
         sb.append(".gamma_id");
      } else {
         Iterator<String> iter = tableAliases.iterator();
         iter.next();
         sb.append("(");
         sb.append(iter);
         sb.append(".gamma_id = ");
         sb.append(txsAlias);
         sb.append(".gamma_id");
         while (iter.hasNext()) {
            iter.next();
            sb.append(" OR ");
            sb.append(iter);
            sb.append(".gamma_id = ");
            sb.append(txsAlias);
            sb.append(".gamma_id");
         }
         sb.append(")");
      }
   }

   private void buildTxClause(StringBuilder sb, String txsAlias) {

      sb.append(txsAlias);
      if (!OptionsUtil.isHistorical(getOptions())) {
         sb.append(".tx_current = ");
         sb.append(String.valueOf(TxChange.CURRENT));
      }
   }

   @Override
   public String getWithClauseTxBranchFilter(String txsAlias, boolean deletedPredicate) throws OseeCoreException {
      return Strings.emptyString();
   }

}
