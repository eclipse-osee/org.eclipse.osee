/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs.server.internal.ext;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ExceptionRegistryOperations;

/**
 * Implementation of the JAX-RS {@link ExceptionMapper} interface for {@link Throwable} exceptions. This
 * {@link ExceptionMapper} provides a default {@link ExceptionMapper} for exceptions.
 *
 * @author Roberto E. Escobar
 * @author Loren K. Ashley
 */

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

   /**
    * Saves a handle to the {@link ExceptionRegistryOperations} service.
    */

   private final ExceptionRegistryOperations exceptionRegistryOperations;

   /**
    * Saves a handle to the {@link Log} service.
    */

   private final Log logger;

   /**
    * Magic member which is set to the URL of the REST call that caused the exception.
    */

   @Context
   private UriInfo uriInfo;

   /**
    * Creates a new instance of the {@link ExceptionMapper}.
    *
    * @param exceptionRegistryOperations a handle to the {@link ExceptionRegistryOperations} service.
    * @param logger a handle to the {@link Log} service.
    */

   public GenericExceptionMapper(ExceptionRegistryOperations exceptionRegistryOperations, Log logger) {
      this.exceptionRegistryOperations = exceptionRegistryOperations;
      this.logger = logger;
   }

   /**
    * Converts an exception into a {@link Response} object.
    *
    * @param throwable the server side exception to be converted to a {@link Response}.
    */

   @Override
   public final Response toResponse(Throwable throwable) {

      return ThrowableToResponse.toResponse(throwable, this.uriInfo, this.exceptionRegistryOperations, this.logger);
   }
}