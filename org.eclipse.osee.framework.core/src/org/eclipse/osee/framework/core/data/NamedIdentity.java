/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.core.data;

/**
 * @author Ryan D. Brooks
 */
public class NamedIdentity implements Identity, Named {
   private final String guid;
   private final String name;

   public NamedIdentity(String guid, String name) {
      this.guid = guid;
      this.name = name;
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public int hashCode() {
      return getGuid().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof NamedIdentity) {
         return getGuid().equals(((NamedIdentity) obj).getGuid());
      }
      return false;
   }

   @Override
   public String toString() {
      return getName();
   }
}