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

package org.eclipse.osee.framework.core.data;

/**
 * @author Roberto E. Escobar
 */
public final class HelpContext {
   private final String name;
   private final String pluginId;

   protected HelpContext(String pluginId, String name) {
      this.pluginId = pluginId;
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public String getPluginId() {
      return pluginId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (name == null ? 0 : name.hashCode());
      result = prime * result + (pluginId == null ? 0 : pluginId.hashCode());
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
      HelpContext other = (HelpContext) obj;
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      if (pluginId == null) {
         if (other.pluginId != null) {
            return false;
         }
      } else if (!pluginId.equals(other.pluginId)) {
         return false;
      }
      return true;
   }

   public String asReference() {
      return asReference(pluginId, name);
   }

   public static String asReference(String pluginId, String name) {
      return String.format("%s.%s", pluginId, name);
   }
}
