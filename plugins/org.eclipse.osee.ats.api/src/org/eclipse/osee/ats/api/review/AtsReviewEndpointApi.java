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

package org.eclipse.osee.ats.api.review;

import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Paul A. Garcia
 */
@Path("review")
@Swagger
public interface AtsReviewEndpointApi {

   @GET
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public IAtsAbstractReview getReview(@PathParam("id") String id);

   @GET
   @Path("/ids/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<IAtsAbstractReview> getReviews(@PathParam("id") String ids);
}
