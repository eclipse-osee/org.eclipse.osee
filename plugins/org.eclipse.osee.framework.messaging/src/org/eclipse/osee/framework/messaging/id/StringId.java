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
package org.eclipse.osee.framework.messaging.id;

import java.io.Serializable;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class StringId implements Serializable {

   private static final long serialVersionUID = 80655792810954088L;
   private Namespace namespace;
   private Name name;

   public StringId(Namespace namespace, Name name) {
      this.namespace = namespace;
      this.name = name;
   }
   
   @Override
   public String toString() {
      return String.format("StringId (NameSpace: %s, Name: %s)", namespace.toString(), name.toString());
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof StringId) {
         return namespace.equals(((StringId) obj).namespace) && name.equals(((StringId) obj).name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 31 * hash + namespace.hashCode();
      hash = 31 * hash + name.hashCode();
      return hash;
   }

   public Name getName() {
      return name;
   }

   public Namespace getNamespace() {
      return namespace;
   }
}
