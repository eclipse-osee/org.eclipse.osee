/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.define.rest.reports;

import java.util.List;
import java.util.Objects;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.define.operations.api.DefineOperations;
import org.eclipse.osee.define.operations.publisher.publishing.PublishingPermissions;
import org.eclipse.osee.define.operations.publisher.publishing.UserNotAuthorizedForPublishingException;
import org.eclipse.osee.define.operations.reports.FeatureImpactStreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Provides the wrapper methods that expose the Reports operations methods as REST API end points.
 *
 * @author Loren K. Ashley
 */

public class ReportsEndpointImpl {

   /**
    * Saves a handle to the Define Operations.
    */

   private final DefineOperations defineOperations;

   /**
    * Saves a handle to the {@link OrcsApi}.
    */

   private final OrcsApi orcsApi;

   /**
    * Creates a new REST API end point implementation for Reports.
    *
    * @param defineOperations a handle to the Define Service Publishing operations.
    * @throws NullPointerException when the parameter <code>defineOperations</code> is <code>null</code>.
    */

   public ReportsEndpointImpl(DefineOperations defineOperations, OrcsApi orcsApi) {
      this.defineOperations = Objects.requireNonNull(defineOperations);
      this.orcsApi = Objects.requireNonNull(orcsApi);
   }

   public Response applicabilityImpact(BranchId branch, String publish, List<ArtifactTypeToken> artTypes, List<AttributeTypeToken> attrTypes) {
      boolean publishUpdates = (publish.equals("true")) ? true : false;
      try {
         PublishingPermissions.verifyNonGroup();
         Branch branchArt =
            this.orcsApi.getQueryFactory().branchQuery().andId(branch).getResults().getAtMostOneOrDefault(
               Branch.SENTINEL);
         StreamingOutput streamingOutput =
            new FeatureImpactStreamingOutput(branchArt, orcsApi, defineOperations, publishUpdates, artTypes, attrTypes);
         ResponseBuilder builder = Response.ok(streamingOutput);
         builder.header("Content-Disposition",
            "attachment; filename=" + branchArt.getName().replaceAll("[^a-zA-Z0-9-]", "_") + ".zip");
         return builder.build();
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }

}

/* EOF */