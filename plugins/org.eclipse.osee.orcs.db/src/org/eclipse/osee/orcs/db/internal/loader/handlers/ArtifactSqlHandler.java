/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.loader.handlers;

import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaArtifact;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactSqlHandler extends SqlHandler<CriteriaArtifact> {

   private CriteriaArtifact criteria;
   private String jArtAlias;
   private String artAlias;
   private String txsAlias;

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ARTIFACT_LOADER.ordinal();
   }

   @Override
   public void setData(CriteriaArtifact criteria) {
      this.criteria = criteria;
   }

   @Override
   public void writeSelectFields(AbstractSqlWriter writer) {
      writer.writeCommaIfNotFirst();
      writer.write("%s.art_type_id, %s.guid", artAlias, artAlias);
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      jArtAlias = writer.addTable(OseeDb.OSEE_JOIN_ID4_TABLE);
      artAlias = writer.addTable(OseeDb.ARTIFACT_TABLE);
      txsAlias = writer.addTable(OseeDb.TXS_TABLE, ObjectType.ARTIFACT);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.writeEqualsAnd(artAlias, "art_id", jArtAlias, "id2");
      writer.writeEqualsParameterAnd(jArtAlias, "query_id", criteria.getQueryId());
      writer.writeEqualsAnd(artAlias, txsAlias, "gamma_id");
      writer.writeTxBranchFilter(txsAlias);
   }
}