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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.exception.InvalidTaggerException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProvider;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProviderManager;
import org.eclipse.osee.framework.search.engine.MatchLocation;
import org.eclipse.osee.framework.search.engine.SearchOptions;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTaggerProviderManager implements IAttributeTaggerProviderManager {

   private final Map<String, IAttributeTaggerProvider> attributeTaggerProviders;

   public AttributeTaggerProviderManager() {
      this.attributeTaggerProviders = Collections.synchronizedMap(new HashMap<String, IAttributeTaggerProvider>());
   }

   @Override
   public void addAttributeTaggerProvider(IAttributeTaggerProvider attributeTaggerProvider) {
      synchronized (this.attributeTaggerProviders) {
         this.attributeTaggerProviders.put(attributeTaggerProvider.getClass().getSimpleName(), attributeTaggerProvider);
      }
   }

   @Override
   public void removeAttributeTaggerProvider(IAttributeTaggerProvider attributeTaggerProvider) {
      synchronized (this.attributeTaggerProviders) {
         this.attributeTaggerProviders.remove(attributeTaggerProvider);
      }
   }

   private IAttributeTaggerProvider getProvider(int attrTypeId) throws OseeCoreException {
      AttributeTypeCache typeCache = Activator.getInstance().getOseeCachingService().getAttributeTypeCache();
      AttributeType attributeType = typeCache.getById(attrTypeId);
      IAttributeTaggerProvider toReturn = this.attributeTaggerProviders.get(attributeType.getTaggerId());
      if (toReturn == null) {
         throw new InvalidTaggerException();
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
