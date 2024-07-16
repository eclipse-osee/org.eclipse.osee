/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.rest.internal.workitem;

import java.util.Collections;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
@Path("action/state")
public final class StateResource {

   private final AtsApi atsApi;
   private static final String ATS_UI_ACTION_PREFIX = "/ui/action/ID";

   public StateResource(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   /**
    * @param form containing information to transition action
    * @param form.id (id or atsId) of action to transition
    * @param form.operation - transition
    * @param form.toState - name of state to transition to
    * @param form.reason - reason if this transition is a cancel
    * @param form.asUserId - userId of user performing transition
    * @return html representation of action after transition
    */
   @POST
   @Consumes("application/x-www-form-urlencoded")
   public Object addOrUpdateAction(MultivaluedMap<String, String> form, @Context UriInfo uriInfo) throws Exception {

      String id = form.getFirst("id");
      if (!Strings.isValid(id)) {
         id = form.getFirst("atsId");
      }
      if (!Strings.isValid(id)) {
         return RestUtil.returnBadRequest("id is not valid");
      }

      String operation = form.getFirst("operation");
      if (!Strings.isValid(operation)) {
         return RestUtil.returnBadRequest("operation is not valid");
      }
      String toState = form.getFirst("toState");
      if (!Strings.isValid(toState)) {
         return RestUtil.returnBadRequest("toState is not valid");
      }
      String reason = form.getFirst("reason");
      String asUserId = form.getFirst("asUserId");
      if (!Strings.isValid(asUserId)) {
         return RestUtil.returnBadRequest("asUserId is not valid");
      }
      AtsUser transitionUser = atsApi.getUserService().getUserByUserId(asUserId);
      if (transitionUser == null) {
         return RestUtil.returnBadRequest(String.format("User by id [%s] does not exist", asUserId));
      }

      if (operation.equals("transition")) {
         ArtifactToken action = null;
         try {
            action = atsApi.getQueryService().getArtifactById(id);
         } catch (Exception ex) {
            // do nothing
         }
         if (action == null) {
            return RestUtil.returnBadRequest(String.format("Action by id [%s] does not exist", id));
         }
         IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(action);

         IAtsChangeSet changes =
            atsApi.getStoreService().createAtsChangeSet("Transition Action - Server", transitionUser);
         TransitionData transData = new TransitionData("Transition " + id, Collections.singleton(workItem), toState,
            workItem.getAssignees(), reason, changes, TransitionOption.None);
         transData.setTransitionUser(transitionUser);
         TransitionManager mgr = new TransitionManager(transData);
         TransitionResults results = mgr.handleAll();
         if (!results.isEmpty()) {
            return RestUtil.returnInternalServerError("Transition Failed: " + results.toString());
         }
         if (!changes.isEmpty()) {
            changes.execute();
         }

         return RestUtil.redirect(workItem, ATS_UI_ACTION_PREFIX, atsApi);
      } else {
         return RestUtil.returnBadRequest(String.format("Unhandled operation [%s]", operation));
      }

   }
}
