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

package org.eclipse.osee.orcs.rest.internal;

import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.core.exceptionregistry.ExceptionRegistryEntry;
import org.eclipse.osee.orcs.ExceptionRegistryOperations;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.ExceptionRegistryEndpoint;

/**
 * Implementation of the {@link ExceptionRegistryEndpoint} interface.
 *
 * @author Loren K. Ashley
 */

public class ExceptionRegistryEndpointImpl implements ExceptionRegistryEndpoint {

   private final ExceptionRegistryOperations exceptionRegistryOperations;

   /**
    * Creates a new implementation of the {@link ExceptionRegistryEndpoint} interface.
    *
    * @param orcsApi a handle to the {@link OrcsApi} used to obtain the {@link ExceptionRegistryOperations}
    * implementation.
    */

   ExceptionRegistryEndpointImpl(OrcsApi orcsApi) {
      //@formatter:off
      this.exceptionRegistryOperations =
         Objects.requireNonNull
            (
              Objects.requireNonNull
                 (
                   orcsApi,
                   "ExceptionRegistryEndpointImpl::new, parameter \"orcsApi\" cannot be null."
                 )
                 .getExceptionRegistryOperations(),
                 "ExceptionRegistryEndpointImpl::new, parameter \"orcsApi\" cannot have null result for the method \"getExceptionRegistryOperations\"."
            );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void clearCache() {
      this.exceptionRegistryOperations.clearCache();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setException(ExceptionRegistryEntry exceptionRegistryEntry) {
      this.exceptionRegistryOperations.setException(exceptionRegistryEntry);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setInclusion(ExceptionRegistryEntry exceptionRegistryEntry) {
      this.exceptionRegistryOperations.setInclusion(exceptionRegistryEntry);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public List<ExceptionRegistryEntry> list() {
      return this.exceptionRegistryOperations.list();
   }

}

/* EOF */