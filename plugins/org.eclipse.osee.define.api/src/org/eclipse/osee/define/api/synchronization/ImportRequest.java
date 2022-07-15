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

package org.eclipse.osee.define.api.synchronization;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.IndentedString;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * <p>
 * The data structure used to request the Synchronization Service to import a Synchronization Artifact. The following
 * sections describe the required information to make a request.
 * </p>
 * <h2>Synchronization Artifact Type</h2>
 * <p>
 * The Synchronization Artifact Service can produce artifacts of different type. Each Synchronization Artifact Type has
 * a unique string descriptor. The following types are supported:
 * </p>
 * <dl>
 * <dt>reqif
 * <dd>A Synchronization Artifact conforming to the Requirements Interchange Format (ReqIF) Version 1.2 will be
 * generated. The ReqIF specification can be found at
 * <a href="https://www.omg.org/spec/ReqIF/1.2">https://www.omg.org/spec/ReqIF/1.2</a></dd>
 * </dl>
 * <h2>Import Mappings</h2>
 * <p>
 * Synchronization Artifacts may contain one or more Specifications. Each Specification contains a tree of Spec Objects
 * belonging to the Specification. An Import Mapping provides the association between each Synchronization Artifact
 * Specification to be imported and the OSEE artifact the Synchronization Artifact Specification's Spec Objects will be
 * imported under.
 * </p>
 * <p>
 * The Synchronization Artifact Specifications are specified with their Synchronization Artifact Specification
 * identifier. The table below describes how to locate the identifier for each type of Synchronization Artifact.
 * </p>
 * <table border="1" style="border-collapse:collapse">
 * <caption>Synchronization Artifact Specification Identifier Location</caption>
 * <tr>
 * <th>Synchronization Artifact Type</th>
 * <th>Specification Identifier Location</th>
 * </tr>
 * <tr>
 * <td>reqif</td>
 * <td>The Specification Identifier is given by the &quot;IDENTIFIER&quot; attribute of the &quot;SPECIFICATION&quot;
 * tag.</td>
 * </tr>
 * </table>
 * <p>
 * The root OSEE artifacts for the Synchronization Artifact Specifications are specified as OSEE Branch Identifier
 * ({@link BranchId}) and OSEE Artifact Identifier ({@link ArtifactId}) pairs. An OSEE Branch Identifier may also
 * include an OSEE View Identifier.
 * </p>
 *
 * @author Loren K. Ashley
 */

public class ImportRequest implements ToMessage {

   /**
    * {@link String} descriptor for the type of Synchronization Artifact to import.
    */

   private String synchronizationArtifactType;

   /**
    * Array of mapping from Synchronization Artifact Specifications to OSEE artifacts.
    */

   private ImportMapping[] importMappings;

   /**
    * Creates a new empty {@link ImportRequest} for JSON deserialization.
    */

   public ImportRequest() {
      this.synchronizationArtifactType = null;
      this.importMappings = null;
   }

   /**
    * Creates a new {@link ImportRequest} with data for serialization (client) or for making a Synchronization Artifact
    * service call (server).
    *
    * @param synchronizationArtifactType the {@link String} descriptor for the type of Synchronization Artifact to be
    * requested.
    * @param importMappings an Array of {@link ImportMapping} objects specifying the OSEE artifact each Synchronization
    * Artifact Specification is to be associated with.
    * @throws NullPointerException when either of the parameters <code>synchronizationArtifactType</code> or
    * <code>importMappings</code> is <code>null</code>.
    */

   public ImportRequest(String synchronizationArtifactType, ImportMapping[] importMappings) {
      this.synchronizationArtifactType = Objects.requireNonNull(synchronizationArtifactType,
         "ImportRequest::new, the parameter \"synchronizationArtifactType\" is null.");
      this.importMappings =
         Objects.requireNonNull(importMappings, "ImportRequest::new, the parameter \"importMappings\" is null.");
   }

   /**
    * Gets the array of associations between Synchronization Artifact Specifications and OSEE root artifacts.
    *
    * @return the array of {@link ImportMapping} objects.
    * @throws IllegalStateException when an attempt is made to get the {@link #importMappings} for an
    * {@link ImportRequest} where the array of {@link ImportMapping} objects has not been set.
    */

   public ImportMapping[] getImportMappings() {
      if (Objects.isNull(this.importMappings)) {
         //@formatter:off
         var message =
            new StringBuilder( 1024 )
                   .append( "ImportRequest::getImportMappings, the member \"importMappings\" has not been set." ).append( "\n" )
                   .append( "\n" )
                   ;
         //@formatter:on
         this.toMessage(1, message);
         throw new IllegalStateException(message.toString());
      }
      return this.importMappings;
   }

   /**
    * Gets the type of Synchronization Artifact to be imported.
    *
    * @return the string descriptor for the type of Synchronization Artifact to be imported.
    * @throws IllegalStateException when an attempt is made to get the {@link #synchronizationArtifactType} for an
    * {@link ImportRequest} where the descriptor has not been set.
    */

   public String getSynchronizationArtifactType() {
      if (Objects.isNull(this.synchronizationArtifactType)) {
         //@formatter:off
         var message =
            new StringBuilder( 1024 )
                   .append( "ImportRequest::getSynchronizationArtifactType, the member \"synchronizationArtifactType\" has not been set." ).append( "\n" )
                   .append( "\n" )
                   ;
         //@formatter:on
         this.toMessage(1, message);
         throw new IllegalStateException(message.toString());
      }
      return synchronizationArtifactType;
   }

   /**
    * Predicate to test the validity of the {@link ImportRequest} object.
    *
    * @return <code>true</code>, when member {@link #synchronizationArtifactType} is non-<code>null</code>, member
    * {@link #importMappings} is non-<code>null</code>, and all elements of the {@link #importMappings} array are valid;
    * otherwise, <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.synchronizationArtifactType )
         && Objects.nonNull( this.importMappings )
         && !Arrays.stream( this.importMappings ).anyMatch( Predicate.not( ImportMapping::isValid ) );
      //@formatter:on
   }

   /**
    * Sets the array {@link ImportMapping} objects. Used for deserialization.
    *
    * @param importMappings the array of {@link ImportMapping} objects.
    * @throws NullPointerException when the parameter <code>importMappings</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the {@link @importMappings} for an
    * {@link ImportRequest} object that already has an array of {@link ImportMapping} objects.
    */

   public void setImportMappings(ImportMapping[] importMappings) {
      if (Objects.nonNull(this.importMappings)) {
         //@formatter:off
         var message =
            new StringBuilder( 1024 )
                   .append( "ImportRequest::setImportMappings, the member \"importMappings\" has already been set." ).append( "\n" )
                   .append( "\n" )
                   ;
         //@formatter:on
         this.toMessage(1, message);
         throw new IllegalStateException(message.toString());
      }
      this.importMappings =
         Objects.requireNonNull(importMappings, "ImportRequest::new, the parameter \"importMappings\" is null.");
   }

   /**
    * Sets the requested Synchronization Artifact type. Used for deserialization.
    *
    * @param synchronizationArtifactType the string descriptor for the Synchronization Artifact type to be imported.
    * @throws NullPointerException when the parameters <code>synchronizationArtifactType</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the {@link #synchronizationArtifactType} description
    * for an {@link ImportRequest} that already has a descriptor.
    */

   public void setSynchronizationArtifactType(String synchronizationArtifactType) {
      if (Objects.nonNull(this.synchronizationArtifactType)) {
         //@formatter:off
         var message =
            new StringBuilder( 1024 )
                   .append( "ImportRequest::setSynchronizationArtifactType, the member \"synchronizationArtifactType\" has already been set." ).append( "\n" )
                   .append( "\n" )
                   ;
         //@formatter:on
         this.toMessage(1, message);
         throw new IllegalStateException(message.toString());
      }
      this.synchronizationArtifactType = Objects.requireNonNull(synchronizationArtifactType,
         "ImportRequest::new, the parameter \"synchronizationArtifactType\" is null.");
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);
      var indent1 = IndentedString.indentString(indent + 1);

      //@formatter:off
      outMessage
         .append( indent0 ).append( "ImportRequest:" ).append( "\n" )
         .append( indent1 ).append( "synchronizationArtifactType: " ).append( Objects.nonNull( this.synchronizationArtifactType ) ? this.synchronizationArtifactType : "(null)" ).append( "\n" )
         .append( indent1 ).append( "importMappings:              " )
         ;
      //@formatter:on

      if (Objects.nonNull(this.importMappings)) {
         outMessage.append("\n");
         Arrays.stream(this.importMappings).forEach((importMapping) -> importMapping.toMessage(2, outMessage));
      } else {
         outMessage.append("(null)").append("\n");
      }
      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}

/* EOF */