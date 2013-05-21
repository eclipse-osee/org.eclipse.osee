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

import java.util.List;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeExistsSqlHandler extends SqlHandler<CriteriaRelationTypeExists, QueryOptions> {

   private CriteriaRelationTypeExists criteria;

   private String relAlias;
   private String txsAlias;

   @Override
   public void setData(CriteriaRelationTypeExists criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter<QueryOptions> writer) {
      List<String> artAliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (artAliases.isEmpty()) {
         writer.addTable(TableEnum.ARTIFACT_TABLE);
      }
      relAlias = writer.addTable(TableEnum.RELATION_TABLE);
      txsAlias = writer.addTable(TableEnum.TXS_TABLE);
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter<QueryOptions> writer) throws OseeCoreException {
      IRelationType type = criteria.getType();
      writer.write(relAlias);
      writer.write(".rel_link_type_id = ?");
      writer.addParameter(toLocalId(type));

      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (!aliases.isEmpty()) {
         writer.writeAndLn();
         int aSize = aliases.size();
         for (int index = 0; index < aSize; index++) {
            String artAlias = aliases.get(index);

            writer.write("(");
            writer.write(relAlias);
            writer.write(".a_art_id = ");
            writer.write(artAlias);
            writer.write(".art_id");

            writer.write(" OR ");

            writer.write(relAlias);
            writer.write(".b_art_id = ");
            writer.write(artAlias);
            writer.write(".art_id)");

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
      return SqlHandlerPriority.RELATION_TYPE_EXISTS.ordinal();
   }
}
