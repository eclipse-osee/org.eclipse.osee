/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.tagger;

import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.db.internal.search.util.MatcherFactory;

/**
 * @author Roberto E. Escobar
 */
public class TaggingEngine {
   private final TagProcessor tagProcessor;
   private final Tagger plainTextTagger;
   private final Tagger xmlTagger;

   public TaggingEngine(TagProcessor tagProcessor) {
      this.tagProcessor = tagProcessor;
      StreamMatcher matcher = MatcherFactory.createMatcher();
      plainTextTagger = new TextStreamTagger(tagProcessor, matcher);
      xmlTagger = new XmlTagger(tagProcessor, matcher);
   }

   public TagProcessor getTagProcessor() {
      return tagProcessor;
   }

   public Tagger getTagger(TaggerTypeToken taggerType) {
      if (taggerType.equals(TaggerTypeToken.PlainTextTagger)) {
         return plainTextTagger;
      }
      if (taggerType.equals(TaggerTypeToken.XmlTagger)) {
         return xmlTagger;
      }
      throw new OseeArgumentException("No tagger found for %s", taggerType);
   }
}