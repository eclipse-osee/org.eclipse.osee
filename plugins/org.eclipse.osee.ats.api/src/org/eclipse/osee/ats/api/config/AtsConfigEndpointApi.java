/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

/**
 * @author Donald G. Dunne
 */
@Path("config")
public interface AtsConfigEndpointApi {

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public AtsConfigurations get();

   /**
    * @return html5 action entry page
    */
   @Path("ui/NewAtsBranchConfig")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getNewSource();

   /**
    * Create new ATS configuration branch and ATS config object on Common branch
    * 
    * @param form containing information to configure new ATS branch
    * @param form.fromBranchUuid of branch to get config artifacts from
    * @param form.newBranchName of new branch
    * @param form.userId - userId of user performing transition
    * @param uriInfo
    * @return json object with new branchUuid
    */
   @POST
   @Consumes("application/x-www-form-urlencoded")
   @Produces(MediaType.APPLICATION_JSON)
   public AtsConfiguration createConfig(MultivaluedMap<String, String> form, @Context UriInfo uriInfo);

}
