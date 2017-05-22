/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.query;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsQueryService implements IAtsQueryService {

   protected final JdbcService jdbcService;
   private final IAtsServices services;

   public AbstractAtsQueryService(JdbcService jdbcService, IAtsServices services) {
      this.jdbcService = jdbcService;
      this.services = services;
   }

   @Override
   public Collection<IAtsWorkItem> getWorkItemsFromQuery(String query, Object... data) {
      List<IAtsWorkItem> workItems = new LinkedList<>();
      for (ArtifactId art : getArtifactsFromQuery(query, data)) {
         if (services.getStoreService().isOfType(art, AtsArtifactTypes.AbstractWorkflowArtifact)) {
            IAtsWorkItem workItem = services.getWorkItemFactory().getWorkItem(art);
            if (workItem != null) {
               workItems.add(workItem);
            }
         }
      }
      return workItems;
   }

   @Override
   public Collection<ArtifactId> getArtifactsFromQuery(String query, Object... data) {
      JdbcStatement chStmt = jdbcService.getClient().getStatement();
      List<Integer> ids = new LinkedList<Integer>();
      try {
         chStmt.runPreparedQuery(query, data);
         while (chStmt.next()) {
            ids.add(chStmt.getInt("art_id"));
         }
      } finally {
         chStmt.close();
      }
      return services.getQueryService().getArtifacts(ids, services.getAtsBranch());
   }

   @Override
   public void runUpdate(String query, Object... data) {
      jdbcService.getClient().runPreparedUpdate(query, data);
   }

}
