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

import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;

/**
 * Class implements a unique identifier for each {@link GroveThing} representing a part of the Synchronization Artifact
 * DOM.
 */

public class Identifier {

   /**
    * Saves the string representation of the identifier.
    */

   private final String identifierText;

   /**
    * Saves the type ({@link IdentifierType}) of {@link GroveThing} the {@link Identifier} is for.
    */

   private final IdentifierType identifierType;

   /**
    * Saves the pre-computed hash code for the {@link Identifier}.
    */

   private final int hashCode;

   /**
    * Saves the numerical portion of the identifier.
    */

   private final Long identifierCount;

   /**
    * Creates a new {@link Identifier} object. The constructor is package private. Instances of the class should be
    * created using the {@link IdentifierFactory}.
    *
    * @param text the Synchronization Artifact textual representation for the identifier.
    * @param count the numerical portion of the identifier.
    * @param type the {@link IdentifierType} associated with the identifier.
    */

   Identifier(String text, Long count, IdentifierType type) {
      this.identifierText = text;
      this.identifierType = type;
      this.identifierCount = count;

      this.hashCode = (int) ((((type.ordinal() << 5) - type.ordinal()) << 24) ^ (count * count - count));
   }

   /**
    * Tests the {@link Identifier} with another {@link Object} for equality. The other {@link Object} must be non-null,
    * have the same associated {@link IdentifierType} and the same numeric identifier to be equal.
    *
    * @return <code>true</code>, if the provided object is equal; otherwise, <code>false</code>.
    */

   @Override
   public boolean equals(Object obj) {
      //@formatter:off
      return
            ( obj != null)
         && ( obj instanceof Identifier )
         && ( this.identifierCount == ((Identifier) obj).identifierCount )
         && ( this.identifierType  == ((Identifier) obj).identifierType  );
      //@formatter:on
   }

   /**
    * Gets the numeric portion of the identifier as a <code>long</code>.
    *
    * @return numeric portion of the identifier.
    */

   public long getCount() {
      return this.identifierCount;
   }

   /**
    * Gets the text representation of the identifier for use in the Synchronization Artifact.
    *
    * @return the {@link Identifier} String representation.
    */

   String getText() {
      return this.identifierText;
   }

   /**
    * Gets the identifier type.
    *
    * @return the associated {@link IdentifierType}.
    */

   public IdentifierType getType() {
      return this.identifierType;
   }

   /**
    * Gets the {@link Identifier} object's pre-computed hash code.
    *
    * @return a hash code for the {@link Identifier}.
    */

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   /**
    * Predicate to determine if the {@link IdentifierType} of this {@link Identifier} is a part of the specified
    * {@link IdentifierTypeGroup}.
    *
    * @param identifierTypeGroup the identifier type group to check for membership in.
    * @return <code>true</code> when the {@link IdentifierType} of this {@link Identifier} is a member of the specified
    * {@link IdentifierTypeGroup}; otherwise, <code>false</code>.
    */

   public boolean isInGroup(IdentifierTypeGroup identifierTypeGroup) {
      return this.identifierType.isInGroup(identifierTypeGroup);
   }

   /**
    * Predicate to determine if the {@link IdentifierType} of the {@link Identifier} is the specified
    * {@link IdentifierType}.
    *
    * @param identifierType the {@link IdentifierType} to check
    * @return <code>true</code>, when the {@link IdentifierType} of this {@link Identifier} matches the specified
    * {@link IdentifierType}; otherwise, <code>false</code>.
    */

   public boolean isType(IdentifierType identifierType) {
      return this.identifierType.equals(identifierType);
   }

   /**
    * Get a {@link String} representation of the {@link Identifier} for debugging purposes. There is no contract for the
    * format of the returned string.
    *
    * @return a {@link String} representation of the {@link Identifier}.
    */

   @Override
   public String toString() {
      return this.identifierText;
   }
}

/* EOF */
