/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.config.admin.internal;

import static org.eclipse.osee.config.admin.internal.ConfigManagerConstants.CONFIGURATION_POLL_TIME;
import static org.eclipse.osee.config.admin.internal.ConfigManagerConstants.CONFIGURATION_POLL_TIME_UNIT;
import static org.eclipse.osee.config.admin.internal.ConfigManagerConstants.CONFIGURATION_URI;
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
      config.setConfigUri(configUri);
      return this;
   }

   private static final class ConfigManagerConfigurationImpl implements ConfigManagerConfiguration, Cloneable {

      private String configUri;
      private long pollTime;
      private TimeUnit pollTimeUnit;

      @Override
      public synchronized ConfigManagerConfigurationImpl clone() {
         ConfigManagerConfigurationImpl cloned = new ConfigManagerConfigurationImpl();
         cloned.configUri = this.configUri;
         cloned.pollTime = this.pollTime;
         cloned.pollTimeUnit = this.pollTimeUnit;
         return cloned;
      }

      @Override
      public String getConfigUri() {
         return configUri;
      }

      @Override
      public long getPollTime() {
         return pollTime;
      }

      @Override
      public TimeUnit getTimeUnit() {
         return pollTimeUnit;
      }

      public void setConfigUri(String configUri) {
         this.configUri = configUri;
      }

      public void setPollTime(long pollTime) {
         this.pollTime = pollTime;
      }

      public void setPollTimeUnit(TimeUnit pollTimeUnit) {
         this.pollTimeUnit = pollTimeUnit;
      }

      public void loadProperties(Map<String, Object> props) {
         if (props != null) {
            setConfigUri(get(props, CONFIGURATION_URI, ConfigUtil.getDefaultConfig()));
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
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (configUri == null ? 0 : configUri.hashCode());
         result = prime * result + (int) (pollTime ^ pollTime >>> 32);
         result = prime * result + (pollTimeUnit == null ? 0 : pollTimeUnit.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         ConfigManagerConfigurationImpl other = (ConfigManagerConfigurationImpl) obj;
         if (configUri == null) {
            if (other.configUri != null) {
               return false;
            }
         } else if (!configUri.equals(other.configUri)) {
            return false;
         }
         if (pollTime != other.pollTime) {
            return false;
         }
         if (pollTimeUnit != other.pollTimeUnit) {
            return false;
         }
         return true;
      }

      @Override
      public String toString() {
         return "ConfigManagerConfigurationImpl [configFile=" + configUri + ", pollTime=" + pollTime + ", pollTimeUnit=" + pollTimeUnit + "]";
      }

   }

}