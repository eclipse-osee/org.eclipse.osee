/*******************************************************************************
 * Copyright (c) 2014 Boeing.
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
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeNotExists;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author John Misinco
 */
public class RelationTypeNotExistsSqlHandler extends AbstractRelationSqlHandler<CriteriaRelationTypeNotExists> {

   @Override
   public boolean addPredicates(AbstractSqlWriter writer) {
      super.addPredicates(writer);

      IRelationType type = criteria.getType();

      writer.write("NOT EXISTS (SELECT 1 FROM ");
      writer.write(TableEnum.RELATION_TABLE.getName());
      writer.write(" rel, ");
      writer.write(TableEnum.TXS_TABLE.getName());
      writer.write(" txs WHERE rel.rel_link_type_id = ?");
      writer.addParameter(type.getId());

      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      writer.writeAndLn();
      int aSize = aliases.size();
      for (int index = 0; index < aSize; index++) {
         String artAlias = aliases.get(index);

         writer.write("(rel.a_art_id = ");
         writer.write(artAlias);
         writer.write(".art_id");

         writer.write(" OR ");

         writer.write("rel.b_art_id = ");
         writer.write(artAlias);
         writer.write(".art_id)");

         if (index + 1 < aSize) {
            writer.writeAndLn();
         }
      }
      writer.writeAndLn();
      writer.write("rel.gamma_id = txs.gamma_id");
      writer.writeAndLn();
      writer.write(writer.getTxBranchFilter("txs"));
      writer.write(")");
      return true;
   }

}
