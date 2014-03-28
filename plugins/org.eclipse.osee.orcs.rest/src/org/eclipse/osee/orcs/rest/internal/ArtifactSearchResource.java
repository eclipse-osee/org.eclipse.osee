/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.orcs.rest.internal.search.artifact.ArtifactSearch;
import org.eclipse.osee.orcs.rest.internal.search.artifact.ArtifactSearch_V1;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactSearchResource {

   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   Long branchUuid;

   public ArtifactSearchResource(UriInfo uriInfo, Request request, Long branchUuid) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchUuid = branchUuid;
   }

   @Path("{version}")
   public ArtifactSearch getArtifact(@PathParam("version") String version) {
      String versionToMatch = version.toUpperCase();

      ArtifactSearch toReturn = null;
      if ("V1".equals(versionToMatch)) {
         toReturn = new ArtifactSearch_V1(uriInfo, request, branchUuid);
      }
      return toReturn;
   }
}
