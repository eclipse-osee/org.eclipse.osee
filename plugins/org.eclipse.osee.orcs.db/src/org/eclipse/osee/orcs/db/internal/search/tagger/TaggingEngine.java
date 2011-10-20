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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.db.internal.search.util.WordOrderMatcher;
import org.eclipse.osee.orcs.search.CaseType;

/**
 * @author Roberto E. Escobar
 */
public class TaggingEngine {

   private final Map<String, Tagger> taggers = new HashMap<String, Tagger>();
   private final AttributeTypeCache attributeTypeCache;
   private final TagProcessor tagProcessor;

   public TaggingEngine(TagProcessor tagProcessor, AttributeTypeCache attributeTypeCache) {
      this.tagProcessor = tagProcessor;
      this.attributeTypeCache = attributeTypeCache;
      taggers.put("DefaultAttributeTaggerProvider", new DefaultAttributeTagger(tagProcessor, new WordOrderMatcher()));
      taggers.put("XmlAttributeTaggerProvider", new XmlAttributeTagger(tagProcessor, new WordOrderMatcher()));
   }

   public TagProcessor getTagProcessor() {
      return tagProcessor;
   }

   public Tagger getDefaultTagger() throws OseeCoreException {
      return getTagger("DefaultAttributeTaggerProvider");
   }

   public Tagger getTagger(Identity<Long> identity) throws OseeCoreException {
      AttributeType attributeType = attributeTypeCache.get(identity);
      Conditions.checkNotNull(attributeType, "attributeType", "Unable to find attribute type with identity [%s]",
         identity);
      String taggerId = attributeType.getTaggerId();
      return getTagger(taggerId);
   }

   private Tagger getTagger(String alias) throws OseeCoreException {
      String key = alias;
      if (key.contains(".")) {
         key = Lib.getExtension(key);
      }
      Tagger tagger = taggers.get(key);
      Conditions.checkNotNull(tagger, "tagger", "Unable to find tagger for [%s]", alias);
      return tagger;
   }

   public void tagIt(ReadableAttribute<?> attribute, TagCollector collector) throws OseeCoreException {
      Tagger tagger = getTagger(attribute.getAttributeType());
      tagger.tagIt(attribute, collector);
   }

   public List<MatchLocation> find(ReadableAttribute<?> attribute, String toSearch, CaseType caseType, boolean matchAllLocations) throws OseeCoreException {
      Tagger tagger = getTagger(attribute.getAttributeType());
      return tagger.find(attribute, toSearch, caseType, matchAllLocations);
   }
}
