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

package org.eclipse.osee.framework.core.xml.publishing;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A package prive super class encapsulating the parent and XML Element for part of a Word ML document.
 *
 * @author Loren K. Ashley
 */

abstract class AbstractElement implements WordElement {

   /**
    * A {@link Map} for storing hierarchical children by type. Children maybe stored as {@link AbstractElement} objects
    * or as {@link AbstractElementList} objects.
    */

   private final Map<Class<?>, Object> childMap;

   /**
    * Initially the list will be open. Once closed and attempts to add more children will result in an exception.
    */

   private boolean closed;

   /**
    * The XML DOM {@link Element} represented.
    */

   private final Element element;

   /**
    * Flag to indicate if this {@link WordElement} has any children.
    */

   private boolean isLeaf;

   /**
    * Flag to indicate if this {@link WordElement} is the root.
    */

   private final boolean isRoot;

   /**
    * The parent {@link WordElement}.
    */

   private final WordElement parent;

   /**
    * The Word ML XML tag for this element.
    */

   private final WordXmlTag wordXmlTag;

   /**
    * Creates a new {@link AbstractElement}.
    *
    * @apiNote This method is package private only sub-classes are intended to be exposed by the package.
    * @param parent the parent {@link org.w3c.dom.Document} or {@link AbstractElement} sub-class of the
    * {@link AbstractElement} being created.
    * @param element the {@link org.w3c.dom.Element} represented by this {@link AbstractElement}.
    * @throw NullPointerException when any of the parameters are <code>null</code>.
    */

   AbstractElement(WordElement parent, Element element, WordXmlTag wordXmlTag) {
      this.parent = Objects.requireNonNull(parent);
      this.element = Objects.requireNonNull(element);
      this.wordXmlTag = Objects.requireNonNull(wordXmlTag);
      this.childMap = new HashMap<>();
      this.closed = false;
      this.isLeaf = true;
      this.isRoot = Objects.isNull(this.parent);
   }

   /**
    * Creates a new {@link AbstractElement} for a top level element that does not have a parent.
    *
    * @apiNote This method is package private only sub-classes are intended to be exposed by the package.
    * @param element the {@link org.w3c.dom.Element} represented by this {@link AbstractElement}.
    * @throw NullPointerException when any of the parameters are <code>null</code>.
    */

   AbstractElement(Element element, WordXmlTag wordXmlTag) {
      this.parent = null;
      this.element = Objects.requireNonNull(element);
      this.wordXmlTag = Objects.requireNonNull(wordXmlTag);
      this.childMap = new HashMap<>();
      this.closed = false;
      this.isLeaf = true;
      this.isRoot = Objects.isNull(this.parent);
   }

   /**
    * Closes the {@link AbstractElement} to prevent any further modifications.
    *
    * @apiNote This method is package private. Sub-classes should only expose type specific hierarchy building methods.
    * @throws IllegalStateException when the list has already been closed.
    */

   void close() {

      if (this.closed) {
         throw new IllegalStateException("AbstractElement::close, the Object has already been closed.");
      }

      this.closed = true;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public <C> Optional<C> getChild(Class<C> childClass) {

      @SuppressWarnings("unchecked")
      var child = (C) this.childMap.get(childClass);

      return Optional.ofNullable(child);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Document getDocument() {
      return this.element.getOwnerDocument();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Element getElement() {
      return this.element;
   }

   /**
    * {@inheritDoc}
    *
    * @apiNote This method is package private. Sub-classes should provide a type specific method to obtain the parent.
    * @return the parent {@link AbstractElement}.
    * @implNote The top level class sub-class (derived) for a Word document is {@link WordDocument} which returns an
    * {@link org.w3c.dom.Document} as the parent. All implementations have a parent and this method should never return
    * <code>null</code>.
    * @throws NoSuchElementException {@inheritDoc}
    */

   @Override
   public WordElement getParent() {
      if (this.isRoot) {
         throw new NoSuchElementException("AbstractElement::getParent, WordElement is the root element.");
      }

      return this.parent;
   }

   /**
    * Gets the XML tag name for this element.
    *
    * @return the tag name.
    */

   public String getTag() {
      return this.wordXmlTag.getTagName();
   };

   /**
    * {@inheritDoc}
    */

   @Override
   public String getText() {
      return Objects.requireNonNull(this.element.getTextContent(),
         "AbstractElement::getText, Element text is unexpectedly null.");
   }

   /**
    * Gets the value of an element's attribute.
    *
    * @param wordXmlAttribute the {@link WordXmlAttribute} representing the attribute of the element to get.
    * @return when the attribute is present an {@link Optional} containing the attribute value; otherwise, an empty
    * {@link Optional}.
    */

   @Override
   public Optional<String> getAttribute(WordXmlAttribute wordXmlAttribute) {

      if (!this.wordXmlTag.isValidAttribute(wordXmlAttribute)) {
         return Optional.empty();
      }

      var attributeNode = this.element.getAttributeNode(wordXmlAttribute.getName());

      if (Objects.isNull(attributeNode)) {
         return Optional.empty();
      }

      return Optional.of(attributeNode.getNodeValue());
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isLeaf() {
      return this.isLeaf;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isRootElement() {
      return this.isRoot;
   }

   /**
    * Adds a hierarchical child to the map of children.
    *
    * @apiNote This method is package private. Sub-classes should only expose type specific hierarchy building methods.
    * @param <C> Scalar children must be a sub-class of {@link AbstractElement} and vector children must be a sub-class
    * of {@link AbstractElementList}.
    * @param child the child to be added to the map of children.
    * @throws NullPointerException when the parameter <code>child</code> is <code>null</code>.
    * @throws IllegalArgumentException when the parameter <code>child</code> is not an instance of
    * {@link AbstractElement} or {@link AbstractElementList}.
    * @throws IllegalStateException when a child with the same class as the parameter <code>child</code> has already
    * been set.
    */

   <C> void setChild(C child) {

      if (this.closed) {
         //@formatter:off
         throw
            new IllegalStateException
                   (
                      new StringBuilder( 1024 )
                             .append( "AbstractElement::setChild, cannot add childern after Object has been closed." ).append( "\n" )
                             .append( "   Child Class: " ).append( child.getClass().getName() ).append( "\n" )
                             .toString()
                   );
         //@formatter:off
      }

      Objects.requireNonNull(child, "AbstractElementWithChildern::setChild, the parameter \"child\" cannot be null.");

      if (!(child instanceof AbstractElement) && !(child instanceof AbstractElementList)) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new StringBuilder( 1024 )
                             .append( "AbstractElement::setChild, the parameter \"child\" is not of an allowed class." ).append( "\n" )
                             .append( "   Child Class:     " ).append( child.getClass().getName() ).append( "\n" )
                             .append( "   Allowed Classes: " ).append( "AbstractElement, AbstractElementList" ).append( "\n" )
                             .toString()
                   );
         //@formatter:on
      }

      /*
       * Assume the map does not already have an entry for the child class
       */

      var priorChild = this.childMap.put(child.getClass(), child);
      this.isLeaf = false;

      if (Objects.nonNull(priorChild)) {

         /*
          * Ooops, an entry was already preset. Restore the prior map entry and throw an exception.
          */

         this.childMap.put(child.getClass(), priorChild);
         //@formatter:off
         throw
            new IllegalStateException
                   (
                      new StringBuilder( 1024 )
                             .append( "AbstractElement::setChild, attempt to set a child that has already been set." ).append( "\n" )
                             .append( "   Child Class: " ).append( child.getClass().getName() ).append( "\n" )
                             .append( "   Prior Child: " ).append( priorChild ).append( "\n" )
                             .append( "   Child:       " ).append( child ).append( "\n" )
                             .toString()
                   );
         //@formatter:on
      }
   }

}

/* EOF */
