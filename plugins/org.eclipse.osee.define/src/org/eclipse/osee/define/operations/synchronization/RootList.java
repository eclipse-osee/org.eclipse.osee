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

package org.eclipse.osee.define.operations.synchronization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.define.api.synchronization.Root;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.MultipleItemsExist;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;
import org.eclipse.osee.framework.jdk.core.util.Validation;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Class to encapsulate a list of the branch and artifact identifiers of the native OSEE things that will be the
 * SpecificationGroveThing roots in the Synchronization Artifact.
 */

public class RootList implements Iterable<ArtifactReadable>, ToMessage {

   /**
    * Once the {@link #validate} method has been invoked and completed successfully, this member will contain a
    * {@link List} of {@link ArtifactReadable} objects for each of the specified document roots.
    */

   private List<ArtifactReadable> artifactReadableRoots;

   /**
    * The export or import direction of the Synchronization Artifact operation.
    */

   private final Direction direction;

   /**
    * The handle to the ORCS OSEE API.
    */

   private final OrcsApi orcsApi;

   /**
    * The handle to the ORCS Token Service.
    */

   private final OrcsTokenService orcsTokenService;

   /**
    * A list of the artifact tree roots for artifacts to be included in the synchronization artifact.
    */

   private final List<Root> rootsList;

   /**
    * The synchronization artifact builder to use for the operation.
    */

   private final SynchronizationArtifactBuilder synchronizationArtifactBuilder;

   /**
    * Creates a new empty {@link RootsList}, sets the {@link Direction}, {@link SynchronizationArtifactBuilder} and the
    * {@link OrcsApi}.
    *
    * @param a handle to the ORCS OSEE API.
    * @parameter direction the export or import direction of the Synchronization Artifact operation.
    * @param synchronizationArtifactType the type of synchronization artifact to be produced.
    */

   private RootList(OrcsApi orcsApi, Direction direction, SynchronizationArtifactBuilder synchronizationArtifactBuilder) {
      this.orcsApi = orcsApi;
      this.orcsTokenService = orcsApi.tokenService();
      this.direction = direction;
      this.synchronizationArtifactBuilder = synchronizationArtifactBuilder;
      this.rootsList = new ArrayList<Root>();
      this.artifactReadableRoots = null;
   }

   /**
    * Factory method to create an object implementing the {@link RootList} interface for a Synchronization Artifact.
    *
    * @param orcsApi a handle to the {@link OrcsApi} used to obtain the OSEE artifacts for the Synchronization Artifact.
    * @parameter direction the export or import direction of the Synchronization Artifact operation.
    * @param synchronizationArtifactType a {@link String} identifier for the type of Synchronization Artifact to be
    * used.
    * @return an object implementing the {@link RootList} interface with the provided parameters encapsulated.
    * @throws NullPointerException when either of the parameters <code>orcsApi</code> or
    * <code>synchronizationArtifactBuilder</code> are <code>null</code>.
    */

   public static RootList create(OrcsApi orcsApi, Direction direction, SynchronizationArtifactBuilder synchronizationArtifactBuilder) {

      Objects.requireNonNull(orcsApi, "RootList::create, parameter \"orcsApi\" is null.");

      Objects.requireNonNull(orcsApi.tokenService(),
         "SynchronizationArtifact::create, Failed to initialize ORCS Token Service is null.");

      Objects.requireNonNull(direction, "RootList::create, parameter \"direction\" is null.");

      Objects.requireNonNull(synchronizationArtifactBuilder,
         "RootList::create, parameter \"synchronizationArtifactBuilder\" is null.");

      return new RootList(orcsApi, direction, synchronizationArtifactBuilder);
   }

   /**
    * Adds a {@link Root} object representing a document root to the list.
    *
    * @param root the {@link Root} object to be added.
    * @throws IllegalStateException when this method is called after the {@link #validate} method has been called.
    */

   public void add(Root root) {

      if (Objects.nonNull(this.artifactReadableRoots)) {
         throw new IllegalStateException(
            "Attempt to add an additional document root after the list has been validated.\n");
      }

      this.rootsList.add(root);
   }

   /**
    * Gets the {@link Direction} of the Synchronization Artifact operation.
    *
    * @return the {@link Direction}.
    */

   public Direction getDirection() {
      return this.direction;
   }

   /**
    * Gets the {@link OrcsApi} handle.
    *
    * @return the {@link OrcsApi}.
    */

   public OrcsApi getOrcsApi() {
      return this.orcsApi;
   }

   /**
    * Gets the {@link OrcsTokenService} handle.
    *
    * @return the {@link OrcsTokenService}.
    */
   public OrcsTokenService getOrcsTokenService() {
      return this.orcsTokenService;
   }

   /**
    * Gets the type of synchronization artifact builder to be used for the operation.
    *
    * @return the synchronization artifact builder.
    */

   public SynchronizationArtifactBuilder getSynchronizationArtifactBuilder() {
      return this.synchronizationArtifactBuilder;
   }

   /**
    * Returns an {@link Iterator} of the {@link ArtifactReadable} objects representing the document roots.
    *
    * @throws IllegalStateException when this method is called before the {@link #validate} method has been called.
    */

   @Override
   public Iterator<ArtifactReadable> iterator() {

      if (Objects.isNull(this.artifactReadableRoots)) {
         throw new IllegalStateException("Attempt to iterate the RootList before validation.\n");
      }

      return this.artifactReadableRoots.iterator();
   }

   /**
    * Gets a {@link Stream} of the document root {@link ArtifactReadable} objects on the list.
    *
    * @return {@link Stream} of {@link ArtifactReadable} objects.
    * @throws IllegalStateException when this method is called before the {@link #validate} method has been called.
    */

   public Stream<ArtifactReadable> stream() {

      if (Objects.isNull(this.artifactReadableRoots)) {
         throw new IllegalStateException("Attempt to iterate the RootList before validation.\n");
      }

      return this.artifactReadableRoots.stream();
   }

   /**
    * Validates the specified document root is a valid OSEE artifact.
    *
    * @param root the document {@link Root} to validate.
    * @param message validation error messages are appended to this {@link Message}.
    * @return the document root OSEE artifact as an {@link ArtifactReadable}.
    * @throws Error when building the Query for the document root fails.
    */

   private ArtifactReadable validateRoot(Root root, Message message) {

      try {

         /*
          * Get the native OSEE root object for the specification
          */

         //@formatter:off
         ResultSet<ArtifactReadable> artifactReadableSet =
            this.orcsApi
               .getQueryFactory()
               .fromBranch( root.getBranchId() )
               .andId( root.getArtifactId() )
               .getResults();
         //@formatter:on

         return artifactReadableSet.getExactlyOne();

      } catch (ItemDoesNotExist | MultipleItemsExist e) {

         //@formatter:off
         message
            .title("The document root is not a valid OSEE Artifact.")
            .blank()
            ;

         root.toMessage(1, message);

         message
            .blank()
            .title( "Reason" )
            .indentInc()
            .block( e.getMessage() )
            .indentDec()
            ;
         //@formatter:on

         return null;

      } catch (Exception e) {

         //@formatter:off
         var eMessage =
            new Message()
               .title( "RootList::validateRoot, Failed to build ORCS query for document root." )
               .blank()
               ;
         //@formatter:on

         root.toMessage(1, eMessage);
         throw new Error(eMessage.toString(), e);
      }
   }

   /**
    * Validates the specified document roots are valid OSEE artifacts.
    *
    * @throws BadDocumentRootException when a single OSEE artifact cannot be found for a document root.
    * @throws IllegalStateException when this method is called for a {@link RootList} that has already been validated.
    */

   public void validate() {

      if (Objects.nonNull(this.artifactReadableRoots)) {
         throw new IllegalStateException("Attempt to validate a RootList that has already been validated.\n");
      }

      Message message = null;

      //@formatter:off
      Validation.requireNonNull
         (
            message,
            this.orcsApi,
            Validation.ValueType.MEMBER,
            "RootList",
            "validate",
            "orcsApi"
         );

      Validation.requireNonNull
         (
            message,
            this.orcsTokenService,
            Validation.ValueType.MEMBER,
            "RootList",
            "validate",
            "orcsTokenService"
         );

      Validation.requireNonNull
         (
            message,
            this.direction,
            Validation.ValueType.MEMBER,
            "RootList",
            "validate",
            "direction"
         );

      Validation.requireNonNull
         (
            message,
            this.synchronizationArtifactBuilder,
            Validation.ValueType.MEMBER,
            "RootList",
            "validate",
            "synchronizationArtifactBuilder"
         );

      if( Objects.nonNull( message) ) {
         throw
            new IllegalArgumentException
                   (
                      Validation.buildIllegalArgumentExceptionMessage
                         (
                            this.getClass().getSimpleName(),
                            "validate",
                            message
                         )
                   );
      }
      //@formatter:on

      var validationMessage = new Message();

      //@formatter:off
      this.artifactReadableRoots =
         this.rootsList.stream()
            .map( ( root ) -> this.validateRoot( root, validationMessage ) )
            .filter( Objects::nonNull )
            .collect( Collectors.toList() );
      //@formatter:on

      if (!validationMessage.isEmpty()) {

         throw new BadDocumentRootException(this.direction, validationMessage.toString());
      }
   }

   /**
    * Adds a textual message to the provided {@link StringBuilder} or a new {@link StringBuilder} representing the list
    * of OSEE root artifacts. The message is formatted as follows:
    * <ul style="list-style-type:none">
    * <li>"BranchId(" &lt;branch-id&gt; ") ArtifactId(" &lt;artifact-id&gt; ")" { "," "BranchId(" &lt;branch-id&gt; ")
    * ArtifactId(" &lt;artifact-id&gt; ")" }</li>
    * </ul>
    *
    * @param message when not null the message is appended to this {@link StringBuilder}.
    * @return the provided {@link StringBuilder} when not null; otherwise, a new {@link StringBuilder}.
    */

   public StringBuilder toText(StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      boolean first = true;
      this.rootsList.stream().forEach(root -> {
         if (!first) {
            outMessage.append(", ");
         }
         root.toText(outMessage);
      });

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = (message != null) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "Root List" )
         .indentInc()
         .segmentIndexedList( "Roots List", this.rootsList )
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
