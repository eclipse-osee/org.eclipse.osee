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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An ordered container for the {@link AbstractElement} objects representing a sequence of Word ML elements.
 *
 * @author Loren K. Ashley
 * @param <P> the {@link AbstractElement} derived class for this element's parent or the {@link org.w3c.dom.Document}
 * class.
 * @param <C> the {@link AbstractElement} derived class for this element's children.
 */

public class AbstractElementList<P, C extends AbstractElement> implements Iterable<C> {

   /**
    * The parent {@link org.w3c.dom.Document} or {@link AbstractElement} sub-class.
    */

   private final P parent;

   /**
    * The list of the parent's children.
    */

   private final List<C> childList;

   /**
    * Initially the list will be open. Once closed and attempts to add more children will result in an exception.
    */

   private boolean closed;

   /**
    * Creates a new open and empty {@link AbstractElementList}.
    *
    * @apiNote This method is package private only sub-classes are intended to be exposed by the package.
    * @param parent the parent {@link org.w3c.dom.Document} or {@link AbstractElement} sub-class of the
    * {@link AbstractElementList} being created.
    * @throw NullPointerException when the parameter <code>parent</code> is <code>null</code>.
    */

   public AbstractElementList(P parent) {
      this.parent = Objects.requireNonNull(parent, "AbstractElementList::new, parameter \"parent\" cannot be null.");
      this.childList = new ArrayList<>();
      this.closed = false;
   }

   /**
    * Appends an {@link AbstractElement} representing a child of the parent to the end of the list of children.
    *
    * @apiNote This method is package private. Sub-classes should only expose type specific hierarchy building methods.
    * @param child the child to be appended to the list.
    * @throws IllegalStateException when the list has been closed.
    * @throws NullPointerException when the parameter <code>child</code> is <code>null</code>.
    */

   void add(C child) {

      if (this.closed) {
         throw new IllegalStateException("AbstractElementList::add, the list has already been closed.");
      }

      Objects.requireNonNull(child, "AbstractElementList::add, parameter \"child\" cannot be null.");

      this.childList.add(child);
   }

   /**
    * Closes the list to prevent any further modifications.
    *
    * @apiNote This method is package private. Sub-classes should only expose type specific hierarchy building methods.
    * @throws IllegalStateException when the list has been closed.
    */

   void close() {

      if (this.closed) {
         throw new IllegalStateException("AbstractElementList::close, the list has already been closed.");
      }

      this.closed = true;
   }

   /**
    * Gets the child at the specified index.
    *
    * @param i the list index of the child to get.
    * @return when the list contains an element at the specified index, an {@link Optional} containing the specified
    * child; otherwise, and empty {@link Optional}.
    */

   public Optional<C> get(int i) {
      //@formatter:off
      return
         ( ( i >= 0 ) && ( i < this.size() ) )
            ? Optional.of( this.childList.get(i) )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Gets the {@link AbstractElement} representing the XML DOM parent of the children ({@link AbstractElement}) on this
    * {@link AbstractElementList}.
    *
    * @apiNote This method is package private. Sub-classes should provide a type specific method to obtain the parent.
    * @return the parent {@link AbstractElement}.
    * @implNote The top level class sub-class (derived) for a Word document is {@link WordDocument} which returns an
    * {@link org.w3c.dom.Document} as the parent. All implementations have a parent and this method should never return
    * <code>null</code>.
    */

   P getParent() {
      return this.parent;
   }

   /**
    * Gets an {@link Iterator} over the children currently on the list. Any children added to the list after the
    * creation of the {@link Iterator} will not be included in the iteration. The {@link Iterator} does not support
    * modification of the {@link AbstractElementList}.
    *
    * @return an {@link Iterator} over the children on the list.
    */

   @Override
   public Iterator<C> iterator() {
      return new Iterator<C>() {
         private int i = 0;
         private final int m = AbstractElementList.this.childList.size();

         @Override
         public boolean hasNext() {
            return this.i < this.m;
         }

         @Override
         public C next() {
            if (this.i >= this.m) {
               throw new NoSuchElementException("AbstractElementList::iterator, no more elements.");
            }
            return AbstractElementList.this.childList.get(this.i++);
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   /**
    * Gets the number of children on the list.
    *
    * @return the number of children on the list.
    */

   public int size() {
      return this.childList.size();
   }

   /**
    * Gets a {@link Stream} of the children ({@link AbstractElement}) on the {@link AbstractElementList}.
    *
    * @return a {@link Stream} of {@link AbstractElement} objects.
    */

   public Stream<C> stream() {
      return this.childList.stream();
   }

}

/* EOF */