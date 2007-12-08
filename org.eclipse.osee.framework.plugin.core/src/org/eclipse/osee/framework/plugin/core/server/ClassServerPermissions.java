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
package org.eclipse.osee.framework.plugin.core.server;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * @author Andrew M. Finkbeiner
 */
public class ClassServerPermissions extends PermissionCollection {

   private static final long serialVersionUID = 7752469678730039503L;
   private ArrayList<Permission> list;

   public ClassServerPermissions() {
      list = new ArrayList<Permission>();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.security.PermissionCollection#add(java.security.Permission)
    */
   public void add(Permission permission) {
      list.add(permission);
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.security.PermissionCollection#implies(java.security.Permission)
    */
   public boolean implies(Permission permission) {
      for (int i = 0; i < list.size(); i++) {
         if (list.get(i).implies(permission)) return true;
      }
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.security.PermissionCollection#elements()
    */
   public Enumeration<Permission> elements() {
      return null;
   }

}
