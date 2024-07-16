/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.artifact;

import static org.eclipse.osee.framework.core.enums.LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;
import static org.eclipse.osee.framework.core.enums.LoadLevel.ARTIFACT_DATA;
import java.util.Collection;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Ryan Schmitt
 */
class RelationLoader {

   public static void loadRelationData(int joinQueryId, Collection<Artifact> artifacts, boolean historical, LoadLevel loadLevel, OrcsTokenService tokenservice) {
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
            RelationId relationId = RelationId.valueOf(chStmt.getLong("rel_link_id"));
            BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
            ArtifactToken aArtifactId = ArtifactToken.valueOf(chStmt.getLong("a_art_id"), branch);
            ArtifactToken bArtifactId = ArtifactToken.valueOf(chStmt.getLong("b_art_id"), branch);
            RelationTypeToken relationType = tokenservice.getRelationType(chStmt.getLong("rel_link_type_id"));

            GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
            String rationale = chStmt.getString("rationale");
            ModificationType modificationType = ModificationType.valueOf(chStmt.getInt("mod_type"));
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
