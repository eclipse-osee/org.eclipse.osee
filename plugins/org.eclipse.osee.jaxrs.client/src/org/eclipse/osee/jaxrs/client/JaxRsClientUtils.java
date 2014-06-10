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
package org.eclipse.osee.jaxrs.client;

import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsClientUtils {

   private JaxRsClientUtils() {
      // Utility class
   }

   public static Integer getInt(Map<String, Object> props, String key, int defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return Strings.isNumeric(toReturn) ? Integer.parseInt(toReturn) : -1;
   }

   public static boolean getBoolean(Map<String, Object> props, String key, boolean defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return Boolean.parseBoolean(toReturn);
   }

   public static String get(Map<String, Object> props, String key, String defaultValue) {
      String toReturn = defaultValue;
      Object object = props != null ? props.get(key) : null;
      if (object != null) {
         toReturn = String.valueOf(object);
      }
      return toReturn;
   }

   public static Matcher<? super TypeLiteral<?>> subtypeOf(Class<?> superclass) {
      return new SubTypeOfMatcher(TypeLiteral.get(superclass));
   }

   private static final class SubTypeOfMatcher extends AbstractMatcher<TypeLiteral<?>> {
      private final TypeLiteral<?> superType;

      public SubTypeOfMatcher(TypeLiteral<?> superType) {
         super();
         this.superType = superType;
      }

      @Override
      public boolean matches(TypeLiteral<?> subType) {
         return subType.equals(superType) || superType.getRawType().isAssignableFrom(subType.getRawType());
      }
   }

}
