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
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.w3c.dom.Element;

/**
 * Class to encapsulate a Word sub-section that is not contained within another Word sub-section.
 *
 * @author Loren K. Ashley
 */

public class AuxHintSubSection extends AbstractElement {

   //@formatter:off
   private static Pair<Class<?>,Class<?>> wordParagraphListWithParentWordSubSectionChildKey =
      Pair.createNonNullImmutable( WordParagraphList.class, AuxHintSubSection.class );
   //@formatter:on

   /**
    * Creates a new {@link AuxHintSubSection}.
    *
    * @param parent the {@link WordElement} implementation that is considered the parent by the parser implementation.
    * @param wordSubSectionElement the {@link org.w3c.dom.Element} with the tag "wx:sub-section" in the Word ML.
    * @throws NullPointerException when either of the parameters <code>wordSection</code> or
    * <code>wordSubSectionElement</code> are <code>null</code>.
    */

   public AuxHintSubSection(WordElement parent, Element wordSubSectionElement) {
      super(parent, wordSubSectionElement, AuxHintTag.SUBSECTION);
   }

   /**
    * For top level Word Sub-Sections, gets the containing (parent) {@link WordSection}.
    *
    * @return when the {@link AuxHintSubSection} is for a top level Word Sub-Section, a {@link Optional} containing the
    * parent {@link WordSection}; otherwise, an empty {@link Optional}.
    */

   public Optional<WordSection> getWordSection() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordSection)
            ? Optional.of( (WordSection) this.getParent() )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Gets the {@link WordParagraphList} for the Sub-Section.
    *
    * @return when Paragraphs have been parsed for the Sub-Section, a {@link Optional} containing a possibly empty
    * {@link WordParagraphList}; otherwise, a {@link Optional}.
    */

   public Optional<WordParagraphList<AuxHintSubSection>> getWordParagraphList() {
      return this.getChild(AuxHintSubSection.wordParagraphListWithParentWordSubSectionChildKey);
   }

}

/* EOF */
