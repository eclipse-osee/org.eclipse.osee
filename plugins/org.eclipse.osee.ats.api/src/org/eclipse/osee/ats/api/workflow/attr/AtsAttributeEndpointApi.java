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

package org.eclipse.osee.ats.api.workflow.attr;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Donald G. Dunne
 */
@Path("attr")
@Swagger
public interface AtsAttributeEndpointApi {

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public AtsAttributes get();

   @Path("{id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getValidValues(@PathParam("id") String idOrName);
}