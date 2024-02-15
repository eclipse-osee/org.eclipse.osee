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
 * Class to encapsulate a Word ML document.
 *
 * @author Loren K. Ashley
 */

public class WordDocument extends AbstractElement {

   //@formatter:off
   private static Pair<Class<?>,Class<?>> wordBodyChildKey =
      Pair.createNullableImmutable( WordBody.class, null );
   //@formatter:on

   //@formatter:off
   private static Pair<Class<?>,Class<?>> wordTableListOfWordDocumentChildKey =
      Pair.createNullableImmutable( WordTableList.class, WordDocument.class );
   //@formatter:on

   /**
    * Creates a new {@link WordDocument} object.
    *
    * @param wordDocumentElement The {@link org.w3c.dom.Element} with the tag "w:wordDocument" in the Word ML document.
    * @throws NullPointerException when either of the parameter <code>wordDocumentElement</code> is <code>null</code>.
    */

   public WordDocument(Element wordDocumentElement) {
      super(wordDocumentElement, WordMlTag.WORD_DOCUMENT);
   }

   /**
    * Gets the body of the Word ML document as a {@link WordBody}.
    *
    * @return when the {@link WordDocument} contains a {@link WordBody}, an {@link Optional} containing the
    * {@link WordBody}; otherwise, an empty {@link Optional}.
    */

   public Optional<WordBody> getWordBody() {
      return this.getChild(WordDocument.wordBodyChildKey);
   }

   public Optional<WordTableList<WordDocument>> getWordTableList() {
      return this.getChild(WordDocument.wordTableListOfWordDocumentChildKey);
   }
}

/* EOF */
