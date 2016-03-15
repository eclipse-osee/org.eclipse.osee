/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Ryan D. Brooks
 */
public class BaseId implements Id {
   private final Long id;

   public BaseId(Long id) {
      this.id = id;
   }

   @Override
   public int hashCode() {
      return id.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Id) {
         return ((Id) obj).getId().equals(id);
      }
      if (obj instanceof Identity<?>) {
         return id.equals(((Identity<?>) obj).getGuid());
      }
      return false;
   }

   @Override
   public String toString() {
      return String.valueOf(id);
   }

   @Override
   public Long getId() {
      return id;
   }
}