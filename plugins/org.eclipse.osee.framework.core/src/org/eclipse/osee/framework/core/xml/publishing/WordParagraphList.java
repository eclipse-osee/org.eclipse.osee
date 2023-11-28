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

public class WordParagraphList extends AbstractElementList<AbstractElement, WordParagraph> {

   /**
    * Creates a new open and empty list for {@link WordParagraph}s.
    *
    * @param wordSubSection the {@link WordSubSection} for the sub-section of the Word ML document.
    * @throws NullPointerException when the parameter <code>wordSubSection</code> is <code>null</code>.
    */

   public WordParagraphList(WordSubSection wordSubSection) {
      super(wordSubSection);
   }

   /**
    * Creates a new open and empty list for {@link WordParagraph}s.
    *
    * @param wordSection the {@link WordSection} for the section of the Word ML document.
    * @throws NullPointerException when the parameter <code>wordSection</code> is <code>null</code>.
    */

   public WordParagraphList(WordSection wordSection) {
      super(wordSection);
   }

   /**
    * Gets the containing (parent) {@link WordSubSection}.
    *
    * @return the containing {@link WordSubSection}.
    */

   public WordSubSection getWordSubSection() {
      return (WordSubSection) this.getParent();
   }

   /**
    * Gets the containing (parent) {@link WordSection}.
    *
    * @return the containing {@link WordSection}.
    */

   public WordSection getWordSection() {
      return (WordSection) this.getParent();
   }

}

/* EOF */
