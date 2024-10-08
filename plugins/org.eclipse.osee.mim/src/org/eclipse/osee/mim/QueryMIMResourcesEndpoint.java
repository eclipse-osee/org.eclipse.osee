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
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.accessor.types.AttributeQuery;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

@Path("query")
@Swagger
public interface QueryMIMResourcesEndpoint {

   @POST()
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public Collection<? extends ArtifactAccessorResult> get(AttributeQuery query);

   @POST()
   @Path("exact")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public Collection<? extends ArtifactAccessorResult> getExact(AttributeQuery query);
}
