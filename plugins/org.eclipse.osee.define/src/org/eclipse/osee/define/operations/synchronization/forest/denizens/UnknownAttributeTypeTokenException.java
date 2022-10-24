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

package org.eclipse.osee.define.operations.synchronization.forest.denizens;

import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * {@link RuntimeException} which is thrown when an unknown attribute type is requested from an
 * {@link ArtifactReadable}.
 *
 * @author Loren K. Ashley
 */

public class UnknownAttributeTypeTokenException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the attribute type unknown to the
    * {@link ArtifactReadable}.
    *
    * @param artifactReadable the {@link ArtifactReadable} an unknown attribute type was requested from.
    * @param attributeTypeToken the unknown attribute type that was requested.
    */

   public UnknownAttributeTypeTokenException(ArtifactReadable artifactReadable, AttributeTypeToken attributeTypeToken) {
      //@formatter:off
      super
         (
            new StringBuilder( 2 * 1024 )
               .append( "\n" )
               .append( "Requested attribute value for unknown attribute type." ).append( "\n" )
               .append( "   ArtifactReadable Identifier and Name:   " ).append( artifactReadable.getId()   ).append( "( \"" ).append( artifactReadable.getName()   ).append( "\" )" ).append( "\n" )
               .append( "   AttributeTypeToken Identifier and Name: " ).append( attributeTypeToken.getId() ).append( "( \"" ).append( attributeTypeToken.getName() ).append( "\" )" ).append( "\n" )
               .append( "   ArtifactReadable Follows:" ).append( "\n" )
               .append( artifactReadable ).append( "\n" )
               .append( "   AttributeTypeToken Follows:" ).append( "\n" )
               .append( attributeTypeToken ).append( "\n" )
               .toString()
         );
      //@formatter:on
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the attribute type unknown to the
    * {@link ArtifactReadable}.
    *
    * @param artifactReadable the {@link ArtifactReadable} an unknown attribute type was requested from.
    * @param attributeTypeToken the unknown attribute type that was requested.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public UnknownAttributeTypeTokenException(ArtifactReadable artifactReadable, AttributeTypeToken attributeTypeToken, Throwable cause) {
      this(artifactReadable, attributeTypeToken);

      this.initCause(cause);
   }

}

/* EOF */