/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.api.report;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
@Path("report")
public interface AtsReportEndpointApi {

   /**
    * @param date like 2025-07-15
    * @param artTypeId
    * @param attrTypeIds - comma delimited attr type ids
    */
   @Path("AttrDiffReport")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getAttrDiffReport(@QueryParam("date") String date, @QueryParam("artTypeId") String artTypeId,
      @QueryParam("attrTypeIds") String attrTypeIds);

   @Path("RestCoverageReport")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   XResultData getRestCoverageReport();
}