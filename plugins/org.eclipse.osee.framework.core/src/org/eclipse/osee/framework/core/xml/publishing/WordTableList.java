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
 * Class to encapsulate a list of the top level Word tables in the body of a Word ML document.
 *
 * @author Loren K. Ashley
 */

public class WordTableList extends AbstractElementList<WordBody, WordTable> {

   /**
    * Creates a new open and empty list for {@link WordTable}s.
    *
    * @apiNote This method is package private. Objects are created by package public methods in the class
    * {@link PublishingXmlUtils}.
    * @param wordBody the {@link WordBody} for the body of the Word ML document.
    * @throws NullPointerException when the parameter <code>wordBody</code> is <code>null</code>.
    */

   public WordTableList(WordBody wordBody) {
      super(wordBody);
   }

   /**
    * Gets the containing (parent) {@link WordBody}.
    *
    * @return the containing {@link WordBody}.
    */

   @SuppressWarnings("cast")
   public Optional<WordBody> getWordBody() {
      var parent = this.getParent();
      //@formatter:off
      return
         (parent instanceof WordBody)
            ? Optional.of( parent )
            : Optional.empty();
      //@formatter:on
   }

}

/* EOF */
