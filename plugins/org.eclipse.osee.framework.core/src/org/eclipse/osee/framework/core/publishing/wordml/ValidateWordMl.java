/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core.publishing.wordml;

import org.eclipse.osee.framework.core.xml.publishing.PublishingXmlUtils;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * A validation class for Word Markup Language content.
 *
 * @author Loren K. Ashley
 */

public class ValidateWordMl {

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private ValidateWordMl() {
   }

   /**
    * Checks that Word Markup Language content is valid XML.
    *
    * @param wordMl the content to validate.
    * @return <code>null</code> when the content is valid XML; otherwise, a {@link Message} describing the XML parse
    * error.
    */

   public static Message validateWordMl(String wordMl) {

      if (Strings.isInvalidOrBlank(wordMl)) {
         return null;
      }

      var publishingXmlUtils = new PublishingXmlUtils();

      publishingXmlUtils.parse(wordMl);

      if (publishingXmlUtils.isOk()) {
         return null;
      }

      //@formatter:off
      return
         new Message()
                .title( "Word Markup Language template content failed to parse." )
                .reasonFollows( publishingXmlUtils.getLastError().get() );
      //@formatter:on
   }

}

/* EOF */
