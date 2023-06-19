/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.authorization.admin;

import static org.eclipse.osee.authorization.admin.AuthorizationConstants.AUTHORIZATION_OVERRIDE;
import static org.eclipse.osee.authorization.admin.AuthorizationConstants.AUTHORIZATION_SCHEME_ALLOWED;
import static org.eclipse.osee.authorization.admin.AuthorizationConstants.DEFAULT_AUTHORIZATION_OVERRIDE;
import static org.eclipse.osee.authorization.admin.AuthorizationConstants.DEFAULT_AUTHORIZATION_PROVIDER;
import static org.eclipse.osee.authorization.admin.internal.AuthorizationUtil.unmodifiableSortedIterable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.authorization.admin.internal.AuthorizationUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class AuthorizationConfigurationBuilder {

   private final AuthorizationConfigurationImpl config = new AuthorizationConfigurationImpl();

   private AuthorizationConfigurationBuilder() {
      //Builder class
   }

   public static AuthorizationConfigurationBuilder newBuilder() {
      return new AuthorizationConfigurationBuilder();
   }

   public AuthorizationConfiguration build() {
      return config.clone();
   }

   public AuthorizationConfigurationBuilder properties(Map<String, Object> props) {
      config.loadProperties(props);
      return this;
   }

   public AuthorizationConfigurationBuilder override(AuthorizationOverride override) {
      config.setOverride(override);
      return this;
   }

   public AuthorizationConfigurationBuilder scheme(String scheme) {
      config.addScheme(scheme);
      return this;
   }

   private static final class AuthorizationConfigurationImpl implements AuthorizationConfiguration, Cloneable {

      private AuthorizationOverride override;
      private final Set<String> schemes = new HashSet<>();
      private String defaultScheme;

      @Override
      public synchronized AuthorizationConfigurationImpl clone() {
         AuthorizationConfigurationImpl cloned = new AuthorizationConfigurationImpl();
         cloned.override = this.override;
         cloned.addSchemes(this.schemes);
         return cloned;
      }

      public void setOverride(AuthorizationOverride override) {
         this.override = override;
      }

      @Override
      public AuthorizationOverride getOverride() {
         return override;
      }

      @Override
      public boolean hasOverride() {
         return override != null && AuthorizationOverride.NONE != override;
      }

      @Override
      public Iterable<String> getAllowedSchemes() {
         return unmodifiableSortedIterable(schemes);
      }

      @Override
      public String getDefaultScheme() {
         return defaultScheme;
      }

      public void addSchemes(Collection<String> toAdd) {
         if (toAdd != null && !toAdd.isEmpty()) {
            for (String scheme : toAdd) {
               addScheme(scheme);
            }
         }
      }

      public void addScheme(String scheme) {
         if (Strings.isValid(scheme)) {
            schemes.add(AuthorizationUtil.normalize(scheme));
         }
      }

      public void setDefaultScheme(String scheme) {
         if (Strings.isValid(scheme)) {
            this.defaultScheme = scheme;
         }
      }

      public void loadProperties(Map<String, Object> props) {
         if (props != null && !props.isEmpty()) {
            setOverride(getOverrideType(props, AUTHORIZATION_OVERRIDE, DEFAULT_AUTHORIZATION_OVERRIDE));
            addSchemes(getSet(props, AUTHORIZATION_SCHEME_ALLOWED, DEFAULT_AUTHORIZATION_PROVIDER));
            setDefaultScheme(get(props, AuthorizationConstants.AUTHORIZATION_SCHEME_DEFAULT,
               AuthorizationConstants.DEFAULT_AUTHORIZATION_SCHEME_DEFAULT));
         }
      }

      private AuthorizationOverride getOverrideType(Map<String, Object> props, String key,
         AuthorizationOverride defaultValue) {
         String toReturn = get(props, key, defaultValue);
         return AuthorizationOverride.parse(toReturn);
      }

      private Collection<String> getSet(Map<String, Object> props, String key, String defaultValue) {
         Set<String> toReturn = new HashSet<>();
         String joinedArray = get(props, key, "");
         if (Strings.isValid(joinedArray)) {
            String[] split = joinedArray.split(",");
            for (String value : split) {
               String toAdd = AuthorizationUtil.normalize(value);
               if (Strings.isValid(toAdd)) {
                  toReturn.add(toAdd);
               }
            }
         } else {
            toReturn.add(defaultValue);
         }
         return toReturn;
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
   }

}