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

import static org.eclipse.osee.config.admin.internal.ConfigManagerConstants.CONFIGURATION_FILE;
import static org.eclipse.osee.config.admin.internal.ConfigManagerConstants.CONFIGURATION_POLL_TIME;
import static org.eclipse.osee.config.admin.internal.ConfigManagerConstants.CONFIGURATION_POLL_TIME_UNIT;
import static org.eclipse.osee.config.admin.internal.ConfigManagerConstants.DEFAULT_POLL_TIME;
import static org.eclipse.osee.config.admin.internal.ConfigManagerConstants.DEFAULT_POLL_TIME_UNIT;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ConfigManagerConfigurationBuilder {

   private final ConfigManagerConfigurationImpl config = new ConfigManagerConfigurationImpl();

   private ConfigManagerConfigurationBuilder() {
      //Builder class
   }

   public static ConfigManagerConfigurationBuilder newBuilder() {
      return new ConfigManagerConfigurationBuilder();
   }

   public ConfigManagerConfiguration build() {
      return config.clone();
   }

   public ConfigManagerConfigurationBuilder properties(Map<String, Object> props) {
      config.loadProperties(props);
      return this;
   }

   public ConfigManagerConfigurationBuilder pollTime(long pollTime, TimeUnit pollTimeUnit) {
      config.setPollTime(pollTime);
      config.setPollTimeUnit(pollTimeUnit);
      return this;
   }

   public ConfigManagerConfigurationBuilder configUri(String configUri) {
      config.setConfigFile(configUri);
      return this;
   }

   private static final class ConfigManagerConfigurationImpl implements ConfigManagerConfiguration, Cloneable {

      private String configFile;
      private long pollTime;
      private TimeUnit pollTimeUnit;

      @Override
      public synchronized ConfigManagerConfigurationImpl clone() {
         ConfigManagerConfigurationImpl cloned = new ConfigManagerConfigurationImpl();
         cloned.configFile = this.configFile;
         cloned.pollTime = this.pollTime;
         cloned.pollTimeUnit = this.pollTimeUnit;
         return cloned;
      }

      @Override
      public String getConfigFile() {
         return configFile;
      }

      @Override
      public long getPollTime() {
         return pollTime;
      }

      @Override
      public TimeUnit getTimeUnit() {
         return pollTimeUnit;
      }

      public void setConfigFile(String configFile) {
         this.configFile = configFile;
      }

      public void setPollTime(long pollTime) {
         this.pollTime = pollTime;
      }

      public void setPollTimeUnit(TimeUnit pollTimeUnit) {
         this.pollTimeUnit = pollTimeUnit;
      }

      public void loadProperties(Map<String, Object> props) {
         if (props != null) {
            setConfigFile(get(props, CONFIGURATION_FILE, ConfigUtil.getDefaultConfig()));
            setPollTime(getLong(props, CONFIGURATION_POLL_TIME, DEFAULT_POLL_TIME));
            setPollTimeUnit(getTimeUnit(props, CONFIGURATION_POLL_TIME_UNIT, DEFAULT_POLL_TIME_UNIT));
         }
      }

      private TimeUnit getTimeUnit(Map<String, Object> props, String pollTimeUnit, TimeUnit defaultPollTimeUnit) {
         String value = get(props, pollTimeUnit, defaultPollTimeUnit);
         TimeUnit toReturn = TimeUnit.SECONDS;
         for (TimeUnit unit : TimeUnit.values()) {
            if (unit.name().equalsIgnoreCase(value)) {
               toReturn = unit;
               break;
            }
         }
         return toReturn;
      }

      private long getLong(Map<String, Object> props, String key, Long defaultValue) {
         String toReturn = get(props, key, String.valueOf(defaultValue));
         return Strings.isNumeric(toReturn) ? Long.parseLong(toReturn) : 3L;
      }

      private String get(Map<String, Object> props, String key, Enum<?> defaultValue) {
         return get(props, key, defaultValue != null ? defaultValue.name() : null);
      }

      private String get(Map<String, Object> props, String key, String defaultValue) {
         String toReturn = defaultValue;
         Object object = props.get(key);
         if (object != null) {
            toReturn = String.valueOf(object);
         }
         return toReturn;
      }

      @Override
      public String toString() {
         return "ConfigManagerConfigurationImpl [configFile=" + configFile + ", pollTime=" + pollTime + ", pollTimeUnit=" + pollTimeUnit + "]";
      }

   }

}