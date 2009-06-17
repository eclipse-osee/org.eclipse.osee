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
package org.eclipse.osee.ote.core.framework.saxparse.elements;


/**
 * @author Andrew M. Finkbeiner
 *
 */
public class UserData{

   private String email;
   private String id;
   private String name;
   
   
   /**
    * @param name 
    * @param id 
    * @param email 
    * @param name
    */
   UserData(String email, String id, String name) {
      this.email = email;
      this.id = id;
      this.name = name;
   }


   /**
    * @return the email
    */
   public String getEmail() {
      return email;
   }


   /**
    * @return the id
    */
   public String getId() {
      return id;
   }


   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

}
