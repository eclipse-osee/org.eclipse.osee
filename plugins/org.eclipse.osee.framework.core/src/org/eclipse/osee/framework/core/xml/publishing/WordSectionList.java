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

/**
 * Class to encapsulate a list of the top level Word Sections in the body of a Word ML document.
 *
 * @author Loren K. Ashley
 */

public class WordSectionList extends AbstractElementList<WordBody, WordSection> {

   /**
    * Creates a new open and empty list for {@link WordSection}s.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param wordBody the {@link WordBody} for the body of the Word ML document.
    * @throws NullPointerException when the parameter <code>wordBody</code> is <code>null</code>.
    */

   WordSectionList(WordBody wordBody) {
      super(wordBody);
   }

   /**
    * Gets the containing (parent) {@link WordBody}.
    *
    * @return the containing {@link WordBody}.
    */

   public WordBody getWordBody() {
      return this.getParent();
   }

}

/* EOF */
