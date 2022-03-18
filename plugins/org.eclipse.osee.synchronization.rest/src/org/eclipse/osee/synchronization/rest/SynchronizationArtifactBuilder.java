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

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Implementations of this interface contain the Synchronization Artifact artifact type specific building logic.
 * <p>
 * The {@link SynchronizationArtifact} contains {@link Grove}s of {@link GroveThing}s. There is a grove associated with
 * each member of the {@link IdentifierType} enumeration. The following list shows the classes implementing the
 * {@link GroveThing} interface associated with each member of the {@link IdentifierType} enumeration:
 * <dl>
 * <dt>{@link IdentifierType#ATTRIBUTE_DEFINITION}</dt>
 * <dd>{@link AttributeDefinitionGroveThing}</dd>
 * <dt>{@link IdentifierType#ATTRIBUTE_VALUE}</dt>
 * <dd>{@link AttributeValueGroveThing}</dd>
 * <dt>{@link IdentifierType#DATA_TYPE_DEFINITION}</dt>
 * <dd>{@link DataTypeDefinitionGroveThing}</dd>
 * <dt>{@link IdentifierType#HEADER}</dt>
 * <dd>{@link HeaderGroveThing}</dd>
 * <dt>{@link IdentifierType#SPECIFICATION}</dt>
 * <dd>{@link SpecificationGroveThing}</dd>
 * <dt>{@link IdentifierType#SPECIFICATION_TYPE}</dt>
 * <dd>{@link SpecTypeGroveThing}</dd>
 * <dt>{@link IdentifierType#SPEC_OBJECT}</dt>
 * <dd>{@link SpecObjectGroveThing}</dd>
 * <dt>{@link IdentifierType#SPEC_OBJECT_TYPE}</dt>
 * <dd>{@link SpecObjectTypeGroveThing}</dd>
 * </dl>
 * The implementation of this interface is expected to provide a converter method implementing the {@link Consumer}
 * functional interface for each of the classes implementing the {@link GroveThing} interface that contain data needed
 * for the Synchronization Artifact artifact type being built. If data from any of the {@link GroveThing}
 * implementations is not needed for the Synchronization Artifact, the interface implementation may return a
 * <code>null</code> converter for the {@link IdentifierType} associated with the unneeded {@link GroveThing}
 * implementations. <br>
 * <br>
 * The {@link Consumer} implementations returned by {@link #getConverter} will be invoked for each {@link GroveThing} in
 * each {@link Grove} of the {@link SynchronizationArtifact}. These converters will obtain the native OSEE things from
 * the {@link GroveThing} implementation, create a foreign thing, and save a reference to the foreign thing in the
 * {@link GroveThing} implementation. The order in which the converters are called is undefined. The only guarantee the
 * interface implementation has is that a converter will be invoked for all {@link GroveThing}s in all of the
 * {@link Grove}s.<br>
 * <br>
 * Once all of the {@link GroveThing} implementations have been converted, the interface {@link #build} method is
 * invoked. This method is expected to assemble the foreign things created by the converters into the structure needed
 * for the Synchronization Artifact artifact type being built.<br>
 * <br>
 * The final step is to invoke the {@link #serialize} method. This method is expected to produce an {@link InputStream}
 * that the serialized Synchronization Artifact may be read from. <br>
 *
 * @author Loren K. Ashley
 */

public interface SynchronizationArtifactBuilder {

   /**
    * Gets a converter method implementing the {@link Consumer} functional interface for the {@link GroveThing} type
    * associated with the specified {@link IdentifierType}.
    *
    * @param identifierType the {@link IdentifierType} associated with the {@link GroveThing} class to get a converter
    * for.
    * @return when a converter method is defined for the {@link IdentifierType}, an {@link Optional} containing the
    * converter reference; otherwise, an empty {@link Optional}.
    */

   Optional<Consumer<GroveThing>> getConverter(IdentifierType identifierType);

   /**
    * This method is called to assemble the Synchronization Artifact after all of the {@link GroveThing}s have been
    * converted from the native OSEE objects into foreign objects.
    *
    * @param synchronizationArtifact the {@link SynchronizationArtifact} containing the {@link GroveThing}s to be
    * assembled.
    * @return <code>true</code>, when building completed successfully; otherwise, <code>false</code>.
    */

   boolean build(SynchronizationArtifact synchronizationArtifact);

   /**
    * Creates an {@link InputStream} the serialized Synchronization Artifact may be read from.
    *
    * @return an {@link InputStream} the serialized Synchronization Artifact may be read from.
    */

   InputStream serialize();
}

/* EOF */
