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
package org.eclipse.osee.ote.core;

import java.io.Serializable;

public class OSEEPerson1_4 implements Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = 6345874159597384661L;

   private String name;

   private String email;

   private String id;

   public OSEEPerson1_4(String name, String email, String id) {
      this.name = name;
      this.email = email;
      this.id = id;
   }

   public String getEmail() {
      return email;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public boolean equals(Object arg0) {
      if (arg0 instanceof OSEEPerson1_4) {
         OSEEPerson1_4 person = (OSEEPerson1_4) arg0;
         return person.name.equals(this.name) && person.email.equals(this.email) && person.id.equals(this.id);
      }
      return false;
   }

   public int hashCode() {
      int hashCode = 0;
      if (name != null) {
         hashCode += name.hashCode();
      }
      if (email != null) {
         hashCode += email.hashCode();
      }
      if (id != null) {
         hashCode += id.hashCode();
      }
      return hashCode;
   }

   public String toString() {
      return name + ":" + email + ":" + id + " hash=" + hashCode();
   }
}
