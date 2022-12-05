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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.eclipse.osee.framework.core.exceptionregistry.ExceptionRegistryEntry;

/**
 * A registry of exceptions to be suppressed from logging. When a REST API endpoint method throws an exception it is
 * usually logged by the server before a {@link Response} is sent back to the client. This class maintains a registry of
 * exception for which that automatic logging is suppressed.
 *
 * @author Loren K. Ashley
 */

class ExceptionRegistry {

   /**
    * Saves an entry for each REST API endpoint exception to be excluded from the log.
    */

   Set<ExceptionRegistryEntry> set;

   /**
    * Creates a new empty exception log suppression registry.
    */

   ExceptionRegistry() {
      this.set = new HashSet<>();
   }

   /**
    * Adds an exception to the registry for log suppression. Exceptions are specified by the fully qualified class name
    * of the exception and also optionally by the fully qualified class name of the causing exception.
    *
    * @param exceptionRegistryEntry the exception to suppress logging for.
    */

   void add(ExceptionRegistryEntry exceptionRegistryEntry) {
      this.set.add(exceptionRegistryEntry);
   };

   /**
    * Removes all entries from the exception registry.
    */

   void clear() {
      this.set.clear();
   }

   /**
    * Removes an exception from the registry for log suppression. If an exception was added to the registry with a
    * specified cause, the fully qualified class names of the exception and cause must be provided.
    *
    * @param exceptionRegistryEntry the exception to end log suppression for.
    */

   void remove(ExceptionRegistryEntry exceptionRegistryEntry) {
      this.set.remove(exceptionRegistryEntry);
   }

   /**
    * Predicate to determine if automatic logging for an exception is OK.
    *
    * @param throwable the exception to be checked.
    * @return <code>true</code>, when the exception is eligible for automatic logging; otherwise, <code>false</code>.
    */

   boolean okToLog(Throwable throwable) {

      var primaryKey = throwable.getClass().getName();
      var cause = throwable.getCause();
      var secondaryKey = Objects.nonNull(cause) ? cause.getClass().getName() : "(none)";

      return !this.set.contains(new ExceptionRegistryEntry(primaryKey, secondaryKey));
   }

   /**
    * Gets a list of the exception suppression entries in the registry.
    *
    * @return a list of the exception registry entries.
    */

   List<ExceptionRegistryEntry> getList() {
      return new ArrayList<>(this.set);
   }
}

/* EOF */
