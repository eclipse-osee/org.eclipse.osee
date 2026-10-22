/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.rest.internal.workitem.workdef;

import java.util.ArrayList;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefEndpointApi;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefArtifactTypeContext;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
@Path("workdef")
public class AtsWorkDefEndpointImpl implements AtsWorkDefEndpointApi {

   private final AtsApi atsApi;

   public AtsWorkDefEndpointImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
   }

   @Override
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<WorkDefinition> get() {
      return atsApi.getWorkDefinitionService().getAllWorkDefinitions();
   }

   @Override
   @GET
   @Path("artifactTypeContexts")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<WorkDefArtifactTypeContext> getArtifactTypeContexts() {
      String query = "SELECT attr.value AS work_def_id, art.art_type_id, COUNT(DISTINCT attr.art_id) AS workflow_count "
         + "FROM osee_attribute attr "
         + "JOIN osee_txs attr_txs ON attr_txs.gamma_id = attr.gamma_id "
         + "JOIN osee_artifact art ON art.art_id = attr.art_id "
         + "JOIN osee_txs art_txs ON art_txs.gamma_id = art.gamma_id "
         + "WHERE attr.attr_type_id = ? AND attr_txs.branch_id = ? AND attr_txs.tx_current = 1 "
         + "AND art_txs.branch_id = ? AND art_txs.tx_current = 1 "
         + "GROUP BY attr.value, art.art_type_id ORDER BY attr.value, art.art_type_id";
      return atsApi.getJdbcService().getClient().fetch(new ArrayList<WorkDefArtifactTypeContext>(), statement -> {
         Collection<WorkDefArtifactTypeContext> contexts = new ArrayList<>();
         while (statement.next()) {
            long artifactTypeId = statement.getLong("art_type_id");
            if (isWorkflowArtifactType(artifactTypeId)) {
               contexts.add(new WorkDefArtifactTypeContext(statement.getLong("work_def_id"), artifactTypeId,
                  statement.getLong("workflow_count")));
            }
         }
         return contexts;
      }, query, org.eclipse.osee.ats.api.data.AtsAttributeTypes.WorkflowDefinitionReference.getId(),
         atsApi.getAtsBranch().getId(), atsApi.getAtsBranch().getId());
   }

   private boolean isWorkflowArtifactType(long artifactTypeId) {
      org.eclipse.osee.framework.core.data.ArtifactTypeToken type = atsApi.tokenService().getArtifactType(artifactTypeId);
      return type.inheritsFrom(AtsArtifactTypes.TeamWorkflow) || type.inheritsFrom(AtsArtifactTypes.Task)
         || type.inheritsFrom(AtsArtifactTypes.Goal) || type.inheritsFrom(AtsArtifactTypes.AbstractReview);
   }

   @Override
   @Path("{id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public WorkDefinition getWorkDef(@PathParam("id") String id) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(Long.valueOf(id));
      return workItem.getWorkDefinition();
   }

   @Override
   @Path("teamdef/{id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public WorkDefinition getWorkDefForTeam(@PathParam("id") String id) {
      IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(ArtifactId.valueOf(id));
      WorkDefinition workDef = atsApi.getWorkDefinitionService().computeWorkDefinitionForTeamWfNotYetCreated(teamDef);
      return workDef;
   }

   @Override
   @GET
   @Path("validate")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData validate() {
      XResultData results = atsApi.getWorkDefinitionService().validateWorkDefinitions();
      return results;
   }

}
