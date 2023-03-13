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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierTypeGroup;
import org.eclipse.osee.define.operations.synchronization.identifier.IncorrectIdentifierTypeException;

/**
 * A map with first in first out iteration order of {@link DocumentImpl} objects by {@link Identifier}.
 *
 * @author Loren K. Ashley
 */

class DocumentMapImpl implements DocumentMap, HierarchicalNodeSetter {

   /**
    * Map of {@link Document} objects by document {@link Identifier}.
    */

   private final Map<Identifier, Document> documentMap;

   /**
    * An array containing the numerical hierarchical position of the {@link HierarchicalNode}. The number of entries in
    * the array indicates the hierarchical depth of the {@link HierarchicalNode}. The value in each index of the array
    * indicates the sequence position of the {@link HierarchicalNode} parent at the hierarchical level indicated by the
    * array index.
    */

   private int[] hierarchyLevels;

   /**
    * A {@link String} representation of the {@link #hierarchyLevels} array.
    */

   private String hierarchyLevelsString;

   /**
    * The {@link Node} identifier for the {@link DocumentMapImpl}.
    */

   private final Identifier identifier;

   /**
    * Map of all {@link AbstractNode} objects in the DOM by {@link Identifier}.
    */

   private final Map<Identifier, Node> nodeMap;

   /**
    * Creates a new empty {@link DocumentMapImpl}.
    *
    * @param documentMapIdentifier the {@link Identifier} for the new {@link DocumentMapImpl}.
    */

   DocumentMapImpl(Identifier documentMapIdentifier) {

      //@formatter:off
      assert   Objects.nonNull(documentMapIdentifier)
             : "DocumentMapImpl::new, parameter \"documentMapIdentifier\" cannot be null.";

      assert   documentMapIdentifier.isType( IdentifierType.FOREST )
             : "DocumentMapImpl::new, parameter \"documentMapIdentifier\" is not of \"IdentifierType.FOREST\".";
      //@formatter:on

      this.identifier = documentMapIdentifier;
      this.documentMap = new LinkedHashMap<>();
      this.nodeMap = new HashMap<>();
      this.nodeMap.put(documentMapIdentifier, this);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws NodeNotMemberOfDomException {@inheritDoc}
    * @throws NodeNotFoundException {@inheritDoc}
    * @throws NodeWrongTypeException {@inheritDoc}
    */

   @Override
   public void appendAttributeDefinition(Identifier parentIdentifier, AttributeDefinition attributeDefinition) {

      Objects.requireNonNull(parentIdentifier,
         "DocumentMapImpl::appendAttributeDefinition, parameter \"parentIdentifier\" cannot be null.");
      Objects.requireNonNull(attributeDefinition,
         "DocumentMapImpl::appendAttributeDefinition, parameter \"attributeDefinition\" cannot be null.");

      if (((AttributeDefinitionImpl) attributeDefinition).documentMap != this) {
         throw new NodeNotMemberOfDomException(attributeDefinition, this, "DocumentMapImp::appendAttributeDefinition");
      }

      var parentNode = this.nodeMap.get(parentIdentifier);

      if (Objects.isNull(parentNode)) {
         throw new NodeNotFoundException(parentIdentifier);
      }

      //@formatter:off
      if (    !( parentNode instanceof DocumentObjectImpl )
           && !( parentNode instanceof DocumentImpl       ) ) {
         throw new NodeWrongTypeException(parentIdentifier, parentNode, DocumentObjectImpl.class);
      }
      //@formatter:on

      var parentAbstractNode = (AbstractNode) parentNode;

      var attributeDefinitionIdentifier = attributeDefinition.getIdentifier();

      ((AttributeDefinitionImpl) attributeDefinition).attach(parentIdentifier);

      parentAbstractNode.append(attributeDefinition);

      this.nodeMap.put(attributeDefinitionIdentifier, attributeDefinition);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws NodeNotMemberOfDomException {@inheritDoc}
    * @throws NodeNotFoundException {@inheritDoc}
    * @throws NodeWrongTypeException {@inheritDoc}
    * @throws IllegalStateException {@inheritDoc}
    * @throws DuplicateNodeException {@inheritDoc}
    */

   @Override
   public void appendAttributeValue(Identifier parentIdentifier, Identifier attributeDefinitionIdentifier, AttributeValue attributeValue) {

      Objects.requireNonNull(parentIdentifier,
         "DocumentMapImpl::appendAttributeValue, parameter \"parentIdentifier\" cannot be null.");
      Objects.requireNonNull(attributeValue,
         "DocumentMapImpl::appendAttributeValue, parameter \"attributeValue\" cannot be null.");

      if (((AttributeValueImpl) attributeValue).documentMap != this) {
         throw new NodeNotMemberOfDomException(attributeValue, this, "DocumentMapImp::appendAttributeValue");
      }

      var parentNode = this.nodeMap.get(parentIdentifier);

      if (Objects.isNull(parentNode)) {
         throw new NodeNotFoundException(parentIdentifier);
      }

      //@formatter:off
      if (    !( parentNode instanceof DocumentObjectImpl )
           && !( parentNode instanceof DocumentImpl       ) ) {
         throw new NodeWrongTypeException(parentIdentifier, parentNode, DocumentObjectImpl.class);
      }
      //@formatter:on

      var parentAbstractNode = (AbstractNode) parentNode;

      if (attributeValue.isAttached()) {
         throw new IllegalStateException();
      }

      var attributeValueIdentifier = attributeValue.getIdentifier();

      if (this.nodeMap.containsKey(attributeValueIdentifier)) {
         throw new DuplicateNodeException(attributeValueIdentifier);
      }

      ((AttributeValueImpl) attributeValue).attach(attributeDefinitionIdentifier);

      parentAbstractNode.append(attributeValue);

      this.nodeMap.put(attributeValueIdentifier, attributeValue);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws NodeNotMemberOfDomException {@inheritDoc}
    * @throws NodeNotFoundException {@inheritDoc}
    * @throws IllegalStateException {@inheritDoc}
    * @throws DuplicateNodeException {@inheritDoc}
    */

   @Override
   public void appendDocumentObjectToDocument(Identifier parentDocumentIdentifier, DocumentObject documentObject) {

      Objects.requireNonNull(parentDocumentIdentifier,
         "DocumentMapImpl::appendDocumentObjectToDocument, parameter \"parentDocumentIdentifier\" cannot be null.");
      Objects.requireNonNull(documentObject,
         "DocumentMapImpl::appendDocumentObjectToDocument, parameter \"documentObject\" cannot be null.");

      if (((DocumentObjectImpl) documentObject).documentMap != this) {
         throw new NodeNotMemberOfDomException(documentObject, this, "DocumentMapImp::appendDocumentObjectToDocument");
      }

      var parentDocument = this.documentMap.get(parentDocumentIdentifier);

      if (Objects.isNull(parentDocument)) {
         throw new NodeNotFoundException(parentDocumentIdentifier);
      }

      if (documentObject.isAttached()) {
         throw new IllegalStateException();
      }

      var documentObjectIdentifier = documentObject.getIdentifier();

      if (this.nodeMap.containsKey(documentObjectIdentifier)) {
         throw new DuplicateNodeException(documentObjectIdentifier);
      }

      ((DocumentObjectImpl) documentObject).attach(parentDocumentIdentifier);

      ((DocumentImpl) parentDocument).append(documentObject);

      this.nodeMap.put(documentObjectIdentifier, documentObject);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws NodeNotMemberOfDomException {@inheritDoc}
    * @throws NodeNotFoundException {@inheritDoc}
    * @throws NodeWrongTypeException {@inheritDoc}
    * @throws IllegalStateException {@inheritDoc}
    * @throws DuplicateNodeException {@inheritDoc}
    */

   @Override
   public void appendDocumentObjectToDocumentObject(Identifier parentDocumentObjectIdentifier, DocumentObject documentObject) {

      Objects.requireNonNull(parentDocumentObjectIdentifier,
         "DocumentMapImpl::appendDocumentObjectToDocumentObject, parameter \"parentDocumentObjectIdentifier\" cannot be null.");
      Objects.requireNonNull(documentObject,
         "DocumentMapImpl::appendDocumentObjectToDocumentObject, parameter \"documentObject\" cannot be null.");

      if (((DocumentObjectImpl) documentObject).documentMap != this) {
         throw new NodeNotMemberOfDomException(documentObject, this,
            "DocumentMapImp::appendDocumentObjectToDocumentObject");
      }

      var parentNode = this.nodeMap.get(parentDocumentObjectIdentifier);

      if (Objects.isNull(parentNode)) {
         throw new NodeNotFoundException(parentDocumentObjectIdentifier);
      }

      if (!(parentNode instanceof DocumentObjectImpl)) {
         throw new NodeWrongTypeException(parentDocumentObjectIdentifier, parentNode, DocumentObjectImpl.class);
      }

      var parentDocumentObject = (DocumentObjectImpl) parentNode;

      if (documentObject.isAttached()) {
         throw new IllegalStateException();
      }

      var documentObjectIdentifier = documentObject.getIdentifier();

      if (this.nodeMap.containsKey(documentObjectIdentifier)) {
         throw new DuplicateNodeException(documentObjectIdentifier);
      }

      ((DocumentObjectImpl) documentObject).attach(parentDocumentObjectIdentifier);

      parentDocumentObject.append(documentObject);

      this.nodeMap.put(documentObjectIdentifier, documentObject);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws NodeNotMemberOfDomException {@inheritDoc}
    * @throws NodNotFoundException {@inheritDoc}
    * @throws IllegalStateException {@inheritDoc}
    * @throws DuplicateNodeException {@inheritDoc}
    */

   @Override
   public void appendDocumentToDocumentMap(Identifier parentDocumentMapIdentifier, Document document) {

      Objects.requireNonNull(parentDocumentMapIdentifier,
         "DocumentMapImpl::appendDocumentToDocumentMap, parameter \"parentDocumentMapIdentifier\" cannot be null.");
      Objects.requireNonNull(document,
         "DocumentMapImpl::appendDocumentToDocumentMap, parameter \"document\" cannot be null.");

      if (((DocumentImpl) document).documentMap != this) {
         throw new NodeNotMemberOfDomException(document, this, "DocumentMapImp::appendDocumentObjectToDocumentObject");
      }

      if (!this.identifier.equals(parentDocumentMapIdentifier)) {
         throw new NodeNotFoundException(parentDocumentMapIdentifier);
      }

      if (document.isAttached()) {
         throw new IllegalStateException();
      }

      var documentIdentifier = document.getIdentifier();

      if (this.nodeMap.containsKey(documentIdentifier)) {
         throw new DuplicateNodeException(documentIdentifier);
      }

      ((DocumentImpl) document).attach(identifier);

      this.nodeMap.put(documentIdentifier, document);
      this.documentMap.put(documentIdentifier, document);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int attributeDefinitionSize() {
      return 0;
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IncorrectIdentifierTypeException {@inheritDoc}
    */

   @Override
   public AttributeDefinitionImpl createAttributeDefinition(Identifier attributeDefinitionIdentifier, String name, String description) {

      Objects.requireNonNull(attributeDefinitionIdentifier,
         "DocumentMapImpl::createAttributeDefinition, parameter \"attributeDefintionIdentifier\" cannot be null.");
      Objects.requireNonNull(name, "DocumentMapImpl::createAttributeDefinition, parameter \"name\" cannot be null.");
      Objects.requireNonNull(description,
         "DocumentMapImpl::createAttributeDefinition, parameter \"description\" cannot be null.");

      attributeDefinitionIdentifier.requireType(IdentifierType.ATTRIBUTE_DEFINITION);

      return new AttributeDefinitionImpl(this, attributeDefinitionIdentifier, name, description);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IncorrectIdentifierTypeException {@inheritDoc}
    */

   @Override
   public AttributeValueImpl createAttributeValue(Identifier attributeValueIdentifier, String value) {

      Objects.requireNonNull(attributeValueIdentifier,
         "DocumentMapImpl::createAttributeValue, parameter \"attributeValueIdentifier\" cannot be null.");
      Objects.requireNonNull(value, "DocumentMapImpl::createAttributeValue, parameter \"value\" cannot be null.");

      attributeValueIdentifier.requireType(IdentifierType.ATTRIBUTE_VALUE);

      return new AttributeValueImpl(this, attributeValueIdentifier, value);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IncorrectIdentifierTypeException {@inheritDoc}
    */

   @Override
   public Document createDocument(Identifier documentIdentifier, String name, String type) {

      Objects.requireNonNull(documentIdentifier,
         "DocumentMapImpl::createDocument, parameter \"documentIdentifier\" cannot be null.");
      Objects.requireNonNull(name, "DocumentMapImpl::createDocument, parameter \"name\" cannot be null.");
      Objects.requireNonNull(type, "DocumentMapImpl::createDocument, parameter \"type\" cannot be null.");

      documentIdentifier.requireType(IdentifierType.SPECIFICATION);

      return new DocumentImpl(this, documentIdentifier, name, type);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IllegalArgumentException {@inheritDoc}
    */

   @Override
   public DocumentObjectImpl createDocumentObject(Identifier documentObjectIdentifier, String name, String type) {

      Objects.requireNonNull(documentObjectIdentifier,
         "DocumentMapImpl::createDocumentObject, parameter \"documentObjectIdentifier\" cannot be null.");
      Objects.requireNonNull(name, "DocumentMapImpl::createDocumentObject, parameter \"name\" cannot be null.");
      Objects.requireNonNull(type, "DocumentMapImpl::createDocumentObject, parameter \"type\" cannot be null.");

      documentObjectIdentifier.requireInGroup(IdentifierTypeGroup.SUBORDINATE_OBJECT);

      return new DocumentObjectImpl(this, documentObjectIdentifier, name, type);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int getHierarchyLevel() {

      return Objects.nonNull(this.hierarchyLevels) ? this.hierarchyLevels.length : -1;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int[] getHierarchyLevels() {
      return Arrays.copyOf(this.hierarchyLevels, this.hierarchyLevels.length);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getHierarchyLevelString() {
      return this.hierarchyLevelsString;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Identifier getIdentifier() {
      return this.identifier;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Identifier getParentIdentifier() {
      return null;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public IdentifierType getType() {
      return this.identifier.getType();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int hierarchicalSize() {
      return this.documentMap.size();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isAttached() {
      return true;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isHierarchical() {
      return true;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isType(IdentifierType identifierType) {
      return this.identifier.isType(identifierType);
   }

   /**
    * Recursive method to set the hierarchy level of a node's hierarchical children.
    *
    * @param parentNode the node whose children are to be processed.
    */

   private void setHierarchyLevelForChildren(HierarchicalNodeSetter parentNode) {

      var count = new int[] {0};

      var parentHierarchyLevels = parentNode.getHierarchyLevels();

      //@formatter:off
      parentNode.streamHierarchicalChildren().forEach
         (
            ( childHierarchicalNode ) ->
            {
               this.setNodeHierarchyLevel( (HierarchicalNodeSetter) childHierarchicalNode, parentHierarchyLevels, count[0]++);
               this.setHierarchyLevelForChildren( (HierarchicalNodeSetter) childHierarchicalNode);
            }
         );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setHierarchyLevels() {

      var count = new int[] {0};

      this.setHierarchyLevels(new int[] {0});

      var parentHierarchyLevels = this.getHierarchyLevels();

      //@formatter:off
      this.streamHierarchicalChildren().forEach
         (
            ( hierarchicalNode ) ->
            {
               this.setNodeHierarchyLevel( (HierarchicalNodeSetter) hierarchicalNode,parentHierarchyLevels,count[0]++);
               this.setHierarchyLevelForChildren( (HierarchicalNodeSetter) hierarchicalNode );
            }
         );
      //@formatter:on

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setHierarchyLevels(int[] hierarchyLevels) {

      Objects.requireNonNull(hierarchyLevels,
         "DocumentImpl:setHierarchyLevels, parameter \"hierarchyLevels\" cannot be null.");

      if (Objects.nonNull(this.hierarchyLevels)) {
         throw new IllegalStateException(
            "Object::setHierarchyLevels, member \"hierarchyLevels\" has already been set.");
      }

      this.hierarchyLevels = hierarchyLevels;
      this.hierarchyLevelsString =
         Arrays.stream(this.hierarchyLevels).map((level) -> level + 1).mapToObj(Integer::toString).collect(
            Collectors.joining("."));
   }

   /**
    * Sets the hierarchy level array of a node.
    *
    * @param childNode the node to set the hierarchy level array.
    * @param parentHierarchyLevels the hierarchy level array of the parent node.
    * @param childCount the sequence position of the <code>childNode</code> at it's hierarchy level.
    */

   private void setNodeHierarchyLevel(HierarchicalNodeSetter childNode, int[] parentHierarchyLevels, int childCount) {

      var childLevel = parentHierarchyLevels.length + 1;

      var childHierarchyLevel = Arrays.copyOf(parentHierarchyLevels, childLevel);

      childHierarchyLevel[childLevel - 1] = childCount;

      childNode.setHierarchyLevels(childHierarchyLevel);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<AttributeDefinitionImpl> streamAttributeDefinitionChildren() {
      return Stream.empty();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<HierarchicalNode> streamHierarchicalChildren() {
      @SuppressWarnings("unchecked")
      var nodes = (Collection<HierarchicalNode>) (Object) this.documentMap.values();

      return nodes.stream();
   }

}

/* EOF */
