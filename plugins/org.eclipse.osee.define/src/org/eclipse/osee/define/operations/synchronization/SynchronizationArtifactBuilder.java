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

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.forest.Grove;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.EnumValue;

/**
 * Implementations of this interface contain the Synchronization Artifact artifact type specific building logic.
 * <p>
 * Implementations may support export and/or import operations. When an implementation does not support an operational
 * direction, the methods for that operational direction are expected to throw an {@link UnsupportedOperationException}.
 * <dl>
 * <dt>Methods that must be implemented for export operations:</dt>
 * <dd>
 * <ul>
 * <li>{@link #initialize},</li>
 * <li>{@link #getConverter},</li>
 * <li>{@link #build},and</li>
 * <li>{@link #serialize}.</li>
 * </ul>
 * </dd>
 * <dt>Methods that must be implemented for import operations:</dt>
 * <dd>
 * <ul>
 * <li>{@link #deserialize},</li>
 * <li>{@link #getAttributeDefinition},</li>
 * <li>{@link #getAttributeDefinitions},</li>
 * <li>{@link #getDatatypeDefinition},</li>
 * <li>{@link #getEnumValues},</li>
 * <li>{@link #getForeignThings},</li>
 * <li>{@link #getSpecificationType},</li>
 * <li>{@link #getSpecObject},</li>
 * <li>{@link #getSpecObjectType},</li>
 * <li>{@link #getSpecRelationType}, and</li>
 * <li>{@link #isEnumerated}.</li>
 * </ul>
 * </dd>
 * <dt>Methods that must be implemented for both import and export operations:</dt>
 * <dd>
 * <ul>
 * <li>{@link close}.</li>
 * </ul>
 * </dl>
 * <h2>Export Operations</h2>
 * <p>
 * The general flow for export operations is as follows:
 * <ul>
 * <li>OSEE Artifacts are retrieved for the export and built into the Synchronization Artifact DOM.</li>
 * <li>Object Conversion:
 * <ul>
 * <li>A converter is retrieved from the {@link SynchronizationArtifactBuilder} via the method {@link #getConverter} for
 * each {@link IdentifierType}.</li>
 * <li>All {@link GroveThing}s in the Synchronization Artifact DOM of each {@link IdentifierType} are processed with the
 * converter for that {@link IdentifierType}.</li>
 * </ul>
 * </li>
 * <li>The {@link #build} method of the {@link SynchronizationArtifactBuilder} is called to build a foreign DOM from the
 * foreign things created by the converters.</li>
 * <li>The {@link #serialize} method is then called to convert the foreign DOM into an {@link InputStream} that can be
 * read by the OSEE web-server application.</li>
 * </ul>
 * The {@link SynchronizationArtifact} contains {@link Grove}s of {@link GroveThing}s. There is a {@link Grove} and
 * {@link GroveThing} implementation associated with each member of the {@link IdentifierType} enumeration.
 * <p>
 * <h2>Import Operations</h2>
 * <p>
 * The general flow for import operations is as follows:
 * <ul>
 * <li>The {@link #deserialize} method of the {@link SynchronizationArtifactBuilder} is called to build the foreign DOM
 * from an {@link InputStream}.</li>
 * <li>The method {@link #getForeignThings} is called for each type of {@link IdentifierType}. The received stream is
 * used to populate the Synchronization Artifact DOM with {@link GroveThing}s created from the foreign things.</li>
 * <li>The relationships between {@link GroveThing}s in the Synchronization Artifact DOM are established using the
 * following methods:
 * <ul>
 * <li>{@link #getAttributeDefinition},</li>
 * <li>{@link #getAttributeDefinitions},</li>
 * <li>{@link #getDatatypeDefinition},</li>
 * <li>{@link #getEnumValues},</li>
 * <li>{@link #getSpecificationType},</li>
 * <li>{@link #getSpecObject},</li>
 * <li>{@link #getSpecObjectType},</li>
 * <li>{@link #getSpecRelationType}, and</li>
 * <li>{@link #isEnumerated}.</li>
 * </ul>
 * </ul>
 * <p>
 * <h2>Export and Import Operations</h2>
 * <p>
 * Once an export or import operation has been completed the {@link #close} method of the
 * {@link SynchronizationArtifactBuilder} will be called so that any resources allocated by the
 * {@link SynchronizationArtifactBuilder} implementation may be released.
 *
 * @author Loren K. Ashley
 */

public interface SynchronizationArtifactBuilder {

   /*
    * Export Methods
    */

   /**
    * This method is called with the built {@link SynchronizationArtifact} before the export methods
    * {@link #getConverter} or {@link #build} are invoked. This provides the {@link SynchronizationArtifactBuilder}
    * implementation an opportunity to perform any necessary setup before the process of building the foreign DOM
    * begins.
    *
    * @param synchronizationArtifact the {@link SynchronizationArtifact} containing the {@link GroveThing}s to be
    * assembled into a foreign DOM.
    */

   void initialize(SynchronizationArtifact synchronizationArtifact);

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
    * This method is called to assemble the Synchronization Artifact DOM after all of the {@link GroveThing}s have been
    * converted from the native OSEE things into foreign things.
    *
    * @return <code>true</code>, when building completed successfully; otherwise, <code>false</code>.
    */

   boolean build();

   /**
    * Creates an {@link InputStream} the serialized Synchronization Artifact may be read from.
    *
    * @return an {@link InputStream} the serialized Synchronization Artifact may be read from.
    */

   InputStream serialize();

   /*
    * Import Methods
    */

   /**
    * Reads a Synchronization Artifact from an {@link InputStream} into a foreign DOM or buffer. If any indexing of the
    * foreign DOM is needed to facilitate the access methods, it should be done by the implementation of this method.
    *
    * @param inputStream the serialized Synchronization Artifact to be read.
    * @throws SynchronizationArtifactParseException when an error occurs deserializing the Synchronization Artifact
    * stream.
    */

   void deserialize(InputStream inputStream);

   /**
    * Gets the foreign identifier as a {@link String} of the foreign Attribute Definition referenced by the foreign
    * Attribute Value contained in the specified {@link GroveThing}.
    *
    * @param attributeValueGroveThing the {@link GroveThing} containing the foreign Attribute Value.
    * @return when the foreign Attribute Value has an associated foreign Attribute Definition, an {@link Optional}
    * containing the foreign identifier of the associated foreign Attribute Definition; otherwise, an empty
    * {@link Optional}.
    */

   Optional<String> getAttributeDefinition(GroveThing attributeValueGroveThing);

   /**
    * Gets a stream of the foreign identifiers of the {@link AttributeDefinition}s that are referenced by the specified
    * Specification. The {@link Stream} contains the {@link String} representation of the foreign identifiers.
    *
    * @param specificationTypeGroveThing the {@link GroveThing} containing the foreign Specification to get the related
    * foreign Attribute Definitions for.
    * @return a {@link Stream} of the foreign identifiers of the foreign Attribute Definitions associated with the
    * foreign Specification.
    */

   Stream<String> getAttributeDefinitions(GroveThing specificationTypeGroveThing);

   /**
    * Gets the foreign identifier as a {@link String} of the foreign Data Type Definition referenced by the foreign
    * Attribute Definition contained in the specified {@link GroveThing}.
    *
    * @param attributeDefinitionGroveThing the {@link GroveThing} containing the foreign Attribute Definition.
    * @return when the foreign Attribute Definition has an associated foreign Data Type Definition, an {@link Optional}
    * containing the foreign identifier of the associated foreign Data Type Definition; otherwise, an empty
    * {@link Optional}.
    */

   Optional<String> getDatatypeDefinition(GroveThing attributeDefinitionGroveThing);

   /**
    * Gets a stream of the foreign identifiers of the {@link EnumValue}s that are referenced by the specified foreign
    * Data Type Definition. The {@link Stream} contains the {@link String} representation of the foreign identifiers.
    *
    * @param datatypeDefinitionGroveThing the {@link GroveThing} containing the foreign Data Type Definition to get the
    * related foreign Enum Value for.
    * @return a {@link Stream} of the foreign identifiers of the foreign Enum Values associated with the foreign Data
    * Type Definition.
    */

   Stream<String> getEnumValues(GroveThing datatypeDefinitionGroveThing);

   /**
    * Provides a stream of {@link ForeignThingFamily} objects representing all of the things in the foreign DOM of the
    * specified type.
    *
    * @param identifierType the type of foreign things to be returned in the stream.
    * @return a {@link Stream} of {@Link ForeignThingFamily} objects representing all of the things in the foreign DOM
    * of the type specified by the parameter <code>identifierType</code>.
    */

   Stream<ForeignThingFamily> getForeignThings(IdentifierType identifierType);

   /**
    * Gets the foreign identifier as a {@link String} of the foreign Specification Type referenced by the foreign
    * Specification contained in the specified {@link GroveThing}.
    *
    * @param specificationGroveThing the {@link GroveThing} containing the foreign Specification.
    * @return when the foreign Specification has an associated foreign Specification Type, an {@link Optional}
    * containing the foreign identifier of the associated foreign Specification Type; otherwise, an empty
    * {@link Optional}.
    */

   Optional<String> getSpecificationType(GroveThing specificationGroveThing);

   /**
    * Gets the foreign identifier as a {@link String} of the foreign Spec Object referenced by the specified foreign
    * Spec Relation contained in the specified {@link GroveThing} for the specified {@link RelationshipTerminal}.
    *
    * @param specRelationGroveThing the {@link GroveThing} containing the foreign Spec Relation.
    * @param relationshipTerminal the {@link RelationTerminal} of the foreign Spec Relation to get the related foreign
    * Spec Object for.
    * @return when the foreign Spec Relation has an associated foreign Spec Object for the specified
    * {@link RelationTerminal}, an {@link Optional} containing the foreign identifier of the associated foreign Spec
    * Object; otherwise an empty {@link Optional}.
    */

   Optional<ForeignThingFamily> getSpecObject(GroveThing specRelationGroveThing, RelationshipTerminal relationshipTerminal);

   /**
    * Gets the foreign identifier as a {@link String} of the foreign Spec Object Type referenced by the specified
    * foreign Spec Object contained in the specified {@link GroveThing}.
    *
    * @param specObjectGroveThing the {@link GroveThing} containing the foreign Spec Object.
    * @return when the foreign Spec Object has an associated foreign Spec Object Type, an {@link Optional} containing
    * the foreign identifier of the associated foreign Spec Object Type; otherwise, an empty {@link Optional}.
    */

   Optional<String> getSpecObjectType(GroveThing specObjectGroveThing);

   /**
    * Gets the foreign identifier as a {@link String} of the foreign Spec Relation Type referenced by the specified
    * foreign Spec Relation contained in the specified {@link GroveThing}.
    *
    * @param specRelationGroveThing the {@link GroveThing} containing the foreign Spec Relation.
    * @return when the foreign Spec Relation has an associated foreign Spec Relation Type, an {@link Optional}
    * containing the foreign identifier of the associated Spec Relation Type; otherwise, an empty {@link Optional}.
    */

   Optional<String> getSpecRelationType(GroveThing specRelationGroveThing);

   /**
    * Predicate to determine if the foreign Attribute Value is an enumerated type.
    *
    * @param attributeValueGroveThing the {@link GroveThing} containing the foreign Attribute Value to be tested.
    * @return <code>true</code>, when the foreign Attribute Value is an enumerated type; otherwise, <code>false</code>.
    */

   boolean isEnumerated(GroveThing attributeValueGroveThing);

   /*
    * Export Import Methods
    */

   /**
    * Releases any outstanding resources held by the {@link SynchronizationArtifactBuilder} implementation.
    */

   void close();

}

/* EOF */
