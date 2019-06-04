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
package org.eclipse.osee.orcs.db.internal.loader.handlers;

import org.eclipse.osee.framework.core.enums.ObjectType;
import org.eclipse.osee.framework.core.enums.TableEnum;
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
      jArtAlias = writer.addTable(TableEnum.JOIN_ID4_TABLE);
      artAlias = writer.addTable(TableEnum.ARTIFACT_TABLE);
      txsAlias = writer.addTable(TableEnum.TXS_TABLE, ObjectType.ARTIFACT);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.writeEqualsAnd(artAlias, "art_id", jArtAlias, "id2");
      writer.writeEqualsParameterAnd(jArtAlias, "query_id", criteria.getQueryId());
      writer.writeEqualsAnd(artAlias, txsAlias, "gamma_id");
      writer.writeTxBranchFilter(txsAlias);
   }
}