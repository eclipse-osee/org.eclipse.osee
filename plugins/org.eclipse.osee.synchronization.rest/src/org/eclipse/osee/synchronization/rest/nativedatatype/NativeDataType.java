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

package org.eclipse.osee.synchronization.rest.nativedatatype;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ToMessage;

/**
 * Enumeration of the OSEE native data type categories that can be determined using predicates defined by the OSEE
 * native {@link AttributeTypeToken} interface.
 *
 * @author Loren K. Ashley
 */

public enum NativeDataType implements Id, ToMessage {

   //@formatter:off

   /**
    *  An integer data type category for OSEE artifact identifier attributes.
    */

   ARTIFACT_IDENTIFIER
      (
         "OSEE Artifact Identifier Datatype"
      ),

   /**
    * An integer data type category for OSEE branch identifier attributes.
    */

   BRANCH_IDENTIFIER
      (
         "OSEE Branch Identifier Datatype"
      ),

   /**
    * A Boolean data type category for OSEE Boolean attributes.
    */

   BOOLEAN
      (
         "OSEE Boolean Datatype"
      ),

   /**
    * A calendar data type category for OSEE date and time attributes.
    */

   DATE
     (
        "OSEE Date Datatype"
     ),

   /**
    * A floating point category for OSEE floating point attributes.
    */

   DOUBLE
      (
         "OSEE Double Datatype"
      ),

   /**
    * A enumeration category for OSEE enumerated attributes.
    */

   ENUMERATED
      (
         "OSEE Enumerated Datatype"
      ),

   /**
    * Unknown
    */

   INPUT_STREAM
      (
         "OSEE Input Stream Datatype"
      ),

   /**
    * An integer data type category for OSEE integer attributes.
    */

   INTEGER
      (
         "OSEE Integer Datatype"
      ),

   /**
    * Unknown
    */

   JAVA_OBJECT
      (
         "OSEE Java Object Datatype"
      ),

   /**
    * An integer data type category for OSEE long attributes.
    */

   LONG
      (
         "OSEE Long Datatype"
      ),

   /**
    * A string data type category for OSEE regular string attributes.
    */

   STRING
      (
         "OSEE String Datatype"
      ),

   /**
    * A string data type category for OSEE string attributes containing Word Markup Language.
    */

   STRING_WORD_ML
      (
         "OSEE String Word ML Datatype"
      ),

   /**
    * A string data type category for OSEE attributes containing universal resource identifiers.
    */

   URI
      (
         "OSEE URI Datatype"
      );

   //@formatter:on

   /**
    * Member saves a descriptive string for the data type.
    */

   private String description;

   /**
    * Constructor for the static enumeration members.
    *
    * @param description the description used for the data type category in the Synchronization Artifact.
    */

   private NativeDataType(String description) {
      this.description = description;
   }

   /**
    * Uses the predicates defined by the OSEE native {@link AttributeTypeToken} interface to categorize the OSEE native
    * data type represented by an object implementing the {@link AttributeTypeToken} interface.
    *
    * @param attributeTypeToken the OSEE native data type to categorize.
    * @return the {@link NativeDataType} enumeration member representing the category of the OSEE native data type.
    */

   public static NativeDataType classifyNativeDataType(AttributeTypeToken attributeTypeToken) {

      if (attributeTypeToken.isString()) {

         switch (attributeTypeToken.getMediaType()) {

            case AttributeTypeToken.APPLICATION_MSWORD:
               return STRING_WORD_ML;

            case AttributeTypeToken.TEXT_URI_LIST:
               return URI;
         }

         return STRING;
      }

      if (attributeTypeToken.isEnumerated()) {
         return ENUMERATED;
      }

      if (attributeTypeToken.isBoolean()) {
         return BOOLEAN;
      }

      if (attributeTypeToken.isDate()) {
         return DATE;
      }

      if (attributeTypeToken.isInteger()) {
         return INTEGER;
      }

      if (attributeTypeToken.isDouble()) {
         return DOUBLE;
      }

      if (attributeTypeToken.isLong()) {
         return LONG;
      }

      if (attributeTypeToken.isArtifactId()) {
         return ARTIFACT_IDENTIFIER;
      }

      if (attributeTypeToken.isBranchId()) {
         return BRANCH_IDENTIFIER;
      }

      if (attributeTypeToken.isInputStream()) {
         return INPUT_STREAM;
      }

      if (attributeTypeToken.isJavaObject()) {
         return JAVA_OBJECT;
      }

      if (attributeTypeToken.isUri()) {
         return URI;
      }

      return STRING;
   }

   /**
    * {@inheritDoc}
    * <p>
    * Gets a unique identifier for each enumeration member.
    *
    * @return the enumeration member's ordinal as a {@link Long}.
    * @implNote The enumeration implements the OSEE native {@link Id} interface so that the enumeration members can be
    * used by the Synchronization Artifact builder as though the enumeration members were native OSEE data type objects.
    */

   @Override
   public Long getId() {
      return (long) this.ordinal();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);

      //@formatter:off
      outMessage
         .append( indent0 ).append( this.name() ).append( ": " ).append( this.description ).append( "\n" )
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.description;
   }

}

/* EOF */
