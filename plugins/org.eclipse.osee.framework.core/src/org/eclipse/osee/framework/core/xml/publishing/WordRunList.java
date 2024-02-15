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
 * Class to encapsulate a list of {@link WordRun} elements.
 *
 * @author Loren K. Ashley
 */

public class WordRunList<P extends AbstractElement> extends AbstractElementList<P, WordRun> {

   /**
    * {@link WordElementParserFactory} instance for creating a {@link WordRunList} with a {@link WordParagraph} parent.
    */

   public static WordElementParserFactory<WordParagraph, WordRunList<WordParagraph>, WordRun> wordParagraphParentFactory =
      new WordElementParserFactory<>(WordRunList::new, WordRun::new, WordMlTag.RUN);

   /**
    * Creates a new open and empty list for {@link WordRun}s.
    *
    * @param wordParagraph the {@link WordParagraph} that contains this {@link WordRun}.
    * @throws NullPointerException when the parameter <code>wordParagraph</code> is <code>null</code>.
    */

   public WordRunList(P parent) {
      super(parent);
   }

   /**
    * Gets the containing (parent) {@link WordParagraph}.
    *
    * @return the containing {@link WordRun}.
    */

   public Optional<WordParagraph> getWordParagraph() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordParagraph)
            ? Optional.of((WordParagraph)parent)
            : Optional.empty();
      //@formatter:on
   }
}

/* EOF */
