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
 * Class to encapsulate a list of the top level Word Paragraphs in the sub-section of a Word ML document.
 *
 * @author Loren K. Ashley
 */

public class WordParagraphList extends AbstractElementList<WordSubSection, WordParagraph> {

   /**
    * Creates a new open and empty list for {@link WordParagraph}s.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param wordSubSection the {@link WordSubSection} for the sub-section of the Word ML document.
    * @throws NullPointerException when the parameter <code>wordSubSection</code> is <code>null</code>.
    */

   WordParagraphList(WordSubSection wordSubSection) {
      super(wordSubSection);
   }

   /**
    * Gets the containing (parent) {@link WordSubSection}.
    *
    * @return the containing {@link WordSubSection}.
    */

   public WordSubSection getWordSubSection() {
      return this.getParent();
   }

}

/* EOF */
