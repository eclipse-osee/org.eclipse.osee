/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.messaging;

import java.io.Serializable;
import java.net.URI;

/**
 * @author Roberto E. Escobar
 */
public class NodeInfo implements Serializable {

   private static final long serialVersionUID = -5245181117185581620L;
   private final URI uri;
   private final String name;
   private final String nameWithColon;

   public NodeInfo(String name, URI uri) {
      this.uri = uri;
      this.name = name;
      nameWithColon = name + ":";
   }

   public URI getUri() {
      return uri;
   }

   @Override
   public String toString() {
      return name + ":" + uri;
   }

   public String getComponentName() {
      return name;
   }

   public String getComponentNameForRoutes() {
      return nameWithColon;
   }

   public boolean isVMComponent() {
      return getComponentName().equals(Component.VM.getComponentName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (name == null ? 0 : name.hashCode());
      result = prime * result + (uri == null ? 0 : uri.hashCode());
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
      NodeInfo other = (NodeInfo) obj;
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      if (uri == null) {
         if (other.uri != null) {
            return false;
         }
      } else if (!uri.equals(other.uri)) {
         return false;
      }
      return true;
   }

}
