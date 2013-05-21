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
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactHrids;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactHridsSqlHandler extends SqlHandler<CriteriaArtifactHrids, QueryOptions> {

   private CriteriaArtifactHrids criteria;

   private String artAlias;
   private String jHridAlias;
   private String txsAlias;
   private AbstractJoinQuery joinQuery;

   @Override
   public void setData(CriteriaArtifactHrids criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter<QueryOptions> writer) {
      if (criteria.getIds().size() > 1) {
         jHridAlias = writer.addTable(TableEnum.CHAR_JOIN_TABLE);
      }
      artAlias = writer.addTable(TableEnum.ARTIFACT_TABLE);
      txsAlias = writer.addTable(TableEnum.TXS_TABLE);
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter<QueryOptions> writer) throws OseeCoreException {
      Collection<String> ids = criteria.getIds();
      if (ids.size() > 1) {
         joinQuery = writer.writeCharJoin(ids);
         writer.write(artAlias);
         writer.write(".human_readable_id = ");
         writer.write(jHridAlias);
         writer.write(".id AND ");
         writer.write(jHridAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());
      } else {
         writer.write(artAlias);
         writer.write(".human_readable_id = ?");
         writer.addParameter(ids.iterator().next());
      }
      writer.write(" AND ");
      writer.write(artAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias);
      writer.write(".gamma_id AND ");
      writer.write(writer.getTxBranchFilter(txsAlias));
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ARTIFACT_HRID.ordinal();
   }
}
