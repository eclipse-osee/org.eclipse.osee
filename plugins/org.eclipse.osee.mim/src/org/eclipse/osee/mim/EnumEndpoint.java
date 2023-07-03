/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Luciano T. Vaglienti
 */
@Path("enums")
@Swagger
public interface EnumEndpoint {

   @GET()
   @Path("MessagePeriodicities")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<String> getPeriodicity();

   /**
    * @deprecated
    * @see org.eclipse.osee.mim.InterfaceMessageRatesEndpoint
    */
   @Deprecated
   @GET()
   @Path("MessageRates")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<String> getMessageRates();

   /**
    * @deprecated
    * @see org.eclipse.osee.mim.InterfaceMessageTypesEndpoint
    */
   @Deprecated
   @GET()
   @Path("MessageTypes")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<String> getMessageTypes();

   @GET()
   @Path("StructureCategories")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<String> getStructureCategories();

   /**
    * @deprecated
    * @see org.eclipse.osee.mim.InterfaceUnitEndpoint
    */
   @Deprecated
   @GET()
   @Path("Units")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<String> getPossibleUnits();
}
