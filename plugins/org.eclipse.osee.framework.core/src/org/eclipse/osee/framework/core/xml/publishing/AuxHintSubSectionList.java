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

/**
 * Class to encapsulate a list of the top level Word sub-sections in a section of a Word ML document.
 *
 * @author Loren K. Ashley
 */

public class AuxHintSubSectionList<P extends AbstractElement> extends AbstractElementList<P, AuxHintSubSection> {

   /**
    * {@link WordElementParserFactory} instance for creating a {@link AuxHintSubSectionList} with a {@link WordSection}
    * parent.
    */

   public static WordElementParserFactory<WordSection, AuxHintSubSectionList<WordSection>, AuxHintSubSection> wordSectionParentFactory =
      new WordElementParserFactory<>(AuxHintSubSectionList::new, AuxHintSubSection::new, AuxHintTag.SUBSECTION);

   /**
    * {@link WordElementParserFactory} instance for creating a {@link AuxHintSubSectionList} with a
    * {@link AuxHintSubSection} parent.
    */

   public static WordElementParserFactory<AuxHintSubSection, AuxHintSubSectionList<AuxHintSubSection>, AuxHintSubSection> wordSubSectionParentFactory =
      new WordElementParserFactory<>(AuxHintSubSectionList::new, AuxHintSubSection::new, AuxHintTag.SUBSECTION);

   /**
    * Creates a new open and empty list for {@link AuxHintSubSection}s.
    *
    * @param wordSection the {@link WordSection} that contains this {@link AuxHintSubSectionList}.
    * @throws NullPointerException when the parameter <code>wordSection</code> is <code>null</code>.
    */

   public AuxHintSubSectionList(P parent) {
      super(parent);
   }

   /**
    * Gets the containing (parent) {@link WordSection}.
    *
    * @return the containing {@link WordSection}.
    */

   public Optional<WordSection> getWordSection() {
      final var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordSection)
            ? Optional.of((WordSection)parent)
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Gets the containing (parent) {@link AuxHintSubSection}.
    *
    * @return the containing {@link AuxHintSubSection}.
    */

   public Optional<AuxHintSubSection> getWordSubSection() {
      final var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof AuxHintSubSection)
            ? Optional.of((AuxHintSubSection)parent)
            : Optional.empty();
      //@formatter:on
   }

}

/* EOF */
