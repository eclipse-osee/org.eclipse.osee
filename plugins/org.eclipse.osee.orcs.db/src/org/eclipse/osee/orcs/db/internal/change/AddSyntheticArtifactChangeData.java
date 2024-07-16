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

package org.eclipse.osee.orcs.db.internal.change;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeIgnoreType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.jdbc.JdbcClient;

public class AddSyntheticArtifactChangeData {
   private final List<ChangeItem> changeItems;
   private final JdbcClient jdbcClient;
   private final BranchId branch;
   private final OrcsTokenService tokenService;
   private static final String ART_TYPE_ID_QUERY =
      "select art.art_id, art.art_type_id from osee_artifact art, osee_txs txs where txs.BRANCH_ID = ? " //
         + "and art.GAMMA_ID = txs.GAMMA_ID and txs.tx_current = 1 and art_id in (%s)";

   public AddSyntheticArtifactChangeData(List<ChangeItem> changeItems, JdbcClient jdbcClient, BranchId branch, OrcsTokenService tokenService) {
      this.changeItems = changeItems;
      this.jdbcClient = jdbcClient;
      this.branch = branch;
      this.tokenService = tokenService;
   }

   public List<ChangeItem> doWork() {
      Map<ArtifactId, ChangeItem> artifactChanges = new HashMap<>();
      for (ChangeItem item : changeItems) {
         if (item.getChangeType().isArtifactChange()) {
            artifactChanges.put(item.getArtId(), item);
         }
      }

      List<ChangeItem> attrItems = new ArrayList<>();
      Map<ArtifactId, ChangeItem> syntheticArtifactChanges = new HashMap<>();

      Set<Long> artIds = new HashSet<>();
      for (ChangeItem item : changeItems) {
         artIds.add(item.getArtId().getId());
         if (item.getChangeType().isAttributeChange()) {
            ChangeItem attributeChange = item;
            ArtifactId artIdToCheck = attributeChange.getArtId();
            ChangeItem artifactChange = artifactChanges.get(artIdToCheck);
            if (artifactChange == null) {
               artifactChange = syntheticArtifactChanges.get(artIdToCheck);
               if (artifactChange == null) {
                  artifactChange = ChangeItemUtil.newArtifactChange(artIdToCheck, ArtifactTypeId.SENTINEL,
                     GammaId.SENTINEL, null, null, TransactionToken.SENTINEL);
                  syntheticArtifactChanges.put(artIdToCheck, artifactChange);
                  artifactChange.setSynthetic(true);
               }
               attrItems.add(attributeChange);
               updateArtifactChangeItem(artifactChange, attributeChange);
            }
         }
      }

      Map<Long, Long> artIdToArtTypeid = getArtIdToArtTypeIdMap(branch, artIds);
      for (ChangeItem change : syntheticArtifactChanges.values()) {
         ArtifactTypeToken artifactTypeToken = ArtifactTypeToken.SENTINEL;
         if (isAllowableChange(change.getIgnoreType())) {
            changeItems.add(change);
            if (change.getChangeType().isArtifactChange()) {
               if (artIdToArtTypeid.containsKey(change.getArtId().getId())) {
                  artifactTypeToken = tokenService.getArtifactType(artIdToArtTypeid.get(change.getArtId().getId()));
               }
               change.setItemTypeId(artifactTypeToken);
            }
         }
      }
      return changeItems;
   }

   private Map<Long, Long> getArtIdToArtTypeIdMap(BranchId branchId, Set<Long> artIds) {
      Map<Long, Long> map = new HashMap<>();
      if (!artIds.isEmpty()) {
         for (Collection<Long> ids : Collections.subDivide(new ArrayList<>(artIds), 999)) {
            String query = String.format(ART_TYPE_ID_QUERY, Collections.toString(",", ids));
            jdbcClient.runQuery(stmt -> map.put(stmt.getLong("art_id"), stmt.getLong("art_type_id")), query,
               branchId.getId());
         }
      }
      return map;
   }

   private void updateArtifactChangeItem(ChangeItem artifact, ChangeItem attribute) {
      try {
         if (attribute.getBaselineVersion().isValid()) {
            ChangeItemUtil.copy(attribute.getBaselineVersion(), artifact.getBaselineVersion());
         }

         if (attribute.getCurrentVersion().isValid()) {
            ChangeItemUtil.copy(attribute.getCurrentVersion(), artifact.getCurrentVersion());
         }

         if (attribute.getDestinationVersion().isValid()) {
            ChangeItemUtil.copy(attribute.getDestinationVersion(), artifact.getDestinationVersion());
         }

         if (attribute.getNetChange().isValid()) {
            ChangeItemUtil.copy(attribute.getNetChange(), artifact.getNetChange());
            artifact.getNetChange().setModType(ModificationType.MODIFIED);
         }
         if (artifact.getIgnoreType().isInvalid() || !isAllowableChange(artifact.getIgnoreType())) {
            ChangeItemUtil.checkAndSetIgnoreCase(artifact);
         }

      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private static boolean isAllowableChange(ChangeIgnoreType ignoreType) {
      return //
      ignoreType.isNone() || //
         ignoreType.isResurrected() || //
         ignoreType.isDeletedOnDestAndNotResurrected() || //
         ignoreType.isDeletedOnDestination();
   }
}
