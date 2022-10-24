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

package org.eclipse.osee.define.operations.synchronization.forest;

import java.util.function.Predicate;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;

/**
 * Implementations of the interface are used to specify a {@link Forest} {@link Grove} to be streamed and an optional
 * filter.
 *
 * @author Loren K. Ashley
 */

public interface StreamEntry {

   /**
    * Creates a {@link StreamEntry} for the specified {@link IdentifierType} without a filter.
    *
    * @param identifierType specifies the {@link Grove} to be streamed.
    * @return a {@link StreamEntry} implementation for the specified {@link IdentifierType}.
    */

   public static StreamEntry create(IdentifierType identifierType) {
      return new StreamEntry() {
         @Override
         public IdentifierType getIdentifierType() {
            return identifierType;
         }

         @Override
         public Predicate<GroveThing> getFilter() {
            return null;
         }

         @Override
         public boolean hasFilter() {
            return false;
         }
      };
   }

   /**
    * Creates a {@link StreamEntry} for the specified {@link IdentifierType} with a filter.
    *
    * @param identifierType specifies the {@link Grove} to be streamed.
    * @param filter a {@link Predicate} to filter the stream with.
    * @return a {@link StreamEntry} implementation for the specified {@link IdentifierType}.
    */

   public static StreamEntry create(IdentifierType identifierType, Predicate<GroveThing> filter) {
      return new StreamEntry() {
         @Override
         public IdentifierType getIdentifierType() {
            return identifierType;
         }

         @Override
         public Predicate<GroveThing> getFilter() {
            return filter;
         }

         @Override
         public boolean hasFilter() {
            return true;
         }
      };
   }

   /**
    * Gets the {@link IdentifierType} of the {@link Grove} to be streamed.
    *
    * @return the {@link IdentifierType} of the {@link Grove} to be streamed.
    */

   IdentifierType getIdentifierType();

   /**
    * Gets the {@link Predicate} to be applied as a filter to the stream.
    *
    * @return a {@link Predicate} or <code>null</code>.
    */

   Predicate<GroveThing> getFilter();

   /**
    * Predicate to determine if this {@link StreamEntry} has a specified filter.
    *
    * @return <code>true</code> when the {@link StreamEntry} has a filter; otherwise, <code>false</code>.
    */

   boolean hasFilter();

}

/* EOF */
