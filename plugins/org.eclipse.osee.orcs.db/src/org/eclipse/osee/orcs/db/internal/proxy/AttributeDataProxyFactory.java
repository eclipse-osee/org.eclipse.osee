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

package org.eclipse.osee.orcs.db.internal.proxy;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataProxy;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataProxyFactory {
   private final IResourceManager resourceManager;
   private final Log logger;

   public AttributeDataProxyFactory(IResourceManager resourceManager, Log logger) {
      this.resourceManager = resourceManager;
      this.logger = logger;
   }

   public <T> DataProxy<T> createProxy(AttributeTypeToken attributeTypeToken, T value, String uri) {
      @SuppressWarnings("unchecked")
      T checkedValue = (T) intern(attributeTypeToken, value);

      if (attributeTypeToken.isUri()) {
         @SuppressWarnings("unchecked")
         AbstractDataProxy<T> uriDataProxy = new UriDataProxy();
         uriDataProxy.setLogger(logger);
         uriDataProxy.setStorage(new Storage(resourceManager, uriDataProxy));
         uriDataProxy.setData(checkedValue, uri);
         return uriDataProxy;
      } else if (attributeTypeToken.isMapEntry()) {
         var mapEntryDataProxy = new MapEntryDataProxy();
         mapEntryDataProxy.setLogger(logger);
         mapEntryDataProxy.setStorage(new Storage(resourceManager, mapEntryDataProxy));
         mapEntryDataProxy.setDataByObject(checkedValue, uri);
         @SuppressWarnings("unchecked")
         var dataProxy = (DataProxy<T>) mapEntryDataProxy;
         return dataProxy;
      } else {
         var varCharDataProxy = new VarCharDataProxy<T>();
         varCharDataProxy.setLogger(logger);
         varCharDataProxy.setStorage(new Storage(resourceManager, varCharDataProxy));
         varCharDataProxy.setData(checkedValue, uri);
         return varCharDataProxy;
      }
   }

   private Object intern(AttributeTypeToken attributeType, Object original) {
      Object value = original;
      if (attributeType.isEnumerated() && value instanceof String) {
         value = Strings.intern((String) value);
      }
      return value;
   }
}