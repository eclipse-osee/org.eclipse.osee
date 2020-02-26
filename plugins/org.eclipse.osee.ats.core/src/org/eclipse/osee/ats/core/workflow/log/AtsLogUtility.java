/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow.log;

import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.ILogStorageProvider;
import org.eclipse.osee.ats.core.internal.log.AtsLogHtml;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public class AtsLogUtility {

   public static String getHtml(IAtsLog atsLog, ILogStorageProvider storageProvider, IAtsUserService userService) {
      return getHtml(atsLog, storageProvider, true, userService);
   }

   public static String getHtml(IAtsLog atsLog, ILogStorageProvider storageProvider, boolean showLogTitle, IAtsUserService userService) {
      return new AtsLogHtml(atsLog, storageProvider, userService, showLogTitle).get();
   }

   public static String getTable(IAtsLog atsLog, ILogStorageProvider storageProvider, IAtsUserService userService) {
      return new AtsLogHtml(atsLog, storageProvider, userService, true).getTable();
   }

   public static String getToStringUser(IAtsLogItem item, IAtsUserService userService) {
      AtsUser user = userService.getUserById(item.getUserId());
      return user == null ? "unknown" : user.getName();
   }

   public static String toString(IAtsLogItem item, IAtsUserService userService) {
      AtsUser user = userService.getUserById(item.getUserId());
      return String.format("%s (%s)%s by %s on %s", getToStringMsg(item), item.getType(), getToStringState(item),
         user.getName(), DateUtil.getMMDDYYHHMM(item.getDate()));
   }

   public static String getToStringState(IAtsLogItem item) {
      return item.getState().isEmpty() ? "" : "from " + item.getState();
   }

   public static String getToStringMsg(IAtsLogItem item) {
      return item.getMsg().isEmpty() ? "" : item.getMsg();
   }

   public static String getUserName(String userId, IAtsUserService userService) {
      String name = "unknown (" + userId + ")";
      AtsUser user = userService.getUserById(userId);
      if (user != null) {
         name = user.getName();
      }
      return name;
   }

}
