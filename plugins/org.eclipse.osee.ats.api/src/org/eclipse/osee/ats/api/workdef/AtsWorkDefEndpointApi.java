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

package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
@Path("workdef")
@Swagger
public interface AtsWorkDefEndpointApi {

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<WorkDefinition> get();

   @Path("{id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public WorkDefinition getWorkDef(@PathParam("id") String id);

   @Path("teamdef/{id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public WorkDefinition getWorkDefForTeam(@PathParam("id") String id);

   /**
    * Get all work definitions and ensure all attr types exist
    */
   @GET
   @Path("validate")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData validate();

}
