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

package org.eclipse.osee.define.rest.api.synchronization;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * <p>
 * The data structure used to request a Synchronization Artifact from the Synchronization Service. The following
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
 * <h2>Specification Roots</h2>
 * <p>
 * Synchronization Artifacts may contain one or more Specifications. Each Specification is generated from an OSEE
 * artifact. The hierarchical children of that artifact will be exported into the Synchronization Artifact as Spec
 * Objects belonging to the Specification.
 * </p>
 * The root OSEE artifacts for the Synchronization Artifact Specifications are specified as OSEE Branch Identifier
 * ({@link BranchId}) and OSEE Artifact Identifier ({@link ArtifactId}) pairs. An OSEE Branch Identifier may also
 * include an OSEE View Identifier.
 *
 * @author Loren K. Ashley
 */

public class ExportRequest implements ToMessage {

   /**
    * {@link String} descriptor for the type of Synchronization Artifact to export.
    */

   private String synchronizationArtifactType;

   /**
    * Array of OSEE Artifact specifications to be included as Specifications in the Synchronization Artifact.
    */

   private Root[] roots;

   /**
    * Creates a new empty {@link ExportRequest} for JSON deserialization.
    */

   public ExportRequest() {
      this.synchronizationArtifactType = null;
      this.roots = null;
   }

   /**
    * Creates a new {@link ExportRequset} with data for serialization (client) or for making a Synchronization Artifact
    * service call (server).
    *
    * @param synchronizationArtifactType the {@link String} descriptor for the type of Synchronization Artifact being
    * requested.
    * @param roots an Array of {@link Root} objects specifying the OSEE Artifacts that will be represented as
    * Specifications in the generated Synchronization Artifact.
    * @throws NullPointerException when either of the parameters <code>synchronizationArtifactType</code> or
    * <code>roots</code> is <code>null</code>.
    */

   public ExportRequest(String synchronizationArtifactType, Root[] roots) {
      this.synchronizationArtifactType = Objects.requireNonNull(synchronizationArtifactType,
         "ExportRequest::new, the parameter \"synchronizationArtifactType\" is null.");
      this.roots = Objects.requireNonNull(roots, "ExportRequest::new, the parameter \"roots\" is null.");
   }

   /**
    * Gets the array of OSEE Artifact specifications that are requested as Specifications for the Synchronization
    * Artifact.
    *
    * @return the array of OSEE Artifact specifications.
    * @throws IllegalStateException when an attempt is made to get the {@link #roots} for an {@link ExportRequest} where
    * the array of {@link Root} objects has not been set.
    */

   public Root[] getRoots() {
      if (Objects.isNull(this.roots)) {
         //@formatter:off
         var message =
            new Message()
                   .title( "ExportRequest::getRoots, the member \"roots\" has not been set." )
                   .blank()
                   ;
         //@formatter:on
         this.toMessage(1, message);
         throw new IllegalStateException(message.toString());
      }
      return this.roots;
   }

   /**
    * Gets the requested Synchronization Artifact type.
    *
    * @return the requested Synchronization Artifact type.
    * @throws IllegalStateException when an attempt is made to get the {@link #synchronizationArtifactType} for an
    * {@link ExportRequest} where the descriptor has not been set.
    */

   public String getSynchronizationArtifactType() {
      if (Objects.isNull(this.synchronizationArtifactType)) {
         //@formatter:off
         var message =
            new Message()
                   .title( "ExportRequest::getSynchronizationArtifactType, the member \"synchronizationArtifactType\" has not been set." )
                   .blank()
                   ;
         //@formatter:on
         this.toMessage(1, message);
         throw new IllegalStateException(message.toString());
      }
      return synchronizationArtifactType;
   }

   /**
    * Predicate to test the validity of the {@link ExportRequest} object.
    *
    * @return <code>true</code>, when member {@link #synchronizationArtifactType} is non-<code>null</code>, member
    * {@link #roots} is non-<code>null</code>, and all elements of the {@link #roots} array are valid; otherwise,
    * <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.synchronizationArtifactType )
         && Objects.nonNull( this.roots )
         && !Arrays.stream( this.roots ).anyMatch( Predicate.not( Root::isValid ) );
      //@formatter:on
   }

   /**
    * Sets the array of {@link Root} objects. Used for deserialization.
    *
    * @param roots the array of {@link Root} objects.
    * @throws NullPointerException when the parameter <code>roots</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the {@link #roots} for an {@link ExportRequest}
    * object that already has an array of {@link Root} objects.
    */

   public void setRoots(Root[] roots) {
      if (Objects.nonNull(this.roots)) {
         //@formatter:off
         var message =
            new Message()
                   .title( "ExportRequest::setRoots, the member \"roots\" has already been set." )
                   .blank()
                   ;
         //@formatter:on
         this.toMessage(1, message);
         throw new IllegalStateException(message.toString());
      }
      this.roots = Objects.requireNonNull(roots, "ExportRequest::setRoots, the parameter \"roots\" is null.");
   }

   /**
    * Sets the requested Synchronization Artifact type. Used for deserialization.
    *
    * @param synchronizationArtifactType the requested Synchronization Artifact type.
    * @throws NullPointerException when the parameter <code>synchronizationArtifactType</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the {@link #synchronizationArtifactType} description
    * for an {@link ExportRequest} that already has a descriptor.
    */

   public void setSynchronizationArtifactType(String synchronizationArtifactType) {

      if (Objects.nonNull(this.synchronizationArtifactType)) {
         //@formatter:off
         var message =
            new Message()
                   .title( "ExportRequest::getSynchronizationArtifactType, the member \"synchronizationArtifactType\" has already been set." )
                   .blank()
                   ;
         //@formatter:on
         this.toMessage(1, message);
         throw new IllegalStateException(message.toString());
      }

      this.synchronizationArtifactType = Objects.requireNonNull(synchronizationArtifactType,
         "ExportRequest::new, the parameter \"synchronizationArtifactType\" is null.");
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "ExportRequest" )
         .indentInc()
         .segment( "synchronizationArtifactType", this.synchronizationArtifactType )
         .segmentIndexed( "roots", this.roots )
         .indentDec()
         ;
      //@formatter:on

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