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
 * Class to encapsulate a list of the top level Word Sections in the body of a Word ML document.
 *
 * @author Loren K. Ashley
 */

public class WordSectionList<P extends AbstractElement> extends AbstractElementList<P, WordSection> {

   /**
    * {@link WordElementParserFactory} instance for creating a {@link WordSectionList} with a {@link WordBody} parent.
    */

   public static WordElementParserFactory<WordBody, WordSectionList<WordBody>, WordSection> wordBodyParentFactory =
      new WordElementParserFactory<>(WordSectionList::new, WordSection::new, WordMlTag.SECTION);

   /**
    * Creates a new open and empty list for {@link WordSection}s.
    *
    * @param wordBody the {@link WordBody} for the body of the Word ML document.
    * @throws NullPointerException when the parameter <code>wordBody</code> is <code>null</code>.
    */

   public WordSectionList(P parent) {
      super(parent);
   }

   /**
    * Gets the containing (parent) {@link WordBody}.
    *
    * @return the containing {@link WordBody}.
    */

   public Optional<WordBody> getWordBody() {
      final var parent = this.getParent();
      //@formatter:off
      return
         (parent instanceof WordBody)
            ? Optional.of((WordBody)parent)
            : Optional.empty();
      //@formatter:on

   }

}

/* EOF */
