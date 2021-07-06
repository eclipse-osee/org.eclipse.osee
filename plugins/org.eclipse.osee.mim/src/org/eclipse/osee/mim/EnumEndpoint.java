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

/**
 * @author Luciano T. Vaglienti
 */
@Path("enums")
public interface EnumEndpoint {

   @GET()
   @Path("MessagePeriodicities")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<String> getPeriodicity();

   @GET()
   @Path("MessageRates")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<String> getMessageRates();

   @GET()
   @Path("MessageTypes")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<String> getMessageTypes();

   @GET()
   @Path("StructureCategories")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<String> getStructureCategories();
}
