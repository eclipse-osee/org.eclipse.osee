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
package org.eclipse.osee.define.operations.synchronization.identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * An implementation of the {@link IdentifierTracker} interface for {@link IdentifierFactoryType#PATTERN_MATCHING}
 * {@link IdentifierFactory}.
 *
 * @author Loren K. Ashley
 */

public class IdentifierTrackerPatternMatching implements IdentifierTracker {

   /**
    * String builder used for creating the identifier strings.
    */

   StringBuilder stringBuilder;

   /**
    * The type of identifiers created by this {@link IdentifierTracker}.
    */

   IdentifierType identifierType;

   /**
    * A counter used to generate the numeric portion of the identifiers.
    */

   Long identifierCount;

   /**
    * A regular expression pattern defining the expected format of the primary identifiers for the
    * {@link IdentifierType} of the {@link IdentifierTracker}.
    */

   Pattern primaryIdentifierPattern;

   /**
    * A map that tracks all generated primary identifiers. Used to detect duplicate identifiers and to associate the
    * foreign identifiers to the primary identifiers.
    */

   Map<String, Identifier> allocatedIdentifiers;

   /**
    * Creates a new {@link IdentifierTracker} to be used when importing a Synchronization Artifact.
    *
    * @param identifierType the {@link IdentifierType} to create identifiers for.
    */

   IdentifierTrackerPatternMatching(IdentifierType identifierType) {
      this.identifierType = Objects.requireNonNull(identifierType);
      this.allocatedIdentifiers = new HashMap<>();
      this.identifierCount = 0L;
      this.stringBuilder = new StringBuilder();
      this.primaryIdentifierPattern =
         Pattern.compile("(".concat(identifierType.getIdentifierPrefix()).concat(")-([0-9]+)"));
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalStateException when the parameter <code>foreignIdentifierString</code> is <code>null</code>. When
    * generating primary {@link Identifier} objects with a {@link IdentifierFactoryType#PATTERN_MATCHING}
    * {@link IdentifierTracker} the string representation of a foreign thing's identifier must be specified.
    */

   @Override
   synchronized public Identifier create(String foreignIdentifierString) {
      if (Objects.isNull(foreignIdentifierString)) {
         throw new IllegalStateException();
      }

      var matcher = this.primaryIdentifierPattern.matcher(foreignIdentifierString);

      Identifier identifier;

      if (matcher.matches()) {

         /*
          * Foreign identifier matches the primary pattern, parse the parts and use the foreign identifier as the
          * primary identifier.
          */

         if (this.allocatedIdentifiers.containsKey(foreignIdentifierString)) {
            throw new DuplicateIdentifierException(this.identifierType, foreignIdentifierString);
         }

         var numericPart = matcher.group(2);

         var identifierCount = Long.valueOf(numericPart);

         identifier = new Identifier(foreignIdentifierString, identifierCount, this.identifierType);
      } else {

         /*
          * Create identifier for non-pattern matching foreign identifiers.
          */

         this.stringBuilder.setLength(0);

         //@formatter:off
         foreignIdentifierString =
            this.stringBuilder
               .append( this.identifierType.getIdentifierPrefix() )
               .append( "-G-" )
               .append( Long.toString( this.identifierCount, 10 ) )
               .toString();
         //@formatter:on

         identifier = new Identifier(foreignIdentifierString, this.identifierCount++, this.identifierType);
      }

      this.allocatedIdentifiers.put(foreignIdentifierString, identifier);

      return identifier;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Identifier> getPrimaryIdentifierByForeignIdentifierString(String identifierString) {
      return Optional.ofNullable(this.allocatedIdentifiers.get(identifierString));
   }
}

/* EOF */