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
package org.eclipse.osee.orcs.db.internal.search.handlers;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class RelatedToSqlHandler extends SqlHandler<CriteriaRelatedTo, QueryOptions> {

   private CriteriaRelatedTo criteria;

   private String jIdAlias;
   private String relAlias;
   private String txsAlias;

   @Override
   public void setData(CriteriaRelatedTo criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter<QueryOptions> writer) {
      if (criteria.getIds().size() > 1) {
         jIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }

      List<String> artAliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (artAliases.isEmpty()) {
         writer.addTable(TableEnum.ARTIFACT_TABLE);
      }
      relAlias = writer.addTable(TableEnum.RELATION_TABLE);
      txsAlias = writer.addTable(TableEnum.TXS_TABLE);
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter<QueryOptions> writer) throws OseeCoreException {
      IRelationTypeSide typeSide = criteria.getType();
      writer.write(relAlias);
      writer.write(".rel_link_type_id = ?");
      writer.addParameter(toLocalId(typeSide));

      Collection<Integer> ids = criteria.getIds();
      if (!ids.isEmpty()) {
         writer.write(" AND ");
         String aOrbArtId = typeSide.getSide().isSideA() ? ".a_art_id" : ".b_art_id";
         if (ids.size() > 1) {
            AbstractJoinQuery joinQuery = writer.writeIdJoin(ids);
            writer.write(relAlias);
            writer.write(aOrbArtId);
            writer.write(" = ");
            writer.write(jIdAlias);
            writer.write(".id AND ");
            writer.write(jIdAlias);
            writer.write(".query_id = ?");
            writer.addParameter(joinQuery.getQueryId());
         } else {
            writer.write(relAlias);
            writer.write(aOrbArtId);
            writer.write(" = ?");
            writer.addParameter(ids.iterator().next());
         }
      }

      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (!aliases.isEmpty()) {
         writer.writeAndLn();
         String oppositeAOrBartId = typeSide.getSide().isSideA() ? ".b_art_id" : ".a_art_id";
         int aSize = aliases.size();
         for (int index = 0; index < aSize; index++) {
            String artAlias = aliases.get(index);

            writer.write(relAlias);
            writer.write(oppositeAOrBartId);
            writer.write(" = ");
            writer.write(artAlias);
            writer.write(".art_id");

            if (index + 1 < aSize) {
               writer.writeAndLn();
            }
         }
      }
      writer.writeAndLn();
      writer.write(relAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias);
      writer.write(".gamma_id AND ");
      writer.write(writer.getTxBranchFilter(txsAlias));
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.RELATED_TO_ART_IDS.ordinal();
   }
}
