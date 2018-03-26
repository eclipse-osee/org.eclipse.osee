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
package org.eclipse.osee.orcs.db.internal.search.handlers;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAuthorIds;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public final class TxAuthorIdsSqlHandler extends SqlHandler<CriteriaAuthorIds> {
   private CriteriaAuthorIds criteria;
   private String txdAlias;
   private String jIdAlias;

   @Override
   public void setData(CriteriaAuthorIds criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (criteria.getIds().size() > 1) {
         jIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }
      List<String> aliases = writer.getAliases(TableEnum.TX_DETAILS_TABLE);
      if (aliases.isEmpty()) {
         txdAlias = writer.addTable(TableEnum.TX_DETAILS_TABLE);
      } else {
         txdAlias = aliases.iterator().next();
      }
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      Collection<ArtifactId> ids = criteria.getIds();
      if (ids.size() > 1) {
         AbstractJoinQuery joinQuery = writer.writeJoin(ids);
         writer.writeEquals(txdAlias, "author", jIdAlias, "id");
         writer.write(" AND ");
         writer.writeEqualsParameter(jIdAlias, "query_id", joinQuery.getQueryId());

      } else {
         writer.writeEqualsParameter(txdAlias, "author", ids.iterator().next());
      }
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_ID.ordinal();
   }
}