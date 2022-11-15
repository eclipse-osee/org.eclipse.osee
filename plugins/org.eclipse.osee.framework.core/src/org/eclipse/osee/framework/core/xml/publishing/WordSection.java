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

import java.util.Optional;
import org.w3c.dom.Element;

/**
 * Class to encapsulate a Word Section that is not contained within another Word Section.
 *
 * @author Loren K. Ashley
 */

public class WordSection extends AbstractElement {

   /**
    * Creates a new {@link WordSection}.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param parent the {@link WordElement} implementation that is considered the parent by the parser implementation.
    * @param wordSectionElement the {@link org.w3c.dom.Element} with the tag "wx:sect" in the Word ML.
    * @throws NullPointerException when either of the parameters <code>wordBody</code> or
    * <code>wordSectionElement</code> are <code>null</code>.
    */

   WordSection(WordElement parent, Element wordSectionElement) {
      super(parent, wordSectionElement);
   }

   /**
    * For top level Word Sections, gets the containing (parent) {@link WordBody}.
    *
    * @return when the {@link WordSection} is for a top level Word Section, a {@link Optional} containing the parent
    * {@link WordBody}; otherwise, an empty {@link Optional}.
    */

   public Optional<WordBody> getWordBody() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordBody)
            ? Optional.of( (WordBody) this.getParent() )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Gets the {@link WordSubSectionList} for the Section.
    *
    * @return when Sub-Sections have been parsed for the Section, a {@link Optional} containing a possibly empty
    * {@link WordSubSectionList}; otherwise, a {@link Optional}.
    */

   public Optional<WordSubSectionList> getWordSectionRowList() {
      return this.getChild(WordSubSectionList.class);
   }
}

/* EOF */
