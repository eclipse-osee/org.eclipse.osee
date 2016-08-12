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

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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
   public DataProxy createProxy(long typeUuid, String value, String uri) throws OseeCoreException {
      IAttributeType attributeType = attributeTypeCache.getByUuid(typeUuid);
      Conditions.checkNotNull(attributeType, "AttributeType", "Unable to find attributeType for [%s]", typeUuid);

      String dataProxyFactoryId = attributeTypeCache.getAttributeProviderId(attributeType);
      if (dataProxyFactoryId.contains(".")) {
         dataProxyFactoryId = Lib.getExtension(dataProxyFactoryId);
      }

      DataProxyFactory factory = proxyProvider.getFactory(dataProxyFactoryId);
      Conditions.checkNotNull(factory, "DataProxyFactory", "Unable to find data proxy factory for [%s]",
         dataProxyFactoryId);

      String checkedValue = intern(attributeType, value);
      DataProxy proxy = factory.createInstance(dataProxyFactoryId);
      proxy.setData(checkedValue, uri);
      return proxy;
   }

   private String intern(IAttributeType attributeType, String original) throws OseeCoreException {
      String value = original;
      if (isEnumOrBoolean(attributeType)) {
         value = Strings.intern(value);
      }
      return value;
   }

   protected boolean isEnumOrBoolean(IAttributeType attributeType) throws OseeCoreException {
      boolean isEnumAttribute = attributeTypeCache.isEnumerated(attributeType);
      boolean isBooleanAttribute = attributeTypeCache.isBooleanType(attributeType);
      return isBooleanAttribute || isEnumAttribute;
   }

   @Override
   public DataProxy createProxy(long typeUuid, Object... data) throws OseeCoreException {
      Conditions.checkNotNull(data, "data");
      Conditions.checkExpressionFailOnTrue(data.length < 2, "Data must have at least [2] elements - size was [%s]",
         data.length);

      String value = (String) data[0];
      String uri = (String) data[1];
      return createProxy(typeUuid, value, uri);
   }
}
