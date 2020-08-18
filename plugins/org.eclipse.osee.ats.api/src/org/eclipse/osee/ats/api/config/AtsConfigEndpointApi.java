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

package org.eclipse.osee.ats.api.config;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.SkipAtsConfigJsonWriter;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactImage;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;

/**
 * @author Donald G. Dunne
 */
@Path("config")
public interface AtsConfigEndpointApi {

   /**
    * @return cached copy of AtsConfigurations that is reloaded every 5 minutes. Use getFromDb() for latest copy from
    * database. This should not be used unless configurations are being updated. Use AtsApi.getConfigurations.
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public AtsConfigurations get();

   @POST
   public XResultData createUpdateConfig();

   /**
    * @return non-cached copy of AtsConfigurations read straight from database. Will update server cache with newly read
    * copy. Can take 30ish seconds to load. Use get() for quick access to cached copy. This should not be used unless
    * configurations are being updated. Use AtsApi.getConfigurations
    */
   @GET
   @Path("fromdb")
   @Produces(MediaType.APPLICATION_JSON)
   public AtsConfigurations getWithPend();

   @GET
   @Path("user/{loginId}")
   @Produces(MediaType.APPLICATION_JSON)
   public AtsUser getUserByLogin(@PathParam("loginId") String loginId);

   @GET
   @Path("image")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ArtifactImage> getArtifactImages();

   /**
    * Requests that the server reload the ATS configuration cache and returns without waiting for the reload to occur
    */
   @GET
   @Path("clearcache")
   @Produces(MediaType.APPLICATION_JSON)
   public String requestCacheReload();

   @GET
   @Path("ui/NewAtsBranchConfig")
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getNewSource();

   /**
    * @return json representation of all Attribute Types as AtsAttributeValueColumn for use as a starting point in the
    * views configuration of AtsConfig
    */
   @GET
   @Path("genAttrTypeViews")
   @Produces(MediaType.APPLICATION_JSON)
   public List<AtsAttributeValueColumn> generateAttrTypeViews() throws Exception;

   @GET
   @Path("alive")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData alive();

   @PUT
   @Path("init/demo")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData demoDbInit();

   @PUT
   @Path("init/ats")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData atsDbInit();

   @GET
   @SkipAtsConfigJsonWriter
   @Path("ai/{aiId}")
   @Produces(MediaType.APPLICATION_JSON)
   public ActionableItem getActionableItem(@PathParam("aiId") ArtifactId aiId);

   @GET
   @SkipAtsConfigJsonWriter
   @Path("version/{verId}")
   @Produces(MediaType.APPLICATION_JSON)
   public Version getVersion(@PathParam("verId") ArtifactId verId);

   @GET
   @SkipAtsConfigJsonWriter
   @Path("teamdef/{teamDefId}")
   @Produces(MediaType.APPLICATION_JSON)
   public TeamDefinition getTeamDefinition(@PathParam("teamDefId") ArtifactId teamDefId);

   @POST
   @Path("initialize/demo")
   @Produces(MediaType.APPLICATION_JSON)
   TransactionId demoInitilize();

   @GET
   @Path("validate")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData validate();

   @GET
   @Path("test/result")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData getResultTableTest();

}