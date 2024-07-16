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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeSqlHandler extends SqlHandler<CriteriaArtifactType> {
   private CriteriaArtifactType criteria;
   private String jIdAlias;
   private AbstractJoinQuery joinQuery;
   private String artAlias;
   private String txsAlias;

   @Override
   public void setData(CriteriaArtifactType criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (criteria.getTypes().size() > 1) {
         jIdAlias = writer.addTable(OseeDb.OSEE_JOIN_ID_TABLE);
      }
      artAlias = writer.getMainTableAlias(OseeDb.ARTIFACT_TABLE);
      txsAlias = writer.getMainTableAlias(OseeDb.TXS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      Collection<? extends ArtifactTypeId> types = criteria.getTypes();

      if (types.size() > 1) {
         joinQuery = writer.writeJoin(types);
         writer.writeEqualsParameterAnd(jIdAlias, "query_id", joinQuery.getQueryId());
         writer.writeEquals(jIdAlias, "id", artAlias, "art_type_id");
      } else {
         writer.writeEqualsParameter(artAlias, "art_type_id", types.iterator().next());
      }
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ARTIFACT_TYPE.ordinal();
   }
}