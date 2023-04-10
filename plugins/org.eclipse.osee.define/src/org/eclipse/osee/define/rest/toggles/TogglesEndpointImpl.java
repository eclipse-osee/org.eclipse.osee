/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.rest.toggles;

import java.util.Objects;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import org.eclipse.osee.define.api.DefineOperations;
import org.eclipse.osee.define.api.toggles.TogglesEndpoint;

/**
 * Provides the wrapper methods that expose the Toggles operations methods as REST API end points.
 *
 * @author Loren K. Ashley
 */

public class TogglesEndpointImpl implements TogglesEndpoint {

   /**
    * Saves a handle to the Define Service Publishing operations implementation.
    */

   private final DefineOperations defineOperations;

   /**
    * Creates a new REST API end point implementation for Toggles.
    *
    * @param defineOperations a handle to the Define Service Synchronization operations.
    * @throws NullPointerException when the parameter <code>defineOperations</code> is <code>null</code>.
    */

   public TogglesEndpointImpl(DefineOperations defineOperations) {
      this.defineOperations = Objects.requireNonNull(defineOperations);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Boolean apply(String name) {
      try {
         return this.defineOperations.getTogglesOperations().apply(name);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }

}

/* EOF */
