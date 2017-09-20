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
package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.DataProxyFactory;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataProxyFactory implements ProxyDataFactory {

   private final DataProxyFactoryProvider proxyProvider;
   private final AttributeTypes attributeTypeCache;
   private final JdbcClient jdbcClient;

   public AttributeDataProxyFactory(DataProxyFactoryProvider proxyProvider, JdbcClient jdbcClient, AttributeTypes attributeTypes) {
      super();
      this.proxyProvider = proxyProvider;
      this.attributeTypeCache = attributeTypes;
      this.jdbcClient = jdbcClient;
   }

   @Override
   public DataProxy createProxy(long typeUuid, Object value, String uri) throws OseeCoreException {
      AttributeTypeId attributeType = attributeTypeCache.get(typeUuid);

      Conditions.checkNotNull(attributeType, "AttributeType", "Unable to find attributeType for [%s]", typeUuid);

      String dataProxyFactoryId = attributeTypeCache.getAttributeProviderId(attributeType);

      DataProxyFactory factory = proxyProvider.getFactory(dataProxyFactoryId);
      Conditions.checkNotNull(factory, "DataProxyFactory", "Unable to find data proxy factory for [%s]",
         dataProxyFactoryId);

      Object checkedValue = intern(attributeType, value);
      DataProxy proxy = factory.createInstance(dataProxyFactoryId);
      proxy.setData(checkedValue, uri);
      return proxy;
   }

   private Object intern(AttributeTypeId attributeType, Object original) throws OseeCoreException {
      Object value = original;
      if (attributeTypeCache.isEnumerated(attributeType) && value instanceof String) {
         value = Strings.intern((String) value);
      }
      return value;
   }

   @Override
   public DataProxy createProxy(long typeUuid, Object... data) throws OseeCoreException {
      Conditions.checkNotNull(data, "data");
      Conditions.checkExpressionFailOnTrue(data.length < 2, "Data must have at least [2] elements - size was [%s]",
         data.length);

      Object value = data[0];
      String uri = (String) data[1];
      return createProxy(typeUuid, value, uri);
   }
}
