/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.model.access;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roberto E. Escobar
 * @author John R. Misinco
 */
public class Scope implements Cloneable {

   private static final String LEGACY_SCOPE = "##";
   private static final String ARTIFACT_LOCK_SCOPE = "##**";

   private final List<String> scopePath = new ArrayList<>();

   public Scope() {
      // Do nothing
   }

   public int getScopeDepth() {
      return scopePath.size();
   }

   public Scope add(String value) {
      scopePath.add(normalize(value));
      return this;
   }

   public String getPath() {
      StringBuilder builder = new StringBuilder();
      for (String entry : scopePath) {
         if (!entry.startsWith("#")) {
            builder.append("/");
         }
         builder.append(entry);
      }
      return builder.toString();
   }

   private String normalize(String value) {
      return value.replaceAll(" ", "_");
   }

   public Scope addSubPath(String value) {
      scopePath.add("#" + normalize(value));
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (scopePath == null ? 0 : scopePath.hashCode());
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
      Scope other = (Scope) obj;
      if (scopePath == null) {
         if (other.scopePath != null) {
            return false;
         }
      } else if (!scopePath.equals(other.scopePath)) {
         return false;
      }
      return true;
   }

   public boolean isLegacy() {
      return this instanceof LegacyScope;
   }

   @Override
   public String toString() {
      return getPath();
   }

   @Override
   public Scope clone() {
      Scope scope = new Scope();
      for (String value : this.scopePath) {
         scope.add(value);
      }
      return scope;
   }

   public static Scope createLegacyScope() {
      return new LegacyScope();
   }

   public static Scope createArtifactLockScope() {
      return new ArtifactLockScope();
   }

   private static abstract class NonCmScope extends Scope {
      @Override
      public Scope add(String path) {
         return this;
      }

      @Override
      public Scope addSubPath(String path) {
         return this;
      }
   }

   private static final class LegacyScope extends NonCmScope {

      @Override
      public String getPath() {
         return LEGACY_SCOPE;
      }

      @Override
      public Scope clone() {
         return Scope.createLegacyScope();
      }
   }

   private static final class ArtifactLockScope extends NonCmScope {

      @Override
      public String getPath() {
         return ARTIFACT_LOCK_SCOPE;
      }

      @Override
      public Scope clone() {
         return Scope.createArtifactLockScope();
      }
   }

}
