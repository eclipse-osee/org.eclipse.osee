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
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.ObjectType;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeSqlHandler extends SqlHandler<CriteriaArtifactType> {

   private CriteriaArtifactType criteria;

   private String jIdAlias;
   private String artAlias;
   private String txsAlias;
   private AbstractJoinQuery joinQuery;

   private List<String> artAliases;
   private List<String> txsAliases;

   private boolean artAliasCreated;
   private boolean txsAliasCreated;

   @Override
   public void setData(CriteriaArtifactType criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      Collection<? extends ArtifactTypeId> types = criteria.getTypes();
      if (types.size() > 1) {
         jIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }

      artAliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      txsAliases = writer.getAliases(TableEnum.TXS_TABLE);

      if (artAliases.isEmpty()) {
         artAlias = writer.addTable(TableEnum.ARTIFACT_TABLE);
         artAliasCreated = true;
      }
      if (txsAliases.isEmpty()) {
         txsAlias = writer.addTable(TableEnum.TXS_TABLE, ObjectType.ARTIFACT);
         txsAliasCreated = true;
      }
      artAliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      txsAliases = writer.getAliases(TableEnum.TXS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      Collection<? extends ArtifactTypeId> types = criteria.getTypes();
      if (types.size() > 1) {
         joinQuery = writer.writeJoin(types);
         writer.write(jIdAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());

         if (!artAliases.isEmpty()) {
            writer.write(" AND ");
            int aSize = artAliases.size();
            for (int index = 0; index < aSize; index++) {
               String artAlias = artAliases.get(index);
               writer.write(artAlias);
               writer.write(".art_type_id = ");
               writer.write(jIdAlias);
               writer.write(".id");
               if (index + 1 < aSize) {
                  writer.write(" AND ");
               }
            }
         }
      } else {
         ArtifactTypeId artifactType = types.iterator().next();

         int aSize = artAliases.size();
         for (int index = 0; index < aSize; index++) {
            String artAlias = artAliases.get(index);
            writer.write(artAlias);
            writer.write(".art_type_id = ?");
            writer.addParameter(artifactType);
            if (index + 1 < aSize) {
               writer.write(" AND ");
            }
         }
      }

      if (artAliasCreated && txsAliasCreated) {
         writer.write(" AND ");
         writer.write(artAlias);
         writer.write(".gamma_id = ");
         writer.write(txsAlias);
         writer.write(".gamma_id AND ");
         writer.write(writer.getTxBranchFilter(txsAlias));
      }
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ARTIFACT_TYPE.ordinal();
   }
}
