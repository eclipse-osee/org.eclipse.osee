/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import java.io.InputStream;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.jdk.core.type.SystemRoles;

/**
 * @author Roberto E. Escobar
 */
@Path("types")
@RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
public interface TypesEndpoint {

   @GET
   @Produces({OrcsMediaType.APPLICATION_ORCS_TYPES, MediaType.TEXT_PLAIN})
   Response getTypes();

   @POST
   @Consumes({OrcsMediaType.APPLICATION_ORCS_TYPES, MediaType.TEXT_PLAIN})
   @Produces(MediaType.APPLICATION_JSON)
   Response setTypes(InputStream inputStream);

   @POST
   @Path("invalidate-caches")
   Response invalidateCaches();
}