/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.tagger;

import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class TaggingEngine {

   private final Map<String, Tagger> taggers;
   private final TagProcessor tagProcessor;

   public TaggingEngine(Map<String, Tagger> taggers, TagProcessor tagProcessor) {
      this.taggers = taggers;
      this.tagProcessor = tagProcessor;
   }

   public TagProcessor getTagProcessor() {
      return tagProcessor;
   }

   public Tagger getDefaultTagger() {
      return getTagger("DefaultAttributeTaggerProvider");
   }

   private String normalize(String alias) {
      String key = alias;
      if (Strings.isValid(key) && key.contains(".")) {
         key = Lib.getExtension(key);
      }
      return key;
   }

   public boolean hasTagger(String taggerId) {
      String key = normalize(taggerId);
      Tagger tagger = taggers.get(key);
      return tagger != null;
   }

   public Tagger getTagger(String taggerId) {
      String key = normalize(taggerId);
      Tagger tagger = taggers.get(key);
      Conditions.checkNotNull(tagger, "tagger", "Unable to find tagger for [%s]", taggerId);
      return tagger;
   }

}
