/*********************************************************************
 * Copyright (c) 2020 Boeing
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
import org.eclipse.osee.ats.api.column.AtsCoreColumn;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.result.ResultRows;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
@Path("world")
@Swagger
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
   @Path("custconv")
   @Produces(MediaType.APPLICATION_JSON)
   XResultData getCustomizationsConv();

   @GET
   @Path("my/{userArtId}")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<IAtsWorkItem> getMyWorld(@PathParam("userArtId") ArtifactId userArtId);

   @GET
   @Path("my/{userArtId}/ui")
   @Produces(MediaType.TEXT_HTML)
   String getMyWorldUI(@PathParam("userArtId") ArtifactId userArtId);

   @GET
   @Path("my/{userArtId}/ui/{customizeGuid}")
   @Produces(MediaType.TEXT_HTML)
   String getMyWorldUICustomized(@PathParam("userArtId") ArtifactId userArtId,
      @PathParam("customizeGuid") String customizeGuid);

   @GET
   @Path("coll/{collectorId}")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<IAtsWorkItem> getCollection(@PathParam("collectorId") ArtifactId collectorId);

   @GET
   @Path("coll/{collectorId}/ui")
   @Produces(MediaType.TEXT_HTML)
   String getCollectionUI(@PathParam("collectorId") ArtifactId collectorId);

   @GET
   @Path("coll/{collectorId}/ui/{customizeGuid}")
   @Produces(MediaType.TEXT_HTML)
   String getCollectionUICustomized(@PathParam("collectorId") ArtifactId collectorId,
      @PathParam("customizeGuid") String customizeGuid);

   @PUT
   @Path("search")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   ResultRows search(AtsSearchData atsSearchData);

   /**
    * New search that, at this time, only supports teams and current state type. To be replaced by full / fast search
    */
   @PUT
   @Path("teamWfsInState")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   ResultRows searchNew(AtsSearchData atsSearchData);

   @GET
   @Path("column")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<AtsCoreColumn> getColumns();
}