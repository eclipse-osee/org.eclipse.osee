/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;

/**
 * @author Donald G. Dunne
 */
@Path("/ui/action")
public interface AtsActionUiEndpointApi {

   @GET
   @Produces(MediaType.TEXT_HTML)
   String get();

   /**
    * @param id (artId, atsId) of action to display
    * @return html representation of the action
    */
   @Path("{ids}")
   @GET
   @Produces(MediaType.TEXT_HTML)
   ViewModel getAction(String ids) throws Exception;

   /**
    * @param id (id, atsId) of action to display
    * @return html representation of the action
    */
   @Path("{id}/details")
   @GET
   @Produces(MediaType.TEXT_HTML)
   ViewModel getActionWithDetails(String id) throws Exception;

   /**
    * @return html5 action entry page
    */
   @Path("NewAction")
   @GET
   @Produces(MediaType.TEXT_HTML)
   ViewModel getNewSource() throws Exception;

   /**
    * @return html5 action entry page
    */
   @Path("Search")
   @GET
   @Produces(MediaType.TEXT_HTML)
   ViewModel getSearch() throws Exception;

   /**
    * @param id (id, atsId) of action to display
    * @return html representation w/ transition ui
    */
   @Path("{id}/Transition")
   @GET
   @Produces(MediaType.TEXT_HTML)
   ViewModel getTransition(String id) throws Exception;

}