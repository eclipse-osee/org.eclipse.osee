/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jdbc.internal.osgi;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.logger.Log;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

/**
 * @author Roberto E. Escobar
 */
public class JdbcComponentFactory {

   private final ConcurrentHashMap<String, JdbcServiceComponent> services = new ConcurrentHashMap<>();

   private JdbcServiceConfigParser parser;

   private Log logger;
   private ComponentFactory componentFactory;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setComponentFactory(ComponentFactory componentFactory) {
      this.componentFactory = componentFactory;
   }

   public void start(Map<String, Object> props) {
      parser = new JdbcServiceConfigParser();
      update(props);
   }

   public void stop() {
      Iterator<JdbcServiceComponent> iterator = services.values().iterator();
      while (iterator.hasNext()) {
         JdbcServiceComponent component = iterator.next();
         component.stop();
         iterator.remove();
      }
      parser = null;
   }

   public void update(Map<String, Object> props) {
      logger.trace("Configuring [%s]...", getClass().getSimpleName());
      String newJsonConfig = (String) props.get(JdbcConstants.JDBC_SERVICE__CONFIGS);
      if (newJsonConfig != null) {
         Map<String, JdbcServiceConfig> newConfigs = parser.parse(newJsonConfig);

         for (JdbcServiceConfig config : newConfigs.values()) {
            JdbcServiceComponent newComponent = new JdbcServiceComponent(config.getId());
            JdbcServiceComponent component = services.putIfAbsent(newComponent.getId(), newComponent);
            if (component == null) {
               component = newComponent;
            }
            component.update(config);
         }

         Iterable<String> removed = difference(services.keySet(), newConfigs.keySet());
         for (String id : removed) {
            JdbcServiceComponent component = services.remove(id);
            if (component != null) {
               component.stop();
            }
         }
      } else {
         logger.trace("No configuration with [%s] found for [%s]", JdbcConstants.JDBC_SERVICE__CONFIGS,
            getClass().getSimpleName());
      }
   }

   public Map<String, JdbcServiceComponent> getServices() {
      return Collections.unmodifiableMap(services);
   }

   private static Iterable<String> difference(Set<String> setA, Set<String> setB) {
      return org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(setA, setB);
   }

   public final class JdbcServiceComponent {

      private final String id;
      private ComponentInstance instance;
      private Map<String, Object> lastConfig;

      public JdbcServiceComponent(String id) {
         super();
         this.id = id;
      }

      public String getId() {
         return id;
      }

      public Map<String, Object> getConfig() {
         return lastConfig;
      }

      public void update(JdbcServiceConfig config) {
         Map<String, Object> newConfig = config.asMap();
         JdbcSvcCfgChangeType changeType = JdbcSvcCfgChangeType.getChangeType(lastConfig, newConfig);
         if (instance == null) {
            instance = componentFactory.newInstance(config.asDictionary());
         } else {
            switch (changeType) {
               case NO_CHANGE:
                  // Do nothing - no config change
                  break;
               case JDBC_PROPERTY:
                  JdbcServiceImpl object = (JdbcServiceImpl) instance.getInstance();
                  object.update(config.asMap());
                  break;
               default:
                  instance.dispose();
                  instance = componentFactory.newInstance(config.asDictionary());
                  break;
            }
         }
         lastConfig = newConfig;
      }

      public void stop() {
         if (instance != null) {
            instance.dispose();
         }
      }

   }

}
