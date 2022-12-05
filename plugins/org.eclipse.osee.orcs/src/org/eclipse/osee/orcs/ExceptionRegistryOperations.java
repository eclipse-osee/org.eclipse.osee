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

package org.eclipse.osee.orcs;

import java.util.List;
import org.eclipse.osee.framework.core.exceptionregistry.ExceptionRegistryEntry;

/**
 * Interface for the Exception Registry OSGi service. This service provides a registry for exceptions that will have
 * automatic logging suppressed. Normally when a REST API endpoint method throws an exception the server will generate
 * an HTTP response for the REST call from the exception. The caught exception is normally also sent to the server log.
 * Exceptions added to the registry will have the automatic logging suppressed.
 *
 * @author Loren K. Ashley
 */

public interface ExceptionRegistryOperations {

   /**
    * Removes all entries from the Exception Registry. This will restore automatic logging for all exceptions.
    */

   void clearCache();

   /**
    * Adds an exception specified with an {@link ExceptionRegistryEntry} to the registry.
    *
    * @param exceptionRegistryEntry the exception to suppress logging for.
    */

   void setException(ExceptionRegistryEntry exceptionRegistryEntry);

   /**
    * Restores automatic logging for the exception specified with an {@link ExceptionRegistryEntry}.
    *
    * @param exceptionRegistryEntry the exception to restore logging for.
    */

   void setInclusion(ExceptionRegistryEntry exceptionRegistryEntry);

   /**
    * Gets a list of the exceptions in the registry. The returned list is not backed by the exception registry. Changes
    * to the exception registry will not be reflected in the returned list.
    *
    * @return a {@link List} of the {@link ExceptionRegistryEntry} objects currently in the exception registry.
    */

   List<ExceptionRegistryEntry> list();

   /**
    * Predicate to determine if automatic logging for the specified exception is allowed.
    *
    * @param throwable the exception to be checked.
    * @return <true>, when log suppression is not enabled for the specified exception; otherwise, <code>fasle</code>.
    */

   boolean okToLog(Throwable throwable);
}

/* EOF */