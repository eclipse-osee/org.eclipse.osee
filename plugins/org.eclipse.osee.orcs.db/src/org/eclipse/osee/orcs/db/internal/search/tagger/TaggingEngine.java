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
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class TaggingEngine {

   private final Map<String, Tagger> taggers;
   private final AttributeTypeCache attributeTypeCache;
   private final TagProcessor tagProcessor;

   public TaggingEngine(Map<String, Tagger> taggers, TagProcessor tagProcessor, AttributeTypeCache attributeTypeCache) {
      this.taggers = taggers;
      this.tagProcessor = tagProcessor;
      this.attributeTypeCache = attributeTypeCache;
   }

   public TagProcessor getTagProcessor() {
      return tagProcessor;
   }

   public Tagger getDefaultTagger() throws OseeCoreException {
      return getTagger("DefaultAttributeTaggerProvider");
   }

   private String normalize(String alias) {
      String key = alias;
      if (Strings.isValid(key) && key.contains(".")) {
         key = Lib.getExtension(key);
      }
      return key;
   }

   public String getTaggerId(Identity<Long> identity) throws OseeCoreException {
      AttributeType attributeType = attributeTypeCache.get(identity);
      Conditions.checkNotNull(attributeType, "attributeType", "Unable to find attribute type with identity [%s]",
         identity);
      String taggerId = attributeType.getTaggerId();
      return normalize(taggerId);
   }

   public Tagger getTagger(Identity<Long> identity) throws OseeCoreException {
      AttributeType attributeType = attributeTypeCache.get(identity);
      Conditions.checkNotNull(attributeType, "attributeType", "Unable to find attribute type with identity [%s]",
         identity);
      String taggerId = attributeType.getTaggerId();
      Conditions.checkNotNull(taggerId, "taggerId", "Attribute type [%s] has no tagger defined",
         attributeType.getName());
      return getTagger(taggerId);
   }

   public Tagger getTagger(String alias) throws OseeCoreException {
      String key = normalize(alias);
      Tagger tagger = taggers.get(key);
      Conditions.checkNotNull(tagger, "tagger", "Unable to find tagger for [%s]", alias);
      return tagger;
   }

}
