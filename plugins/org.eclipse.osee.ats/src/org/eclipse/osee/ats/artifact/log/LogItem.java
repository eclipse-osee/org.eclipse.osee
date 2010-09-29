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
package org.eclipse.osee.ats.artifact.log;

import static org.eclipse.osee.framework.jdk.core.util.Strings.intern;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;

/**
 * @author Donald G. Dunne
 */
public class LogItem {

   private Date date;
   private String msg;
   private String state;
   private User user;
   private LogType type = LogType.None;
   private final String userId;

   public LogItem(LogType type, Date date, User user, String state, String msg, String hrid) throws OseeCoreException {
      this(type.name(), String.valueOf(date.getTime()), user.getUserId(), state, msg, hrid);
   }

   public LogItem(LogType type, String date, String userId, String state, String msg, String hrid) throws OseeCoreException {
      Long dateLong = Long.valueOf(date);
      this.date = new Date(dateLong.longValue());
      this.msg = msg;
      this.state = intern(state);
      this.userId = intern(userId);
      try {
         this.user = UserManager.getUserByUserId(userId);
      } catch (UserNotInDatabase ex) {
         this.user = UserManager.getUser(SystemUser.Guest);
         OseeLog.log(AtsPlugin.class, Level.SEVERE,
            String.format("Error parsing ATS Log for %s - %s", hrid, ex.getLocalizedMessage()), ex);
      }
      this.type = type;
   }

   public LogItem(String type, String date, String userId, String state, String msg, String hrid) throws OseeCoreException {
      this(LogType.getType(type), date, userId, state, msg, hrid);
   }

   public Date getDate() {
      return date;
   }

   public String getDate(String pattern) {
      if (pattern != null) {
         return new SimpleDateFormat(pattern, Locale.US).format(date);
      }
      return date.toString();
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public String getUserId() {
      return userId;
   }

   public String getMsg() {
      return msg;
   }

   public void setMsg(String msg) {
      this.msg = msg;
   }

   @Override
   public String toString() {
      return String.format("%s (%s)%s by %s on %s", getToStringMsg(), type, getToStringState(), getToStringUser(),
         DateUtil.getMMDDYYHHMM(date));
   }

   private String getToStringUser() {
      return user == null ? "unknown" : user.getName();
   }

   private String getToStringState() {
      return state.isEmpty() ? "" : "from " + state;
   }

   private String getToStringMsg() {
      return msg.isEmpty() ? "" : msg;
   }

   public User getUser() {
      return user;
   }

   public LogType getType() {
      return type;
   }

   public void setType(LogType type) {
      this.type = type;
   }

   public String toHTML(String labelFont) {
      return "NOTE (" + type + "): " + msg + " (" + user.getName() + ")";
   }

   public void setUser(User user) {
      this.user = user;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }
}
