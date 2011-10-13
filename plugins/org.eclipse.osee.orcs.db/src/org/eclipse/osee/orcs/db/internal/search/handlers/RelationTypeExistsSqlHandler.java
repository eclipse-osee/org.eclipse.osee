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
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.db.internal.search.SqlConstants.CriteriaPriority;
import org.eclipse.osee.orcs.db.internal.search.SqlConstants.TableEnum;
import org.eclipse.osee.orcs.db.internal.search.SqlHandler;
import org.eclipse.osee.orcs.db.internal.search.SqlWriter;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeExistsSqlHandler extends SqlHandler {

   private CriteriaRelationTypeExists criteria;

   private String relAlias;
   private String txsAlias;

   @Override
   public void setData(Criteria criteria) {
      this.criteria = (CriteriaRelationTypeExists) criteria;
   }

   @Override
   public void addTables(SqlWriter writer) throws OseeCoreException {
      relAlias = writer.writeTable(TableEnum.RELATION_TABLE);
      txsAlias = writer.writeTable(TableEnum.TXS_TABLE);
   }

   @Override
   public void addPredicates(SqlWriter writer) throws OseeCoreException {
      IRelationTypeSide typeSide = criteria.getType();
      writer.write(relAlias);
      writer.write(".rel_link_type_id = ?");
      writer.addParameter(toLocalId(typeSide));

      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (!aliases.isEmpty()) {
         writer.write("\n AND \n");
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
               writer.write("\n AND \n");
            }
         }
      }
      writer.write("\n AND \n");
      writer.write(relAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias);
      writer.write(".gamma_id AND ");
      writer.writeTxBranchFilter(txsAlias);
   }

   @Override
   public int getPriority() {
      return CriteriaPriority.RELATION_TYPE_EXISTS.ordinal();
   }
}
