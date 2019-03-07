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
package org.eclipse.osee.config.admin.internal;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.UriWatcher;
import org.eclipse.osee.framework.jdk.core.util.io.UriWatcher.UriWatcherListener;
import org.eclipse.osee.logger.Log;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Roberto E. Escobar
 */
public class ConfigManagerImpl implements UriWatcherListener {

   private final ConfigParser parser = new ConfigParser();

   private Log logger;
   private ConfigurationAdmin configAdmin;

   private ConfigManagerConfiguration config;
   private final AtomicReference<UriWatcher> watcherRef = new AtomicReference<>();
   private final Map<String, ServiceConfig> services = new HashMap<>();

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setConfigAdmin(ConfigurationAdmin configAdmin) {
      this.configAdmin = configAdmin;
   }

   public void start(Map<String, Object> properties) {
      logger.trace("Starting ConfigurationManagerImpl...");

      update(properties);
   }

   public void stop() {
      logger.trace("Stopping ConfigurationManagerImpl...");

      UriWatcher watcher = watcherRef.get();
      close(watcher);
   }

   public void update(Map<String, Object> properties) {
      logger.trace("Configuring ConfigurationManagerImpl...");

      ConfigManagerConfiguration newConfig = ConfigManagerConfigurationBuilder.newBuilder()//
         .properties(properties) //
         .build();
      if (Compare.isDifferent(config, newConfig)) {
         configure(newConfig);
         config = newConfig;
      }
   }

   private void configure(ConfigManagerConfiguration config) {
      logger.info("Configuration Manager settings: [%s]", config);
      logger.warn("configuration file [" + config.getConfigUri() + "]");
      URI configUri = ConfigUtil.asUri(config.getConfigUri());
      if (configUri != null) {
         long pollTime = config.getPollTime();
         TimeUnit timeUnit = config.getTimeUnit();

         logger.warn("Reading configuration from: [%s] every [%s %s]", configUri, pollTime, timeUnit);

         UriWatcher newWatcher = new UriWatcher(pollTime, timeUnit);
         newWatcher.addUri(configUri);
         newWatcher.addListener(this);

         UriWatcher oldWatcher = watcherRef.getAndSet(newWatcher);
         close(oldWatcher);
         newWatcher.start();

         processUri(configUri);
      } else {
         logger.warn("Invalid configuration file");
      }
   }

   private void close(UriWatcher watcher) {
      if (watcher != null) {
         watcher.stop();
         watcher.removeListener(this);
      }
   }

   private void processUri(URI uri) {
      try {
         String source = Lib.inputStreamToString(uri.toURL().openStream());
         final Map<String, Dictionary<String, Object>> newConfigs = new HashMap<>();
         parser.process(new ConfigWriter() {

            @Override
            public void write(String serviceId, Dictionary<String, Object> props) {
               newConfigs.put(serviceId, props);
            }
         }, source);
         configureServices(newConfigs);
      } catch (Exception ex) {
         logger.error(ex, "Error processing config [%s]", uri);
      }
   }

   @Override
   public void handleException(Exception ex) {
      logger.error(ex, "Error monitoring framework configuration [%s]", config);
   }

   @Override
   public void modificationDateChanged(Collection<URI> uris) {
      for (URI uri : uris) {
         processUri(uri);
      }
   }

   private void configureServices(Map<String, Dictionary<String, Object>> newConfigs) {
      Iterable<String> removed =
         org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(services.keySet(), newConfigs.keySet());
      for (String id : removed) {
         ServiceConfig component = services.remove(id);
         if (component != null) {
            component.stop();
         }
      }

      for (Entry<String, Dictionary<String, Object>> entry : newConfigs.entrySet()) {
         String serviceId = entry.getKey();
         ServiceConfig component = services.get(serviceId);
         if (component == null) {
            component = new ServiceConfig(serviceId);
            services.put(serviceId, component);
         }
         component.update(entry.getValue());
      }
   }

   private final class ServiceConfig {

      private final AtomicBoolean isRegistered = new AtomicBoolean(false);
      private final String serviceId;
      private Dictionary<String, Object> properties;

      public ServiceConfig(String serviceId) {
         super();
         this.serviceId = serviceId;
      }

      public void update(Dictionary<String, Object> config) {
         if (!isRegistered.getAndSet(true) || Compare.isDifferent(config, properties)) {
            properties = config;
            try {
               Configuration configuration = configAdmin.getConfiguration(serviceId, null);
               configuration.update(config);
               if (logger.isDebugEnabled()) {
                  StringBuilder builder = new StringBuilder();
                  ConfigUtil.writeConfig(configuration, builder);
                  logger.debug(builder.toString());
               }
            } catch (IOException ex) {
               logger.error(ex, "Error configuring [%s] - config [%s]", serviceId, config);
            }
         }
      }

      public void stop() {
         if (isRegistered.getAndSet(false)) {
            try {
               Configuration configuration = configAdmin.getConfiguration(serviceId, null);
               configuration.delete();
            } catch (IOException ex) {
               logger.error(ex, "Error removing config [%s]", serviceId);
            }
         }
      }
   }
}
