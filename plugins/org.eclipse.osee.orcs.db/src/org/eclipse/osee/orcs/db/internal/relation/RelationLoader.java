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
package org.eclipse.osee.orcs.db.internal.relation;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.orcs.core.ds.RelationRow;
import org.eclipse.osee.orcs.core.ds.RelationRowHandler;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.attribute.LoadOptions;
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

   public void loadRelationData(int fetchSize, RelationRowHandler handler, LoadOptions options, int queryId) throws OseeCoreException {
      if (options.getLoadLevel().isShallow() || options.getLoadLevel().isRelationsOnly()) {
         return;
      }
      if (options.isHistorical()) {
         return; // TODO: someday we might have a use for historical relations, but not now
      }
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         IOseeStatement statement = dbService.getStatement();
         String sqlQuery = sqlProvider.getSql(OseeSql.LOAD_RELATIONS);
         statement.runPreparedQuery(fetchSize, sqlQuery, queryId);
         while (statement.next()) {
            RelationRow nextRelation = new RelationRow();
            nextRelation.setRelationId(statement.getInt("rel_link_id"));
            nextRelation.setArtIdA(statement.getInt("a_art_id"));
            nextRelation.setArtIdB(statement.getInt("b_art_id"));
            nextRelation.setBranchId(statement.getInt("branch_id"));
            nextRelation.setRelationTypeId(statement.getInt("rel_link_type_id"));
            nextRelation.setGammaId(chStmt.getInt("gamma_id"));
            nextRelation.setRationale(chStmt.getString("rationale"));
            nextRelation.setModType(ModificationType.getMod(chStmt.getInt("mod_type")));
            handler.onRow(nextRelation);
            //            RelationManager.getOrCreate(aArtifactId, bArtifactId, branch, relationType, relationId, gammaId, rationale,
            //               modificationType);
         }
      } finally {
         chStmt.close();
      }
      //      for (Artifact artifact : artifacts) {
      //         artifact.setLinksLoaded(true);
      //      }
   }
}
