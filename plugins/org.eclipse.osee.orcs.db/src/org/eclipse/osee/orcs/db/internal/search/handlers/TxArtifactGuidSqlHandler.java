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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxArtifactGuids;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class TxArtifactGuidSqlHandler extends SqlHandler<CriteriaTxArtifactGuids> {

   private CriteriaTxArtifactGuids criteria;

   private String txdAlias;
   private String artAlias;
   private String jguidAlias;
   private AbstractJoinQuery joinQuery;

   @Override
   public void setData(CriteriaTxArtifactGuids criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (criteria.getIds().size() > 1) {
         jguidAlias = writer.addTable(TableEnum.CHAR_JOIN_TABLE);
      }
      List<String> aliases = writer.getAliases(TableEnum.TX_DETAILS_TABLE);
      if (aliases.isEmpty()) {
         txdAlias = writer.addTable(TableEnum.TX_DETAILS_TABLE);
      } else {
         txdAlias = aliases.iterator().next();
      }
      aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (aliases.isEmpty()) {
         artAlias = writer.addTable(TableEnum.ARTIFACT_TABLE);
      } else {
         artAlias = aliases.iterator().next();
      }
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer) throws OseeCoreException {
      Collection<String> ids = criteria.getIds();
      if (ids.size() > 1) {
         joinQuery = writer.writeCharJoin(ids);
         writer.write(artAlias);
         writer.write(".guid = ");
         writer.write(jguidAlias);
         writer.write(".id AND ");
         writer.write(jguidAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());
      } else {
         writer.write(artAlias);
         writer.write(".guid = ?");
         writer.addParameter(ids.iterator().next());
      }
      writer.write(" AND ");
      writer.write(artAlias);
      writer.write(".art_id = ");
      writer.write(txdAlias);
      writer.write(".author");
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ARTIFACT_GUID.ordinal();
   }
}
