/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.engines;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactQuerySqlWriter extends AbstractSqlWriter {
   private final BranchId branch;

   public ArtifactQuerySqlWriter(SqlJoinFactory joinFactory, JdbcClient jdbcClient, SqlContext context, QueryData queryData) {
      super(joinFactory, jdbcClient, context, queryData);
      this.branch = queryData.getBranch();
   }

   public ArtifactQuerySqlWriter(AbstractSqlWriter parent) {
      super(null, null, parent.getContext(), null);
      this.branch = ((ArtifactQuerySqlWriter) parent).branch;
   }

   @Override
   protected void writeSelectFields() {
      String txAlias = getMainTableAlias(TableEnum.TXS_TABLE);
      String artAlias = getMainTableAlias(TableEnum.ARTIFACT_TABLE);

      writeSelectFields(artAlias, "art_id", txAlias, "branch_id");
      if (OptionsUtil.isHistorical(getOptions())) {
         writeSelectFields(txAlias, "transaction_id");
      }
   }

   @Override
   public void writeGroupAndOrder() {
      if (queryData.isCountQueryType()) {
         if (OptionsUtil.isHistorical(getOptions())) {
            write("\n) xTable");
         }
      } else {
         write("\n ORDER BY %s.art_id, %s.branch_id", getMainTableAlias(TableEnum.ARTIFACT_TABLE),
            getMainTableAlias(TableEnum.TXS_TABLE));
      }
   }

   @Override
   public void writeTxBranchFilter(String txsAlias, boolean allowDeleted) {
      writeTxFilter(txsAlias, output, allowDeleted);
      if (branch.isValid()) {
         write(" AND ");
         write(txsAlias);
         write(".branch_id = ?");
         addParameter(branch);
      } else {
         throw new OseeArgumentException("getTxBranchFilter: branch uuid must be > 0");
      }
   }

   private void writeTxFilter(String txsAlias, StringBuilder sb, boolean allowDeleted) {
      if (OptionsUtil.isHistorical(getOptions())) {
         if (allowDeleted) {
            removeDanglingSeparator(AND_NEW_LINE);
            removeDanglingSeparator(" AND ");
         } else {
            sb.append(txsAlias);
            sb.append(".mod_type <> ");
            sb.append(ModificationType.DELETED.getIdString());
         }
      } else {
         writeTxCurrentFilter(txsAlias, sb, allowDeleted);
      }
   }

   @Override
   public String getWithClauseTxBranchFilter(String txsAlias, boolean deletedPredicate) {
      StringBuilder sb = new StringBuilder();

      if (deletedPredicate) {
         boolean allowDeleted = //
            OptionsUtil.areDeletedArtifactsIncluded(getOptions()) || //
               OptionsUtil.areDeletedAttributesIncluded(getOptions()) || //
               OptionsUtil.areDeletedRelationsIncluded(getOptions());
         writeTxFilter(txsAlias, sb, allowDeleted);
      } else {
         if (OptionsUtil.isHistorical(getOptions())) {
            sb.append(txsAlias);
            sb.append(".transaction_id <= ?");
            addParameter(OptionsUtil.getFromTransaction(getOptions()));
         }
      }
      if (branch.isValid()) {
         if (sb.length() > 0) {
            sb.append(" AND ");
         }
         sb.append(txsAlias);
         sb.append(".branch_id = ?");
         addParameter(branch);
      }
      return sb.toString();
   }
}