/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.query;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class TokenSearchOperations {

   public static HashCollection<ArtifactId, ArtifactToken> getArtifactTokenListFromRelated(BranchId branch, Collection<ArtifactId> artifacts, ArtifactTypeId artifactType, RelationTypeSide relationType, OrcsApi orcsApi, JdbcService jdbcService) {
      List<Long> artIds = new LinkedList<>();
      String ids = "";
      for (ArtifactId art : artifacts) {
         artIds.add(art.getId());
         ids += art.getIdString() + ",";
      }
      ids = ids.replaceFirst(",$", "");

      Map<Long, Long> artBIdToArtAId = new HashMap<>();
      Map<Long, Long> artAIdToArtBId = new HashMap<>();
      JdbcStatement chStmt = jdbcService.getClient().getStatement();
      boolean isSideA = relationType.getSide().isSideA();
      try {
         String query = OseeSql.ARTIFACT_TO_RELATED_B_ARTIFACT_ID.getSql().replaceFirst("ART_IDS_HERE", ids);
         query = query.replaceAll("REL_SIDE_HERE", isSideA ? "b_art_id" : "a_art_id");
         query = query.replaceAll("REL_TYPE_LINKE_ID_HERE", relationType.getIdString());
         query = query.replaceAll("BRANCH_ID_HERE", branch.getIdString());
         chStmt.runPreparedQuery(query);
         while (chStmt.next()) {
            Long aArtId = chStmt.getLong("a_art_id");
            Long bArtId = chStmt.getLong("b_art_id");
            artBIdToArtAId.put(bArtId, aArtId);
            artAIdToArtBId.put(aArtId, bArtId);
         }
      } finally {
         chStmt.close();
      }

      chStmt = jdbcService.getClient().getStatement();
      HashCollection<ArtifactId, ArtifactToken> artToRelatedTokens = new HashCollection<>();
      try {
         String query = OseeSql.ARTIFACT_TOKENS_RELATED_TO_ARTIFACT_QUERY.getSql().replaceFirst("ART_IDS_HERE", ids);
         query = query.replaceAll("OPPOSITE_REL_SIDE_HERE", isSideA ? "a_art_id" : "b_art_id");
         query = query.replaceAll("REL_SIDE_HERE", isSideA ? "b_art_id" : "a_art_id");
         query = query.replaceAll("REL_TYPE_LINKE_ID_HERE", relationType.getIdString());
         query = query.replaceAll("BRANCH_ID_HERE", branch.getIdString());
         chStmt.runPreparedQuery(query);
         while (chStmt.next()) {
            Long artId = chStmt.getLong("art_id");
            ArtifactTypeToken artTypeId = orcsApi.getOrcsTypes().getArtifactTypes().get(chStmt.getLong("art_type_id"));
            String name = chStmt.getString("value");
            ArtifactToken token = ArtifactToken.valueOf(artId, name, branch, artTypeId);
            Long artIdLong = isSideA ? artAIdToArtBId.get(artId) : artBIdToArtAId.get(artId);
            ArtifactId aArtId = ArtifactId.valueOf(artIdLong);
            artToRelatedTokens.put(aArtId, token);
         }
      } finally {
         chStmt.close();
      }
      return artToRelatedTokens;
   }

   private static String ARTIFACTS_MATCHING_ATTRIBUTE_VALUE_QUERY =
      "SELECT branch_id, attr.art_id, attr.VALUE AS name FROM OSEE_ATTRIBUTE attr, " + //
         "OSEE_TXS txs WHERE attr.ART_ID IN (ART_IDS_HERE) " + //
         "AND txs.BRANCH_ID = ? AND txs.GAMMA_ID = attr.GAMMA_ID AND txs.TX_CURRENT = 1 AND txs.MOD_TYPE NOT IN (3,5,9,10) " + //
         "AND attr.ATTR_TYPE_ID = ? AND attr.value = ?";

   /**
    * @return ArtifactTokens in artifacts that have attribute matching value in database. Excludes binary attribute
    * values.
    */
   public static Collection<ArtifactToken> getArtifactTokensMatchingAttrValue(BranchId branch, Collection<ArtifactToken> artifacts, AttributeTypeId attributeType, Object value, OrcsApi orcsApi, JdbcService jdbcService) {
      List<Long> artIds = new LinkedList<>();
      Map<Long, ArtifactToken> artIdToTokenMap = new HashMap<>();
      String ids = "";
      for (ArtifactToken art : artifacts) {
         artIds.add(art.getId());
         ids += art.getIdString() + ",";
         artIdToTokenMap.put(art.getId(), art);
      }
      ids = ids.replaceFirst(",$", "");

      JdbcStatement chStmt = jdbcService.getClient().getStatement();
      Set<ArtifactToken> results = new HashSet<>();
      try {
         String query = ARTIFACTS_MATCHING_ATTRIBUTE_VALUE_QUERY.replaceFirst("ART_IDS_HERE", ids);
         query = query.replaceAll("ATTR_TYPE_ID_HERE", attributeType.getIdString());
         query = query.replaceAll("BRANCH_ID_HERE", branch.getId().toString());
         chStmt.runPreparedQuery(query, branch.getId(), attributeType.getId(), value);
         while (chStmt.next()) {
            Long artId = chStmt.getLong("art_id");
            results.add(artIdToTokenMap.get(artId));
         }
      } finally {
         chStmt.close();
      }
      return results;
   }

}
