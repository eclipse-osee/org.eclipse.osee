/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.ResultRows;

/**
 * @author Donald G. Dunne
 */
@Path("world")
public interface AtsWorldEndpointApi {

   @GET
   @Path("cust/global")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<CustomizeData> getCustomizationsGlobal();

   @GET
   @Path("cust")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<CustomizeData> getCustomizations();

   @GET
   @Path("my/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<IAtsWorkItem> getMyWorld(@PathParam("id") ArtifactId userArtId);

   @GET
   @Path("my/{id}/ui")
   @Produces(MediaType.TEXT_HTML)
   String getMyWorldUI(@PathParam("id") ArtifactId userArtId);

   @GET
   @Path("my/{id}/ui/{customize_guid}")
   @Produces(MediaType.TEXT_HTML)
   String getMyWorldUICustomized(@PathParam("id") ArtifactId userArtId, @PathParam("customize_guid") String customize_guid);

   @GET
   @Path("coll/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<IAtsWorkItem> getCollection(@PathParam("id") ArtifactId userArtId);

   @GET
   @Path("coll/ui")
   @Produces(MediaType.TEXT_HTML)
   String getCollectionUI(ArtifactId collectorArtId);

   @GET
   @Path("coll/{id}/ui/{customize_guid}")
   @Produces(MediaType.TEXT_HTML)
   String getCollectionUICustomized(@PathParam("id") ArtifactId collectorArtId, @PathParam("customize_guid") String customize_guid);

   @PUT
   @Path("search")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   ResultRows search(AtsSearchData atsSearchData);
}