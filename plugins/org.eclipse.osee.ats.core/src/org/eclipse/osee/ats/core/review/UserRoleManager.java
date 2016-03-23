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
package org.eclipse.osee.ats.core.review;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class UserRoleManager {

   private final static String ROLE_ITEM_TAG = "Role";

   private final Matcher roleMatcher =
      java.util.regex.Pattern.compile("<" + ROLE_ITEM_TAG + ">(.*?)</" + ROLE_ITEM_TAG + ">",
         Pattern.DOTALL | Pattern.MULTILINE).matcher("");
   private List<UserRole> roles;
   private final IAtsUserService userService;
   private final IAttributeResolver attrResolver;
   private final IAtsWorkItem workItem;

   public UserRoleManager(IAttributeResolver attrResolver, IAtsUserService userService, IAtsWorkItem workItem) {
      this.attrResolver = attrResolver;
      this.userService = userService;
      this.workItem = workItem;
   }

   public void ensureLoaded() throws OseeCoreException {
      if (roles == null) {
         roles = new ArrayList<>();
         if (attrResolver != null) {
            for (String xml : attrResolver.getAttributesToStringList(workItem, AtsAttributeTypes.Role)) {
               roleMatcher.reset(xml);
               while (roleMatcher.find()) {
                  UserRole item = new UserRole(roleMatcher.group());
                  roles.add(item);
               }
            }
         }
      }
   }

   public List<UserRole> getUserRoles() throws OseeCoreException {
      ensureLoaded();
      return roles;
   }

   public List<UserRole> getUserRoles(Role role) throws OseeCoreException {
      List<UserRole> roles = new ArrayList<>();
      for (UserRole uRole : getUserRoles()) {
         if (uRole.getRole() == role) {
            roles.add(uRole);
         }
      }
      return roles;
   }

   public List<IAtsUser> getRoleUsers(Role role) throws OseeCoreException {
      List<IAtsUser> users = new ArrayList<>();
      for (UserRole uRole : getUserRoles()) {
         if (uRole.getRole() == role && !users.contains(userService.getUserById(uRole.getUserId()))) {
            users.add(userService.getUserById(uRole.getUserId()));
         }
      }
      return users;
   }

   public static List<IAtsUser> getRoleUsers(IAtsWorkItem workItem, Collection<UserRole> roles, IAtsUserService userService) throws OseeCoreException {
      List<IAtsUser> users = new ArrayList<>();
      for (UserRole uRole : roles) {
         if (!users.contains(userService.getUserById(uRole.getUserId()))) {
            users.add(userService.getUserById(uRole.getUserId()));
         }
      }
      return users;
   }

   public static List<UserRole> getUserRoles(IAtsWorkItem workItem, IAtsServices services) {
      return new UserRoleManager(services.getAttributeResolver(), services.getUserService(), workItem).getUserRoles();
   }
}
