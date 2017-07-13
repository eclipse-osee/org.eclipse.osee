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

import static org.eclipse.osee.framework.core.enums.LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;
import static org.eclipse.osee.framework.core.enums.LoadLevel.ARTIFACT_DATA;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Ryan Schmitt
 */
class RelationLoader {

   public static void loadRelationData(int joinQueryId, Collection<Artifact> artifacts, boolean historical, LoadLevel loadLevel) throws OseeCoreException {
      if (loadLevel == ARTIFACT_DATA || loadLevel == ARTIFACT_AND_ATTRIBUTE_DATA) {
         return;
      }

      if (historical) {
         return; // TODO: someday we might have a use for historical relations, but not now
      }

      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         String sqlQuery = ServiceUtil.getSql(OseeSql.LOAD_RELATIONS);
         chStmt.runPreparedQuery(artifacts.size() * 8, sqlQuery, joinQueryId);
         while (chStmt.next()) {
            int relationId = chStmt.getInt("rel_link_id");
            BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
            ArtifactToken aArtifactId = ArtifactToken.valueOf(chStmt.getLong("a_art_id"), branch);
            ArtifactToken bArtifactId = ArtifactToken.valueOf(chStmt.getLong("b_art_id"), branch);
            RelationType relationType = RelationTypeManager.getTypeByGuid(chStmt.getLong("rel_link_type_id"));

            int gammaId = chStmt.getInt("gamma_id");
            String rationale = chStmt.getString("rationale");
            ModificationType modificationType = ModificationType.getMod(chStmt.getInt("mod_type"));
            ApplicabilityId applicabilityId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));

            RelationManager.getOrCreate(aArtifactId, bArtifactId, relationType, relationId, gammaId, rationale,
               modificationType, applicabilityId);
         }
      } finally {
         chStmt.close();
      }
      for (Artifact artifact : artifacts) {
         artifact.setLinksLoaded(true);
      }
   }
}
