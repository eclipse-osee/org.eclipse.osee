/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactTxComment;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactTxCommentSqlHandler extends SqlHandler<CriteriaArtifactTxComment> {
   private CriteriaArtifactTxComment criteria;
   private String txdAlias;
   private String txsAttAlias;
   private String attAlias;
   private String tuple2Alias;
   private String artAlias;

   @Override
   public void setData(CriteriaArtifactTxComment criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      txdAlias = writer.addTable(TableEnum.TX_DETAILS_TABLE);
      txsAttAlias = writer.addTable(TableEnum.TXS_TABLE);
      attAlias = writer.addTable(TableEnum.ATTRIBUTE_TABLE);
      tuple2Alias = writer.addTable(TableEnum.TUPLE2);
      artAlias = writer.getMainTableAlias(TableEnum.ARTIFACT_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.writePatternMatch(txdAlias, "osee_comment", criteria.getCommentPattern());
      writer.writeBranchFilter(txdAlias);
      writer.writeAnd();
      writer.writeEqualsAnd(txdAlias, txsAttAlias, "transaction_id");
      writer.writeEqualsAnd(txdAlias, txsAttAlias, "branch_id");
      writer.writeEqualsAnd(txsAttAlias, attAlias, "gamma_id");
      writer.writeEqualsAnd(attAlias, "attr_type_id", tuple2Alias, "e2");
      writer.writeEqualsParameterAnd(tuple2Alias, "e1", criteria.getTypeJoin());
      writer.writeEqualsParameterAnd(tuple2Alias, "tuple_type", criteria.getTypeJoin().getTupleType());
      writer.writeEquals(attAlias, artAlias, "art_id");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.LAST.ordinal();
   }
}