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
package org.eclipse.osee.orcs.db.internal.search.handlers;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.db.internal.search.SqlConstants.CriteriaPriority;
import org.eclipse.osee.orcs.db.internal.search.SqlConstants.TableEnum;
import org.eclipse.osee.orcs.db.internal.search.SqlHandler;
import org.eclipse.osee.orcs.db.internal.search.SqlWriter;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactIdsSqlHandler extends SqlHandler {

   private CriteriaArtifactIds criteria;

   private String artAlias;
   private String jIdAlias;
   private String txsAlias;
   private AbstractJoinQuery joinQuery;

   @Override
   public void setData(Criteria criteria) {
      this.criteria = (CriteriaArtifactIds) criteria;
   }

   @Override
   public void addTables(SqlWriter writer) throws OseeCoreException {
      if (criteria.getIds().size() > 1) {
         jIdAlias = writer.writeTable(TableEnum.ID_JOIN_TABLE);
      }
      artAlias = writer.writeTable(TableEnum.ARTIFACT_TABLE);
      txsAlias = writer.writeTable(TableEnum.TXS_TABLE);
   }

   @Override
   public void addPredicates(SqlWriter writer) throws OseeCoreException {
      Collection<Integer> ids = criteria.getIds();
      if (ids.size() > 1) {
         joinQuery = writer.writeIdJoin(ids);
         writer.write(artAlias);
         writer.write(".art_id = ");
         writer.write(jIdAlias);
         writer.write(".id AND ");
         writer.write(jIdAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());
      } else {
         writer.write(artAlias);
         writer.write(".art_id = ?");
         writer.addParameter(ids.iterator().next());
      }
      writer.write(" AND ");
      writer.write(artAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias);
      writer.write(".gamma_id AND ");
      writer.writeTxBranchFilter(txsAlias);
   }

   @Override
   public int getPriority() {
      return CriteriaPriority.ARTIFACT_ID.ordinal();
   }
}
