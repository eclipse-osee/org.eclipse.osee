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

package org.eclipse.osee.orcs.rest.model;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.exceptionregistry.ExceptionRegistryEntry;

/**
 * This interface defines the REST API end points for the Exception Registry.
 *
 * @author Loren K. Ashley
 */

@Path("exceptionregistry")
public interface ExceptionRegistryEndpoint {

   /**
    * Removes all entries from the Exception Registry. This will restore automatic logging for all exceptions.
    */

   //@formatter:off
   @DELETE
   @Path("cache")
   void clearCache();
   //@formatter:on

   /**
    * Adds an exception specified with an {@link ExceptionRegistryEntry} to the registry.
    *
    * @param exceptionRegistryEntry the exception to suppress logging for.
    */

   //@formatter:off
   @POST
   @Path("exclude")
   @Consumes(MediaType.APPLICATION_JSON)
   void setException( ExceptionRegistryEntry exceptionRegistryEntry );
   //@formatter:on

   /**
    * Restores automatic logging for the exception specified with an {@link ExceptionRegistryEntry}.
    *
    * @param exceptionRegistryEntry the exception to restore logging for.
    */

   //@formatter:off
   @POST
   @Path("include")
   @Consumes(MediaType.APPLICATION_JSON)
   void setInclusion( ExceptionRegistryEntry exceptionRegistryEntry );
   //@formatter:on

   /**
    * Gets a list of the exceptions in the registry. The returned list is not backed by the exception registry. Changes
    * to the exception registry will not be reflected in the returned list.
    *
    * @return a {@link List} of the {@link ExceptionRegistryEntry} objects currently in the exception registry.
    */

   //@formatter:off
   @GET
   @Path("list")
   @Produces(MediaType.APPLICATION_JSON)
   List<ExceptionRegistryEntry> list();
   //@formatter:on
}

/* EOF */
