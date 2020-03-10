/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.ResultRow;
import org.eclipse.osee.framework.jdk.core.result.ResultRows;

/**
 * @author Donald G. Dunne
 */
public class AtsWorldResultRowOperation {

   private final AtsApi atsApi;
   private final AtsSearchData atsSearchData;
   private boolean teamWfsInState;

   public AtsWorldResultRowOperation(AtsApi atsApi, AtsSearchData atsSearchData) {
      this.atsApi = atsApi;
      this.atsSearchData = atsSearchData;
      AtsDemoOseeTypes.Action.getName();
   }

   public ResultRows run() {
      ResultRows rows = new ResultRows();
      if (atsSearchData.getCustomizeData() == null || atsSearchData.getCustomizeData().getColumnData().getColumns().isEmpty()) {
         rows.getRd().error("CustomizeData can not be null or empty.");
      }

      Collection<ArtifactToken> artifacts = getArtifacts();
      List<XViewerColumn> showCols = new ArrayList<>();
      for (XViewerColumn col : atsSearchData.getCustomizeData().getColumnData().getColumns()) {
         if (col.isShow()) {
            showCols.add(col);
         }
      }
      for (ArtifactToken art : artifacts) {
         ResultRow row = new ResultRow(art.getId(), atsApi.getAtsBranch().getId());
         rows.add(row);
         for (XViewerColumn col : showCols) {
            if (col.isShow()) {
               addCellData(atsApi, art, row, col);
            }
         }
      }
      return rows;
   }

   private Collection<ArtifactToken> getArtifacts() {
      if (teamWfsInState) {
         StringBuilder sb = new StringBuilder("\'");
         for (Long teamDefId : atsSearchData.getTeamDefIds()) {
            sb.append(teamDefId.toString());
            sb.append("','");
         }
         String teamIds = sb.toString().replaceFirst(",'$", "");
         sb = new StringBuilder("\'");
         for (StateType type : atsSearchData.getStateTypes()) {
            sb.append(type.toString());
            sb.append("','");
         }
         String stateType = sb.toString().replaceFirst(",'$", "");
         String query = String.format(getQuery(), teamIds, stateType);
         List<ArtifactId> artIds = atsApi.getQueryService().getArtifactIdsFromQuery(query);
         return atsApi.getQueryService().getArtifacts(artIds, atsApi.getAtsBranch());
      } else {
         return atsApi.getQueryService().getArtifacts(atsSearchData, null);
      }
   }

   private void addCellData(AtsApi atsApi, ArtifactToken art, ResultRow row, XViewerColumn xCol) {
      String value = "";
      if (art.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(art);
         value = atsApi.getColumnService().getColumnText(xCol.getId(), workItem);
      }
      row.addValue(value);
   }

   public void setNew(boolean teamWfsInState) {
      this.teamWfsInState = teamWfsInState;
   }

   public String getQuery() {
      return "SELECT distinct art.art_id as art_id FROM osee_artifact art, osee_txs txs, OSEE_ATTRIBUTE attr \n" + //
         "WHERE attr.gamma_id = txs.gamma_id AND txs.tx_current = 1 AND txs.branch_id = 570 and \n" + //
         "attr.ART_ID = art.ART_ID and attr.ATTR_TYPE_ID = 4730961339090285773 and attr.VALUE \n IN (%s) \n" + //
         "AND art.art_id IN (SELECT distinct art.art_id AS art_id FROM osee_artifact art, osee_txs txs, OSEE_ATTRIBUTE attr \n" + //
         "WHERE attr.gamma_id = txs.gamma_id AND txs.tx_current = 1 AND txs.branch_id = 570 and \n" + //
         "attr.ART_ID = art.ART_ID and attr.ATTR_TYPE_ID = 1152921504606847147 and attr.VALUE in (%s))"; //
   }
}
