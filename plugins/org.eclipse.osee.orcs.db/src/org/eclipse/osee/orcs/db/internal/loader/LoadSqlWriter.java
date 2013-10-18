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
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
         {
            getAliasManager().hasAlias(TableEnum.ARTIFACT_TABLE),
            getAliasManager().hasAlias(TableEnum.ATTRIBUTE_TABLE),
            getAliasManager().hasAlias(TableEnum.RELATION_TABLE)};

      /**********************************************************************
       * Allow deleted artifacts applies even if the artifact table is not in the query. The other two only make sense
       * when the table is also used
       */
      boolean allowDeletedAtrifacts = OptionsUtil.areDeletedArtifactsIncluded(getOptions());
      boolean allowDeletedAttributes = OptionsUtil.areDeletedAttributesIncluded(getOptions());
      boolean allowDeletedRelations = OptionsUtil.areDeletedRelationsIncluded(getOptions());
      boolean areDeletedIncluded = allowDeletedAtrifacts || allowDeletedAttributes || allowDeletedRelations;
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
            areDeletedSame =
               !((hasTable[0] && !allowDeletedAtrifacts) || (hasTable[1] && !allowDeletedAttributes) || (hasTable[2] && !allowDeletedRelations));
         }
      }
      if (OptionsUtil.isHistorical(getOptions())) {
         sb.append(txsAlias);
         sb.append(".transaction_id <= ");
         sb.append(artJoinAlias);
         sb.append(".transaction_id");
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
               sb.append(String.valueOf(TxChange.CURRENT.getValue()));
               sb.append(", ");
               sb.append(String.valueOf(TxChange.DELETED.getValue()));
               sb.append(", ");
               sb.append(String.valueOf(TxChange.ARTIFACT_DELETED.getValue()));
               sb.append(")");
            } else {
               buildDeletedClause(sb, txsAlias);
            }
         } else {
            sb.append(txsAlias);
            sb.append(".tx_current = ");
            sb.append(String.valueOf(TxChange.CURRENT.getValue()));
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
      if (getAliasManager().hasAlias(TableEnum.ARTIFACT_TABLE)) {
         List<String> artTables = getAliasManager().getAliases(TableEnum.ARTIFACT_TABLE);
         if (OptionsUtil.areDeletedArtifactsIncluded(getOptions())) {
            sb.append("(");
            buildTableGamma(sb, artTables, txsAlias);
            sb.append(" AND ");
            buildTxClause(sb, txsAlias);
            sb.append(")");
            count++;
         }
      }
      if (getAliasManager().hasAlias(TableEnum.ATTRIBUTE_TABLE)) {
         List<String> attrTables = getAliasManager().getAliases(TableEnum.ATTRIBUTE_TABLE);
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
      if (getAliasManager().hasAlias(TableEnum.RELATION_TABLE)) {
         List<String> relationTables = getAliasManager().getAliases(TableEnum.RELATION_TABLE);
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
      sb.append(".tx_current");
      if (OptionsUtil.isHistorical(getOptions())) {
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
