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
package org.eclipse.osee.ats.core.internal.log;

import static org.eclipse.osee.framework.jdk.core.util.Strings.intern;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.workflow.log.AtsLogUtility;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public class LogItem implements IAtsLogItem {

   private Date date;
   private String msg;
   private String state;
   private String userId;
   private LogType type = LogType.None;

   public LogItem(LogType type, Date date, String userId, String state, String msg)  {
      this(type.name(), String.valueOf(date.getTime()), userId, state, msg);
   }

   public LogItem(LogType type, String date, String userId, String state, String msg)  {
      Long dateLong = Long.valueOf(date);
      this.date = new Date(dateLong.longValue());
      this.msg = msg;
      this.state = intern(state);
      this.userId = intern(userId);
      this.type = type;
   }

   public LogItem(String type, String date, String userId, String state, String msg)  {
      this(LogType.getType(type), date, userId, state, msg);
   }

   @Override
   public Date getDate() {
      return date;
   }

   @Override
   public String getDate(String pattern) {
      if (pattern != null) {
         return new SimpleDateFormat(pattern, Locale.US).format(date);
      }
      return date.toString();
   }

   @Override
   public void setDate(Date date) {
      this.date = date;
   }

   @Override
   public String getUserId() {
      return userId;
   }

   @Override
   public String getMsg() {
      return msg;
   }

   @Override
   public void setMsg(String msg) {
      this.msg = msg;
   }

   @Override
   public String toString() {
      return String.format("%s (%s)%s by %s on %s", AtsLogUtility.getToStringMsg(this), type,
         AtsLogUtility.getToStringState(this), getUserId(), DateUtil.getMMDDYYHHMM(date));
   }

   @Override
   public LogType getType() {
      return type;
   }

   @Override
   public void setType(LogType type) {
      this.type = type;
   }

   @Override
   public String getState() {
      return state;
   }

   @Override
   public void setState(String state) {
      this.state = state;
   }

   @Override
   public String setUserId(String userId) {
      return this.userId = userId;
   }
}
