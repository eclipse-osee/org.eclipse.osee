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
package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.orcs.core.ds.RelationRow;
import org.eclipse.osee.orcs.core.ds.RelationRowHandler;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationLoader {

   private final IOseeDatabaseService dbService;
   private final SqlProvider sqlProvider;

   public RelationLoader(SqlProvider sqlProvider, IOseeDatabaseService dbService) {
      this.sqlProvider = sqlProvider;
      this.dbService = dbService;
   }

   public void loadRelationData(RelationRowHandler handler, LoadOptions options, int fetchSize, int queryId) throws OseeCoreException {
      if (options.isHistorical()) {//should this be done by the MasterLoader
         return; // TODO: someday we might have a use for historical relations, but not now
      }
      String sqlQuery = sqlProvider.getSql(OseeSql.LOAD_RELATIONS_NEWER);
      IOseeStatement statement = dbService.getStatement();
      try {
         statement.runPreparedQuery(fetchSize, sqlQuery, queryId);
         while (statement.next()) {
            RelationRow nextRelation = new RelationRow();
            nextRelation.setParentId(statement.getInt("art_id"));
            nextRelation.setRelationId(statement.getInt("rel_link_id"));
            nextRelation.setArtIdA(statement.getInt("a_art_id"));
            nextRelation.setArtIdB(statement.getInt("b_art_id"));
            nextRelation.setBranchId(statement.getInt("branch_id"));
            nextRelation.setRelationTypeId(statement.getInt("rel_link_type_id"));
            nextRelation.setGammaId(statement.getInt("gamma_id"));
            nextRelation.setRationale(statement.getString("rationale"));
            nextRelation.setModType(ModificationType.getMod(statement.getInt("mod_type")));
            handler.onRow(nextRelation);
         }
      } finally {
         statement.close();
      }
   }
}
