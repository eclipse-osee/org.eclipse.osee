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

package org.eclipse.osee.synchronization.rest;

import java.util.Objects;
import org.eclipse.osee.synchronization.rest.forest.AttributeDefinitionGroveThing;
import org.eclipse.osee.synchronization.rest.forest.AttributeValueGroveThing;
import org.eclipse.osee.synchronization.rest.forest.DataTypeDefinitionGroveThing;
import org.eclipse.osee.synchronization.rest.forest.EnumValueGroveThing;
import org.eclipse.osee.synchronization.rest.forest.SpecObjectGroveThing;
import org.eclipse.osee.synchronization.rest.forest.SpecObjectTypeGroveThing;
import org.eclipse.osee.synchronization.rest.forest.SpecTypeGroveThing;
import org.eclipse.osee.synchronization.rest.forest.SpecificationGroveThing;

/**
 * An enumeration of the parts of a Synchronization Artifact. The members of the enumeration also serve as a factory for
 * creating the objects that represent that part of a Synchronization Artifact. Thread local storage is used to build
 * and track the identifier counts. Multiple threads can build Synchronization Artifacts concurrently. However, only one
 * thread should be used to build a single Synchronization Artifact.
 *
 * @author Loren.K.Ashley
 */

public enum IdentifierType {

   /**
    * The {@link IdentifierType} for {@link AttributeDefinitionGroveThing} Synchronization Artifact things.
    */

   ATTRIBUTE_DEFINITION("AD"),

   /**
    * The {@link IdentifierType} for {@link AttributeValueGroveThing} Synchronization Artifact things.
    */

   ATTRIBUTE_VALUE("AV"),

   /**
    * The {@link IdentifierType} for {@link DataTypeDefinitionGroveThing} Synchronization Artifact things.
    */

   DATA_TYPE_DEFINITION("DD"),

   /**
    * The {@link IdentifierType} for {@link EnumValueGroveThing} Synchronization Artifact things.
    */

   ENUM_VALUE("EV"),

   /**
    * The {@link IdentifierType} for {@link ReqIfHeader} Synchronization Artifact things.
    */

   HEADER("H"),

   /**
    * The {@link IdentifierType} for {@link SpecificationGroveThing} Synchronization Artifact things.
    */

   SPECIFICATION("S"),

   /**
    * The {@link IdentifierType} for {@link SpecTypeGroveThing} Synchronization Artifact things.
    */

   SPECIFICATION_TYPE("ST"),

   /**
    * The {@link IdentifierType} for {@link SpecObjectGroveThing} Synchronization Artifact things.
    */

   SPEC_OBJECT("SO"),

   /**
    * The {@link IdentifierType} for {@link SpecObjectTypeGroveThing} Synchronization Artifact things.
    */

   SPEC_OBJECT_TYPE("SOT");

   /**
    * Class implements a unique identifier for each object representing a part of the Synchronization Artifact. The
    * class is a nested static class to limit access to the constructor.
    */

   public static class Identifier {

      /**
       * Saves the string representation of the identifier.
       */

      private final String identifierText;

      /**
       * Save the {@link IdentifierType} of the {@link Identifier}.
       */

      private final IdentifierType identifierType;

      /**
       * Saves the pre-computed hash code for the {@link Identifier}.
       */

      private final int hashCode;

      /**
       * Save the numerical portion of the identifier.
       */

      private final Long identifierCount;

      /**
       * Creates a new {@link Identifier} object.
       *
       * @param text the Synchronization Artifact textual representation for the identifier.
       * @param count the numerical portion of the identifier.
       * @param type the {@link IdentifierType} associated with the identifier.
       */

      private Identifier(CharSequence text, Long count, IdentifierType type) {
         this.identifierText = text.toString();
         this.identifierType = type;
         this.identifierCount = count;

         this.hashCode = (int) ((type.ordinal() << 24) ^ ((count >> 32) & 0xFFFFFFFF) ^ (count & 0xFFFFFFFF));
      }

      /**
       * Tests the {@link Identifier} with another {@link Object} for equality. The other {@link Object} must be
       * non-null, have the same associated {@link IdentifierType} and the same numeric identifier to be equal.
       *
       * @return <code>true</code>, if the provided object is equal; otherwise, <code>false</code>.
       */

      @Override
      public boolean equals(Object obj) {
         return (obj != null) && (obj instanceof Identifier) && (this.identifierCount == ((Identifier) obj).identifierCount) && (this.identifierType == ((Identifier) obj).identifierType);
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
       * Get a {@link String} representation of the {@link Identifier} for debugging purposes. There is no contract for
       * the format of the returned string.
       *
       * @return a {@link String} representation of the {@link Identifier}.
       */

      @Override
      public String toString() {
         return this.identifierText;
      }
   }

   /**
    * Thread local storage to track the number of identifiers produced.
    */

   private ThreadLocal<Long> identifierCount;

   /**
    * The string prefix used for identifier of the {@link IdentifierType}.
    */

   private String identifierPrefix;

   /**
    * Thread local storage used for building the identifier strings.
    */

   private ThreadLocal<StringBuilder> stringBuilder;

   /**
    * Constructor for the enumeration members.
    *
    * @param identifierPrefix the string prefix used for identifiers of the type being constructed.
    * @param factory a factory method used to produce the Synchronization Artifact thing associated with the type being
    * constructed.
    */

   private IdentifierType(String identifierPrefix) {
      assert Objects.nonNull(identifierPrefix);

      this.identifierPrefix = identifierPrefix;

      this.identifierCount = new ThreadLocal<Long>() {
         @Override
         protected Long initialValue() {
            return 0L;
         }
      };

      this.stringBuilder = new ThreadLocal<StringBuilder>() {
         @Override
         protected StringBuilder initialValue() {
            return new StringBuilder(32);
         }
      };

   }

   /**
    * Creates a new unique {@link Identifier} for the Synchronization Artifact thing associated with the
    * {@link IdentifierType}. This method is intended to only be called by the Synchronization Artifact thing
    * constructors.
    *
    * @return a new {@link Identifier}.
    */

   public Identifier createIdentifier() {
      var identifierCount = this.identifierCount.get();
      var stringBuilder = this.stringBuilder.get();

      stringBuilder.setLength(0);

      //@formatter:off
      stringBuilder
         .append( this.identifierPrefix )
         .append( "-" )
         .append( Long.toString( identifierCount, 10 ) );
      //@formatter:on

      var identifier = new Identifier(stringBuilder, identifierCount++, this);

      this.identifierCount.set(identifierCount);

      return identifier;
   }

}

/* EOF */
