/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.orcs.db.internal.search.handlers;

import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaGetReferenceArtifact;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

public class GetReferenceDetailsHandler extends SqlHandler<CriteriaGetReferenceArtifact> {

   CriteriaGetReferenceArtifact criteria;

   private String referenceAlias;
   private String mainAttAlias;
   private String artAlias;
   private String txsAlias;
   @Override
   public void setData(CriteriaGetReferenceArtifact criteria) {
      this.criteria = criteria;

   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      mainAttAlias = writer.getAliasManager().getFirstUsedAlias("atts");
      artAlias = writer.addTable(OseeDb.ARTIFACT_TABLE);
      txsAlias = writer.addTable(OseeDb.TXS_TABLE);
      if (OptionsUtil.getIncludeApplicabilityTokens(writer.getOptions())) {
         writer.getMainTableAlias(OseeDb.OSEE_KEY_VALUE_TABLE);
      }
      if (OptionsUtil.getIncludeLatestTransactionDetails(writer.getOptions())) {
         writer.getMainTableAlias(OseeDb.TX_DETAILS_TABLE);
      }
   }

   public void writeFromClause(AbstractSqlWriter writer) {
      writer.write("from " + mainAttAlias + ", osee_artifact " + artAlias + ", osee_txs " + txsAlias);

   }

   @Override
   public void writeSelectFields(AbstractSqlWriter writer) {
      writer.write(
         "select " + artAlias + ".art_id, " + artAlias + ".art_type_id, " + txsAlias + ".app_id, " + txsAlias + ".transaction_id, " + txsAlias + ".mod_type, " + txsAlias + ".tx_current," + txsAlias + ".gamma_id, 0 AS top, " + mainAttAlias + ".art_id source_art_id , " + mainAttAlias + ".art_type_id source_art_type_id, " + mainAttAlias + ".attr_id source_attr_id \n");
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.write(
         mainAttAlias + ".type_id = ? and " + mainAttAlias + ".value = " + writer.getJdbcClient().getDbType().getPostgresCastStart() + artAlias + ".art_id " + writer.getJdbcClient().getDbType().getPostgresCastVarCharEnd() + " and " + artAlias + ".gamma_id = " + txsAlias + ".gamma_id and " + txsAlias + ".branch_id = ? and " + txsAlias + ".tx_current =  ? ");
      if (writer.getRootQueryData().getView().isValid()) {
         writer.writeAnd();
         writer.write(writer.getAliasManager().getFirstUsedAlias(
            AbstractSqlWriter.validApps) + ".app_id = " + txsAlias + ".app_id ");
      }
      writer.addParameter(criteria.getAttributeType().getId());
      writer.addParameter(writer.getRootQueryData().getBranch().getId());
      writer.addParameter(1);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.REFERENCE_ARTIFACT.ordinal();
   }

}
