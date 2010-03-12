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
package org.eclipse.osee.ote.core.cmd;

import java.io.Serializable;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class StringCommandId implements CommandId, Serializable {

   private static final long serialVersionUID = 2236967568467058971L;
   private Namespace namespace;
   private Name name;
   
   public StringCommandId(Namespace namespace, Name name){
      this.namespace = namespace;
      this.name = name;
   }

   @Override
   public boolean equals(Object obj) {
      if(obj instanceof StringCommandId){
         return namespace.equals(((StringCommandId)obj).namespace) && 
         name.equals(((StringCommandId)obj).name);
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
