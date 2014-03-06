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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.FileChangeEvent;
import org.eclipse.osee.framework.jdk.core.util.io.FileChangeType;
import org.eclipse.osee.framework.jdk.core.util.io.FileWatcher;
import org.eclipse.osee.framework.jdk.core.util.io.IFileWatcherListener;
import org.eclipse.osee.logger.Log;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Roberto E. Escobar
 */
public class ConfigManagerImpl implements IFileWatcherListener, ConfigWriter {

   private final ConfigParser parser = new ConfigParser();

   private Log logger;
   private ConfigurationAdmin configAdmin;

   private ConfigManagerConfiguration config;
   private final AtomicReference<FileWatcher> watcherRef = new AtomicReference<FileWatcher>();

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

      FileWatcher watcher = watcherRef.get();
      close(watcher);
   }

   public void update(Map<String, Object> properties) {
      logger.trace("Configuring ConfigurationManagerImpl...");

      config = ConfigManagerConfigurationBuilder.newBuilder()//
      .properties(properties) //
      .build();
      configure(config);
   }

   private void configure(ConfigManagerConfiguration config) {
      logger.info("Configuration Manager settings: [%s]", config);
      String path = config.getConfigFile();
      if (Strings.isValid(path)) {
         File configFile = new File(path);
         if (configFile.exists() && configFile.canRead()) {
            logger.warn("Reading configuration from: [%s]", configFile);
            long pollTime = config.getPollTime();
            TimeUnit timeUnit = config.getTimeUnit();

            FileWatcher newWatcher = new FileWatcher(pollTime, timeUnit);
            newWatcher.addFile(configFile);
            newWatcher.addListener(this);

            FileWatcher oldWatcher = watcherRef.getAndSet(newWatcher);
            close(oldWatcher);
            newWatcher.start();

            processFile(configFile);
         } else {
            logger.warn("Config file [%s] is not readable", configFile);
         }
      } else {
         logger.warn("Invalid configuration file: [%s]", path);
      }
   }

   private void close(FileWatcher watcher) {
      if (watcher != null) {
         watcher.stop();
         watcher.removeListener(this);
      }
   }

   @Override
   public void filesModified(Collection<FileChangeEvent> fileChangeEvents) {
      for (FileChangeEvent event : fileChangeEvents) {
         if (event.getChangeType() == FileChangeType.MODIFIED) {
            File file = event.getFile();
            processFile(file);
         }
      }
   }

   private void processFile(File file) {
      try {
         String source = Lib.fileToString(file);
         parser.process(this, source);
      } catch (Exception ex) {
         logger.error(ex, "Error processing config [%s]", file);
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
         configuration.update(properties);
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
      if (logger.isDebugEnabled()) {
         StringBuilder builder = new StringBuilder();
         ConfigUtil.writeConfig(configuration, builder);
         logger.debug(builder.toString());
      }
   }

}
