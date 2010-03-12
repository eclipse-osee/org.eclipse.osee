/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.server.internal;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public final class BuildInfo {
   private final String typeName;
   private final Set<String> versionPattern;

   public BuildInfo(String typeName, String... patterns) {
      this.typeName = typeName;
      this.versionPattern = new HashSet<String>();

      if (patterns != null && patterns.length > 0) {
         for (String pattern : patterns) {
            addPattern(pattern);
         }
      }
   }

   public void addPattern(String version) {
      versionPattern.add(version);
   }

   public String getName() {
      return typeName;
   }

   public String[] getVersions() {
      return versionPattern.toArray(new String[versionPattern.size()]);
   }

   @Override
   public String toString() {
      return String.format("Build:[%s] Patterns:%s", getName(), versionPattern);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (typeName == null ? 0 : typeName.hashCode());
      result = prime * result + (versionPattern == null ? 0 : versionPattern.hashCode());
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
      BuildInfo other = (BuildInfo) obj;
      if (typeName == null) {
         if (other.typeName != null) {
            return false;
         }
      } else if (!typeName.equals(other.typeName)) {
         return false;
      }
      if (versionPattern == null) {
         if (other.versionPattern != null) {
            return false;
         }
      } else if (!versionPattern.equals(other.versionPattern)) {
         return false;
      }
      return true;
   }

}