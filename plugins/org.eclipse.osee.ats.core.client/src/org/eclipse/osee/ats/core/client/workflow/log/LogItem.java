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
package org.eclipse.osee.ats.core.client.workflow.log;

import static org.eclipse.osee.framework.jdk.core.util.Strings.intern;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public class LogItem {

   private Date date;
   private String msg;
   private String state;
   private IAtsUser user;
   private LogType type = LogType.None;
   private final String userId;

   public LogItem(LogType type, Date date, IAtsUser user, String state, String msg, String hrid) throws OseeCoreException {
      this(type.name(), String.valueOf(date.getTime()), user.getUserId(), state, msg, hrid);
   }

   public LogItem(LogType type, String date, String userId, String state, String msg, String hrid) throws OseeCoreException {
      Long dateLong = Long.valueOf(date);
      this.date = new Date(dateLong.longValue());
      this.msg = msg;
      this.state = intern(state);
      this.userId = intern(userId);
      this.user = AtsClientService.get().getUserAdmin().getUserById(userId);
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

   public IAtsUser getUser() {
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

   public void setUser(IAtsUser user) {
      this.user = user;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }
}
