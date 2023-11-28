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

package org.eclipse.osee.orcs.core.internal;

import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.core.exceptionregistry.ExceptionRegistryEntry;
import org.eclipse.osee.orcs.ExceptionRegistryOperations;

/**
 * Implementation of the Exception Registry service. This service is used to exclude REST API method endpoint exceptions
 * from automatic logging.
 *
 * @author Loren K. Ashley
 */

public class ExceptionRegistryOperationsImpl implements ExceptionRegistryOperations {

   /**
    * Saves the single instance of the Exception Registry service implementation.
    */

   private static ExceptionRegistryOperationsImpl exceptionRegistryOperationsImpl = null;

   /**
    * Gets or creates the single implementation of the Exception Registry service.
    *
    * @return the single implementation of the {@link ExceptionRegistryOperations} interface.
    */

   public synchronized static ExceptionRegistryOperationsImpl create() {

      //@formatter:off
      return
         Objects.isNull( ExceptionRegistryOperationsImpl.exceptionRegistryOperationsImpl )
            ? ( ExceptionRegistryOperationsImpl.exceptionRegistryOperationsImpl =
                   new ExceptionRegistryOperationsImpl()
              )
            : ExceptionRegistryOperationsImpl.exceptionRegistryOperationsImpl;
      //@formatter:on
   }

   /**
    * Nulls the static reference to the {@link ExceptionRegistryOperationsImpl} instance so that it can be garbage
    * collected.
    */

   public synchronized static void free() {
      ExceptionRegistryOperationsImpl.exceptionRegistryOperationsImpl = null;
   }

   /**
    * Member saves unique {@link ExceptionRegistyEntry} objects.
    */

   private final ExceptionRegistry exceptionRegistry;

   /**
    * Creates the Exception Registry service implementation with an empty registry.
    */

   private ExceptionRegistryOperationsImpl() {
      this.exceptionRegistry = new ExceptionRegistry();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void clearCache() {
      this.exceptionRegistry.clear();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public List<ExceptionRegistryEntry> list() {
      return this.exceptionRegistry.getList();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean okToLog(Throwable throwable) {
      return this.exceptionRegistry.okToLog(throwable);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setException(ExceptionRegistryEntry exceptionRegistryEntry) {
      this.exceptionRegistry.add(exceptionRegistryEntry);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setInclusion(ExceptionRegistryEntry exceptionRegistryEntry) {
      this.exceptionRegistry.remove(exceptionRegistryEntry);
   }

}

/* EOF */
