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

package org.eclipse.osee.define.operations.synchronization.publishingdom;

import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierTypeGroup;
import org.eclipse.osee.define.operations.synchronization.identifier.IncorrectIdentifierTypeException;

/**
 * The {@link DocumentMap} interface is the root {@link HierarchicalNode} node of the Document Object Model. The
 * {@link DocumentMap} also serves as the factory for all subordinate nodes which cannot be created outside the context
 * of a {@link DocumentMap} implementation.
 *
 * @author Loren K. Ashley
 */

public interface DocumentMap extends HierarchicalNode {

   /**
    * Appends an {@link AttributeDefintion} to a {@link Document} or {@link DocumentObjectImpl} specified by
    * <code>parentIdentifer</code>.
    *
    * @param parentDocumentObjectIentifier the {@link Identifier} for the {@link DocumentObjectImpl} to append to.
    * @param attributeDefinition the {@link AttributeDefinitionImpl} to append.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws NodeNotMemberOfDomException when the <code>attributeDefinition</code> {@link Node} is not a member of this
    * {@link DocumentMap}.
    * @throws NodeNotFoundException when a {@link Document} or {@link DocumentObjectImpl} with the
    * <code>parentIdentifier</code> is not found in the {@link DocumentMapImpl}.
    * @throws NodeWrongTypeException when the {@link Node} specified by <code>parentDocumentObjectIdentifier</code> is
    * not a {@link Document} or {@link DocumentObjectImpl} {@link Node}.
    */

   void appendAttributeDefinition(Identifier parentIdentifier, AttributeDefinition attributeDefinition);

   /**
    * Appends an unattached {@link AttributeValueImpl} to the {@link AttributeDefinitionImpl} specified by
    * <code>parentAttributeDefinitionIdentifer</code>.
    *
    * @param parentIdentifier the {@link Identifier} for the {@link AttributeDefintion} to append to.
    * @param attributeValue the {@link AttributeValueImpl} to append.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws NodeNotMemberOfDomException when the <code>attributeDefinition</code> {@link Node} is not a member of this
    * {@link DocumentMap}.
    * @throws NodeNotFoundException when an {@link AttributeDefinitionImpl} with the
    * <code>parentAttributeDefinitionIdentifier</code> is not found in the {@link DocumentMapImpl}.
    * @throws NodeWrongTypeException when the {@link Node} specified by <code>parentAttributeDefinitionIdentifier</code>
    * is not an {@link AttributeDefinitionImpl} {@link Node}.
    * @throws IllegalStateException when the {@link AttributeValueImpl} has already been attached to a
    * {@link DocumentMapImpl}.
    * @throws DuplicateNodeException when the {@link DocumentMapImpl} already contains an entry for the specified
    * <code>attributeValue</code>.
    */

   void appendAttributeValue(Identifier parentIdentifier, Identifier attributeDefinitionIdentifier, AttributeValue attributeValue);

   /**
    * Appends an unattached {@link DocumentObjectImpl} to the {@link Document} specified by
    * <code>parentDocumentIdentifier</code>.
    *
    * @param parentDocumentIdentifier the {@link Identifier} for the {@link Document} to append to.
    * @param documentObject the {@link DocumentObjectImpl} to append.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws NodeNotMemberOfDomException when the <code>attributeDefinition</code> {@link Node} is not a member of this
    * {@link DocumentMap}.
    * @throws NodeNotFoundException when a {@link Document} with the <code>documentIdentifier</code> is not found in
    * this {@link DocumentMapImpl}.
    * @throws IllegalStateException when the {@link DocumentObjectImpl} has already been attached to a
    * {@link DocumentMapImpl}.
    * @throws DuplicateNodeException when the {@link DocumentMapImpl} already contains an entry for the specified
    * <code>documentObject</code>.
    */

   void appendDocumentObjectToDocument(Identifier parentDocumentIdentifier, DocumentObject documentObject);

   /**
    * Appends an unattached {@link DocumentObjectImpl} to the {@link DocumentObjectImpl} specified by
    * <code>parentDocumentObjectIdentifier</code>.
    *
    * @param parentDocumentObjectIdentifier the {@link Identifier} for the {@link DocumentObjectImpl} to append to.
    * @param documentObject the {@link DocumentObjectImpl} to append.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws NodeNotMemberOfDomException when the <code>attributeDefinition</code> {@link Node} is not a member of this
    * {@link DocumentMap}.
    * @throws NodeNotFoundException when a {@link DocumentObjectImpl} with the
    * <code>parentDocumentObjectIdentifier</code> is not found in the {@link DocumentMapImpl}.
    * @throws NodeWrongTypeException when the {@link Node} specified by <code>parentDocumentObjectIdentifier</code> is
    * not a {@link DocumentObjectImpl} {@link Node}.
    * @throws IllegalStateException when the {@link DocumentObjectImpl} has already been attached to a
    * {@link DocumentMapImpl}.
    * @throws DuplicateNodeException when the {@link DocumentMapImpl} already contains an entry for the specified
    * <code>documentObject</code>.
    */

   void appendDocumentObjectToDocumentObject(Identifier parentDocumentObjectIdentifier, DocumentObject documentObject);

   /**
    * Appends an unattached {@link Document} to this {@link DocumentMapImpl}.
    *
    * @param parentDocumentMapIdentifier the {@link Identifier} of the {@link DocumentMapImpl} to append to.
    * @param document the {@link Document} to append.
    * @throws NullPointerException when the parameter <code>document</code> is<code>null</code>.
    * @throws NodeNotMemberOfDomException when the <code>attributeDefinition</code> {@link Node} is not a member of this
    * {@link DocumentMap}.
    * @throws NodNotFoundException when the parameter <code>parentDocumentMapIdentifier</code> is not the
    * {@link Identifier} of this {@link DocumentMapImpl}.
    * @throws IllegalStateException when the {@link Document} has already been attached to a {@link DocumentMapImpl}.
    * @throws DuplicateNodeException when the {@link DocumentMapImpl} already contains an entry for the specified
    * <code>document</code>.
    */

   void appendDocumentToDocumentMap(Identifier parentDocumentMapIdentifier, Document document);

   /**
    * Factory method to create an new unattached {@link AttributeDefinitionImpl}.
    *
    * @param attributeDefinitionIdentifier the {@link AttributeDefinitionImpl} {@link Identifier}.
    * @param name the {@link AttributeDefinitionImpl} name.
    * @param description the {@link AttributeDefinitionImpl} description.
    * @return an initialized unattached {@link AttributeDefinitionImpl}.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws IncorrectIdentifierTypeException when the specified <code>attributeDefinitionIdentifier</code> is not of
    * the type {@link IdentifierType#ATTRIBUTE_DEFINITION}.
    */

   AttributeDefinitionImpl createAttributeDefinition(Identifier attributeDefinitionIdentifier, String name, String description);

   /**
    * Factory method to create an new unattached {@link AttributeValueImpl}.
    *
    * @param attributeValueIdentifier the {@link AttributeValueImpl} {@link Identifier}.
    * @param value the {@link AttributeValueImpl} value.
    * @return an initialized unattached {@link AttributeValueImpl}.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws IncorrectIdentifierTypeException when the specified <code>attributeValueIdentifier</code> is not of the
    * type {@link IdentifierType#ATTRIBUTE_VALUE}.
    */

   AttributeValueImpl createAttributeValue(Identifier attributeValueIdentifier, String value);

   /**
    * Factory method to create an new unattached {@link Document}.
    *
    * @param documentIdentifier the {@link Document} {@link Identifier}.
    * @param name the {@link Document} name.
    * @param type the {@link Document} type.
    * @return an initialized unattached {@link Document}.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws IncorrectIdentifierTypeException when the specified <code>documentIdentifier</code> is not of the type
    * {@link IdentifierType#SPECIFICATION}.
    */

   Document createDocument(Identifier documentIdentifier, String name, String type);

   /**
    * Factory method to create an new unattached {@link DocumentObjectImpl}.
    *
    * @param documentObjectIdentifier the {@link DocumentObjectImpl} {@link Identifier}.
    * @param name the {@link DocumentObjectImpl} name.
    * @param type the {@link DocumentObjectImpl} type.
    * @return an initialized unattached {@link DocumentObjectImpl}.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws IncorrectIdentifierTypeException when the specified <code>documentObjectIdentifier</code>
    * {@link IdentifierType} is not a member of the group {@link IdentifierTypeGroup#SUBORDINATE_OBJECT}.
    */

   DocumentObjectImpl createDocumentObject(Identifier documentObjectIdentifier, String name, String type);

   /**
    * Calculates the hierarchy level and hierarchy sequence at each level for all {@link HierarchicalNode}s in the
    * Document Object Model.
    */

   void setHierarchyLevels();
}

/* EOF */