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

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.workdef.JaxAtsWorkDef;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;

/**
 * @author Donald G. Dunne
 */
@Path("config")
public interface AtsConfigEndpointApi {

   /**
    * @return cached copy of AtsConfigurations that is reloaded every 5 minutes. Use getFromDb() for latest copy from
    * database.
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public AtsConfigurations get();

   /**
    * @return non-cached copy of AtsConfigurations read straight from database. Can take 30ish seconds to load. Use
    * get() for quick access to cached copy.
    */
   @GET
   @Path("fromdb")
   @Produces(MediaType.APPLICATION_JSON)
   public AtsConfigurations getFromDb();

   @GET
   @Path("clearcache")
   @Produces(MediaType.APPLICATION_JSON)
   public String clearCaches();

   /**
    * @return html5 action entry page
    */
   @GET
   @Path("ui/NewAtsBranchConfig")
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getNewSource() throws Exception;

   /**
    * @return json representation of all Attribute Types as AtsAttributeValueColumn for use as a starting point in the
    * views configuration of AtsConfig
    */
   @GET
   @Path("genAttrTypeViews")
   @Produces(MediaType.APPLICATION_JSON)
   public List<AtsAttributeValueColumn> generateAttrTypeViews() throws Exception;

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
   @Path("branch")
   @Consumes("application/x-www-form-urlencoded")
   @Produces(MediaType.APPLICATION_JSON)
   public AtsConfiguration createConfig(MultivaluedMap<String, String> form, @Context UriInfo uriInfo);

   @POST
   public Response createUpdateConfig();

   @PUT
   @Path("workDef")
   @Consumes(MediaType.APPLICATION_JSON)
   public Response storeWorkDef(JaxAtsWorkDef jaxWorkDef);

}
