/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.rest.internal.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.query.IAtsQueryServiceServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryServiceServerImpl implements IAtsQueryServiceServer {

   protected final JdbcClient jdbcClient;
   private final AtsApi atsApi;

   public AtsQueryServiceServerImpl(JdbcService jdbcService, AtsApi atsApi) {
      jdbcClient = jdbcService.getClient();
      this.atsApi = atsApi;
   }

   @Override
   public Collection<IAtsWorkItem> getWorkItemsFromQuery(String query, Object... data) {
      List<ArtifactId> ids = new LinkedList<>();
      jdbcClient.runQuery(stmt -> ids.add(ArtifactId.valueOf(stmt.getLong("art_id"))), query, data);
      List<IAtsWorkItem> workItems = new LinkedList<>();
      for (ArtifactToken art : atsApi.getQueryService().getArtifacts(ids, atsApi.getAtsBranch())) {
         if (art.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
            IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(art);
            if (workItem != null) {
               workItems.add(workItem);
            }
         }
      }
      return workItems;
   }

   @Override
   public List<Map<String, String>> query(String query, Object... data) {
      List<Map<String, String>> rows = new ArrayList<>(10);

      JdbcStatement stmt = jdbcClient.getStatement();
      try {
         stmt.runPreparedQuery(query, data);

         while (stmt.next()) {
            Map<String, String> rowMap = new HashMap<String, String>();
            for (int x = 1; x <= stmt.getColumnCount(); x++) {
               /**
                * Force to upercase cause postgres will return colName in lowercase, where oracle in uppercase
                */
               String colName = stmt.getColumnName(x).toUpperCase();
               String val = stmt.getString(colName);
               rowMap.put(colName, val);
            }
            rows.add(rowMap);
         }
      } finally {
         stmt.close();
      }
      return rows;
   }

   @Override
   public void runUpdate(String query, Object... data) {
      jdbcClient.runPreparedUpdate(query, data);
   }

   @Override
   public Collection<ArtifactToken> getArtifactsFromQuery(String query, Object... data) {
      List<ArtifactId> ids = new LinkedList<>();
      jdbcClient.runQuery(stmt -> ids.add(ArtifactId.valueOf(stmt.getLong("art_id"))), query, data);
      return atsApi.getQueryService().getArtifacts(ids, atsApi.getAtsBranch());
   }

   @Override
   public List<ArtifactId> getArtifactIdsFromQuery(String query, Object... data) {
      List<ArtifactId> ids = new LinkedList<>();
      jdbcClient.runQuery(stmt -> ids.add(ArtifactId.valueOf(stmt.getLong("art_id"))), query, data);
      return ids;
   }

}
