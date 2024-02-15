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

public class WordInstructionTextList<P extends AbstractElement> extends AbstractElementList<P, WordInstructionText> {

   /**
    * {@link WordElementParserFactory} instance for creating a {@link WordInstructionTextList} with a {@link WordRun}
    * parent.
    */

   public static WordElementParserFactory<WordRun, WordInstructionTextList<WordRun>, WordInstructionText> wordRunParentFactory =
      new WordElementParserFactory<>(WordInstructionTextList::new, WordInstructionText::new,
         WordMlTag.INSTRUCTION_TEXT);

   /**
    * Creates a new open and empty list for {@link WordInstructionText}s.
    *
    * @param wordParagraph the {@link WordRun} that contains this {@link WordInstructionText}.
    * @throws NullPointerException when the parameter <code>wordRun</code> is <code>null</code>.
    */

   public WordInstructionTextList(P parent) {
      super(parent);
   }

   /**
    * Gets the containing (parent) {@link WordRun}.
    *
    * @return the containing {@link WordRun}.
    */

   public Optional<WordRun> getWordRun() {
      var parent = this.getParent();

      //@formatter:off
      return
         (parent instanceof WordRun)
            ? Optional.of((WordRun) parent)
            : Optional.empty();
   }
}

/* EOF */