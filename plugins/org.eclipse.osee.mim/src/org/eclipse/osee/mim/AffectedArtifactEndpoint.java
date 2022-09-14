/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.mim.types.ArtifactMatch;

@Path("affected")
@Produces(MediaType.APPLICATION_JSON)
public interface AffectedArtifactEndpoint {

   @GET()
   @Path("enums/{id}")
   Collection<ArtifactMatch> getAffectedFromEnum(@PathParam("id") ArtifactId affectedArtifactId);

   @GET()
   @Path("enumsets/{id}")
   Collection<ArtifactMatch> getAffectedFromEnumSet(@PathParam("id") ArtifactId affectedArtifactId);

   @GET()
   @Path("types/{id}")
   Collection<ArtifactMatch> getAffectedFromType(@PathParam("id") ArtifactId affectedArtifactId);

   @GET()
   @Path("elements/{id}")
   Collection<ArtifactMatch> getAffectedFromElement(@PathParam("id") ArtifactId affectedArtifactId);

   @GET()
   @Path("structures/{id}")
   Collection<ArtifactMatch> getAffectedFromStructure(@PathParam("id") ArtifactId affectedArtifactId);

   @GET()
   @Path("submessages/{id}")
   Collection<ArtifactMatch> getAffectedFromSubMessage(@PathParam("id") ArtifactId affectedArtifactId);

   @GET()
   @Path("messages/{id}")
   Collection<ArtifactMatch> getAffectedFromMessage(@PathParam("id") ArtifactId affectedArtifactId);

   @GET()
   @Path("connections/{id}")
   Collection<ArtifactMatch> getAffectedFromConnection(@PathParam("id") ArtifactId affectedArtifactId);
}
