/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.resources;

import java.util.Collections;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.impl.action.ActionPage;
import org.eclipse.osee.ats.impl.action.ActionUtility;
import org.eclipse.osee.ats.impl.action.ActionUtility.ActionLoadLevel;
import org.eclipse.osee.ats.rest.internal.AtsServerService;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
@Path("state")
public final class StateResource {
   private final IResourceRegistry registry;
   private IAtsWorkItem workItem;
   private final String guid;

   public StateResource(OrcsApi orcsApi, String guid) {
      this.guid = guid;
      registry = orcsApi.getResourceRegistry();
   }

   /**
    * @return html representation of action with states
    */
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getStates() throws Exception {
      ArtifactReadable action = AtsServerService.get().getArtifactByGuid(guid);
      return ActionUtility.displayAction(registry, action, "Action - " + guid, ActionLoadLevel.STATE);
   }

   /**
    * @param guid
    * @return html representation w/ transition ui
    */
   @Path("trans")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getTransition(@PathParam("id") String guid) throws Exception {
      ArtifactReadable action = AtsServerService.get().getArtifactByGuid(guid);
      ActionPage page = new ActionPage(registry, action, "Action - " + guid, ActionLoadLevel.STATE);
      page.addTransitionStates();
      return page.generate();

   }

   /**
    * @param form containing information to transition action
    * @param form.guid of action to transition
    * @param form.operation - transition
    * @param form.toState - name of state to transition to
    * @param form.reason - reason if this transition is a cancel
    * @param form.asUserId - userId of user performing transition
    * @param uriInfo
    * @return html representation of action after transition
    */
   @POST
   @Consumes("application/x-www-form-urlencoded")
   public Response addOrUpdateAction(MultivaluedMap<String, String> form, @Context UriInfo uriInfo) throws Exception {
      String htmlStr;

      // get parameters
      String query = uriInfo.getPath();
      System.out.println("query [" + query + "]");
      String guid = form.getFirst("guid");
      String operation = form.getFirst("operation");
      String toState = form.getFirst("toState");
      Conditions.checkNotNullOrEmpty(toState, "toState");
      String reason = form.getFirst("reason");
      Conditions.checkNotNull(reason, "reason");
      String asUserId = form.getFirst("asUserId");
      Conditions.checkNotNull(asUserId, "asUserId");
      Conditions.checkNotNullOrEmpty(asUserId, "UserId");
      IAtsUser transitionUser = AtsCore.getUserService().getUserById(asUserId);
      if (transitionUser == null) {
         throw new OseeStateException("User by id [%s] does not exist", asUserId);
      }

      if (operation.equals("transition")) {
         ArtifactReadable action = AtsServerService.get().getArtifactByGuid(guid);
         workItem = AtsServerService.get().getWorkItemFactory().getWorkItem(action);

         IAtsChangeSet changes =
            AtsServerService.get().getStoreFactory().createAtsChangeSet("Transition Action - Server", transitionUser);
         TransitionHelper helper =
            new TransitionHelper("Transition " + guid, Collections.singleton(workItem), toState,
               workItem.getAssignees(), reason, changes, TransitionOption.None);
         helper.setTransitionUser(transitionUser);
         TransitionManager mgr = new TransitionManager(helper);
         TransitionResults results = mgr.handleAll();
         if (!results.isEmpty()) {
            throw new OseeArgumentException(results.toString());
         }
         if (!changes.isEmpty()) {
            changes.execute();
         }

         // reload before display
         action = AtsServerService.get().getArtifactByGuid(guid);
         htmlStr =
            ActionUtility.displayAction(registry, action, "Action Transitioned - " + action.getGuid(),
               ActionLoadLevel.HEADER);
      } else {
         throw new OseeCoreException("Unhandled operation [%s]", operation);
      }
      return Response.status(200).entity(htmlStr).build();

   }
}