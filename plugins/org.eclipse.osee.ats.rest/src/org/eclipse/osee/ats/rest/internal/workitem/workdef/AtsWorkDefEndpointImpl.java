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

import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefEndpointApi;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.rest.internal.workitem.operations.ConvertWorkDefinitionOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
@Path("workdef")
public class AtsWorkDefEndpointImpl implements AtsWorkDefEndpointApi {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public AtsWorkDefEndpointImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   @Override
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<WorkDefinition> get() {
      return atsApi.getWorkDefinitionService().getAllWorkDefinitions();
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
      WorkDefinition workDef =
         atsApi.getWorkDefinitionService().computeWorkDefinitionForTeamWfNotYetCreated(teamDef);
      return workDef;
   }

   @Override
   @GET
   @Path("convert/artrefattr")
   @Produces(MediaType.APPLICATION_JSON)
   public String convertToWorkDefRefs() {
      ConvertWorkDefinitionOperations ops = new ConvertWorkDefinitionOperations(atsApi, orcsApi);
      XResultData data = new XResultData();
      ops.convert(data);
      return data.toString();
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
