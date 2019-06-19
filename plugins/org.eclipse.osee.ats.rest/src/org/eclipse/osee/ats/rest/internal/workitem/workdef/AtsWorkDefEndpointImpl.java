/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem.workdef;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefEndpointApi;
import org.eclipse.osee.ats.rest.internal.workitem.operations.ConvertWorkDefinitionOperations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
@Path("workdef")
public class AtsWorkDefEndpointImpl implements AtsWorkDefEndpointApi {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public AtsWorkDefEndpointImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   @Override
   @GET
   @Path("convert/artrefattr")
   @Produces(MediaType.APPLICATION_JSON)
   public String convertToWorkDefRefs() {
      ConvertWorkDefinitionOperations ops = new ConvertWorkDefinitionOperations(atsApi, orcsApi);
      XResultData data = new XResultData();
      ops.convert(data);
      return data.toString();
   }

}
