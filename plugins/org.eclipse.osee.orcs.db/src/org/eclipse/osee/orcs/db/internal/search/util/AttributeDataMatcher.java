/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.util;

import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.executor.HasCancellation;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.BinaryDataProxy;
import org.eclipse.osee.orcs.core.ds.CharacterDataProxy;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.IndexedResource;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.search.tagger.Tagger;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataMatcher {

   private final Log logger;
   private final TaggingEngine engine;
   private final AttributeTypes attrTypes;

   public AttributeDataMatcher(Log logger, TaggingEngine engine, AttributeTypes attrTypes) {
      this.logger = logger;
      this.engine = engine;
      this.attrTypes = attrTypes;
   }

   protected Tagger getTagger(String taggerId) {
      Tagger toReturn = null;
      if (Strings.isValid(taggerId)) {
         toReturn = engine.getTagger(taggerId);
      }
      return toReturn;
   }

   public List<MatchLocation> process(HasCancellation cancellation, AttributeData<?> data, Collection<String> valuesToMatch, Collection<AttributeTypeId> typesFilter, QueryOption... options) throws Exception {
      logger.debug("Attribute Data match for attr[%s] - [%s]", data.getLocalId(), valuesToMatch);
      if (Conditions.hasValues(options)) {
         return matchTokenizedValue(cancellation, data, valuesToMatch, typesFilter, options);
      } else {
         return matchValuesExactly(cancellation, data, valuesToMatch);
      }
   }

   private void checkCancelled(HasCancellation cancellation) {
      if (cancellation != null) {
         cancellation.checkForCancelled();
      }
   }

   private List<MatchLocation> matchValuesExactly(HasCancellation cancellation, AttributeData<?> data, Iterable<String> valuesToMatch) throws Exception {
      String value = getValue(data);
      List<MatchLocation> matched = Lists.newLinkedList();
      for (String toMatch : valuesToMatch) {
         checkCancelled(cancellation);
         if (value.equals(toMatch) || !Strings.isValid(value) && !Strings.isValid(toMatch)) {
            MatchLocation matchLocation = new MatchLocation(1, value.length());
            matched.add(matchLocation);
         }
      }
      return matched;
   }

   private List<MatchLocation> matchTokenizedValue(HasCancellation cancellation, AttributeData<?> data, Iterable<String> valuesToMatch, Collection<AttributeTypeId> typesFilter, QueryOption... options) {
      AttributeIndexedResource source = adapt(data);
      AttributeTypeId attrType = AttributeTypeId.valueOf(source.getTypeUuid());
      if (typesFilter.contains(attrType)) {
         checkCancelled(cancellation);
         String taggerId = attrTypes.getTaggerId(attrType);
         Tagger tagger = getTagger(taggerId);
         if (tagger != null) {
            checkCancelled(cancellation);
            try {
               List<MatchLocation> matched = Lists.newLinkedList();
               for (String toMatch : valuesToMatch) {
                  matched.addAll(tagger.find(source, toMatch, true, options));
               }
               return matched;
            } catch (Exception ex) {
               logger.error(ex, "Error searching attrId [%d] gamma [%d]", data.getLocalId(),
                  data.getVersion().getGammaId());
            }
         }
      }
      return null;
   }

   private AttributeIndexedResource adapt(AttributeData<?> data) {
      return new AttributeIndexedResource(data);
   }

   private String getValue(AttributeData<?> data) {
      String value = "";
      DataProxy dataProxy = data.getDataProxy();
      if (dataProxy instanceof CharacterDataProxy) {
         value = ((CharacterDataProxy) dataProxy).getValueAsString();
      } else {
         value = dataProxy.toString();
      }
      return value;
   }

   private final class AttributeIndexedResource implements IndexedResource {
      private final AttributeData<?> attrData;

      public AttributeIndexedResource(AttributeData<?> attrData) {
         super();
         this.attrData = attrData;
      }

      @Override
      public long getTypeUuid() {
         return attrData.getTypeUuid();
      }

      @Override
      public long getGammaId() {
         return attrData.getVersion().getGammaId().getId();
      }

      @Override
      public InputStream getInput() throws IOException {
         InputStream stream = null;
         DataProxy<?> dataProxy = attrData.getDataProxy();
         if (dataProxy instanceof BinaryDataProxy) {
            ByteBuffer valueAsBytes;
            try {
               valueAsBytes = ((BinaryDataProxy) dataProxy).getValueAsBytes();
            } catch (OseeCoreException ex) {
               throw new IOException(ex);
            }
            stream = new ByteArrayInputStream(valueAsBytes.array());
         } else {
            String value = null;
            if (dataProxy instanceof CharacterDataProxy) {
               try {
                  value = ((CharacterDataProxy<?>) dataProxy).getValueAsString();
               } catch (OseeCoreException ex) {
                  throw new IOException(ex);
               }
            } else {
               value = dataProxy.toString();
            }
            stream = new ByteArrayInputStream(value.getBytes("UTF-8"));
         }
         return stream;
      }
   }
}
