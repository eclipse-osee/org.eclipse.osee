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

package org.eclipse.osee.define.operations.synchronization.identifier;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.eclipse.jdt.annotation.NonNull;

/**
 * This class implements a factory for creating the primary {@link Identifier} objects used in a Synchronization
 * Artifact DOM. For exports the primary identifiers are created using a simple count and a prefix for the type of
 * identifier. For imports when the foreign identifier matches the pattern for the primary identifier of the identifier
 * type, the foreign identifier is parse and a primary identifier is created from the foreign identifier. For imports
 * when the foreign identifier does not match the pattern for the primary identifier a primary identifier is generated
 * using the prefix for the identifier type followed by a "-G-" and an integer count.
 *
 * @author Loren K. Ashley
 */

public class IdentifierFactory {

   /**
    * Saves the type (export/{@link IdentifierFactoryType#COUNTING} or
    * import/{@link IdentifierFactoryType#PATTERN_MATCHING}).
    */

   IdentifierFactoryType identifierFactoryType;

   /**
    * A map of {@link IdentifierTracker} objects for each {@link IdentifierType}.
    */

   private final EnumMap<IdentifierType, IdentifierTracker> identifierTrackerMap;

   /**
    * Creates a new {@link IdentifierFactory}. A new factory should be created for each export or import operation.
    *
    * @param identifierFactoryType the type (export/import) of factory to be created.
    */

   public IdentifierFactory(IdentifierFactoryType identifierFactoryType) {

      this.identifierFactoryType = Objects.requireNonNull(identifierFactoryType,
         "IdentifierFactory::new, parameter \"identifierFactoryType\" is null.");

      this.identifierTrackerMap = new EnumMap<>(IdentifierType.class);

      //@formatter:off
      Function<IdentifierType, IdentifierTracker> identifierTrackerFactory =
         ( identifierFactoryType == IdentifierFactoryType.COUNTING )
            ? IdentifierTrackerCounting::new
            : IdentifierTrackerPatternMatching::new;

      Arrays.stream( IdentifierType.values() )
         .forEach
            (
               ( identifierType ) -> this.identifierTrackerMap.put
                                        (
                                           identifierType,
                                           identifierTrackerFactory.apply( identifierType )
                                        )
            );
      //@formatter:on
   }

   /**
    * Factory method for exports to create a new primary {@link Identifier} of the specified {@link IdentifierType}.
    *
    * @param identifierType the type of primary identifier to be created.
    * @return the new unique primary {@link Identifier}.
    * @throws IllegalStateException when this method is called for an {@link IdentifierFactory} that was created for
    * imports.
    */

   @SuppressWarnings("null")
   public @NonNull Identifier createIdentifier(IdentifierType identifierType) {
      if (this.identifierFactoryType != IdentifierFactoryType.COUNTING) {
         throw new IllegalStateException();
      }
      return this.identifierTrackerMap.get(identifierType).create(null);
   }

   /**
    * Factory method for imports to create a new primary {@link Identifier} of the specified {@link IdentifierType}.
    *
    * @param identifierType the type of primary identifier to be created.
    * @param foreignIdentifier the string representation of the identifier for the foreign thing that the primary
    * {@link Identifier} is being created for.
    * @return the new unique primary {@link Identifier}.
    * @throw IllegalStateException when this method is called for an {@link IdentifierFactory} that was created for
    * exports.
    */

   @SuppressWarnings("null")
   public @NonNull Identifier createIdentifier(IdentifierType identifierType, String foreignIdentifier) {
      if (this.identifierFactoryType != IdentifierFactoryType.PATTERN_MATCHING) {
         throw new IllegalStateException();
      }
      return this.identifierTrackerMap.get(identifierType).create(foreignIdentifier);
   }

   /**
    * Gets the primary {@link Identifier} that was associated with the specified foreign identifier string.
    *
    * @param identifierType the type of primary identifier the specified foreign identifier string is associated with.
    * @param foreignIdentifierString the string representation of the identifier of a foreign thing
    * @return the primary {@link Identifier} associated with the specified foreign identifier string.
    * @throws IllegalStateException when this method is called for an {@link IdentifierFactory} that was created for
    * exports.
    */

   public Optional<Identifier> getPrimaryIdentifierByForeignIdentifierString(IdentifierType identifierType,
      String foreignIdentifierString) {
      if (this.identifierFactoryType != IdentifierFactoryType.PATTERN_MATCHING) {
         throw new IllegalStateException();
      }
      var identifierTracker = this.identifierTrackerMap.get(identifierType);
      return identifierTracker.getPrimaryIdentifierByForeignIdentifierString(foreignIdentifierString);
   }
}

/* EOF */