/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactResource {

   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   BranchId branchId;
   Long artifactUuid;

   public ArtifactResource(UriInfo uriInfo, Request request, BranchId branchId, Long artifactUuid) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchId = branchId;
      this.artifactUuid = artifactUuid;
   }

   @Path("attribute")
   public AttributesResource getAttributes() {
      return new AttributesResource(uriInfo, request, branchId, artifactUuid);
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getAsHtml()  {
      QueryFactory factory = OrcsApplication.getOrcsApi().getQueryFactory();
      ResultSet<ArtifactReadable> arts = factory.fromBranch(branchId).andUuid(artifactUuid).getResults();
      HtmlWriter writer = new HtmlWriter(uriInfo);
      return writer.toHtml(arts);
   }
}
