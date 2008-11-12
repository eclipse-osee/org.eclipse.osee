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
import java.util.Map;
import org.eclipse.osee.framework.db.connection.exception.InvalidTaggerException;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProvider;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProviderManager;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTaggerProviderManager implements IAttributeTaggerProviderManager {

   private Map<String, IAttributeTaggerProvider> attributeTaggerProviders;

   public AttributeTaggerProviderManager() {
      this.attributeTaggerProviders = Collections.synchronizedMap(new HashMap<String, IAttributeTaggerProvider>());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.IAttributeTaggerProviderManager#addAttributeTaggerProvider(org.eclipse.osee.framework.search.engine.IAttributeTaggerProvider)
    */
   @Override
   public void addAttributeTaggerProvider(IAttributeTaggerProvider attributeTaggerProvider) {
      synchronized (this.attributeTaggerProviders) {
         this.attributeTaggerProviders.put(attributeTaggerProvider.getClass().getSimpleName(), attributeTaggerProvider);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.IAttributeTaggerProviderManager#removeAttributeTaggerProvider(org.eclipse.osee.framework.search.engine.IAttributeTaggerProvider)
    */
   @Override
   public void removeAttributeTaggerProvider(IAttributeTaggerProvider attributeTaggerProvider) {
      synchronized (this.attributeTaggerProviders) {
         this.attributeTaggerProviders.remove(attributeTaggerProvider);
      }
   }

   private IAttributeTaggerProvider getProvider(String taggerId) throws InvalidTaggerException {
      IAttributeTaggerProvider toReturn = this.attributeTaggerProviders.get(taggerId);
      if (toReturn == null) {
         throw new InvalidTaggerException();
      }
      return toReturn;
   }

   public void tagIt(AttributeData attributeData, ITagCollector collector) throws Exception {
      IAttributeTaggerProvider provider = getProvider(attributeData.getTaggerId());
      provider.tagIt(attributeData, collector);
   }

   public boolean find(AttributeData attributeData, String value) throws Exception {
      IAttributeTaggerProvider provider = getProvider(attributeData.getTaggerId());
      return provider.find(attributeData, value);
   }

}
