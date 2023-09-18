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

package org.eclipse.osee.mim;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.mim.types.ConnectionValidationResult;

/**
 * @author Ryan T. Baldwin
 */
@Path("validation")
@Swagger
public interface InterfaceValidationEndpoint {
   @GET()
   @Path("connection/{connectionId}")
   @Produces(MediaType.APPLICATION_JSON)
   ConnectionValidationResult validateConnection(@PathParam("connectionId") ArtifactId connectionId,
      @QueryParam("viewId") ArtifactId viewId);

}
