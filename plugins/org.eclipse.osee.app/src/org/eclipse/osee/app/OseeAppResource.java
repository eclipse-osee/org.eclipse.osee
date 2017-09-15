/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.app;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author David W. Miller
 */
@Path("/api")
public final class OseeAppResource {
   private final OrcsApi orcsApi;

   public OseeAppResource(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   /**
    * get a list of all of the available OSEE Application artifacts
    *
    * @return provides json containing a collection of Application name, description, link
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<OseeAppTransfer> getOseeAppListJson(@QueryParam("filter") String filterText) {
      List<OseeAppTransfer> results = new LinkedList<OseeAppTransfer>();
      BranchId branchId =
         orcsApi.getQueryFactory().branchQuery().andNameEquals("Common").getResultsAsId().getExactlyOne();
      ResultSet<ArtifactReadable> apps =
         orcsApi.getQueryFactory().fromBranch(branchId).andTypeEquals(CoreArtifactTypes.OseeApp).getResults();
      for (ArtifactReadable art : apps) {
         String description = art.getSoleAttributeAsString(CoreAttributeTypes.Description, "Not Available");
         String uuid = String.format("%d", art.getUuid());
         if (filterText == null) {
            results.add(createTransfer(art.getName(), description, uuid));
         } else if (description.matches(filterText)) {
            results.add(createTransfer(art.getName(), description, uuid));
         }
      }
      return results;
   }

   /**
    * get the schema for the requested application object
    *
    * @param appId (artifact id for application object type).
    * @return provides the application json for the requested id
    */
   @GET
   @Path("/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public String getOseeAppJson(@PathParam("id") Long id) {
      BranchId branchId =
         orcsApi.getQueryFactory().branchQuery().andNameEquals("Common").getResultsAsId().getExactlyOne();
      ArtifactReadable app =
         orcsApi.getQueryFactory().fromBranch(branchId).andId(ArtifactId.valueOf(id)).getResults().getExactlyOne();
      return app.getSoleAttributeAsString(CoreAttributeTypes.OseeAppDefinition);
   }

   private OseeAppTransfer createTransfer(String name, String description, String uuid) {
      OseeAppTransfer item = new OseeAppTransfer();
      item.setName(name);
      item.setDescription(description);
      item.setUuid(uuid);
      return item;
   }
}
