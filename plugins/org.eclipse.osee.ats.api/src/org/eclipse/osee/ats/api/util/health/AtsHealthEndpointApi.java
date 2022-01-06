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

package org.eclipse.osee.ats.api.util.health;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
@Path("health")
public interface AtsHealthEndpointApi {

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get();

   @GET
   public boolean alive();

   @GET
   @Path("dupart/{id}/{newArtId}")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData dupArtReport(@PathParam("id") ArtifactId id, @PathParam("newArtId") String newArtId);

}
