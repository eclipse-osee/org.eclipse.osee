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

package org.eclipse.osee.define.rest.synchronization.identifier;

import java.util.Objects;
import java.util.Optional;

/**
 * An implementation of the {@link IdentifierTracker} interface for {@link IdentifierFactoryType#COUNTING}
 * {@link IdentifierFactory}.
 *
 * @author Loren K. Ashley
 */

public class IdentifierTrackerCounting implements IdentifierTracker {

   /**
    * Tracks the number of identifiers produced.
    */

   private Long identifierCount;

   /**
    * The type of identifier this tracker is for.
    */

   private final IdentifierType identifierType;

   /**
    * Builder for the identifier strings.
    */

   private final StringBuilder stringBuilder;

   /**
    * Creates a new native identifier tracker set to zero.
    *
    * @param identifierType the {@link IdentifierType} to create identifiers for.
    */

   IdentifierTrackerCounting(IdentifierType identifierType) {
      this.identifierType = identifierType;
      this.identifierCount = 0L;
      this.stringBuilder = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalStateException when the parameter <code>unused</code> is non-<code>null</code>. It is not valid to
    * call this method with a foreign identifier string for {@IdentifierFactoryType#COUNTING} {@link IdentifierTracker}
    * implementations.
    */

   @Override
   synchronized public Identifier create(String unused) {

      if (Objects.nonNull(unused)) {
         throw new IllegalStateException();
      }

      this.stringBuilder.setLength(0);

      //@formatter:off
      this.stringBuilder
         .append( this.identifierType.getIdentifierPrefix() )
         .append( "-" )
         .append( Long.toString( this.identifierCount, 10 ) );
      //@formatter:on

      return new Identifier(this.stringBuilder.toString(), this.identifierCount++, this.identifierType);
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalStateException, it is not valid to call this method for
    * {@IdentifierFactoryType#COUNTING} {@link IdentifierTracker} implementations.
    */

   @Override
   public Optional<Identifier> getPrimaryIdentifierByForeignIdentifierString(String identifierString) {
      throw new IllegalStateException();
   }

}

/* EOF */
