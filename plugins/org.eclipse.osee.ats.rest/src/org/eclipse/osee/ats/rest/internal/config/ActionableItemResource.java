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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
@Path("ai")
public class ActionableItemResource extends AbstractConfigResource {
   public ActionableItemResource(AtsApi atsApi, OrcsApi orcsApi) {
      super(AtsArtifactTypes.ActionableItem, atsApi, orcsApi);
   }

   @GET
   @Path("worktype/{worktype}")
   @Produces({MediaType.APPLICATION_JSON})
   public List<ArtifactToken> getActionableItemsByWorkType(@PathParam("worktype") String worktype) {
      return orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(
         AtsArtifactTypes.ActionableItem).andAttributeIs(AtsAttributeTypes.WorkType, worktype).asArtifactTokens();
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
