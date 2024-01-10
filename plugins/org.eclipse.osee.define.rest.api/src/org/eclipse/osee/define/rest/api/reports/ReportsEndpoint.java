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

package org.eclipse.osee.define.rest.api.reports;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * Interface defining the REST API endpoints for reports.
 *
 * @author Loren K. Ashley
 */

@Path("reports")
public interface ReportsEndpoint {

   @GET
   @Path("applicabilityImpact/{branch}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces("application/zip")
   //@formatter:off
   Response
      applicabilityImpact
         (
            @PathParam( "branch" )                              BranchId                 branch,
            @QueryParam( "publish"   ) @DefaultValue( "false" ) String                   publish,
            @QueryParam( "artTypes"  )                          List<ArtifactTypeToken>  artTypes,
            @QueryParam( "attrTypes" )                          List<AttributeTypeToken> attrTypes
         );
   //@formatter:on

}

/* EOF */