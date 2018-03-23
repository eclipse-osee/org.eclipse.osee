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
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaMainTableField;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Ryan D. Brooks
 */
public abstract class MainTableFieldSqlHandler extends SqlHandler<CriteriaMainTableField> {
   private CriteriaMainTableField criteria;
   private String mainAlias;
   private String jIdAlias;
   private final TableEnum table;
   private final String column;
   private final SqlHandlerPriority priority;
   private Collection<? extends Id> values;

   public MainTableFieldSqlHandler(TableEnum table, String column, SqlHandlerPriority priority) {
      this.table = table;
      this.column = column;
      this.priority = priority;
   }

   @Override
   public void setData(CriteriaMainTableField criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      mainAlias = writer.getMainTableAlias(table);
      values = criteria.getValues();
      if (values.size() > 1) {
         jIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      if (values.size() > 1) {
         if (values.contains(null)) {
            throw new OseeArgumentException("cannot specify null id as part of a multiple value search");
         }
         AbstractJoinQuery joinQuery = writer.writeJoin(values);
         writer.writeEquals(mainAlias, column, jIdAlias, "id");
         writer.write(" AND ");
         writer.writeEqualsParameter(jIdAlias, "query_id", joinQuery.getQueryId());
      } else {
         Id value = values.iterator().next();
         if (value == null) {
            writer.write("%s.%s is null", mainAlias, column);
         } else {
            writer.writeEqualsParameter(mainAlias, column, value);
         }
      }
   }

   @Override
   public int getPriority() {
      return priority.ordinal();
   }
}