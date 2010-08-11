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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Ryan D. Brooks
 */
public class NamedIdentity implements Identity, FullyNamed, HasDescription {
   private final String guid;
   private String name;
   private final String description;

   public NamedIdentity(String name) {
      this(null, name);
   }

   public NamedIdentity(String guid, String name) {
      this(guid, name, "");
   }

   public NamedIdentity(String guid, String name, String description) {
      if (guid == null) {
         this.guid = GUID.create();
      } else {
         this.guid = guid;
      }
      this.name = name;
      this.description = description;
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
   public String getUnqualifiedName() {
      return name.substring(name.lastIndexOf('.') + 1);
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

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public boolean matches(Identity... identities) {
      for (Identity identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }

   public void setName(String name) throws OseeCoreException {
      this.name = name;
   }
}