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
 * Class to encapsulate a Word Section that is not contained within another Word Section.
 *
 * @author Loren K. Ashley
 */

public class WordSection extends AbstractElement {

   //@formatter:off
   private static Pair<Class<?>,Class<?>> wordSubSectionListWithParentWordSectionChildKey =
      Pair.createNonNullImmutable( WordTextList.class, WordTableColumn.class );
   //@formatter:on

   /**
    * Creates a new {@link WordSection}.
    *
    * @param parent the {@link WordElement} implementation that is considered the parent by the parser implementation.
    * @param wordSectionElement the {@link org.w3c.dom.Element} with the tag "wx:sect" in the Word ML.
    * @throws NullPointerException when either of the parameters <code>wordBody</code> or
    * <code>wordSectionElement</code> are <code>null</code>.
    */

   public WordSection(WordElement parent, Element wordSectionElement) {
      super(parent, wordSectionElement, WordMlTag.SECTION);
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
            ? Optional.of( (WordBody) parent )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Gets the {@link AuxHintSubSectionList} for the Section.
    *
    * @return when Sub-Sections have been parsed for the Section, a {@link Optional} containing a possibly empty
    * {@link AuxHintSubSectionList}; otherwise, a {@link Optional}.
    */

   public Optional<AuxHintSubSectionList<WordSection>> getWordSubSectionList() {
      return this.getChild(WordSection.wordSubSectionListWithParentWordSectionChildKey);
   }
}

/* EOF */
