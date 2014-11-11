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
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
public class ConfigManagerImpl implements UriWatcherListener, ConfigWriter {

   private final ConfigParser parser = new ConfigParser();

   private Log logger;
   private ConfigurationAdmin configAdmin;

   private ConfigManagerConfiguration config;
   private final AtomicReference<UriWatcher> watcherRef = new AtomicReference<UriWatcher>();

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

      ConfigManagerConfiguration temp = ConfigManagerConfigurationBuilder.newBuilder()//
      .properties(properties) //
      .build();
      if (!temp.equals(config)) {
         config = temp;
         configure(config);
      }
   }

   private void configure(ConfigManagerConfiguration config) {
      logger.info("Configuration Manager settings: [%s]", config);
      URI configUri = config.getConfigUri();
      if (configUri != null) {
         logger.warn("Reading configuration from: [%s]", configUri);
         long pollTime = config.getPollTime();
         TimeUnit timeUnit = config.getTimeUnit();

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
         parser.process(this, source);
      } catch (Exception ex) {
         logger.error(ex, "Error processing config [%s]", uri);
      }
   }

   @Override
   public void handleException(Exception ex) {
      logger.error(ex, "Error monitoring framework configuration [%s]", config);
   }

   @Override
   public void write(String serviceId, Dictionary<String, Object> properties) {
      Configuration configuration;
      try {
         configuration = configAdmin.getConfiguration(serviceId, null);
         if (Compare.isDifferent(configuration.getProperties(), properties)) {
            configuration.update(properties);
         }
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
      if (logger.isDebugEnabled()) {
         StringBuilder builder = new StringBuilder();
         ConfigUtil.writeConfig(configuration, builder);
         logger.debug(builder.toString());
      }
   }

   @Override
   public void modificationDateChanged(Collection<URI> uris) {
      for (URI uri : uris) {
         processUri(uri);
      }
   }

}
