/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.define.report.api.DataRightInput;
import org.eclipse.osee.define.report.api.DataRightResult;
import org.eclipse.osee.define.report.api.DefineApi;

/**
 * @author Angel Avila
 */
@Path("/publish/dataRights")
public class DataRightsResource {

   private final DefineApi defineApi;

   public DataRightsResource(DefineApi defineApi) {
      this.defineApi = defineApi;
   }

   /**
    * Create request to determine all data rights for a collection of Artifacts
    * 
    * @param data Collection of DataRightArtifacts to find data rights for
    * @return mapping Mapping of DataRightArtifacts to Footers
    * @throws JSONException
    */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public DataRightResult findAllDataRights(DataRightInput data) {
      DataRightResult dataRights = defineApi.getDataRights(data);
      return dataRights;
   }
}
