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
package org.eclipse.osee.ats.artifact;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class LogItem {

   private Date date;
   private String msg;
   private String state;
   private User user;
   private LogType type = LogType.None;
   private String userId;

   /**
    * @return the userId
    */
   public String getUserId() {
      return userId;
   }

   public LogItem() {
   }

   public LogItem(LogType type, Date date, User user, String state, String msg) throws OseeCoreException {
      this(type.name(), date.getTime() + "", user.getUserId(), state, msg);
   }

   public LogItem(LogType type, String date, String userId, String state, String msg) throws OseeCoreException {
      Long l = new Long(date);
      this.date = new Date(l.longValue());
      this.msg = msg;
      this.state = state;
      this.userId = userId;
      this.user = UserManager.getUserByUserId(userId);
      this.type = type;
   }

   public LogItem(String type, String date, String userId, String state, String msg) throws OseeCoreException {
      this(LogType.getType(type), date, userId, state, msg);
   }

   public String toXml() throws OseeCoreException {
      return "<type>" + type.name() + "</type><date>" + date.getTime() + "</date><user>" + user.getUserId() + "</user><state>" + (state != null ? state : "") + "</state><msg>" + (msg != null ? msg : "") + "</msg>";
   }

   public Date getDate() {
      return date;
   }

   public String getDate(String pattern) {
      if (pattern != null) return (new SimpleDateFormat(pattern)).format(date);
      return date.toString();
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public String getMsg() {
      return msg;
   }

   public void setMsg(String msg) {
      this.msg = msg;
   }

   @Override
   public String toString() {
      try {
         return (msg.equals("") ? "" : msg) + " (" + type + ") " + (state.equals("") ? "" : "from " + state + " ") + "by " + user.getUserId() + " on " + getDate(XDate.MMDDYYHHMM) + "\n";
      } catch (Exception ex) {
         return (msg.equals("") ? "" : msg) + " (" + type + ") " + (state.equals("") ? "" : "from " + state + " ");
      }
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

   /**
    * @param user The user to set.
    */
   public void setUser(User user) {
      this.user = user;
   }

   /**
    * @return Returns the state.
    */
   public String getState() {
      return state;
   }

   /**
    * @param state The state to set.
    */
   public void setState(String state) {
      this.state = state;
   }
}
