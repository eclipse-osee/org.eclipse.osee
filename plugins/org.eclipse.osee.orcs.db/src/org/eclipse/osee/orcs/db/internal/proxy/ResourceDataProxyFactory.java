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
package org.eclipse.osee.orcs.db.internal.proxy;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.DataProxyFactory;

/**
 * @author Roberto E. Escobar
 */
public class ResourceDataProxyFactory implements DataProxyFactory {

   private final Map<String, Class<? extends AbstractDataSourceProxy>> proxyClassMap =
      new HashMap<String, Class<? extends AbstractDataSourceProxy>>();

   private Log logger;
   private IResourceManager resourceManager;
   private IResourceLocatorManager resourceLocator;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public Log getLogger() {
      return logger;
   }

   public IResourceManager getResourceManager() {
      return resourceManager;
   }

   public void setResourceManager(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public IResourceLocatorManager getResourceLocator() {
      return resourceLocator;
   }

   public void setResourceLocator(IResourceLocatorManager resourceLocator) {
      this.resourceLocator = resourceLocator;
   }

   private Class<? extends AbstractDataSourceProxy> getProxyClazz(String id) {
      return proxyClassMap.get(id);
   }

   public void start(Map<String, Object> properties) throws Exception {
      Object object = properties.get(PROXY_FACTORY_ALIAS);
      String[] factoryAliases = new String[0];
      if (object instanceof String[]) {
         factoryAliases = (String[]) object;
      }
      Conditions.checkNotNull(factoryAliases, "PROXY_FACTORY_ALIAS");

      proxyClassMap.put("DefaultAttributeDataProvider", SizeManagedCharacterDataProxy.class);
      proxyClassMap.put("ClobAttributeDataProvider", SizeManagedCharacterDataProxy.class);
      proxyClassMap.put("UriAttributeDataProvider", UriDataProxy.class);
      proxyClassMap.put("MappedAttributeDataProvider", MappedFileDataProxy.class);

      List<String> aliases = Arrays.asList(factoryAliases);
      Set<String> keys = proxyClassMap.keySet();

      Conditions.checkExpressionFailOnTrue(Collections.setComplement(aliases, keys).isEmpty(),
         "Check osgi.ds declaration - property mismatch detected");
      Conditions.checkExpressionFailOnTrue(Collections.setComplement(keys, aliases).isEmpty(),
         "Check osgi.ds declaration - property mismatch detected");
   }

   public void stop() {
      proxyClassMap.clear();
   }

   @Override
   public DataProxy createInstance(String factoryAlias) throws OseeCoreException {

      IResourceManager resourceManager = getResourceManager();
      IResourceLocatorManager locatorManager = getResourceLocator();

      DataHandler handler = new ResourceHandler(resourceManager, locatorManager);
      DataResource resource = new DataResource();
      Storage dataStore = new Storage(resource, handler);

      Class<? extends AbstractDataSourceProxy> clazz = getProxyClazz(factoryAlias);
      Conditions.checkNotNull(clazz, "DataProxy", "Unable to find data proxy clazz [%s]", factoryAlias);

      DataProxy dataProxy = null;
      try {
         Constructor<? extends DataProxy> constructor = clazz.getConstructor(new Class[] {Storage.class});
         dataProxy = constructor.newInstance(new Object[] {dataStore});
      } catch (Exception ex) {
         getLogger().error(ex, "Error creating data proxy for [%s]", factoryAlias);
         OseeExceptions.wrapAndThrow(ex);
      }
      return dataProxy;
   }
}
