/*********************************************************************
 * Copyright (c) 2023 Boeing
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
 * Class to encapsulate a list of {@link WordHlink} elements.
 *
 * @author Loren K. Ashley
 */

public class WordHlinkList<P extends AbstractElement> extends AbstractElementList<P, WordHlink> {

   /**
    * {@link WordElementParserFactory} instance for creating a {@link WordHlinkList} with a {@link WordParagraph}
    * parent.
    */

   public static WordElementParserFactory<WordParagraph, WordHlinkList<WordParagraph>, WordHlink> wordParagraphParentFactory =
      new WordElementParserFactory<>(WordHlinkList::new, WordHlink::new, WordMlTag.HLINK);

   /**
    * Creates a new open and empty list for {@link WordHlink}s.
    *
    * @param wordParagraph the {@link WordParagraph} that contains this {@link WordHlink}.
    * @throws NullPointerException when the parameter <code>wordParagraph</code> is <code>null</code>.
    */

   public WordHlinkList(P parent) {
      super(parent);
   }

   /**
    * Gets the containing (parent) {@link WordParagraph}.
    *
    * @return the containing {@link WordParagraph}.
    */

   public Optional<WordParagraph> getWordParagraph() {
      var parent = this.getParent();
      //@formatter:off
      return
         (parent instanceof WordParagraph)
            ? Optional.of( (WordParagraph) parent )
            : Optional.empty();
      //@formatter:on
   }

}

/* EOF */
