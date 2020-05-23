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

package org.eclipse.osee.authentication.admin;

import static org.eclipse.osee.authentication.admin.AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED;
import static org.eclipse.osee.authentication.admin.AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED_DEFAULT;
import static org.eclipse.osee.authentication.admin.AuthenticationConstants.DEFAULT_AUTHENTICATION_SCHEME;
import static org.eclipse.osee.authentication.admin.AuthenticationConstants.DEFAULT_AUTHENTICATION_SCHEME_ALLOWED_DEFAULT;
import static org.eclipse.osee.authentication.admin.internal.AuthenticationUtil.normalize;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.authentication.admin.internal.AuthenticationUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class AuthenticationConfigurationBuilder {

   private final AuthenticationConfigurationImpl config = new AuthenticationConfigurationImpl();

   private AuthenticationConfigurationBuilder() {
      //Builder class
   }

   public static AuthenticationConfigurationBuilder newBuilder() {
      return new AuthenticationConfigurationBuilder();
   }

   public AuthenticationConfigurationBuilder scheme(String scheme) {
      config.addScheme(scheme);
      return this;
   }

   public AuthenticationConfigurationBuilder properties(Map<String, Object> props) {
      config.loadProperties(props);
      return this;
   }

   public AuthenticationConfigurationBuilder defaultScheme(String scheme) {
      config.setDefaultScheme(scheme);
      return this;
   }

   public AuthenticationConfiguration build() {
      return config.clone();
   }

   private static final class AuthenticationConfigurationImpl implements AuthenticationConfiguration, Cloneable {

      private final Set<String> schemes = new HashSet<>();
      private String defaultScheme;

      @Override
      public Iterable<String> getAllowedSchemes() {
         return AuthenticationUtil.unmodifiableSortedIterable(schemes);
      }

      public void addSchemes(Collection<String> toAdd) {
         if (toAdd != null && !toAdd.isEmpty()) {
            schemes.addAll(toAdd);
         }
      }

      public void addScheme(String scheme) {
         if (Strings.isValid(scheme)) {
            schemes.add(scheme);
         }
      }

      @Override
      public String getDefaultScheme() {
         return defaultScheme;
      }

      public void setDefaultScheme(String defaultScheme) {
         this.defaultScheme = defaultScheme;
      }

      @Override
      public synchronized AuthenticationConfigurationImpl clone() {
         AuthenticationConfigurationImpl cloned = new AuthenticationConfigurationImpl();
         cloned.addSchemes(this.schemes);
         cloned.setDefaultScheme(this.getDefaultScheme());
         return cloned;
      }

      public void loadProperties(Map<String, Object> props) {
         if (props != null && !props.isEmpty()) {
            addSchemes(getSet(props, AUTHENTICATION_SCHEME_ALLOWED, DEFAULT_AUTHENTICATION_SCHEME));
            setDefaultScheme(
               get(props, AUTHENTICATION_SCHEME_ALLOWED_DEFAULT, DEFAULT_AUTHENTICATION_SCHEME_ALLOWED_DEFAULT));
         }
      }

      public Collection<String> getSet(Map<String, Object> props, String key, String defaultValue) {
         Set<String> toReturn = new HashSet<>();
         String joinedArray = get(props, key, "");
         if (Strings.isValid(joinedArray)) {
            String[] split = joinedArray.split(",");
            for (String value : split) {
               String toAdd = normalize(value);
               if (Strings.isValid(toAdd)) {
                  toReturn.add(toAdd);
               }
            }
         } else {
            toReturn.add(defaultValue);
         }
         return toReturn;
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