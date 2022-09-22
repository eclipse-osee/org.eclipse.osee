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

import java.util.NoSuchElementException;
import java.util.Optional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Interface containing general methods to be implemented by objects representing part of a Word document.
 *
 * @author Loren K. Ashley
 */

public interface WordElement {

   /**
    * Gets a hierarchical child by class.
    *
    * @param <C> Scalar children must be a sub-class of {@link AbstractElement} and vector children must be a sub-class
    * of {@link AbstractElementList}.
    * @param childClass the class of the child to get.
    * @return when a child with the specified class has been set, an {@link Optional} containing the child; otherwise,
    * an empty {@link Optional}.
    * @throws NoSuchElementException when the {@link WordElement} is a leaf of the Word document.
    */

   <C> Optional<C> getChild(Class<C> childClass);

   /**
    * Gets the {@link org.w3c.dom.Document} that owns the {@link org.w3c.dom.Element} that is represented by the
    * interface implementation.
    *
    * @return the XML DOM {@link Document}.
    */

   Document getDocument();

   /**
    * Gets the {@link org.w3c.dom.Element} represented by the interface implementation.
    *
    * @return the XML DOM {@link Element}.
    */

   Element getElement();

   /**
    * Gets the hierarchical parent of the {@link WordElement}.
    *
    * @return the hierarchical parent of the {@link WordElement}.
    * @throws NoSuchElementException when the {@link WordElement} is the root element of the Word document.
    */

   WordElement getParent();

   /**
    * Gets the text content of this element and its descendants in the XML DOM.
    *
    * @return the XML DOM {@link Element} text content.
    */

   String getText();

   /**
    * Predicate to determine if this {@link WordElement} is a leaf (does not have children) of the Word document.
    */

   boolean isLeaf();

   /**
    * Predicate to determine if this {@link WordElement} is the root (does not have parent) element of the Word
    * document.
    *
    * @return <code>true</code> when the {@link WordElement} is the root element; otherwise, <code>false</code>.
    */

   boolean isRootElement();
}

/* EOF */