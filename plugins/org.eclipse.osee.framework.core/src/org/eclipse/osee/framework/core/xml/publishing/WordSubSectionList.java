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
 * Class to encapsulate a list of the top level Word sub-sections in a section of a Word ML document.
 *
 * @author Loren K. Ashley
 */

public class WordSubSectionList extends AbstractElementList<WordSection, WordSubSection> {

   /**
    * Creates a new open and empty list for {@link WordSubSection}s.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param wordSection the {@link WordSection} for the body of the Word ML document.
    * @throws NullPointerException when the parameter <code>wordSection</code> is <code>null</code>.
    */

   WordSubSectionList(WordSection wordSection) {
      super(wordSection);
   }

   /**
    * Gets the containing (parent) {@link WordSection}.
    *
    * @return the containing {@link WordSection}.
    */

   public WordSection getWordSection() {
      return this.getParent();
   }

}

/* EOF */
