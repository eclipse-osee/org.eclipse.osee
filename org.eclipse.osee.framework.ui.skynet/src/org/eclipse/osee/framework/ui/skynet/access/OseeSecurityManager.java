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
package org.eclipse.osee.framework.ui.skynet.access;

import java.util.ArrayList;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.NoPoliciesException;
import org.eclipse.osee.framework.skynet.core.access.PermissionList;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Provides security for OSEE applications
 * 
 * @author Jeff C. Phillips
 */

public class OseeSecurityManager extends SecurityManager {

   private static OseeSecurityManager ref = null;

   //   private AccessControl accessControl = null;

   OseeSecurityManager() {
      //      accessControl = new AccessControl();
   }

   public static synchronized OseeSecurityManager getInstance() {
      if (ref == null) {
         ref = new OseeSecurityManager();
      }
      return ref;
   }

   /**
    * @param person
    * @param policyIds
    * @throws SecurityException if a person does not have permission for action
    * @throws NoPoliciesException if a person does not have any policies
    */
   public void checkPermission(User person, ArrayList<Integer> policyIds) throws SecurityException, NoPoliciesException {
      return;

      //      for (int policyId : policyIds)
      //         checkPermission(person, null, policyId);
   }

   public void checkPermission(MenuItem[] items, User person, ArrayList<Integer> policyIds) {
      return;

      //      for (MenuItem item : items)
      //         for (int policyId : policyIds)
      //            checkPermission(item, person, policyId);
   }

   public void checkPermission(MenuItem item, User person, ArrayList<Integer> policyIds) {
      return;

      //         for (int policyId : policyIds)
      //            checkPermission(item, person, policyId);
   }

   /**
    * @param item
    * @param person
    * @param policyId
    * @throws SecurityException
    * @throws NoPoliciesException
    */
   public void checkPermission(MenuItem item, User person, int policyId) {
      return;

      //      try {
      //         checkPermission(person, null, policyId);
      //         item.setEnabled(true);
      //         item.setImage(null);
      //      }
      //      catch (NoPoliciesException ex) {
      //         item.setEnabled(false);
      //         item.setImage(CorePlugin.getInstance().getImage("errorSm.gif"));
      //      }
      //      catch (SecurityException ex) {
      //         item.setEnabled(false);
      //         item.setImage(CorePlugin.getInstance().getImage("permission.bmp"));
      //      }
   }

   /**
    * @param person
    * @param permission
    * @throws SecurityException if a person does not have permission for action
    * @throws NoPoliciesException if a person does not have any policies
    */
   public void checkPermission(User person, PermissionList permission) throws SecurityException, NoPoliciesException {
      return;

      //      checkPermission(person, permission, -1);
   }

   public void checkPermission(PermissionList permission) throws SecurityException, NoPoliciesException, OseeCoreException {
      checkPermission(UserManager.getUser(), permission);
   }

   /**
    * @param person person object containing policies
    * @param permission permission to perform a specific action
    * @throws SecurityException if a person does not have permission for action
    * @throws NoPoliciesException if a person does not have any policies
    */
   public void checkPermission(User person, PermissionList permission, int policyId) throws SecurityException, NoPoliciesException {
      return;

      //      if (ConfigUtil.getConfigFactory().getOseeConfig().isBypassSecurity() || true)
      //         return;
      //
      //      if (person == null)
      //         return;
      //
      //      if (!person.getPolicies().isEmpty()) {
      //
      //         if (policyId < 0 && permission != null)
      //            accessControl.checkPermission(person, permission);
      //         else
      //            accessControl.checkPermission(person, policyId);
      //      }
      //      else
      //         throw new NoPoliciesException(person.getName());
   }
}
