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
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.rest.internal.util.ActionPage;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
@Path("state")
public final class StateResource {

   private final IAtsServer atsServer;
   private IAtsWorkItem workItem;
   private final String id;
   private final Log logger;
   private static final String ATS_UI_ACTION_PREFIX = "/ui/action/UUID";

   public StateResource(IAtsServer atsServer, String id, IResourceRegistry registry, Log logger) {
      this.atsServer = atsServer;
      this.id = id;
      this.logger = logger;
   }

   /**
    * @param id
    * @return html representation w/ transition ui
    */
   @Path("trans")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getTransition() throws Exception {
      ArtifactReadable action = atsServer.getArtifactById(id);
      if (action == null) {
         throw new OseeStateException("Invalid id [%s]", id);
      }
      ActionPage page = new ActionPage(logger, atsServer, action, "Action - " + id, false);
      page.setAddTransition(true);
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
      IAtsUser transitionUser = atsServer.getUserService().getUserById(asUserId);
      if (transitionUser == null) {
         return RestUtil.simplePageResponse(String.format("User by id [%s] does not exist", asUserId));
      }

      if (operation.equals("transition")) {
         ArtifactReadable action = atsServer.getArtifactByGuid(guid);
         workItem = atsServer.getWorkItemFactory().getWorkItem(action);

         IAtsChangeSet changes =
            atsServer.getStoreFactory().createAtsChangeSet("Transition Action - Server", transitionUser);
         TransitionHelper helper =
            new TransitionHelper("Transition " + guid, Collections.singleton(workItem), toState,
               workItem.getAssignees(), reason, changes, atsServer.getServices(), TransitionOption.None);
         helper.setTransitionUser(transitionUser);
         IAtsTransitionManager mgr = TransitionFactory.getTransitionManager(helper);
         TransitionResults results = mgr.handleAll();
         if (!results.isEmpty()) {
            throw new OseeArgumentException(results.toString());
         }
         if (!changes.isEmpty()) {
            changes.execute();
         }

         return RestUtil.redirect(workItem, ATS_UI_ACTION_PREFIX, atsServer);

      } else {
         throw new OseeCoreException("Unhandled operation [%s]", operation);
      }

   }
}
