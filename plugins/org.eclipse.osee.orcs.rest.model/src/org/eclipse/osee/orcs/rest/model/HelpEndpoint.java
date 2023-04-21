/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.model.dto.HelpPageDto;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Ryan T. Baldwin
 */
@Path("help")
@Swagger
public interface HelpEndpoint {

   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<HelpPageDto> getHelp(@QueryParam("app") String appName);

   @GET
   @Path("{id}")
   @Produces({MediaType.APPLICATION_JSON})
   HelpPageDto getHelpPage(@PathParam("id") String id);

}
