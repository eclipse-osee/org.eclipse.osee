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
package org.eclipse.osee.framework.search.engine.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.SearchOptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProvider;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProviderManager;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTaggerProviderManagerImpl implements IAttributeTaggerProviderManager {

   private final Map<String, IAttributeTaggerProvider> attributeTaggerProviders =
      new ConcurrentHashMap<String, IAttributeTaggerProvider>();

   private final AttributeTypeCache typeCache;

   public AttributeTaggerProviderManagerImpl(AttributeTypeCache typeCache) {
      this.typeCache = typeCache;
   }

   @Override
   public void addAttributeTaggerProvider(IAttributeTaggerProvider attributeTaggerProvider) {
      this.attributeTaggerProviders.put(attributeTaggerProvider.getClass().getSimpleName(), attributeTaggerProvider);
   }

   @Override
   public void removeAttributeTaggerProvider(IAttributeTaggerProvider attributeTaggerProvider) {
      this.attributeTaggerProviders.remove(attributeTaggerProvider);
   }

   private IAttributeTaggerProvider getProvider(int attrTypeId) throws OseeCoreException {
      AttributeType attributeType = typeCache.getById(attrTypeId);
      IAttributeTaggerProvider toReturn = this.attributeTaggerProviders.get(attributeType.getTaggerId());
      if (toReturn == null) {
         throw new OseeStateException("No tagger found for id [%s]", attributeType.getTaggerId());
      }
      return toReturn;
   }

   @Override
   public void tagIt(AttributeData attributeData, ITagCollector collector) throws OseeCoreException {
      IAttributeTaggerProvider provider = getProvider(attributeData.getAttrTypeId());
      provider.tagIt(attributeData, collector);
   }

   @Override
   public List<MatchLocation> find(AttributeData attributeData, String toSearch, SearchOptions options) throws OseeCoreException {
      IAttributeTaggerProvider provider = getProvider(attributeData.getAttrTypeId());
      return provider.find(attributeData, toSearch, options);
   }
}