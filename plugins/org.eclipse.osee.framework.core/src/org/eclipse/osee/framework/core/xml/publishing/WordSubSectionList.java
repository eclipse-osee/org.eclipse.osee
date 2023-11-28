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

public class WordSubSectionList extends AbstractElementList<AbstractElement, WordSubSection> {

   /**
    * Creates a new open and empty list for {@link WordSubSection}s.
    *
    * @param wordSection the {@link WordSection} that contains this {@link WordSubSectionList}.
    * @throws NullPointerException when the parameter <code>wordSection</code> is <code>null</code>.
    */

   public WordSubSectionList(WordSection wordSection) {
      super(wordSection);
   }

   /**
    * Creates a new open and empty list for {@link WordSubSection}s.
    *
    * @param wordSubSection the {@link WordSubSection} that contains this {@link WordSubSectionList}.
    * @throws NullPointerException when the parameter <code>wordSubSection</code> is <code>null</code>.
    */

   public WordSubSectionList(WordSubSection wordSubSection) {
      super(wordSubSection);
   }

   /**
    * Gets the containing (parent) {@link WordSection}.
    *
    * @return the containing {@link WordSection}.
    */

   public Optional<WordSection> getWordSection() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordSection)
            ? Optional.of((WordSection)parent)
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Gets the containing (parent) {@link WordSubSection}.
    *
    * @return the containing {@link WordSubSection}.
    */

   public Optional<WordSubSection> getWordSubSection() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordSubSection)
            ? Optional.of((WordSubSection)parent)
            : Optional.empty();
      //@formatter:on
   }

}

/* EOF */
