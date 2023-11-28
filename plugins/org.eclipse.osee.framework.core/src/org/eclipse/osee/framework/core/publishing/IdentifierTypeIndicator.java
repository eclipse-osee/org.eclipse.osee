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

package org.eclipse.osee.framework.core.publishing;

import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * Enumeration used to indicate the type of an identifier which can be an {@link ArtifactId}, GUID {@link String}, or an
 * ambiguous {@link String}.
 *
 * @author Loren K. Ashley
 */

public enum IdentifierTypeIndicator {

   /**
    * Indicates the identifier is an {@link ArtifactId}, a {@link String} that represents an {@link ArtifactId}, or a
    * {@link String} GUID.
    */

   AMBIGUOUS,

   /**
    * Indicates the identifier is an {@link ArtifactId} instance.
    */

   ARTIFACT_ID,

   /**
    * Indicates the identifier is a {@link String} representation of an {@link ArtifactId}.
    */

   ARTIFACT_ID_STRING,

   /**
    * Indicates the identifier is a GUID {@link String}.
    */

   GUID,

   /**
    * Indicates the ambiguous identifier is not an instance of an {@link ArtifactId} and not an instance of a
    * {@link String} that meets the constraints for either an {@link ArtifactId} or a GUID.
    */

   UNKNOWN;

   /**
    * Parse the <code>ambiguousIdentifierString</code> to determine its {@link IdentifierTypeIndicator}.
    *
    * @param ambiguousIdentifierString the string to be analyzed.
    * @return as follows when the <code>ambiguousIdentifierString</code>:
    * <dl>
    * <dt>Contains only digits:</dt>
    * <dd>{@link IdentifierTypeIndicator#ARTIFACT_ID_STRING}.</dd>
    * <dt>Contains only the characters that are valid for GUID:</dt>
    * <dd>{@link IdentifierTypeIndicator#GUID}.</dd>
    * <dt>Does not meet the constrains for an {@link ArtifactId} or a GUID:</dt>
    * <dd>{@link IdentifierTypeIndicator#UNKNOWN}.</dd>
    * </dl>
    */

   public static IdentifierTypeIndicator determine(Object ambiguousIdentifier) {
      //@formatter:off
         return
            ( ambiguousIdentifier instanceof ArtifactId )
               ? ARTIFACT_ID
               : (ambiguousIdentifier instanceof String )
                    ? WordCoreUtil.isLinkReferenceAnArtifactId( (String) ambiguousIdentifier )
                         ? ARTIFACT_ID_STRING
                         : WordCoreUtil.isLinkReferenceAnGuid( (String) ambiguousIdentifier )
                             ? GUID
                             : UNKNOWN
                    : UNKNOWN;
         //@formatter:on
   }

   /**
    * Predicate to test if the enumeration member is {@link IdentifierTypeIndicator#AMBIGUOUS}.
    *
    * @return <code>true</code> when the member is {@link IdentifierTypeIndicator#AMBIGUOUS}; otherwise
    * <code>false</code>.
    */

   public boolean isAmbiguous() {
      return this == AMBIGUOUS;
   }

   /**
    * Predicate to test if the enumeration member is {@link IdentifierTypeIndicator#ARTIFACT_ID}.
    *
    * @return <code>true</code> when the member is {@link IdentifierTypeIndicator#ARTIFACT_ID}; otherwise
    * <code>false</code>.
    */

   public boolean isArtifactId() {
      return this == ARTIFACT_ID;
   }

   /**
    * Predicate to test if the enumeration member is {@link IdentifierTypeIndicator#ARTIFACT_ID_STRING}.
    *
    * @return <code>true</code> when the member is {@link IdentifierTypeIndicator#ARTIFACT_ID_STRING}; otherwise
    * <code>false</code>.
    */

   public boolean isArtifactIdString() {
      return this == ARTIFACT_ID_STRING;
   }

   /**
    * Predicate to test if the enumeration member is {@link IdentifierTypeIndicator#GUID}.
    *
    * @return <code>true</code> when the member is {@link IdentifierTypeIndicator#GUID}; otherwise <code>false</code>.
    */

   public boolean isGuid() {
      return this == GUID;
   }

   /**
    * Predicate to test if the enumeration member is {@link IdentifierTypeIndicator#UNKNOWN}.
    *
    * @return <code>true</code> when the member is {@link IdentifierTypeIndicator#UNKNOWN}; otherwise
    * <code>false</code>.
    */

   public boolean isUnknown() {
      return this == UNKNOWN;
   }
}

/* EOF */
