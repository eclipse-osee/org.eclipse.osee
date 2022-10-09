/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.util;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
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
import org.eclipse.osee.orcs.db.internal.search.tagger.Tagger;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataMatcher {

   private final Log logger;
   private final TaggingEngine engine;

   public AttributeDataMatcher(Log logger, TaggingEngine engine) {
      this.logger = logger;
      this.engine = engine;
   }

   public List<MatchLocation> process(HasCancellation cancellation, AttributeData<?> data, Collection<String> valuesToMatch, Collection<AttributeTypeId> typesFilter, QueryOption... options) throws Exception {
      logger.debug("Attribute Data match for attr[%s] - [%s]", data.getId(), valuesToMatch);
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
      AttributeTypeToken attrType = source.getAttributeType();
      if (typesFilter.contains(QueryBuilder.ANY_ATTRIBUTE_TYPE) || typesFilter.contains(attrType)) {
         checkCancelled(cancellation);
         TaggerTypeToken taggerType = attrType.getTaggerType();
         if (taggerType.isValid()) {
            Tagger tagger = engine.getTagger(taggerType);
            checkCancelled(cancellation);
            try {
               List<MatchLocation> matched = Lists.newLinkedList();
               for (String toMatch : valuesToMatch) {
                  matched.addAll(tagger.find(source.getResourceInput(), toMatch, true, options));
               }
               return matched;
            } catch (Exception ex) {
               logger.error(ex, "Error searching attrId [%d] gamma [%d]", data.getId(), data.getVersion().getGammaId());
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

   private final class AttributeIndexedResource extends ByteSource implements IndexedResource {
      private final AttributeData<?> attrData;

      public AttributeIndexedResource(AttributeData<?> attrData) {
         super();
         this.attrData = attrData;
      }

      @Override
      public AttributeTypeToken getAttributeType() {
         return attrData.getType();
      }

      @Override
      public GammaId getGammaId() {
         return attrData.getVersion().getGammaId();
      }

      @Override
      public InputStream getResourceInput() throws IOException {
         return openStream();
      }

      @Override
      public InputStream openStream() throws IOException {
         InputStream stream = null;
         DataProxy<?> dataProxy = attrData.getDataProxy();
         if (dataProxy instanceof BinaryDataProxy) {
            ByteBuffer valueAsBytes;
            try {
               valueAsBytes = ((BinaryDataProxy) dataProxy).getValueAsBytes();
            } catch (OseeCoreException ex) {
               throw new IOException(ex);
            }
            if (valueAsBytes != null) {
               stream = new ByteArrayInputStream(valueAsBytes.array());
            }
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
