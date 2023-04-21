/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import java.util.LinkedHashMap;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Roberto E. Escobar
 */
@Path("types")
@Swagger
public interface TypesEndpoint {

   @GET
   @Path("health")
   @Produces({MediaType.APPLICATION_JSON})
   XResultData getHealthReport();

   @GET
   @Path("serverEnumTypesAndValues")
   @Produces({MediaType.APPLICATION_JSON})
   List<LinkedHashMap<String, Object>> getServerEnumTypesAndValues();

}