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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.db.internal.search.SqlConstants.CriteriaPriority;
import org.eclipse.osee.orcs.db.internal.search.SqlConstants.TableEnum;
import org.eclipse.osee.orcs.db.internal.search.SqlHandler;
import org.eclipse.osee.orcs.db.internal.search.SqlWriter;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeExistsSqlHandler extends SqlHandler {

   private CriteriaAttributeTypeExists criteria;

   private String attrAlias;
   private String txsAlias;

   private String jIdAlias;
   private AbstractJoinQuery joinQuery;

   @Override
   public void setData(Criteria criteria) {
      this.criteria = (CriteriaAttributeTypeExists) criteria;
   }

   @Override
   public void addTables(SqlWriter writer) throws OseeCoreException {
      if (criteria.getTypes().size() > 1) {
         jIdAlias = writer.writeTable(TableEnum.ID_JOIN_TABLE);
      }
      attrAlias = writer.writeTable(TableEnum.ATTRIBUTE_TABLE);
      txsAlias = writer.writeTable(TableEnum.TXS_TABLE);
   }

   @Override
   public void addPredicates(SqlWriter writer) throws OseeCoreException {
      Collection<? extends IAttributeType> types = criteria.getTypes();
      if (types.size() > 1) {
         Set<Integer> typeIds = new HashSet<Integer>();
         for (IAttributeType type : types) {
            typeIds.add(toLocalId(type));
         }
         joinQuery = writer.writeIdJoin(typeIds);

         writer.write(attrAlias);
         writer.write(".attr_type_id = ");
         writer.write(jIdAlias);
         writer.write(".id AND ");
         writer.write(jIdAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());

      } else {
         IAttributeType type = types.iterator().next();
         int localId = toLocalId(type);
         writer.write(attrAlias);
         writer.write(".attr_type_id = ?");
         writer.addParameter(localId);
      }

      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (!aliases.isEmpty()) {
         writer.write("\n AND \n");
         int aSize = aliases.size();
         for (int index = 0; index < aSize; index++) {
            String artAlias = aliases.get(index);
            writer.write(attrAlias);
            writer.write(".art_id = ");
            writer.write(artAlias);
            writer.write(".art_id");

            if (index + 1 < aSize) {
               writer.write(" AND ");
            }
         }
      }
      writer.write("\n AND \n");
      writer.write(attrAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias);
      writer.write(".gamma_id AND ");
      writer.writeTxBranchFilter(txsAlias);
   }

   @Override
   public int getPriority() {
      return CriteriaPriority.ATTRIBUTE_TYPE_EXISTS.ordinal();
   }
}
