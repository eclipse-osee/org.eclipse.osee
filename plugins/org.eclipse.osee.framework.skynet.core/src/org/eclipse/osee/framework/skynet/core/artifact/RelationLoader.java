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
package org.eclipse.osee.framework.skynet.core.artifact;

import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.ATTRIBUTE;
import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.SHALLOW;
import java.util.Collection;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Ryan Schmitt
 */
class RelationLoader {
   static void loadRelationData(int queryId, Collection<Artifact> artifacts, boolean historical, ArtifactLoad loadLevel) throws OseeCoreException {
      if (loadLevel == SHALLOW || loadLevel == ATTRIBUTE) {
         return;
      }

      if (historical) {
         return; // TODO: someday we might have a use for historical relations, but not now
      }
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(artifacts.size() * 8, ClientSessionManager.getSql(OseeSql.LOAD_RELATIONS), queryId);
         while (chStmt.next()) {
            int relationId = chStmt.getInt("rel_link_id");
            int aArtifactId = chStmt.getInt("a_art_id");
            int bArtifactId = chStmt.getInt("b_art_id");
            Branch aBranch = BranchManager.getBranch(chStmt.getInt("branch_id"));
            Branch bBranch = aBranch; // TODO these branch ids need to come from the relation link table
            RelationType relationType = RelationTypeManager.getType(chStmt.getInt("rel_link_type_id"));

            int gammaId = chStmt.getInt("gamma_id");
            String rationale = chStmt.getString("rationale");

            RelationLink.getOrCreate(aArtifactId, bArtifactId, aBranch, bBranch, relationType, relationId, gammaId,
                  rationale, ModificationType.getMod(chStmt.getInt("mod_type")));
         }
      } finally {
         chStmt.close();
      }
      for (Artifact artifact : artifacts) {
         artifact.setLinksLoaded(true);
      }
   }
}
