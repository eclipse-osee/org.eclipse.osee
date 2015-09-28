/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.dsl;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.ats.dsl.atsDsl.UserByName;
import org.eclipse.osee.ats.dsl.atsDsl.UserByUserId;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class UserRefUtil {

   public static Set<String> getUserIds(EList<UserRef> userRefs) {
      Set<String> userIds = new HashSet<>();
      for (UserRef UserRef : userRefs) {
         if (UserRef instanceof UserByUserId) {
            userIds.add(((UserByUserId) UserRef).getUserId());
         }
      }
      return userIds;
   }

   public static Set<String> getUserNames(EList<UserRef> userRefs) {
      Set<String> userNames = new HashSet<>();
      for (UserRef UserRef : userRefs) {
         if (UserRef instanceof UserByName) {
            userNames.add(Strings.unquote(((UserByName) UserRef).getUserName()));
         }
      }
      return userNames;
   }

}
