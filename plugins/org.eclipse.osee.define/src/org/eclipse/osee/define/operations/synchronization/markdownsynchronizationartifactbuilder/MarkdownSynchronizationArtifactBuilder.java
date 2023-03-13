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

package org.eclipse.osee.define.operations.synchronization.markdownsynchronizationartifactbuilder;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.naming.OperationNotSupportedException;
import org.eclipse.osee.define.operations.synchronization.ForeignThingFamily;
import org.eclipse.osee.define.operations.synchronization.IsSynchronizationArtifactBuilder;
import org.eclipse.osee.define.operations.synchronization.RelationshipTerminal;
import org.eclipse.osee.define.operations.synchronization.SynchronizationArtifact;
import org.eclipse.osee.define.operations.synchronization.SynchronizationArtifactBuilder;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.publishingdombuilder.PublishingDomBuilder;

/**
 * Implementation of the {@link SynchronizationArtifactBuilder} interface for building a Mark Down Synchronization
 * Artifact. This implementation does not support import operations. All importing methods will throw an
 * {@link OperationNotSupportedException}.
 *
 * @author Loren K. Ashley
 */

@IsSynchronizationArtifactBuilder(artifactType = "markdown")
public class MarkdownSynchronizationArtifactBuilder implements SynchronizationArtifactBuilder {

   /**
    * Saves an instance of the the general purpose Publishing DOM Builder.
    */

   private PublishingDomBuilder publishingDomBuilder;

   /**
    * Creates a new {@link MarkdownSynchronizationArtifactBuilder}.
    */

   public MarkdownSynchronizationArtifactBuilder() {
      this.publishingDomBuilder = new PublishingDomBuilder(new MarkdownSynchronizationArtifactSerializer());
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean build() {
      return this.publishingDomBuilder.build();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void close() {
      this.publishingDomBuilder.close();
      this.publishingDomBuilder = null;
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public void deserialize(InputStream inputStream) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Optional<String> getAttributeDefinition(GroveThing attributeValueGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Stream<String> getAttributeDefinitions(GroveThing specificationTypeGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Consumer<GroveThing>> getConverter(IdentifierType identifierType) {
      return this.publishingDomBuilder.getConverter(identifierType);
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Optional<String> getDatatypeDefinition(GroveThing attributeDefinitionGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Stream<String> getEnumValues(GroveThing datatypeDefinitionGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Stream<ForeignThingFamily> getForeignThings(IdentifierType identifierType) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Optional<String> getSpecificationType(GroveThing specificationGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Optional<ForeignThingFamily> getSpecObject(GroveThing specRelationGroveThing, RelationshipTerminal relationshipTerminal) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Optional<String> getSpecObjectType(GroveThing specObjectGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Optional<String> getSpecRelationType(GroveThing specRelationGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void initialize(SynchronizationArtifact synchronizationArtifact) {
      this.publishingDomBuilder.initialize(synchronizationArtifact);
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public boolean isEnumerated(GroveThing attributeValueGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public InputStream serialize() {
      return this.publishingDomBuilder.serialize();
   }

}

/* EOF */
