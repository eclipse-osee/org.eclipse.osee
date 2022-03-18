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

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ToMessage;

public enum NativeDataType implements Id, ToMessage {

   //@formatter:off
   ARTIFACT_IDENTIFIER
      (
         "OSEE Artifact Identifier Datatype"
      ),

   BRANCH_IDENTIFIER
      (
         "OSEE Branch Identifier Datatype"
      ),

   BOOLEAN
      (
         "OSEE Boolean Datatype"
      ),

   DATE
     (
        "OSEE Date Datatype"
     ),

   DOUBLE
      (
         "OSEE Double Datatype"
      ),

   ENUMERATED
      (
         "OSEE Enumerated Datatype"
      ),

   INPUT_STREAM
      (
         "OSEE Input Stream Datatype"
      ),

   INTEGER
      (
         "OSEE Integer Datatype"
      ),

   JAVA_OBJECT
      (
         "OSEE Java Object Datatype"
      ),

   LONG
      (
         "OSEE Long Datatype"
      ),

   STRING
      (
         "OSEE String Datatype"
      ),

   URI
      (
         "OSEE URI Datatype"
      );
   //@formatter:on

   /**
    * Member saves a descriptive string for the data type.
    */

   private String description;

   private NativeDataType(String description) {
      this.description = description;
   }

   static NativeDataType classifyNativeDataType(AttributeTypeToken attributeTypeToken) {
      if (attributeTypeToken.isString()) {
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
         .append( indent0 ).append( this.description ).append( "\n" )
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
