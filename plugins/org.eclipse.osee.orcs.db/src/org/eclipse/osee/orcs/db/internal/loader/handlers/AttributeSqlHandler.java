/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.loader.handlers;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaAttribute;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class AttributeSqlHandler extends SqlHandler<CriteriaAttribute> {

   private CriteriaAttribute criteria;
   private String jIdAlias;
   private String jTypeIdAlias;
   private String jArtAlias;
   private String attrAlias;
   private String txsAlias;

   private AbstractJoinQuery joinIdQuery;
   private AbstractJoinQuery joinTypeQuery;

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_LOADER.ordinal();
   }

   @Override
   public void setData(CriteriaAttribute criteria) {
      this.criteria = criteria;
   }

   @Override
   public void writeSelectFields(AbstractSqlWriter writer) {
      writer.writeCommaIfNotFirst();
      writer.write("%s.attr_id, %s.attr_type_id, %s.value, %s.uri", attrAlias, attrAlias, attrAlias, attrAlias);
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      jArtAlias = writer.addTable(OseeDb.OSEE_JOIN_ID4_TABLE);

      if (criteria.getIds().size() > 1) {
         jIdAlias = writer.addTable(OseeDb.OSEE_JOIN_ID_TABLE);
      }

      if (criteria.getTypes().size() > 1) {
         jTypeIdAlias = writer.addTable(OseeDb.OSEE_JOIN_ID_TABLE);
      }

      attrAlias = writer.addTable(OseeDb.ATTRIBUTE_TABLE);
      txsAlias = writer.addTable(OseeDb.TXS_TABLE, ObjectType.ATTRIBUTE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.write(attrAlias);
      writer.write(".art_id = ");
      writer.write(jArtAlias);
      writer.write(".id2");
      writer.writeAnd();
      writer.write(jArtAlias);
      writer.write(".query_id = ?");
      writer.addParameter(criteria.getQueryId());

      Collection<AttributeId> ids = criteria.getIds();
      if (!ids.isEmpty()) {
         writer.writeAnd();
         if (ids.size() > 1) {
            joinIdQuery = writer.writeJoin(ids);
            writer.write(attrAlias);
            writer.write(".attr_id = ");
            writer.write(jIdAlias);
            writer.write(".id");
            writer.writeAnd();
            writer.write(jIdAlias);
            writer.write(".query_id = ?");
            writer.addParameter(joinIdQuery.getQueryId());
         } else {
            writer.write(attrAlias);
            writer.write(".attr_id = ?");
            writer.addParameter(ids.iterator().next());
         }
      }

      Collection<? extends AttributeTypeId> types = criteria.getTypes();
      if (!types.isEmpty()) {
         writer.writeAnd();
         if (types.size() > 1) {
            joinTypeQuery = writer.writeJoin(types);
            writer.write(attrAlias);
            writer.write(".attr_type_id = ");
            writer.write(jTypeIdAlias);
            writer.write(".id");
            writer.writeAnd();
            writer.write(jTypeIdAlias);
            writer.write(".query_id = ?");
            writer.addParameter(joinTypeQuery.getQueryId());
         } else {
            writer.write(attrAlias);
            writer.write(".attr_type_id = ?");
            writer.addParameter(types.iterator().next());
         }
      }

      writer.writeAnd();
      writer.write(attrAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias);
      writer.write(".gamma_id");
      writer.writeAndLn();
      writer.writeTxBranchFilter(txsAlias);
   }
}