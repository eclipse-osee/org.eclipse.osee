/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.CreateNewActionField;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
@Path("ai")
public class ActionableItemResource extends AbstractConfigResource {
   public ActionableItemResource(AtsApi atsApi, OrcsApi orcsApi) {
      super(AtsArtifactTypes.ActionableItem, atsApi, orcsApi);
   }

   @GET
   @Path("all")
   @Produces({MediaType.APPLICATION_JSON})
   public List<ArtifactToken> get(@QueryParam("workType") String workType,
      @QueryParam("orderByName") boolean orderByName) {
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(AtsArtifactTypes.ActionableItem);
      if (Strings.isValid(workType)) {
         query = query.andAttributeIs(AtsAttributeTypes.WorkType, workType);
      }
      if (orderByName) {
         query = query.setOrderByAttribute(CoreAttributeTypes.Name);
      }
      return query.asArtifacts().stream().map(a -> ArtifactToken.valueOf(a.getId(), a.getName())).collect(
         Collectors.toList());

   }

   @GET
   @Path("{id}/additionalFields")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<CreateNewActionField> getAdditionalCreateActionFields(
      @PathParam("id") ArtifactId actionableItemId) {
      IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItemById(actionableItemId);
      if (ai == null || ai.isInvalid()) {
         return Collections.emptyList();
      }
      return atsApi.getActionService().getCreateActionFields(Arrays.asList(ai));
   }

   @GET
   @Path("{id}/teamdef")
   @Produces(MediaType.APPLICATION_JSON)
   public IAtsTeamDefinition getTeamDefinition(@PathParam("id") ArtifactId actionableItemId) {
      IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItemById(actionableItemId);
      if (ai == null || ai.isInvalid()) {
         return IAtsTeamDefinition.SENTINEL;
      }
      return ai.getTeamDefinition();
   }

}
